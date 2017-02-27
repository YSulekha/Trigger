package com.haryadi.trigger.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.haryadi.trigger.R;
import com.haryadi.trigger.TriggerActivity;
import com.haryadi.trigger.data.TriggerContract;
import com.haryadi.trigger.utils.ChangeSettings;

import java.util.ArrayList;
import java.util.List;


public class GeofenceTrasitionService extends IntentService {

    private static final String TAG = GeofenceTrasitionService.class.getSimpleName();
    public static final int GEOFENCE_NOTIFICATION_ID = 0;
    public static final String SHARED_LAST_WIFI = "wifi_name";
    public static final String SHARED_LAST_BLUETOOTH = "bluetooth_name";
    public static final String SHARED_LAST_TRIGGER= "last_trigger";

    public GeofenceTrasitionService() {
        super("GeofenceTrasitionService");
    }

    public GeofenceTrasitionService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Retrieve the Geofencing intent
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        // Handling errors
        if ( geofencingEvent.hasError() ) {
            String errorMsg = ChangeSettings.getErrorString(geofencingEvent.getErrorCode() );
            Log.e( TAG, errorMsg );
            return;
        }

        // Retrieve GeofenceTrasition
        int geoFenceTransition = geofencingEvent.getGeofenceTransition();
        // Check if the transition type
        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ) {
            // Get the geofence that were triggered
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            // Create a detail message with Geofences received
            String geofenceTransitionDetails = getGeofenceTrasitionDetails(geoFenceTransition, triggeringGeofences );
            // Send notification details as a String
            sendNotification( geofenceTransitionDetails );
            checkDatabase(triggeringGeofences,geoFenceTransition);
        }
    }

    public void checkDatabase(List<Geofence> trigger,int transition){
        Log.v("CheckDatabase", String.valueOf(trigger.size()));
        for(Geofence geofence : trigger){
            String name = geofence.getRequestId();
            String connect = "Connect";
            Log.v("InsideFor",name);
            if(transition == Geofence.GEOFENCE_TRANSITION_ENTER){
                connect = "Connect";
            }
            else{
                connect="Disconnect";
            }
            Log.v("Inside Service",connect);
            Uri uri = TriggerContract.TriggerEntry.CONTENT_URI;
            String where = TriggerContract.TriggerEntry.TABLE_NAME + "." +
                    TriggerContract.TriggerEntry.COLUMN_NAME + " = ?" + " AND "+
                    TriggerContract.TriggerEntry.COLUMN_CONNECT + " = ?";
          //  String[] args = new String[]{"LOCATION" + "_" + name,connect};
            String[] args = new String[]{name,connect};
            Cursor c = getContentResolver().query(uri,null,where,args,null);
            Log.v("InsideForCount", String.valueOf(c.getCount()));
            if(c.moveToNext()){
                if (c != null) {
                    Log.v("Inside enabled", "jhh");
                    String bluetooth = c.getString(c.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_ISBLUETOOTHON));
                    String wifi = c.getString((c.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_ISWIFION)));
                    if (bluetooth.equals("Turn On")) {
                        ChangeSettings.changeBluetoothSetting(this, true);
                    } else if(bluetooth.equals("Turn Off")){
                        ChangeSettings.changeBluetoothSetting(this, false);
                    }
                    if (wifi.equals("Turn On")) {
                        ChangeSettings.changeWifiSettings(this, true);
                    } else if(wifi.equals("Turn Off")){
                        ChangeSettings.changeWifiSettings(this, false);
                    }

                    Log.v("Volume", String.valueOf(c.getInt(c.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_MEDIAVOL))));
                    ChangeSettings.changeMediaVolume(this, c.getInt(c.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_MEDIAVOL)));
                    ChangeSettings.changeRingVolume(this, c.getInt(c.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_RINGVOL)));
                    ChangeSettings.writeToSharedPref(this,c.getLong(c.getColumnIndex(TriggerContract.TriggerEntry._ID)));
                    TriggerActivity.notifyWidgets(this);

                }
                c.close();
                Log.v("HasData","true");
                Log.v("FasDataC", String.valueOf(c.getCount()));
            }
        }
    }
    // Create a detail message with Geofences received
    private String getGeofenceTrasitionDetails(int geoFenceTransition, List<Geofence> triggeringGeofences) {
        // get the ID of each geofence triggered
        ArrayList<String> triggeringGeofencesList = new ArrayList<>();
        for ( Geofence geofence : triggeringGeofences ) {
            triggeringGeofencesList.add( geofence.getRequestId() );
        }
        String status = null;
        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER )
            status = "Entering ";
        else if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT )
            status = "Exiting ";
        return status + TextUtils.join( ", ", triggeringGeofencesList);
    }

    // Send a notification
    private void sendNotification( String msg ) {
        Log.i(TAG, "sendNotification: " + msg );

        // Intent to start the main Activity
        Intent notificationIntent = new Intent(this,TriggerActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(TriggerActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Creating and sending Notification
        NotificationManager notificatioMng =
                (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        notificatioMng.notify(
                GEOFENCE_NOTIFICATION_ID,
                createNotification(msg, notificationPendingIntent));
    }

    // Create a notification
    private Notification createNotification(String msg, PendingIntent notificationPendingIntent) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder
                .setSmallIcon(R.drawable.ic_location_on_black_24dp)
                .setColor(Color.RED)
                .setContentTitle(msg)
                .setContentText("Geofence Notification!")
                .setContentIntent(notificationPendingIntent)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setAutoCancel(true);
        return notificationBuilder.build();
    }
}
