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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.haryadi.trigger.R;
import com.haryadi.trigger.data.TriggerContract;
import com.haryadi.trigger.utils.ChangeSettings;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class EditCreateLocationFragment extends DialogFragment {

    @BindView(R.id.media_seekbar)
    SeekBar mediaVolumeBar;
    @BindView(R.id.ring_seekbar)
    SeekBar ringVolumeBar;
    @BindView(R.id.notif_seekbar)
    SeekBar notifVolumeBar;
    @BindView(R.id.content_isbluetoothon)
    Spinner mIsBluetoothOn;
    @BindView(R.id.content_iswifion)
    Spinner mIsWifiOn;
    @BindView(R.id.button_save)
    Button saveButton;
    @BindView(R.id.location_wifi_name)
    EditText mLocationName;
    @BindView(R.id.isConnect)
    RadioGroup radioGroup;
    @BindView(R.id.dialog_radio_connect)
    RadioButton radioConnect;
    @BindView(R.id.dialog_radio_disconnect)
    RadioButton radioDisConnect;
    String triggerPoint;
    ArrayAdapter<CharSequence> optionsAdapter;
    ArrayAdapter<CharSequence> locationsOptionsAdapter;
    String isConnect;
    boolean isEdit = false;
    Uri mUri;
    static locationListener mListener;

    public EditCreateLocationFragment() {

    }

    public interface locationListener {
        void onListen(Bundle args);
    }

    public static EditCreateLocationFragment newInstance(String triggerPoint, boolean isEdit, Uri uri, locationListener list) {
        EditCreateLocationFragment frag = new EditCreateLocationFragment();
        Bundle args = new Bundle();
        mListener = list;
        args.putString("title", triggerPoint);
        args.putBoolean("isEdit", isEdit);
        if (uri != null) {
            args.putString("Uri", uri.toString());
        }
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_location, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        isConnect = getString(R.string.text_connect);
        triggerPoint = getArguments().getString("title", "Enter Name");
        isEdit = getArguments().getBoolean("isEdit");
        configureViews();
        if (isEdit) {
            mUri = Uri.parse(getArguments().getString("Uri"));
            configureValues(mUri);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                onRadioButtonClicked(checkedId);
            }
        });
    }

    public void onRadioButtonClicked(int id) {
        Log.v("OnClick", "onClick");
        switch (id) {
            case R.id.dialog_radio_connect:
                if (radioConnect.isChecked()) {
                    isConnect = getString(R.string.text_connect);
                }
                break;
            case R.id.dialog_radio_disconnect:
                if (radioDisConnect.isChecked()) {
                    isConnect = getString(R.string.text_disconnect);
                }
                break;
            default:
                isConnect = getString(R.string.text_connect);
        }
    }

    private void configureViews() {
        setVolumeSeekBar();
        radioConnect.setChecked(true);
        optionsAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.options, android.R.layout.simple_spinner_item);
        optionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mIsBluetoothOn.setAdapter(optionsAdapter);
        mIsWifiOn.setAdapter(optionsAdapter);
    }

    private void configureValues(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, ChangeSettings.TRIGGER_COLUMNS, null, null, null);
        int pos = 0;
        if (cursor.moveToNext()) {
            if (cursor.getString(ChangeSettings.INDEX_CONNECT).equals(getString(R.string.text_connect))) {
                radioConnect.setChecked(true);
                radioGroup.setEnabled(false);

            } else {
                radioDisConnect.setChecked(true);
                radioGroup.setEnabled(false);
            }
            radioConnect.setEnabled(false);
            radioDisConnect.setEnabled(false);
            mLocationName.setText(cursor.getString(ChangeSettings.INDEX_NAME));
            mLocationName.setEnabled(false);
            mIsBluetoothOn.setSelection(optionsAdapter.getPosition(cursor.getString(ChangeSettings.INDEX_ISBLUETOOTHON)));
            mIsWifiOn.setSelection(optionsAdapter.getPosition(cursor.getString(ChangeSettings.INDEX_ISWIFION)));
            mediaVolumeBar.setProgress(cursor.getInt(ChangeSettings.INDEX_MEDIAVOL));
            ringVolumeBar.setProgress(cursor.getInt(ChangeSettings.INDEX_RINGVOL));
            notifVolumeBar.setProgress(cursor.getInt(ChangeSettings.INDEX_NOTIFVOL));
            cursor.close();
        }
    }

    private void insertRecord() {
        ContentValues values = new ContentValues();
        Uri uri = TriggerContract.TriggerEntry.CONTENT_URI;
        String name = mLocationName.getText().toString();
        values.put(TriggerContract.TriggerEntry.COLUMN_NAME, name);
        values.put(TriggerContract.TriggerEntry.COLUMN_MEDIAVOL, mediaVolumeBar.getProgress());
        values.put(TriggerContract.TriggerEntry.COLUMN_RINGVOL, ringVolumeBar.getProgress());
        values.put(TriggerContract.TriggerEntry.COLUMN_NOTIFVOL,notifVolumeBar.getProgress());
        values.put(TriggerContract.TriggerEntry.COLUMN_ISBLUETOOTHON, mIsBluetoothOn.getSelectedItem().toString());
        values.put(TriggerContract.TriggerEntry.COLUMN_ISWIFION, mIsWifiOn.getSelectedItem().toString());
        if (isEdit) {
            updateRecord(values);
        } else {
            values.put(TriggerContract.TriggerEntry.COLUMN_TRIGGER_POINT, triggerPoint);
            values.put(TriggerContract.TriggerEntry.COLUMN_TRIGGER_NAME, triggerPoint + "_" + name);
            values.put(TriggerContract.TriggerEntry.COLUMN_CONNECT, isConnect);
            getActivity().getContentResolver().insert(uri, values);
        }
    }

    private void updateRecord(ContentValues values) {
        String where = TriggerContract.TriggerEntry.TABLE_NAME + "." +
                TriggerContract.TriggerEntry._ID + " = ?";
        String[] args = new String[]{Long.toString(TriggerContract.TriggerEntry.getIdFromUri(mUri))};
        getActivity().getContentResolver().update(TriggerContract.TriggerEntry.CONTENT_URI, values, where, args);
    }

    private boolean checkForDuplicates() {
        if (!isEdit) {
            String name = mLocationName.getText().toString();
            Uri uri = TriggerContract.TriggerEntry.CONTENT_URI;
            String where = TriggerContract.TriggerEntry.TABLE_NAME + "." +
                    TriggerContract.TriggerEntry.COLUMN_TRIGGER_NAME + " = ?" + " AND " +
                    TriggerContract.TriggerEntry.COLUMN_CONNECT + " = ?";
            String[] args = new String[]{triggerPoint + "_" + name, isConnect};
            Cursor c = getActivity().getContentResolver().query(uri, null, where, args, null);
            if (c.moveToNext()) {
                Toast.makeText(getActivity(), getString(R.string.text_duplicates), Toast.LENGTH_LONG).show();
                c.close();
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private void setVolumeSeekBar() {
        final AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        mediaVolumeBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        ringVolumeBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));
        notifVolumeBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION));
        mediaVolumeBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        ringVolumeBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_RING));
        notifVolumeBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION));
    }

    @OnClick(R.id.button_save)
    public void onClickSave(View view) {
        if (!checkForDuplicates()) {
            if (mLocationName.getText().toString().trim().equals("")) {
                mLocationName.setError(getString(R.string.text_required));
            } else {
                insertRecord();
                Bundle args = new Bundle();
                args.putString("Name", mLocationName.getText().toString());
                String value;
                if (radioGroup.getCheckedRadioButtonId() == R.id.dialog_radio_connect) {
                    value = getString(R.string.text_enter);
                } else {
                    value = getString(R.string.text_exit);
                }
                args.putString("Value", value);
                if (mListener != null) {
                    mListener.onListen(args);
                }
                this.dismiss();
            }
        }
    }
}
