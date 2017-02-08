package com.haryadi.trigger.fragment;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.haryadi.trigger.R;
import com.haryadi.trigger.service.GeofenceTrasitionService;
import com.haryadi.trigger.utils.ChangeSettings;
import com.haryadi.trigger.utils.Constants;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class MainFragment_bac extends Fragment implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMapLongClickListener,
        ResultCallback<Status>,
        EditCreateLocationFragment.locationListener {

    @BindView(R.id.mapView)
    MapView mMapView;
    private GoogleMap map;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    ArrayList<Geofence> mGeofenceList = new ArrayList<>();
    LatLng searchPlace;

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

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
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).
                addApi(LocationServices.API)
                .build();

        EditText search = (EditText) getActivity().findViewById(R.id.toolbarText);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickOfSearchIcon();
            }
        });
        return rootview;

    }

    @Override
    public void onStart() {
        mMapView.onStart();
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
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
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.animateCamera(CameraUpdateFactory.zoomTo(14.0f));
        map.setOnMapLongClickListener(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.v("Connected", "Inside");
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.v("Connected", "InsideIf");
            //requestPermissions();
            requestPermissions(
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            return;
        } else {
            Log.v("Connected", "InsideElse");
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                Log.v("last Location", "kk");
                map.setMyLocationEnabled(true);
                LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                //    map.addMarker(new MarkerOptions().position(latLng).title("CurrentLocation"));
                //   map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        latLng, 18));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.v("InsideReq",String.valueOf(requestCode));
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                Log.v("dsdd", String.valueOf(grantResults.length));
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.v("Location", "granted");
                    if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                      //  Log.v("Connected_else", mLastLocation.getLatitude() + mLastLocation.getProvider());
                        Log.v("Location", "granted");
                        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                        if (mLastLocation != null) {
                            Log.v("last Location", mLastLocation.toString());
                            LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                            //  map.addMarker(new MarkerOptions().position(latLng).title("CurrentLocation"));
                            //    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
                            //  map.animateCamera(cameraUpdate);
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    latLng, 18));
                        }
                        return;
                    }

                } else {
                    Log.v("InsideElse", "OnResult");
                }
                return;
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
        Toast.makeText(getActivity(), "On Click", Toast.LENGTH_LONG).show();
        searchPlace = latLng;
        //showAlertDialog(latLng);
        showLocationFragment();
    }

    private void showLocationFragment() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        EditCreateLocationFragment editNameDialogFragment = EditCreateLocationFragment.newInstance("LOCATION", false, null, this);
        editNameDialogFragment.show(fm, "LOCATION");
    }

    public void populateGeoFenceList() {
        Log.v("Populate", "Geofence");
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
        Log.v("pendingIntent", "Geofence");
        Intent intent = new Intent(getActivity(), GeofenceTrasitionService.class);
        PendingIntent pendingIntent = PendingIntent.getService(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    public GeofencingRequest createGeofenceReq(String name,String transition) {
        Log.v("GeofenceReq", "Geofence");
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
        Log.v("addToGeofence", "Geofence");
       // populateGeoFenceList();
      //  createGeofenceObject(name);
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
            String error = ChangeSettings.getErrorString(status.getStatusCode());
            Log.v("Error", error);

        }
    }

    //searching location
    public void onClickOfSearchIcon() {
        try {
            Log.v("Inside Search","adad");
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
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
        Log.v("Inside Result",String.valueOf(requestCode));
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                searchPlace = place.getLatLng();
                Log.v("SearchAct", "Place: " + place.getName());
                FragmentManager fm = getActivity().getSupportFragmentManager();

                EditCreateLocationFragment editNameDialogFragment = EditCreateLocationFragment.newInstance("LOCATION", false, null, this);
                editNameDialogFragment.show(fm, "LOCATION");
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                Log.v("SearchAct", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onListen(Bundle args) {
        Log.v("Inside Listen", "ddsf");
        BitmapDescriptor defaultMarker =
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        Marker marker = map.addMarker(new MarkerOptions()
                .position(searchPlace)
                .title(args.getString("Name"))
                .snippet(args.getString("Value"))
                .icon(defaultMarker));
        Constants.BAY_AREA_LANDMARKS.put(args.getString("Name"), searchPlace);
        addToGeoFence(args.getString("Name"),args.getString("Value"));
    }
}
