package com.haryadi.trigger.utils;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.telephony.SmsManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.gms.location.GeofenceStatusCodes;
import com.haryadi.trigger.R;
import com.haryadi.trigger.data.TriggerContract;
import com.haryadi.trigger.service.GeofenceTrasitionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ChangeSettings {

    //Projections for database query
    public static final String[] TRIGGER_COLUMNS = {
            TriggerContract.TriggerEntry._ID,
            TriggerContract.TriggerEntry.COLUMN_TRIGGER_NAME,
            TriggerContract.TriggerEntry.COLUMN_NAME,
            TriggerContract.TriggerEntry.COLUMN_TRIGGER_POINT,
            TriggerContract.TriggerEntry.COLUMN_CONNECT,
            TriggerContract.TriggerEntry.COLUMN_ISWIFION,
            TriggerContract.TriggerEntry.COLUMN_ISBLUETOOTHON,
            TriggerContract.TriggerEntry.COLUMN_MEDIAVOL,
            TriggerContract.TriggerEntry.COLUMN_RINGVOL,
            TriggerContract.TriggerEntry.COLUMN_NOTIFVOL,
            TriggerContract.TriggerEntry.COLUMN_PH_NUMBER,
            TriggerContract.TriggerEntry.COLUMN_MSG_TEXT
    };

    public static final int INDEX_TRIGGER_ID = 0;
    public static final int INDEX_TRIGGER_NAME = 1;
    public static final int INDEX_NAME = 2;
    public static final int INDEX_TRIGGER_POINT = 3;
    public static final int INDEX_CONNECT = 4;
    public static final int INDEX_ISWIFION = 5;
    public static final int INDEX_ISBLUETOOTHON = 6;
    public static final int INDEX_MEDIAVOL = 7;
    public static final int INDEX_RINGVOL = 8;
    public static final int INDEX_NOTIFVOL = 9;
    public static final int INDEX_PHNUMBER = 10;
    public static final int INDEX_MSGTEXT = 11;

    public static final String ACTION_DATA_UPDATED = "com.haryadi.trigger.ACTION_DATA_UPDATED";

    //Method to change bluetooth settings
    public static void changeBluetoothSetting(Context context, Boolean enable) {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (enable) {
            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
            }
        } else {
            if (bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.disable();
            }
        }
    }

    //Method to change wifi settings
    public static void changeWifiSettings(Context context, boolean enable) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String name = wifiInfo.getSSID();
        wifiManager.setWifiEnabled(enable);
    }

    //Method to change sound setting
    public static void changeSoundSettings(Context context, boolean enable) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (enable) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
        } else {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 1);
        }
    }

    //Method to set media volume
    public static void changeMediaVolume(Context context, int progress) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
    }

    //Method to set Ring volume
    public static void changeRingVolume(Context context, int progress) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, progress, 0);
    }

    //Method to set Notification volume
    public static void changeNotificationVolume(Context context, int progress) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, progress, 0);
    }

    //Method to change brightness
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void changeBrightness(Context context, int progress) {
        Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,progress);
    }


    //Method to get saved Wifi network name
    public static ArrayList<String> getConfiguredWifi(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        ArrayList<String> wifiNames = new ArrayList<>();
        List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
        if (configurations != null) {
            for (WifiConfiguration configuration : configurations) {
                wifiNames.add(configuration.SSID);
            }
        }
        return wifiNames;
    }

    //Method to get saved Bluetooth names
    public static ArrayList<String> getConfiguredBluetooth(Context context) {
        ArrayList<String> blueToothNames = new ArrayList<>();
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice bluetoothDevice : pairedDevices) {
                blueToothNames.add(bluetoothDevice.getName());
            }
        }
        return blueToothNames;
    }

    //Methos to add last trigger profile to shared preferences
    public static void addWifiNameToSharedPreference(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String name = wifiInfo.getSSID();
        if (!name.equals("<unknown ssid>")) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(GeofenceTrasitionService.SHARED_LAST_WIFI, name);
            edit.commit();
        }
    }

    public static void addBluetoothName(Context context) {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        // bluetoothAdapter.get
    }

    //Get the error string for Geofence
    public static String getErrorString(Context mContext, int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return mContext.getString(R.string.geofence_not_available);
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return mContext.getString(R.string.geofence_many_geofences);
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return mContext.getString(R.string.geofence_many_intents);
            default:
                return mContext.getString(R.string.geofence_unknown_errors);
        }
    }

    public static void writeToSharedPref(Context context, long id) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putLong(GeofenceTrasitionService.SHARED_LAST_TRIGGER, id);
        edit.commit();
    }

    //Method to notify widgets
    public static void notifyWidgets(Context mContext) {
        Intent intent = new Intent(ACTION_DATA_UPDATED);
        mContext.sendBroadcast(intent);
    }

    public static void sendMessage(String number, String message){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number,null,message,null,null);
    }

    public static void slide_down(Context ctx, View v){

        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);
        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    public static void slide_up(Context ctx, View v){

        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_up);
        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    public static String getDisplayNameByNumber(Context context, String number) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        Cursor contactLookup = context.getContentResolver().query(uri, new String[] {ContactsContract.PhoneLookup._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

        int indexName = contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME);

        try {
            if (contactLookup != null && contactLookup.moveToNext()) {
                number = contactLookup.getString(indexName);
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return number;
    }


}
