package com.haryadi.trigger.service;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by aharyadi on 1/26/17.
 */

public class BluetoothConnectivityReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)){
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Log.v("Namebluetooth device",device.getName());
        }
        else if(BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)){
            Log.v("Bluetooth","Disconnected");
        }
    }
}
