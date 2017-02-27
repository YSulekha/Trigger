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
                    serviceIntent.putExtra(ChangeSettingsIntentService.EXTRA_NAME, wifiInfo.getSSID());
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
                Intent serviceIntent = new Intent(context,ChangeSettingsIntentService.class);
                intent.putExtra(ChangeSettingsIntentService.EXTRA_CONNECT,false);
                serviceIntent.putExtra(ChangeSettingsIntentService.EXTRA_NAME,wifiName);
                context.startService(serviceIntent);
            }
        }

    }

}
