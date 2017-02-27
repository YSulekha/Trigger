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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.haryadi.trigger.R;
import com.haryadi.trigger.data.TriggerContract;
import com.haryadi.trigger.utils.ChangeSettings;
import com.haryadi.trigger.utils.Constants;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by aharyadi on 2/8/17.
 */

public class EditCreateProfileFrag_bac extends DialogFragment {

    @BindView(R.id.media_seekbar)
    SeekBar mediaVolumeBar;
    @BindView(R.id.ring_seekbar) SeekBar ringVolumeBar;
    @BindView(R.id.content_isbluetoothon)
    Spinner mIsBluetoothOn;
    @BindView(R.id.content_iswifion) Spinner mIsWifiOn;
    @BindView(R.id.button_save)
    Button saveButton;
    @BindView(R.id.spinner_wifi_name)  Spinner mWifiName;
    @BindView(R.id.dialog_title)
    TextView textTitle;
    @BindView(R.id.isConnect)RadioGroup radioGroup;
    @BindView(R.id.dialog_radio_connect)RadioButton radioConnect;
    @BindView(R.id.dialog_radio_disconnect)RadioButton radioDisConnect;
    String triggerPoint;
    ArrayList<String> names;
    ArrayAdapter<CharSequence> optionsAdapter;
    ArrayAdapter<String> arrayAdapter;
    String isConnect = "Connect";
    boolean isEdit = false;
    Uri mUri;


    public EditCreateProfileFrag_bac(){

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
        ButterKnife.bind(this,view);
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
       /* if(triggerPoint.equals("WIFI DISABLE")||triggerPoint.equals("BLUETOOTH DISABLE")){
            isConnect = "Disconnect";
        }*/
        if(triggerPoint.equals("LOCATION")){
            names = getLocationNames();
        }
        //   names.add("Shiv1a");
        // names.add("Soij");
        configureViews();
        if(isEdit){
            mUri = Uri.parse(getArguments().getString("Uri"));
            configureValues(mUri);
        }

        radioGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });
    }
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.dialog_radio_connect:
                if (checked) {
                    isConnect = "Connect";
                }
                break;
            case R.id.dialog_radio_disconnect:
                if (checked) {
                    isConnect = "Disconnect";
                }
                break;
            default:
                isConnect = "Connect";
        }
    }
    public ArrayList<String> getLocationNames(){
        ArrayList<String> res = new ArrayList<>();
        for(Map.Entry<String,LatLng> entry: Constants.BAY_AREA_LANDMARKS.entrySet()){
            res.add(entry.getKey());
        }
        return res;
    }
    @OnClick(R.id.button_save)
    public void onClickSave(View view) {
        Log.v("Inside Save","dsdd");
        if(!checkForDuplicates()) {
            insertRecord();
            Toast.makeText(getActivity(), "Record Saved", Toast.LENGTH_LONG).show();
            this.dismiss();
        }
    }

    private void configureViews() {
        arrayAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,names);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mWifiName.setAdapter(arrayAdapter);
        setVolumeSeekBar();
        optionsAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.options,android.R.layout.simple_spinner_item);
        optionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mIsBluetoothOn.setAdapter(optionsAdapter);
        mIsWifiOn.setAdapter(optionsAdapter);
        if(triggerPoint.equals("WIFI")||triggerPoint.equals("WIFI DISABLE")){
            mIsWifiOn.setSelection(0);
            mIsWifiOn.setEnabled(false);
        }
        if(triggerPoint.equals("BLUETOOTH")||triggerPoint.equals("BLUETOOTH DISABLE")){
            mIsBluetoothOn.setSelection(0);
            mIsBluetoothOn.setEnabled(false);
        }

    }

    private void configureValues(Uri uri){
        //long id = TriggerContract.TriggerEntry.getIdFromUri(uri);
        Cursor cursor = getActivity().getContentResolver().query(uri,null,null,null,null);
        int pos = 0;
        if(cursor.moveToNext()){
            //int pos = arrayAdapter.getPosition(cursor.getString(cursor.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_NAME)));
            String triggerPoint = cursor.getString(cursor.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_TRIGGER_POINT));
            Log.v("Trigger Point",triggerPoint);
            if(triggerPoint.equals("WIFI")|triggerPoint.equals("WIFI DISABLE")){
                Log.v("fdfg", String.valueOf(names.size()));
                mIsWifiOn.setSelection(0);
                mIsWifiOn.setEnabled(false);
                names = ChangeSettings.getConfiguredWifi(getActivity());
                arrayAdapter.notifyDataSetChanged();
                Log.v("dsdf", String.valueOf(names.size()));
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
            else if(triggerPoint.equals("LOCATION")){
                names = getLocationNames();
                arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,names);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mWifiName.setAdapter(arrayAdapter);
                pos = arrayAdapter.getPosition(cursor.getString(cursor.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_NAME)));
            }
            if(cursor.getString(cursor.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_CONNECT)).equals("Connect")){
                radioConnect.setChecked(true);
                radioGroup.setEnabled(false);

            }
            else{
                radioDisConnect.setChecked(true);
                radioGroup.setEnabled(false);
            }
            radioConnect.setEnabled(false);
            radioDisConnect.setEnabled(false);
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
            Log.v("Trigger",triggerPoint + "_" + name+isConnect);
            values.put(TriggerContract.TriggerEntry.COLUMN_CONNECT,isConnect);
            getActivity().getContentResolver().insert(uri, values);
        }
    }


    private boolean checkForDuplicates(){
        Log.v("Duplicates","sff");
        if(!isEdit) {
            Log.v("Duplicates","sff1");
            String name = mWifiName.getSelectedItem().toString();
            Uri uri = TriggerContract.TriggerEntry.CONTENT_URI;
            String where = TriggerContract.TriggerEntry.TABLE_NAME + "." +
                    TriggerContract.TriggerEntry.COLUMN_TRIGGER_NAME + " = ?" + " AND " +
                    TriggerContract.TriggerEntry.COLUMN_CONNECT + " = ?";
            String[] args = new String[]{triggerPoint + "_" + name, isConnect};
            Log.v("Trigger",triggerPoint + "_" + name);
            Log.v("Where",isConnect);
            Cursor c = getActivity().getContentResolver().query(uri, null, where, args, null);
            Log.v("Duplicates", String.valueOf(c.getCount()));
            if(c.moveToNext()){
                Toast.makeText(getActivity(),"There is already a proile for this trigger",Toast.LENGTH_LONG).show();
                return true;
            }
            else{
                return false;
            }
        }
        return false;
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
        //    mediaVolumeBar.setOnSeekBarChangeListener(createSeekBarChangeListener(AudioManager.STREAM_MUSIC,audioManager));
        //  ringVolumeBar.setOnSeekBarChangeListener(createSeekBarChangeListener(AudioManager.STREAM_RING,audioManager));
    }

    /*  private SeekBar.OnSeekBarChangeListener createSeekBarChangeListener(final int streamType,final AudioManager audioManager){
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
      }*/
}
