package com.haryadi.trigger.fragment;

import android.Manifest;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.haryadi.trigger.R;
import com.haryadi.trigger.TriggerActivity;
import com.haryadi.trigger.ZoomOutPageTransformer;
import com.haryadi.trigger.service.GeofenceTrasitionService;
import com.haryadi.trigger.utils.ChangeSettings;
import com.haryadi.trigger.utils.Constants;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class MapFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMapLongClickListener,
        ResultCallback<Status>,
        EditCreateLocationFragment.locationListener {

    @BindView(R.id.mapView)
    MapView mMapView;
    private GoogleMap map;
    public static GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    ArrayList<Geofence> mGeofenceList = new ArrayList<>();
    LatLng searchPlace;

    boolean submitPressed = false;

    ArrayList<MarkerOptions> markers = new ArrayList<>();

    public static final int REQUEST_ENABLE_BT = 201;

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public static final String SAVE_MAP_STATE = "mapview";
    public static final String SAVE_MARKER = "marker";

    @BindView(R.id.wifi_enable)
    FloatingActionButton wifiEnable;
    @BindView(R.id.bluetooth_enable)
    FloatingActionButton bluetoothEnable;
    @BindView(R.id.location_enable)
    FloatingActionButton locationEnable;
    @BindView(R.id.floatingActionMenu)
    FloatingActionMenu floatingActionMenu;
    @BindView(R.id.toolbarSwipe)
    ImageButton swipeButton;
    @BindView(R.id.coord)
    CoordinatorLayout layout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = (ViewGroup) inflater.inflate(
                R.layout.fragment_loc, container, false);
        ButterKnife.bind(this, rootview);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).
                addApi(LocationServices.API).
                build();

        EditText search = (EditText) rootview.findViewById(R.id.toolbarText);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickOfSearchIcon();
            }
        });
        floatingActionMenu.setClosedOnTouchOutside(true);
        wifiEnable.setOnClickListener(getOnClick(floatingActionMenu));
        bluetoothEnable.setOnClickListener(getOnClick(floatingActionMenu));
        locationEnable.setOnClickListener(getOnClick(floatingActionMenu));
        swipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ViewPager pager= (ViewPager) TriggerActivity.mInstance.findViewById(R.id.viewPager);
                pager.setPageTransformer(true, new ZoomOutPageTransformer());
                pager.setCurrentItem(0,true);

            }
        });
        return rootview;

    }

    public void checkIfBluetoothEnabled(Context context) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                showEditDialog(getString(R.string.bluetooth));
            }
        }
    }

    public void checkIfWifiEnabled(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            if (!wifiManager.isWifiEnabled()) {
                showAlertDialog();
            } else {
                showEditDialog(getString(R.string.wifi));
            }
        }
    }

    public View.OnClickListener getOnClick(final FloatingActionMenu fm) {
        FloatingActionButton b;
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.wifi_enable) {
                    fm.close(true);
                    checkIfWifiEnabled(getActivity());
                } else if (v.getId() == R.id.bluetooth_enable) {
                    fm.close(true);
                    checkIfBluetoothEnabled(getActivity());

                } else if (v.getId() == R.id.location_enable) {
                    Toast t = Toast.makeText(getActivity(), getString(R.string.location_msg), Toast.LENGTH_SHORT);
                    t.show();
                    fm.close(true);
                }
            }
        };
    }

    private void showEditDialog(String title) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        EditCreateProfileFragment editNameDialogFragment = EditCreateProfileFragment.newInstance(title, false, null);
        editNameDialogFragment.show(fm, title);
    }

    @Override
    public void onStart() {
        mMapView.onStart();
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        Log.v("MapFragment","OnResume");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState==null) {
            mMapView.onCreate(null);
            mMapView.getMapAsync(this);
        }
        else{
            mMapView.onCreate(savedInstanceState.getBundle(SAVE_MAP_STATE));
            mMapView.getMapAsync(this);
        }
        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey(SAVE_MARKER)){
                markers = savedInstanceState.getParcelableArrayList(SAVE_MARKER);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Bundle mapState = new Bundle();
        mMapView.onSaveInstanceState(mapState);
        outState.putParcelable(SAVE_MAP_STATE,mapState);
        outState.putParcelableArrayList(SAVE_MARKER,markers);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
           //Styling the map using json
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getActivity(), R.raw.map_style));

            if (!success) {
                Log.e("Style", getString(R.string.map_styling_err));
            }
        } catch (Resources.NotFoundException e) {
            Log.e("Style", getString(R.string.map_styling_exc), e);
        }
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(true);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }
        map.setPadding(5,480,5,5);
        map.animateCamera(CameraUpdateFactory.zoomTo(1.0f));
        map.setOnMapLongClickListener(this);
        if(markers.size()>0){
            for(int i=0;i<markers.size();i++){
                MarkerOptions latLng = markers.get(i);
                BitmapDescriptor defaultMarker =
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                Marker marker = map.addMarker(latLng);
                drawGeofence(marker);
            }
            map.animateCamera(CameraUpdateFactory.zoomTo(12.0f));
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                map.setMyLocationEnabled(true);
                LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        latLng, 14));
            }

        }
    }

    public void showAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());

        // set title
        alertDialogBuilder.setTitle(getString(R.string.wifi_alert_title));

        // set dialog message
        alertDialogBuilder
                .setMessage(getString(R.string.wifi_alert))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.alert_pos),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
                        wifiManager.setWifiEnabled(true);
                        showEditDialog(getString(R.string.wifi));
                    }
                })
                .setNegativeButton(getString(R.string.alert_neg),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        Toast.makeText(getActivity(),getString(R.string.wifi_error),Toast.LENGTH_LONG).show();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void requestPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            Snackbar.make(layout, getString(R.string.loc_perm_exp),
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestPermissions(
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        }
                    })
                    .show();
        } else {

            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                        if (mLastLocation != null) {
                            LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                            //  map.addMarker(new MarkerOptions().position(latLng).title("CurrentLocation"));
                            //    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
                            //  map.animateCamera(cameraUpdate);
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    latLng, 12));
                        }
                        return;
                    }

                } else {
                    requestPermission();
                }
                break;
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        searchPlace = latLng;
        showLocationFragment();
    }

    private void showLocationFragment() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        EditCreateLocationFragment editNameDialogFragment = EditCreateLocationFragment.newInstance("LOCATION", false, null, this);
        editNameDialogFragment.show(fm, "LOCATION");
    }

    public void populateGeoFenceList() {
        for (Map.Entry<String, LatLng> entry : Constants.BAY_AREA_LANDMARKS.entrySet()) {
            mGeofenceList.add(new Geofence.Builder().
                    setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER).
                    setRequestId(entry.getKey()).
                    setExpirationDuration(Geofence.NEVER_EXPIRE).
                    setCircularRegion(entry.getValue().latitude, entry.getValue().longitude, Constants.GEOFENCE_RADIUS_IN_METERS).
                    build());
        }
    }

    public Geofence createGeofenceObject(String name,String transition){
        Geofence.Builder builder= new Geofence.Builder().
                setRequestId(name).
                setExpirationDuration(Geofence.NEVER_EXPIRE).
                setCircularRegion(searchPlace.latitude, searchPlace.longitude, Constants.GEOFENCE_RADIUS_IN_METERS);
        if (transition.equals("Enter"))
            builder.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER);
        else{
            builder.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT);
        }
        return builder.build();
    }

    public PendingIntent getPendingIntent() {
        Intent intent = new Intent(getActivity(), GeofenceTrasitionService.class);
        return  PendingIntent.getService(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public GeofencingRequest createGeofenceReq(String name,String transition) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        //builder.addGeofences(mGeofenceList);
        builder.addGeofence(createGeofenceObject(name,transition));
        if (transition.equals("Enter"))
            builder.setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER);
        else{
            builder.setInitialTrigger(Geofence.GEOFENCE_TRANSITION_EXIT);
        }
        return builder.build();
    }

    public void addToGeoFence(String name, String transition) {

        try {
            LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, createGeofenceReq(name,transition), getPendingIntent()).setResultCallback(this);
        } catch (SecurityException ex) {
            Log.v("No permission", "dds");
        }
    }

    //Method called when geoFence is added
    public void onResult(Status status) {
        if (status.isSuccess()) {
            Toast.makeText(getActivity(), "Geofence Added", Toast.LENGTH_LONG).show();
        } else {
            String error = ChangeSettings.getErrorString(getActivity(),status.getStatusCode());
            Log.v("Error", error);

        }
    }

    //searching location
    public void onClickOfSearchIcon() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            Log.v("PlayServicesRepiar", e.getMessage());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.v("PlayServicesNAva", e.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                searchPlace = place.getLatLng();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                EditCreateLocationFragment editNameDialogFragment = EditCreateLocationFragment.newInstance("LOCATION", false, null, this);
                editNameDialogFragment.show(fm, "LOCATION");
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                Log.e("SearchAct", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == RESULT_OK){
                showEditDialog(getString(R.string.bluetooth));
            }
            if(resultCode == RESULT_CANCELED){
                Toast.makeText(getActivity(),getString(R.string.bluetooth_error),Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onListen(Bundle args) {
        BitmapDescriptor defaultMarker =
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(searchPlace)
                .title(args.getString("Name"))
                .snippet(args.getString("Value"))
                .icon(defaultMarker);
        Marker marker = map.addMarker(markerOptions);
        markers.add(markerOptions);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(searchPlace.latitude,searchPlace.longitude), 12));
        Constants.BAY_AREA_LANDMARKS.put(args.getString("Name"), searchPlace);
        addToGeoFence(args.getString("Name"),args.getString("Value"));
        drawGeofence(marker);
    }
    // Draw Geofence circle on GoogleMap
    private Circle geoFenceLimits;
    private void drawGeofence(Marker marker) {
        if ( geoFenceLimits != null ){
            geoFenceLimits.remove();
        }

        CircleOptions circleOptions = new CircleOptions()
                .center( marker.getPosition())
                .strokeColor(Color.BLACK)
                .fillColor( Color.TRANSPARENT )
                .radius( Constants.GEOFENCE_RADIUS_IN_METERS )
                .strokeWidth( 10 );
        geoFenceLimits = map.addCircle( circleOptions );
    }
}
