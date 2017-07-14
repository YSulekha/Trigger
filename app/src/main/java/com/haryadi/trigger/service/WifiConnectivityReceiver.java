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


//Receiver to get WifiConnectivity status

public class WifiConnectivityReceiver  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            SharedPreferences prefs1 = PreferenceManager.getDefaultSharedPreferences(context);
           String t =  prefs1.getString("added b","true");
            Log.v("Broadcast Receiver","sff");
            if(ConnectivityManager.TYPE_WIFI == netInfo.getType() && netInfo.isConnected() && !t.equals("true")){
               // if(ConnectivityManager.TYPE_WIFI == netInfo.getType() && netInfo.isConnected()){
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                if(!wifiInfo.getSSID().equals("<unknown ssid>")) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString(GeofenceTrasitionService.SHARED_LAST_WIFI, wifiInfo.getSSID());
                    edit.putString(GeofenceTrasitionService.SHARED_LAST_CONNECT_NAME,wifiInfo.getSSID());
                    long time = System.currentTimeMillis();
                    edit.putLong(GeofenceTrasitionService.SHARED_LAST_CONNECTTIME,System.currentTimeMillis());
                    edit.putString("added b","true");
                    edit.commit();
                    Log.v("wifiName",wifiInfo.getSSID());
                    Log.v("Inside wifiConnect","connect");
                    Log.v("connec", String.valueOf(System.currentTimeMillis()));

                    long connectTime = prefs.getLong(GeofenceTrasitionService.SHARED_LAST_CONNECTTIME,1);
                    long disconnectTime = prefs.getLong(GeofenceTrasitionService.SHARED_LAST_DISCONNECTTIME,0);
                    String connectName = prefs.getString(GeofenceTrasitionService.SHARED_LAST_CONNECT_NAME,"No Value");
                    String disconnectName = prefs.getString(GeofenceTrasitionService.SHARED_LAST_DISCONNECT_NAME,"No Value");
                    Intent serviceIntent = new Intent(context, ChangeSettingsIntentService.class);
                    Log.v("Intentconnec", String.valueOf(connectTime));
                    Log.v("IntentDisconnec", String.valueOf(disconnectTime));
                    serviceIntent.putExtra(ChangeSettingsIntentService.EXTRA_CONNECT, true);
                    serviceIntent.putExtra(ChangeSettingsIntentService.EXTRA_NAME, wifiInfo.getSSID());
                /*    serviceIntent.putExtra(ChangeSettingsIntentService.EXTRA_LAST_WIFI_CONNECT_TIME, connectTime);
                    serviceIntent.putExtra(ChangeSettingsIntentService.EXTRA_LAST_WIFI_DISCONNECT_TIME, disconnectTime);
                    serviceIntent.putExtra(ChangeSettingsIntentService.EXTRA_LAST_WIFI_DISCONNECT,disconnectName);
                    serviceIntent.putExtra(ChangeSettingsIntentService.EXTRA_LAST_WIFI_CONNECT,connectName);*/

                    context.startService(serviceIntent);
                }

                //Start service for connected state here.
                    //else if (ConnectivityManager.TYPE_WIFI == netInfo.getType() && !netInfo.isConnected() && t.equals("true"))
            } else if (ConnectivityManager.TYPE_WIFI == netInfo.getType() && !netInfo.isConnected() && t.equals("true")) {
                NetworkInfo netInfo1 = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                netInfo1.getExtraInfo();
                Log.v("Inside wifiConnect","disconnect");
                Log.v("Disc", String.valueOf(System.currentTimeMillis()));

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                String wifiName = prefs.getString(GeofenceTrasitionService.SHARED_LAST_WIFI,"No Value");
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("added b","false");
                edit.putString(GeofenceTrasitionService.SHARED_LAST_DISCONNECT_NAME,wifiName);
                edit.putLong(GeofenceTrasitionService.SHARED_LAST_DISCONNECTTIME,System.currentTimeMillis());
                edit.commit();
                long connectTime = prefs.getLong(GeofenceTrasitionService.SHARED_LAST_CONNECTTIME,1);
                long disconnectTime = prefs.getLong(GeofenceTrasitionService.SHARED_LAST_DISCONNECTTIME,0);
                String connectName = prefs.getString(GeofenceTrasitionService.SHARED_LAST_CONNECT_NAME,"No Value");
                String disconnectName = prefs.getString(GeofenceTrasitionService.SHARED_LAST_DISCONNECT_NAME,"No Value");
                Log.v("DisIntentconnec", String.valueOf(connectTime));
                Log.v("DisIntentDisconnec", String.valueOf(disconnectTime));
                Intent serviceIntent = new Intent(context,ChangeSettingsIntentService.class);
                intent.putExtra(ChangeSettingsIntentService.EXTRA_CONNECT,false);
                serviceIntent.putExtra(ChangeSettingsIntentService.EXTRA_NAME,wifiName);
               /* serviceIntent.putExtra(ChangeSettingsIntentService.EXTRA_LAST_WIFI_CONNECT_TIME, connectTime);
                serviceIntent.putExtra(ChangeSettingsIntentService.EXTRA_LAST_WIFI_DISCONNECT_TIME, disconnectTime);
                serviceIntent.putExtra(ChangeSettingsIntentService.EXTRA_LAST_WIFI_DISCONNECT,disconnectName);
                serviceIntent.putExtra(ChangeSettingsIntentService.EXTRA_LAST_WIFI_CONNECT,connectName);*/
                context.startService(serviceIntent);
            }
        }

    }

}
