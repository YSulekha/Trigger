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
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.haryadi.trigger.R;
import com.haryadi.trigger.data.TriggerContract;
import com.haryadi.trigger.utils.ChangeSettings;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by aharyadi on 2/6/17.
 */

public class EditCreateLocationFragment extends DialogFragment {

    @BindView(R.id.media_seekbar)
    SeekBar mediaVolumeBar;
    @BindView(R.id.ring_seekbar) SeekBar ringVolumeBar;
    @BindView(R.id.content_isbluetoothon)
    Spinner mIsBluetoothOn;
    @BindView(R.id.content_iswifion) Spinner mIsWifiOn;
    @BindView(R.id.button_save)
    Button saveButton;
    @BindView(R.id.location_wifi_name)
    EditText mLocationName;
    @BindView(R.id.location_triggerValue)
    Spinner mValue;
    @BindView(R.id.dialog_title)
    TextView textTitle;
    String triggerPoint;
    ArrayAdapter<CharSequence> optionsAdapter;
    ArrayAdapter<CharSequence> locationsOptionsAdapter;
    String isConnect = "Connect";
    boolean isEdit = false;
    Uri mUri;
    static locationListener mListener;

    public EditCreateLocationFragment(){

    }

    public static interface locationListener{
        void onListen(Bundle args);
    }

    public static EditCreateLocationFragment newInstance(String triggerPoint, boolean isEdit, Uri uri,locationListener list){
        EditCreateLocationFragment frag = new EditCreateLocationFragment();
        Bundle args = new Bundle();
        mListener = list;
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
        return inflater.inflate(R.layout.fragment_dialog_location,container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        triggerPoint = getArguments().getString("title", "Enter Name");
        isEdit = getArguments().getBoolean("isEdit");
        textTitle.setText(triggerPoint);
        //   names.add("Shiv1a");
        // names.add("Soij");
        configureViews();
        if(isEdit){
            mUri = Uri.parse(getArguments().getString("Uri"));
            configureValues(mUri);
        }
    }
    private void configureViews() {
        locationsOptionsAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.location_options,android.R.layout.simple_spinner_item);
        locationsOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mValue.setAdapter(locationsOptionsAdapter);
        setVolumeSeekBar();
        optionsAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.options,android.R.layout.simple_spinner_item);
        optionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mIsBluetoothOn.setAdapter(optionsAdapter);
        mIsWifiOn.setAdapter(optionsAdapter);
    }

    private void configureValues(Uri uri){
        //long id = TriggerContract.TriggerEntry.getIdFromUri(uri);
        Cursor cursor = getActivity().getContentResolver().query(uri,null,null,null,null);
        int pos = 0;
        if(cursor.moveToNext()){
            //int pos = arrayAdapter.getPosition(cursor.getString(cursor.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_NAME)));
            String triggerPoint = cursor.getString(cursor.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_TRIGGER_POINT));
            String value = cursor.getString(cursor.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_CONNECT));
            if(value.equals("Connect")){
                mValue.setSelection(0);
            }
            else{
                mValue.setSelection(1);
            }
            mValue.setEnabled(false);
            mLocationName.setText(cursor.getString(cursor.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_NAME)));
            mLocationName.setEnabled(false);
            mIsBluetoothOn.setSelection(optionsAdapter.getPosition(cursor.getString(cursor.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_ISBLUETOOTHON))));
            mIsWifiOn.setSelection(optionsAdapter.getPosition(cursor.getString(cursor.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_ISWIFION))));
            mediaVolumeBar.setProgress(cursor.getInt(cursor.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_MEDIAVOL)));
            ringVolumeBar.setProgress(cursor.getInt(cursor.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_RINGVOL)));
        }
    }

    private void insertRecord() {
        ContentValues values = new ContentValues();
        Uri uri = TriggerContract.TriggerEntry.CONTENT_URI;
        String name = mLocationName.getText().toString();
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
            if(mValue.getSelectedItem().toString().equals("Enter")){
                isConnect = "Connect";
            }
            else{
                isConnect = "Disconnect";
            }
            values.put(TriggerContract.TriggerEntry.COLUMN_TRIGGER_POINT,triggerPoint);
            values.put(TriggerContract.TriggerEntry.COLUMN_TRIGGER_NAME, triggerPoint + "_" + name);
            values.put(TriggerContract.TriggerEntry.COLUMN_CONNECT,isConnect);
            getActivity().getContentResolver().insert(uri, values);
        }

    }

    private void updateRecord(ContentValues values) {
        String where = TriggerContract.TriggerEntry.TABLE_NAME + "." +
                TriggerContract.TriggerEntry._ID + " = ?";
        String[] args = new String[]{Long.toString(TriggerContract.TriggerEntry.getIdFromUri(mUri))};
        getActivity().getContentResolver().update(TriggerContract.TriggerEntry.CONTENT_URI, values, where, args);
    }

    private boolean checkForDuplicates(){
        Log.v("Duplicates","sff");
        if(!isEdit) {
            Log.v("Duplicates","sff1");
            String name = mLocationName.getText().toString();
            if(mValue.getSelectedItem().toString().equals("Enter")){
                isConnect = "Connect";
            }
            else{
                isConnect = "Disconnect";
            }
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

    @OnClick(R.id.button_save)
    public void onClickSave(View view) {
        Log.v("Save","sdds");
        if(!checkForDuplicates()) {
            insertRecord();
            Toast.makeText(getActivity(), "Record Saved", Toast.LENGTH_LONG).show();
            Bundle args = new Bundle();
            args.putString("Name", mLocationName.getText().toString());
            args.putString("Value", mValue.getSelectedItem().toString());
            if (mListener != null) {
                mListener.onListen(args);
            }
            this.dismiss();
        }
    }

}
