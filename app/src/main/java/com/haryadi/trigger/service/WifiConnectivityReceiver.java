package com.haryadi.trigger.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.haryadi.trigger.data.TriggerContract;
import com.haryadi.trigger.utils.ChangeSettings;

public class WifiConnectivityReceiver  extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("Inside receiver","hhh");
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.v("Inside enabled",wifiInfo.getSSID());

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

        if(activeNetInfo != null && activeNetInfo.getType()==ConnectivityManager.TYPE_WIFI) {
            Log.v("Inside wifi", "hhh");
            if (wifiManager.isWifiEnabled()) {
                Log.v("Inside enabled", "hhh");
                Log.v("Inside enabled", wifiInfo.getSSID());
                //   if(wifiInfo.getSSID().equals("\"Shiva\"")){
                Cursor c = getData(wifiInfo.getSSID(), context);
                if (c != null) {
                    Log.v("Inside enabled", "jhh");
                    String bluetooth = c.getString(c.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_ISBLUETOOTHON));
                    ChangeSettings.changeBluetoothSetting(context, Boolean.parseBoolean(bluetooth));
                    ChangeSettings.changeSoundSettings(context, false);
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
        }

    }

    public Cursor getData(String name, Context context){
        Uri uri = TriggerContract.TriggerEntry.buildUriWithName("WIFI"+"_"+name);
        Cursor cursor = context.getContentResolver().query(uri,null,null,null,null);
        if(cursor.moveToNext()){
            Log.v("HasData","true");
            Log.v("FasDataC", String.valueOf(cursor.getCount()));
            return cursor;
        }
        else{
            Log.v("HasData","false");
            return null;
        }
    }
}
