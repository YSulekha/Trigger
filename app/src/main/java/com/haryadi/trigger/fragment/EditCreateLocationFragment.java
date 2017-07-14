package com.haryadi.trigger.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.haryadi.trigger.R;
import com.haryadi.trigger.data.TriggerContract;
import com.haryadi.trigger.utils.ChangeSettings;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.widget.Toast.makeText;


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

    @BindView(R.id.expand)
    ImageButton buttonExpand;
    @BindView(R.id.content_main_d)
    RelativeLayout relate;
    @BindView(R.id.contact_num)
    EditText con;

    @BindView(R.id.message)
    ImageButton messageButton;
    @BindView(R.id.message_text)
    TextView messageText;
    @BindView(R.id.contact_close)
    ImageButton contactClose;


    String contactName;

    public static final int PICK_CONTACT = 100;


    String triggerPoint;
    ArrayAdapter<CharSequence> optionsAdapter;
    ArrayAdapter<CharSequence> locationsOptionsAdapter;
    String isConnect;
    boolean isEdit = false;
    Uri mUri;
    static locationListener mListener;

    String id;
    String ph_number;
    LatLng searchPlaceLat;

    public static final int REQUEST_SMS = 100;
    public static final int SEND_SMS = 101;


    public EditCreateLocationFragment() {

    }

    public interface locationListener {
        void onListen(Bundle args);
    }

    public static EditCreateLocationFragment newInstance(String triggerPoint, boolean isEdit, Uri uri, LatLng searchPlace, locationListener list) {
        EditCreateLocationFragment frag = new EditCreateLocationFragment();
        Bundle args = new Bundle();
        mListener = list;
        args.putString("title", triggerPoint);
        args.putBoolean("isEdit", isEdit);
        if (searchPlace != null) {
            args.putParcelable("searchPlace", searchPlace);
        }
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
        searchPlaceLat = getArguments().getParcelable("searchPlace");
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

        //Screen Rotation
        if (savedInstanceState != null) {

            if (con.getText().toString().length() > 0) {
                messageText.setVisibility(View.VISIBLE);
                messageButton.setVisibility(View.VISIBLE);
            }
        }
        //Expand exp
        buttonExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (relate.isShown()) {
                    ChangeSettings.slide_up(getActivity(), relate);
                    relate.setVisibility(View.GONE);
                } else {
                    ChangeSettings.slide_down(getActivity(), relate);
                    relate.setVisibility(View.VISIBLE);
                }
            }
        });
        //msg exp
        con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });

        //Close Button
        contactClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                con.setText("");
                ph_number = null;
                messageText.setText("");
                messageButton.setVisibility(View.GONE);
                messageText.setVisibility(View.GONE);
            }
        });

        ringVolumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(300);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = getContext().getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        contactName = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                        if (Integer.parseInt(c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                            checkpermission();
                        } else {
                            makeText(getActivity(), getString(R.string.no_contact_num), Toast.LENGTH_LONG).show();
                        }

                        // TODO Fetch other Contact details as you want to use

                    }
                }

                break;
        }
    }

    public void checkpermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_CONTACTS)) {
            showMessageOKCancel(getString(R.string.message_perm),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS},
                                    REQUEST_SMS);
                        }
                    });
            return;
        } else {
            requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_SMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    int hasSMSPermission = ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_CONTACTS);
                    if (hasSMSPermission == PackageManager.PERMISSION_GRANTED) {
                        // Intent intent= new Intent(Intent.ACTION_PICK,  ContactsContract.Contacts.CONTENT_URI);
                        // startActivityForResult(intent, PICK_CONTACT);
                        requestPermissions(new String[]{android.Manifest.permission.SEND_SMS},
                                SEND_SMS);
                        /*    Log.v("ooo1","onRequestPermissionsResult");
                            Cursor pCur = getContext().getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                                    new String[]{id}, null);
                            while (pCur.moveToNext()) {
                                Log.v("Has NUmber",pCur.toString());
                                ph_number = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                con.setText(ph_number);
                            }
                            pCur.close();*/
                    }
                    return;

                } else {
                    makeText(getActivity(),"Permissin Required",Toast.LENGTH_LONG).show();
                }
                break;
            }
            case SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    int hasSMSPermission = ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.SEND_SMS);
                    if (hasSMSPermission == PackageManager.PERMISSION_GRANTED) {
                        // Intent intent= new Intent(Intent.ACTION_PICK,  ContactsContract.Contacts.CONTENT_URI);
                        // startActivityForResult(intent, PICK_CONTACT);
                        Cursor pCur = getContext().getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        if (pCur.getCount() == 0) {
                            makeText(getActivity(), "No number", Toast.LENGTH_LONG).show();
                        } else {
                            while (pCur.moveToNext()) {
                                ph_number = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                con.setText(contactName);
                                messageButton.setVisibility(View.VISIBLE);
                                messageText.setVisibility(View.VISIBLE);
                            }
                        }
                        pCur.close();
                    }
                    return;

                } else {
                   // checkpermission();
                    makeText(getActivity(),"Permissin Required",Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    public void onRadioButtonClicked(int id) {
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
            messageText.setText(cursor.getString(ChangeSettings.INDEX_MSGTEXT));
            ph_number = cursor.getString(ChangeSettings.INDEX_PHNUMBER);
            if (ph_number != null) {
                con.setText(ChangeSettings.getDisplayNameByNumber(getActivity(), ph_number));
                messageButton.setVisibility(View.VISIBLE);
                messageText.setVisibility(View.VISIBLE);
            }
            cursor.close();
        }
    }

    private long insertRecord() {
        ContentValues values = new ContentValues();
        Uri uri = TriggerContract.TriggerEntry.CONTENT_URI;
        String name = mLocationName.getText().toString();
        values.put(TriggerContract.TriggerEntry.COLUMN_NAME, name);
        values.put(TriggerContract.TriggerEntry.COLUMN_MEDIAVOL, mediaVolumeBar.getProgress());
        values.put(TriggerContract.TriggerEntry.COLUMN_RINGVOL, ringVolumeBar.getProgress());
        values.put(TriggerContract.TriggerEntry.COLUMN_NOTIFVOL, notifVolumeBar.getProgress());
        values.put(TriggerContract.TriggerEntry.COLUMN_ISBLUETOOTHON, mIsBluetoothOn.getSelectedItem().toString());
        values.put(TriggerContract.TriggerEntry.COLUMN_ISWIFION, mIsWifiOn.getSelectedItem().toString());
        values.put(TriggerContract.TriggerEntry.COLUMN_PH_NUMBER, ph_number);
        values.put(TriggerContract.TriggerEntry.COLUMN_MSG_TEXT, messageText.getText().toString());
        if (isEdit) {
            updateRecord(values);
            return TriggerContract.TriggerEntry.getIdFromUri(mUri);
        } else {
            values.put(TriggerContract.TriggerEntry.COLUMN_TRIGGER_POINT, triggerPoint);
            values.put(TriggerContract.TriggerEntry.COLUMN_TRIGGER_NAME, triggerPoint + "_" + name);
            values.put(TriggerContract.TriggerEntry.COLUMN_CONNECT, isConnect);
            values.put(TriggerContract.TriggerEntry.COLUMN_LAT, searchPlaceLat.latitude);
            values.put(TriggerContract.TriggerEntry.COLUMN_LONG, searchPlaceLat.longitude);
            Uri uriInsert = getActivity().getContentResolver().insert(uri, values);
            return TriggerContract.TriggerEntry.getIdFromUri(uriInsert);
        }
        // return -1;
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
                makeText(getActivity(), getString(R.string.text_duplicates), Toast.LENGTH_LONG).show();
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
                long id = insertRecord();
                Toast t = Toast.makeText(getActivity(),"For Location trigger to work,location should be enabled",Toast.LENGTH_LONG);
               // t.setGravity(Gravity.BOTTOM,0,70);
                t.show();
                Bundle args = new Bundle();
                args.putString("Name", mLocationName.getText().toString());
                String value;
                if (radioGroup.getCheckedRadioButtonId() == R.id.dialog_radio_connect) {
                    value = getString(R.string.text_enter);
                } else {
                    value = getString(R.string.text_exit);
                }
                args.putString("Value", value);
                args.putLong("uniqId", id);
                //Listener is null in case of edit
                if (mListener != null) {
                    mListener.onListen(args);
                }
                this.dismiss();
            }
        }
    }
}
