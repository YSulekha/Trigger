package com.haryadi.trigger.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class WifiConnectivityReceiver  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            int intExtra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,0);
            Log.v("WIFI STATE", String.valueOf(intExtra));
            Log.v("WIFI TYPE",netInfo.getTypeName());
            Log.v("WIFI TYPE", String.valueOf(ConnectivityManager.TYPE_WIFI == netInfo.getType()));
            Log.v("WIFI TYPE", String.valueOf(netInfo.isConnected()));
           // if (netInfo.isConnected()) {
            if(ConnectivityManager.TYPE_WIFI == netInfo.getType() && netInfo.isConnected()){

                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo1 = wifiManager.getConnectionInfo();
                Log.v("Supplicant state", String.valueOf(wifiInfo1.getSupplicantState()));
                Log.v("Wifi Name1",wifiInfo1.getSSID());
                Log.v("STATE CHANGE", "connectnetwork");
                WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                Log.v("Wifi Name", wifiInfo.getSSID());
                if(!wifiInfo.getSSID().equals("<unknown ssid>")) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString(GeofenceTrasitionService.SHARED_LAST_WIFI, wifiInfo.getSSID());
                    edit.commit();
                    Log.v("wifi Connect", wifiInfo.getSSID());
                    Intent serviceIntent = new Intent(context, ChangeSettingsIntentService.class);
                    serviceIntent.putExtra(ChangeSettingsIntentService.EXTRA_CONNECT, true);
                    serviceIntent.putExtra(ChangeSettingsIntentService.EXTRA_NAME, "WIFI" + "_" + wifiInfo.getSSID());
                    context.startService(serviceIntent);
                }

                //Start service for connected state here.
            } else {
                NetworkInfo netInfo1 = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                netInfo1.getExtraInfo();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                String wifiName = prefs.getString(GeofenceTrasitionService.SHARED_LAST_WIFI,"No Value");
                Log.v("wifi DisConnect",wifiName);
                WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                Log.v("STATE CHANGE", "disconnectnetwork");
             //   ChangeSettings.changeBluetoothSetting(context, true);
               // ChangeSettings.changeSoundSettings(context, true);
                Intent serviceIntent = new Intent(context,ChangeSettingsIntentService.class);
                intent.putExtra(ChangeSettingsIntentService.EXTRA_CONNECT,false);
                serviceIntent.putExtra(ChangeSettingsIntentService.EXTRA_NAME,"WIFI DISABLE"+"_"+wifiName);
                context.startService(serviceIntent);
//                intent.putExtra(ChangeSettingsIntentService.EXTRA_NAME,wifiInfo.getSSID());
            }
        }

     /*   ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

        Log.v("ActiviNetInfo",activeNetInfo.toString());

        if(activeNetInfo != null && activeNetInfo.getType()==ConnectivityManager.TYPE_WIFI) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiManager.isWifiEnabled()) {
                Cursor c = getData(wifiInfo.getSSID(), context);
                if (c != null) {
                    Log.v("Inside enabled", "jhh");
                    String bluetooth = c.getString(c.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_ISBLUETOOTHON));
                    if(bluetooth.equals("Turn On")){
                        ChangeSettings.changeBluetoothSetting(context, true);
                    }
                    else{
                        ChangeSettings.changeBluetoothSetting(context, false);
                    }
                    Log.v("Volume", String.valueOf(c.getInt(c.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_MEDIAVOL))));
                    ChangeSettings.changeMediaVolume(context,c.getInt(c.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_MEDIAVOL)));
                    ChangeSettings.changeRingVolume(context,c.getInt(c.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_RINGVOL)));
                }
                //  }
            } else {
                Log.v("Inside disabled", "khh");
                ChangeSettings.changeBluetoothSetting(context, true);
                ChangeSettings.changeSoundSettings(context, true);
            }
        }
       else{
            Log.v("Inside enabled", "ghh");
            ChangeSettings.changeBluetoothSetting(context, true);
            ChangeSettings.changeSoundSettings(context, true);
        }*/

    }

}
