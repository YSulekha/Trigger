package com.haryadi.trigger.utils;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

//Geofence constants
public final class Constants {
    private Constants() {

    }

    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
   // public static final float GEOFENCE_RADIUS_IN_METERS = 1609; // 1 mile, 1.6 km

    public static final float GEOFENCE_RADIUS_IN_METERS = 300;

    public static final HashMap<String, LatLng> BAY_AREA_LANDMARKS = new HashMap<>();
 /*   static {
        BAY_AREA_LANDMARKS.put("Stanford",new LatLng(37.4288854,-122.1690248));
        BAY_AREA_LANDMARKS.put("1431 Waverly street",new LatLng(37.4291895,-122.1682141));
    }*/
}
