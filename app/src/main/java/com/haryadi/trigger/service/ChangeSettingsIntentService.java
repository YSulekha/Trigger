package com.haryadi.trigger.service;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.haryadi.trigger.R;
import com.haryadi.trigger.data.TriggerContract;
import com.haryadi.trigger.utils.ChangeSettings;

//Service to change the settings based on the trigger profile

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
        boolean isConnect = intent.getBooleanExtra(EXTRA_CONNECT, false);
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
            ChangeSettings.writeToSharedPref(this, c.getLong(ChangeSettings.INDEX_TRIGGER_ID));
            ChangeSettings.notifyWidgets(this);
            c.close();
        }
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

