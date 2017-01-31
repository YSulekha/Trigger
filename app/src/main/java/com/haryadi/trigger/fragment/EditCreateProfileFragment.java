package com.haryadi.trigger.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.haryadi.trigger.R;
import com.haryadi.trigger.data.TriggerContract;
import com.haryadi.trigger.utils.ChangeSettings;

import java.util.ArrayList;


public class EditCreateProfileFragment extends DialogFragment {

    private SeekBar mediaVolumeBar;
    private SeekBar ringVolumeBar;
    private Spinner mIsBluetoothOn;
    private Spinner mIsWifiOn;
    private int mSoundSetting;
    private Button saveButton;
    private Spinner mWifiName;
    String triggerPoint;
    ArrayList<String> names;
    ArrayAdapter<CharSequence> optionsAdapter;
    ArrayAdapter<String> arrayAdapter;
    String isConnect = "Connect";
    boolean isEdit = false;
    Uri mUri;


    public EditCreateProfileFragment(){

    }

    public static EditCreateProfileFragment newInstance(String triggerPoint, boolean isEdit, Uri uri){
        EditCreateProfileFragment frag = new EditCreateProfileFragment();
        Bundle args = new Bundle();
        Log.v("titleAct",triggerPoint);
        args.putString("title",triggerPoint);
        args.putBoolean("isEdit",isEdit);
        Log.v("isEdit", String.valueOf(isEdit));
        if(uri !=null) {
            args.putString("Uri", uri.toString());
        }
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog,container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textTitle = (TextView)view.findViewById(R.id.dialog_title);
        triggerPoint = getArguments().getString("title", "Enter Name");
        isEdit = getArguments().getBoolean("isEdit");
        textTitle.setText(triggerPoint);
        names = new ArrayList<>();
        if(triggerPoint.equals("WIFI")|triggerPoint.equals("WIFI DISABLE")) {
            names = ChangeSettings.getConfiguredWifi(getActivity());
        }
        if(triggerPoint.equals("BLUETOOTH")|triggerPoint.equals("BLUETOOTH DISABLE")){
            names = ChangeSettings.getConfiguredBluetooth(getActivity());
        }
        if(triggerPoint.equals("WIFI DISABLE")||triggerPoint.equals("BLUETOOTH DISABLE")){
            isConnect = "Disconnect";
        }
     //   names.add("Shiv1a");
       // names.add("Soij");
        configureViews(view);
        if(isEdit){
            mUri = Uri.parse(getArguments().getString("Uri"));
            configureValues(mUri);
        }
    }

    private void onClickSave(View view) {
        insertRecord();
        Cursor cursor = getActivity().getContentResolver().query(TriggerContract.TriggerEntry.CONTENT_URI, null, null, null, null);
        Log.v("Count", String.valueOf(cursor.getCount()));
        Toast.makeText(getActivity(), "Record Saved", Toast.LENGTH_LONG).show();
        this.dismiss();
    }

    private void configureViews(View view) {
        mWifiName = (Spinner)view.findViewById(R.id.spinner_wifi_name);
        arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,names);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mWifiName.setAdapter(arrayAdapter);
        mediaVolumeBar = (SeekBar)view.findViewById(R.id.media_seekbar);
        ringVolumeBar = (SeekBar)view.findViewById(R.id.ring_seekbar);
        setVolumeSeekBar();
        Log.v("titleAct",triggerPoint);
        getDialog().setTitle(triggerPoint);
        mIsBluetoothOn = (Spinner)view.findViewById(R.id.content_isbluetoothon);
        optionsAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.options,android.R.layout.simple_spinner_item);
        optionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mIsBluetoothOn.setAdapter(optionsAdapter);
        mIsWifiOn = (Spinner) view.findViewById(R.id.content_iswifion);
        mIsWifiOn.setAdapter(optionsAdapter);
        if(triggerPoint.equals("WIFI")||triggerPoint.equals("WIFI DISABLE")){
            mIsWifiOn.setSelection(0);
            mIsWifiOn.setEnabled(false);
        }
        if(triggerPoint.equals("BLUETOOTH")||triggerPoint.equals("BLUETOOTH DISABLE")){
            mIsBluetoothOn.setSelection(0);
            mIsBluetoothOn.setEnabled(false);
        }
        if(isEdit){
            //mWifiName.setEnabled(false);
        }
       // mIsWifiOn.setEnabled(false);
        saveButton = (Button)view.findViewById(R.id.button_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSave(v);
            }
        });
    }

    private void configureValues(Uri uri){
        Log.v("Uri", String.valueOf(uri));
        long id = TriggerContract.TriggerEntry.getIdFromUri(uri);
         Log.v("Inside query",String.valueOf(id));
        Cursor cursor = getActivity().getContentResolver().query(uri,null,null,null,null);
        Log.v("Count", String.valueOf(cursor.getCount()));
        int pos = 0;
        if(cursor.moveToNext()){
            //int pos = arrayAdapter.getPosition(cursor.getString(cursor.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_NAME)));
            names.add("Edit");
            String triggerPoint = cursor.getString(cursor.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_TRIGGER_POINT));
Log.v("Trigger Point",triggerPoint);
            if(triggerPoint.equals("WIFI")|triggerPoint.equals("WIFI DISABLE")){
                mIsWifiOn.setSelection(0);
                mIsWifiOn.setEnabled(false);
                names = ChangeSettings.getConfiguredWifi(getActivity());
                arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,names);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mWifiName.setAdapter(arrayAdapter);
                pos = arrayAdapter.getPosition(cursor.getString(cursor.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_NAME)));

            }
            else if(triggerPoint.equals("BLUETOOTH")|triggerPoint.equals("BLUETOOTH DISABLE")){
                mIsBluetoothOn.setSelection(0);
                mIsBluetoothOn.setEnabled(false);
                names = ChangeSettings.getConfiguredBluetooth(getActivity());
                arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,names);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mWifiName.setAdapter(arrayAdapter);
                pos = arrayAdapter.getPosition(cursor.getString(cursor.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_NAME)));
            }
            mWifiName.setSelection(pos);
           mWifiName.setEnabled(false);
            mIsBluetoothOn.setSelection(optionsAdapter.getPosition(cursor.getString(cursor.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_ISBLUETOOTHON))));
            mIsWifiOn.setSelection(optionsAdapter.getPosition(cursor.getString(cursor.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_ISWIFION))));
            mediaVolumeBar.setProgress(cursor.getInt(cursor.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_MEDIAVOL)));
            ringVolumeBar.setProgress(cursor.getInt(cursor.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_RINGVOL)));
        }
    }

    private void insertRecord() {
        ContentValues values = new ContentValues();
        Uri uri = TriggerContract.TriggerEntry.CONTENT_URI;
        String name = mWifiName.getSelectedItem().toString();
        Log.v("Adapter", name);
        Log.v("Adapter", String.valueOf(arrayAdapter.getPosition(name)));
        values.put(TriggerContract.TriggerEntry.COLUMN_NAME, name);
        values.put(TriggerContract.TriggerEntry.COLUMN_MEDIAVOL,mediaVolumeBar.getProgress());
        values.put(TriggerContract.TriggerEntry.COLUMN_RINGVOL,ringVolumeBar.getProgress());
        values.put(TriggerContract.TriggerEntry.COLUMN_ISBLUETOOTHON, mIsBluetoothOn.getSelectedItem().toString());
        values.put(TriggerContract.TriggerEntry.COLUMN_ISWIFION, mIsWifiOn.getSelectedItem().toString());
        if(isEdit){
            updateRecord(values);
        }
        else {
            ChangeSettings.addWifiNameToSharedPreference(getActivity());
            values.put(TriggerContract.TriggerEntry.COLUMN_TRIGGER_POINT,triggerPoint);
            values.put(TriggerContract.TriggerEntry.COLUMN_TRIGGER_NAME, triggerPoint + "_" + name);
            values.put(TriggerContract.TriggerEntry.COLUMN_CONNECT,isConnect);
            getActivity().getContentResolver().insert(uri, values);
        }

    }

    //Function for edit Values
    private void editValues(Uri uri){
        Cursor values = getActivity().getContentResolver().query(uri,null,null,null,null);
        if(values.moveToNext()){
            //mWifiName.getPo
        }
    }

    private void updateRecord(ContentValues values) {
        String where = TriggerContract.TriggerEntry.TABLE_NAME + "." +
                TriggerContract.TriggerEntry._ID + " = ?";
        String[] args = new String[]{Long.toString(TriggerContract.TriggerEntry.getIdFromUri(mUri))};
        getActivity().getContentResolver().update(TriggerContract.TriggerEntry.CONTENT_URI, values, where, args);
    }

    private void setVolumeSeekBar(){
        final AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        mediaVolumeBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        ringVolumeBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));
        mediaVolumeBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        ringVolumeBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_RING));
        mediaVolumeBar.setOnSeekBarChangeListener(createSeekBarChangeListener(AudioManager.STREAM_MUSIC,audioManager));
        ringVolumeBar.setOnSeekBarChangeListener(createSeekBarChangeListener(AudioManager.STREAM_RING,audioManager));
    }

    private SeekBar.OnSeekBarChangeListener createSeekBarChangeListener(final int streamType,final AudioManager audioManager){
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(streamType,progress,0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
    }
}
