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

public class WifiReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

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
        }

    }

    public Cursor getData(String name, Context context){
        Uri uri = TriggerContract.TriggerEntry.buildUriWithName("WIFI"+"_"+name);
        String where = TriggerContract.TriggerEntry.TABLE_NAME + "." +
                TriggerContract.TriggerEntry.COLUMN_CONNECT + " = ?";
        String[] args = new String[]{"Connect"};
        Cursor cursor = context.getContentResolver().query(uri,null,where,args,null);
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
