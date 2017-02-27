package com.haryadi.trigger.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by aharyadi on 1/26/17.
 */

public class BluetoothConnectivityReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR);
            if(state == BluetoothAdapter.STATE_OFF){
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                String bluetoothName = prefs.getString(GeofenceTrasitionService.SHARED_LAST_BLUETOOTH,"No Value");
                Intent serviceIntent = new Intent(context,ChangeSettingsIntentService.class);
                serviceIntent.putExtra(ChangeSettingsIntentService.EXTRA_CONNECT,false);
                serviceIntent.putExtra(ChangeSettingsIntentService.EXTRA_NAME,bluetoothName);
                context.startService(serviceIntent);
                Log.v("Bluetooth123","Disconnected");
            }
        }
        if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)){
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(GeofenceTrasitionService.SHARED_LAST_BLUETOOTH, device.getName());
            edit.commit();
            Intent serviceIntent = new Intent(context,ChangeSettingsIntentService.class);
            serviceIntent.putExtra(ChangeSettingsIntentService.EXTRA_CONNECT,true);
            serviceIntent.putExtra(ChangeSettingsIntentService.EXTRA_NAME,device.getName());
            context.startService(serviceIntent);
            Log.v("Namebluetooth device",device.getName());
        }
        else if(BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String bluetoothName = prefs.getString(GeofenceTrasitionService.SHARED_LAST_BLUETOOTH,"No Value");
            Intent serviceIntent = new Intent(context,ChangeSettingsIntentService.class);
            serviceIntent.putExtra(ChangeSettingsIntentService.EXTRA_CONNECT,false);
            serviceIntent.putExtra(ChangeSettingsIntentService.EXTRA_NAME,bluetoothName);
            context.startService(serviceIntent);
            Log.v("Bluetooth","Disconnected");
        }
    }
}
