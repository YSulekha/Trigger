package com.haryadi.trigger.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


//Receiver to get bluetooth connectivity status
public class BluetoothConnectivityReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR);
            if (state == BluetoothAdapter.STATE_OFF) {
                String bluetoothName = prefs.getString(GeofenceTrasitionService.SHARED_LAST_BLUETOOTH, "No Value");
                Intent serviceIntent = new Intent(context, ChangeSettingsIntentService.class);
                serviceIntent.putExtra(ChangeSettingsIntentService.EXTRA_CONNECT, false);
                serviceIntent.putExtra(ChangeSettingsIntentService.EXTRA_NAME, bluetoothName);
                context.startService(serviceIntent);
            }
        }
        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(GeofenceTrasitionService.SHARED_LAST_BLUETOOTH, device.getName());
            edit.commit();
            Intent serviceIntent = new Intent(context, ChangeSettingsIntentService.class);
            serviceIntent.putExtra(ChangeSettingsIntentService.EXTRA_CONNECT, true);
            serviceIntent.putExtra(ChangeSettingsIntentService.EXTRA_NAME, device.getName());
            context.startService(serviceIntent);
        } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            String bluetoothName = prefs.getString(GeofenceTrasitionService.SHARED_LAST_BLUETOOTH, "No Value");
            Intent serviceIntent = new Intent(context, ChangeSettingsIntentService.class);
            serviceIntent.putExtra(ChangeSettingsIntentService.EXTRA_CONNECT, false);
            serviceIntent.putExtra(ChangeSettingsIntentService.EXTRA_NAME, bluetoothName);
            context.startService(serviceIntent);
        }
    }
}
