package com.haryadi.trigger.utils;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.location.GeofenceStatusCodes;
import com.haryadi.trigger.data.TriggerContract;
import com.haryadi.trigger.service.GeofenceTrasitionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ChangeSettings {

    public  static final String[] TRIGGER_COLUMNS = {
            TriggerContract.TriggerEntry._ID,
            TriggerContract.TriggerEntry.COLUMN_TRIGGER_NAME,
            TriggerContract.TriggerEntry.COLUMN_NAME,
            TriggerContract.TriggerEntry.COLUMN_TRIGGER_POINT,
            TriggerContract.TriggerEntry.COLUMN_CONNECT,
            TriggerContract.TriggerEntry.COLUMN_ISWIFION,
            TriggerContract.TriggerEntry.COLUMN_ISBLUETOOTHON,
            TriggerContract.TriggerEntry.COLUMN_MEDIAVOL,
            TriggerContract.TriggerEntry.COLUMN_RINGVOL
    };

    // these indices must match the projection
    public static final int INDEX_TRIGGER_ID = 0;
    public static final int INDEX_TRIGGER_NAME = 1;
    public static final int INDEX_NAME = 2;
    public static final int INDEX_TRIGGER_POINT = 3;
    public static final int INDEX_CONNECT = 4;
    public static final int INDEX_ISWIFION = 5;
    public static final int INDEX_ISBLUETOOTHON = 6;
    public static final int INDEX_MEDIAVOL = 7;
    public static final int INDEX_RINGVOL = 8;

    public static void changeBluetoothSetting(Context context, Boolean enable){
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if(enable){
            if(!bluetoothAdapter.isEnabled()){
                bluetoothAdapter.enable();
            }
        }
        else{
            if(bluetoothAdapter.isEnabled()){
                bluetoothAdapter.disable();
            }
        }
    }

    public static void changeWifiSettings(Context context,boolean enable){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.v("IsWifiEnabled", String.valueOf(wifiManager.isWifiEnabled()));
        String name = wifiInfo.getSSID();
        Log.v("wifi name",name);
        wifiManager.setWifiEnabled(enable);
    }

    public static void changeSoundSettings(Context context,boolean enable){
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if(enable) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
          //  audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 1);
        }
        else{
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 1);
        }
    }

    public static void changeMediaVolume(Context context,int progress){
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
    }
    public static void changeRingVolume(Context context,int progress){
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        Log.v("setVolume", String.valueOf(audioManager.getStreamVolume(AudioManager.STREAM_RING)));
       // audioManager.setStreamVolume(AudioManager.STREAM_RING,progress,0);
    }
    public static ArrayList<String> getConfiguredWifi(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        ArrayList<String> wifiNames = new ArrayList<>();
        List<WifiConfiguration> configurations =  wifiManager.getConfiguredNetworks();
        if(configurations != null) {
            for (WifiConfiguration configuration : configurations) {
                wifiNames.add(configuration.SSID);
            }
        }
        return wifiNames;
    }

    public static ArrayList<String> getConfiguredBluetooth(Context context){
        ArrayList<String> blueToothNames = new ArrayList<>();
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        Set<BluetoothDevice> pairedDevices =  bluetoothAdapter.getBondedDevices();
        if(pairedDevices.size()>0){
            for(BluetoothDevice bluetoothDevice:pairedDevices){
                blueToothNames.add(bluetoothDevice.getName());
            }
        }
        return blueToothNames;
    }

    public static void addWifiNameToSharedPreference(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String name = wifiInfo.getSSID();
        if(!name.equals("<unknown ssid>")){
            Log.v("ChangeSe",name);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(GeofenceTrasitionService.SHARED_LAST_WIFI, name);
            edit.commit();
        }
    }

    public static void addBluetoothName(Context context){
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
       // bluetoothAdapter.get
    }

    public static String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GeoFence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many GeoFences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";
            default:
                return "Unknown error.";
        }
    }

    public static void writeToSharedPref(Context context,long id){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putLong(GeofenceTrasitionService.SHARED_LAST_TRIGGER, id);
        edit.commit();
    }
}
