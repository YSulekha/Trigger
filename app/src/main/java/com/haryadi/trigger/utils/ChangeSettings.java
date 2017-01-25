package com.haryadi.trigger.utils;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.media.AudioManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class ChangeSettings {

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
}
