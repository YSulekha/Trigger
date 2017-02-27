package com.haryadi.trigger.service;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.haryadi.trigger.R;
import com.haryadi.trigger.TriggerActivity;
import com.haryadi.trigger.data.TriggerContract;
import com.haryadi.trigger.utils.ChangeSettings;


public class ChangeSettingsIntentService extends IntentService {

    public static final String EXTRA_CONNECT = "isConnect";
    public static final String EXTRA_NAME = "name";

    public ChangeSettingsIntentService() {
        super(ChangeSettingsIntentService.class.getName());
    }


    public ChangeSettingsIntentService(String name) {
        super(name);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        String name = intent.getStringExtra(EXTRA_NAME);
        boolean isConnect = intent.getBooleanExtra(EXTRA_CONNECT,false);
        Log.v("isConnect", String.valueOf(isConnect));
        Log.v("name",name);
        Cursor c  = getData(name,isConnect);
        if (c != null) {
            Log.v("Inside enabled", "jhh");
            String bluetooth = c.getString(c.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_ISBLUETOOTHON));
            String wifi = c.getString((c.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_ISWIFION)));
            Log.v("WIFI+Bluetooth",wifi+bluetooth);
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
    }

    private Cursor getData(String name,  boolean isConnect){
        //Uri uri = TriggerContract.TriggerEntry.buildUriWithName("WIFI"+"_"+name);
        Uri uri = TriggerContract.TriggerEntry.CONTENT_URI;
        String where = TriggerContract.TriggerEntry.TABLE_NAME + "." +
                TriggerContract.TriggerEntry.COLUMN_NAME + " = ?" + " AND "+
                TriggerContract.TriggerEntry.COLUMN_CONNECT + " = ?";
        String[] args;
        if(isConnect) {
            args = new String[]{name,getString(R.string.text_connect)};
        }
        else {
            args = new String[]{name,getString(R.string.text_disconnect)};
        }
        Cursor cursor = getContentResolver().query(uri,null,where,args,null);
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

