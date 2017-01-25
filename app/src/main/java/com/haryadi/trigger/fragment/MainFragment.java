package com.haryadi.trigger.fragment;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.haryadi.trigger.R;
import com.haryadi.trigger.service.GeofenceTrasitionService;

/**
 * Created by aharyadi on 1/23/17.
 */

public class MainFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener, ResultCallback<Status>
        {

    private SupportMapFragment mapFragment;
    private GoogleMap map;

    public static final int MY_PERMISSIONS_REQUEST_READ_LOCATION = 1;

    private GoogleApiClient mGoogleApiClient;
            MapView mMapView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_loc, container, false);
    //    mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
      //  mapFragment = rootView.findViewById(R.id.map);
        mMapView = (MapView)rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);


        initGMaps();

        setUpGoogleClient();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

            @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    private void setUpGoogleClient(){
        if(mGoogleApiClient == null){
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).
                    addConnectionCallbacks(this).
                    addOnConnectionFailedListener(this).
                    addApi(LocationServices.API).
                    build();
        }
    }

    private void initGMaps(){

//        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.v("click", String.valueOf(latLng));
        markerForGeofence(latLng);
    }

    private void initCamera( LatLng latLng ) {
        CameraPosition position = CameraPosition.builder()
                .target( new LatLng( latLng.latitude,
                        latLng.longitude ) )
                .zoom( 16f )
                .bearing( 0.0f )
                .tilt( 0.0f )
                .build();

        map.animateCamera( CameraUpdateFactory
                .newCameraPosition( position ), null );

        map.setMapType( GoogleMap.MAP_TYPE_HYBRID);
        map.setTrafficEnabled( true );
        //  map.setMyLocationEnabled( true );
        map.getUiSettings().setZoomControlsEnabled( true );
    }

    private Marker geoFenceMarker;
    private void markerForGeofence(LatLng latLng){
        Log.i("sdsa", "markerForGeofence("+latLng+")");
        String title = latLng.latitude+" , "+ latLng.longitude;
        MarkerOptions markerOptions = new MarkerOptions().title(title).
                position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        if(map!=null){
            if(geoFenceMarker!=null){
                geoFenceMarker.remove();
            }
            geoFenceMarker=map.addMarker(markerOptions);
        }
        initCamera(latLng);
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            checkPermission();
        else
            startGeofence();
    }

    private static final long GEO_DURATION = 60 * 60 * 1000;
    private static final String GEOFENCE_REQ_ID = "My Geofence";
    private static final float GEOFENCE_RADIUS = 100.0f; // in meters

    // Create a Geofence
    private Geofence createGeofence(LatLng latLng, float radius ) {
        Log.d("jjj", "createGeofence");
        Log.v("latitude", String.valueOf(latLng.latitude+latLng.longitude));
        return new Geofence.Builder()
                .setRequestId(GEOFENCE_REQ_ID)
                .setCircularRegion( latLng.latitude, latLng.longitude, GEOFENCE_RADIUS)
                .setExpirationDuration( Geofence.NEVER_EXPIRE )
                .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT )
                .build();
    }

    // Create a Geofence Request
    private GeofencingRequest createGeofenceRequest(Geofence geofence ) {
        Log.d("jj", "createGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger( GeofencingRequest.INITIAL_TRIGGER_ENTER )
                .addGeofence( geofence )
                .build();
    }
    private PendingIntent geoFencePendingIntent;
    private final int GEOFENCE_REQ_CODE = 0;
    private PendingIntent createGeofencePendingIntent() {
        Log.d("nnnn", "createGeofencePendingIntent");
        if ( geoFencePendingIntent != null )
            return geoFencePendingIntent;

        Intent intent = new Intent( getActivity(), GeofenceTrasitionService.class);
        return PendingIntent.getService(
                getActivity(), GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT );
    }

    // Add the created GeofenceRequest to the device's monitoring list
    private void addGeofence(GeofencingRequest request) {
        Log.d("hhh", "addGeofence");

        //   checkPermission();
        // Log.v("checkPermissionValue", String.valueOf(checkPermission()));
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("hhh", "addGeofencePerm");
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    request,
                    createGeofencePendingIntent()
            ).setResultCallback(this);
        }
    }

    // Check for permission to access Location
    private void checkPermission() {
        Log.d("zczc", "checkPermission()");
        // Ask for permission if it wasn't granted yet
        // return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        //       == PackageManager.PERMISSION_GRANTED );

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Log.d("zczcIf", "checkPermission()");

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                Log.d("zczcElse", "checkPermission()");

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    Log.v("Inside Permission G","dsfsf");
                    startGeofence();


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }




    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.v("click", String.valueOf(marker.getPosition()));

        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map  = googleMap;
        Log.v("OnMaphh","sdsdf");
        LatLng sydney = new LatLng(-34, 151);
        //   map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        // map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.v("OnConnected","dfdsf");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v("OnConnectionSuspended","dfdsf");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("OnConnectionFailed","dfdsf");
    }

    @Override
    public void onResult(@NonNull Status status) {
        Log.i("dsdf", "onResult: " + status);
        if ( status.isSuccess() ) {
            drawGeofence();
        } else {
            // inform about fail
        }
    }

    // Draw Geofence circle on GoogleMap
    private Circle geoFenceLimits;
    private void drawGeofence() {
        Log.d("df", "drawGeofence()");


        if ( geoFenceLimits != null ){
            Log.v("drawG",geoFenceLimits.toString());
            geoFenceLimits.remove();
        }


        CircleOptions circleOptions = new CircleOptions()
                .center( geoFenceMarker.getPosition())
                .strokeColor(Color.BLACK)
                .fillColor( Color.TRANSPARENT )
                .radius( GEOFENCE_RADIUS )
                .strokeWidth( 10 );;
        geoFenceLimits = map.addCircle( circleOptions );
    }


    // Start Geofence creation process
    private void startGeofence() {
        Log.i("add", "startGeofence()");
        if( geoFenceMarker != null ) {
            Geofence geofence = createGeofence( geoFenceMarker.getPosition(), GEOFENCE_RADIUS );
            GeofencingRequest geofenceRequest = createGeofenceRequest( geofence );
            addGeofence( geofenceRequest );
            drawGeofence();
        } else {
            Log.e("dfd", "Geofence marker is null");
        }
    }
}
