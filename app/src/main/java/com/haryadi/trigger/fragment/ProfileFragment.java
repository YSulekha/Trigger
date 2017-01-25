package com.haryadi.trigger.fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.haryadi.trigger.R;
import com.haryadi.trigger.data.TriggerContract;

/**
 * Created by aharyadi on 1/23/17.
 */

public class ProfileFragment extends Fragment {


    ToggleButton mIsBluetoothOn;
    ToggleButton mIsWifiOn;
    EditText mSoundSetting;
    EditText mWifiName;
    String bluetoothOn = "false";
    String wifiOn = "false";
    Button saveButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.content_main_bac, container, false);

        mIsBluetoothOn = (ToggleButton)rootView.findViewById(R.id.content_isbluetoothon);
        mIsWifiOn = (ToggleButton) rootView.findViewById(R.id.content_iswifion);
        mSoundSetting = (EditText) rootView.findViewById(R.id.content_soundSetting);
        mWifiName = (EditText) rootView.findViewById(R.id.content_name);
        mIsBluetoothOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bluetoothOn = "true";
                    // The toggle is enabled
                } else {
                    // The toggle is disabled
                    bluetoothOn = "false";
                }
            }
        });
        mIsWifiOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    wifiOn = "true";
                    // The toggle is enabled
                } else {
                    // The toggle is disabled
                    wifiOn = "false";
                }
            }
        });

        saveButton = (Button)rootView.findViewById(R.id.button_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSave(v);
            }
        });
        return rootView;
    }


    public void onClickSave(View view) {
        insertRecord();
        Cursor cursor = getActivity().getContentResolver().query(TriggerContract.TriggerEntry.CONTENT_URI, null, null, null, null);
        Log.v("Count", String.valueOf(cursor.getCount()));
        Toast.makeText(getActivity(), "Record Saved", Toast.LENGTH_LONG).show();
    }

    public void insertRecord() {
        ContentValues values = new ContentValues();
        Uri uri = TriggerContract.TriggerEntry.CONTENT_URI;
        String soundSetting = mSoundSetting.getText().toString();
        String name = "\"" + mWifiName.getText().toString() + "\"";
        values.put(TriggerContract.TriggerEntry.COLUMN_TRIGGER_NAME, "WIFI" + "_" + name);
        Log.v("adadaf", "WIFI" + "_" + name);
        values.put(TriggerContract.TriggerEntry.COLUMN_NAME, name);
        values.put(TriggerContract.TriggerEntry.COLUMN_ISBLUETOOTHON, bluetoothOn);
        values.put(TriggerContract.TriggerEntry.COLUMN_SOUNDSETTING, soundSetting);
        values.put(TriggerContract.TriggerEntry.COLUMN_ISWIFION, "false");
        getActivity().getContentResolver().insert(uri, values);
    }
}
