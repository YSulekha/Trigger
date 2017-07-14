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
import android.util.Log;

import com.haryadi.trigger.R;
import com.haryadi.trigger.TriggerActivity;
import com.haryadi.trigger.data.TriggerContract;
import com.haryadi.trigger.utils.ChangeSettings;

import static com.google.android.gms.internal.zzs.TAG;
import static com.haryadi.trigger.service.GeofenceTrasitionService.GEOFENCE_NOTIFICATION_ID;

//Service to change the settings based on the trigger profile

public class ChangeSettingsIntentService extends IntentService {

    public static final String EXTRA_CONNECT = "isConnect";
    public static final String EXTRA_NAME = "name";
 /*   public static final String EXTRA_LAST_WIFI_CONNECT = "wificonnect";
    public static final String EXTRA_LAST_WIFI_DISCONNECT = "wifidisconnect";
    public static final String EXTRA_LAST_WIFI_CONNECT_TIME = "wificonnecttime";
    public static final String EXTRA_LAST_WIFI_DISCONNECT_TIME = "wifidisconnecttime";*/

    public ChangeSettingsIntentService() {
        super(ChangeSettingsIntentService.class.getName());
    }

    public ChangeSettingsIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String name = intent.getStringExtra(EXTRA_NAME);
        String connectInfo;
        boolean isConnect = intent.getBooleanExtra(EXTRA_CONNECT, false);
     /*   long connectTime = intent.getLongExtra(EXTRA_LAST_WIFI_CONNECT_TIME,0);
        long disconnectTime = intent.getLongExtra(EXTRA_LAST_WIFI_DISCONNECT_TIME,0);
        String connectName = intent.getStringExtra(EXTRA_LAST_WIFI_CONNECT);
        String disconnectName = intent.getStringExtra(EXTRA_LAST_WIFI_DISCONNECT);*/
        Cursor c = getData(name, isConnect);
        if (c != null) {
            String bluetooth = c.getString(ChangeSettings.INDEX_ISBLUETOOTHON);
            String wifi = c.getString((ChangeSettings.INDEX_ISWIFION));
            if (bluetooth.equals(getString(R.string.turn_on))) {
                ChangeSettings.changeBluetoothSetting(this, true);
            } else if (bluetooth.equals(getString(R.string.turn_off))) {
                ChangeSettings.changeBluetoothSetting(this, false);
            }
            if (wifi.equals(getString(R.string.turn_on))) {
                ChangeSettings.changeWifiSettings(this, true);
            } else if (wifi.equals(getString(R.string.turn_off))) {
                ChangeSettings.changeWifiSettings(this, false);
            }
            ChangeSettings.changeMediaVolume(this, c.getInt(ChangeSettings.INDEX_MEDIAVOL));
            ChangeSettings.changeRingVolume(this, c.getInt(ChangeSettings.INDEX_RINGVOL));
            ChangeSettings.changeNotificationVolume(this, c.getInt(ChangeSettings.INDEX_NOTIFVOL));
      /*      String ph_number = c.getString(ChangeSettings.INDEX_PHNUMBER);
            String message = c.getString(ChangeSettings.INDEX_MSGTEXT);
            SharedPreferences prefs1 = PreferenceManager.getDefaultSharedPreferences(this);
           // long lastmsgTime = prefs1.getLong(GeofenceTrasitionService.SHARED_LAST_MSG_TIME,-1);
           // if(System.currentTimeMillis() - lastmsgTime >= 1200*1000) {
            Log.v("ConnectTime", String.valueOf(connectTime));
            Log.v("ConnectName",connectName);
            Log.v("DisconnectName",disconnectName);
            Log.v("DisconnectTime",String.valueOf(disconnectTime));
            Log.v("difference",String.valueOf( Math.abs(connectTime-disconnectTime)));
            if(connectName.equals(disconnectName) && Math.abs(connectTime-disconnectTime) > 15*60*1000) {
                if (ph_number != null) {
                    //   ChangeSettings.sendMessage(c.getString(ChangeSettings.INDEX_PHNUMBER), message);
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putLong(GeofenceTrasitionService.SHARED_LAST_MSG_TIME, System.currentTimeMillis());
                    edit.commit();
                    sendNotification("sentMsg" + name);
                }
            }*/
            ChangeSettings.writeToSharedPref(this, c.getLong(ChangeSettings.INDEX_TRIGGER_ID));
            ChangeSettings.notifyWidgets(this);
            if (isConnect) {
                connectInfo = getString(R.string.text_connect);
            } else {
            connectInfo = getString(R.string.text_disconnect);
            }
            sendNotification(name+" "+connectInfo);
            c.close();
        }
    }

    //Remove after testing
    // Send a notification
    private void sendNotification(String msg) {
        Log.i(TAG, "sendNotification: " + msg);
        // Intent to start the main Activity
        Intent notificationIntent = new Intent(this, TriggerActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(TriggerActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, 0);

        // Creating and sending Notification
        NotificationManager notificatioMng =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificatioMng.notify(
                GEOFENCE_NOTIFICATION_ID,
                createNotification(msg, notificationPendingIntent));
    }

    private Notification createNotification(String msg, PendingIntent notificationPendingIntent) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder
                .setSmallIcon(R.drawable.ic_location_on_black_24dp)
                .setColor(Color.RED)
                .setContentTitle(msg)
                .setContentText(getString(R.string.notification_text))
                .setContentIntent(notificationPendingIntent)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setAutoCancel(true);
        return notificationBuilder.build();
    }



    private Cursor getData(String name, boolean isConnect) {
        Uri uri = TriggerContract.TriggerEntry.CONTENT_URI;
        String where = TriggerContract.TriggerEntry.TABLE_NAME + "." +
                TriggerContract.TriggerEntry.COLUMN_NAME + " = ?" + " AND " +
                TriggerContract.TriggerEntry.COLUMN_CONNECT + " = ?";
        String[] args;
        if (isConnect) {
            args = new String[]{name, getString(R.string.text_connect)};
        } else {
            args = new String[]{name, getString(R.string.text_disconnect)};
        }
        Cursor cursor = getContentResolver().query(uri, ChangeSettings.TRIGGER_COLUMNS, where, args, null);
        if (cursor.moveToNext()) {
            return cursor;
        } else {
            return null;
        }
    }
}

