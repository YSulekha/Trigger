package com.haryadi.trigger.fragment;

import android.app.Activity;
import android.app.Dialog;
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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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

import com.haryadi.trigger.R;
import com.haryadi.trigger.data.TriggerContract;
import com.haryadi.trigger.utils.ChangeSettings;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.haryadi.trigger.R.string.wifi;


public class EditCreateProfileFragment extends DialogFragment {

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
    @BindView(R.id.wifiOn)
    ImageButton mWifiButton;
    @BindView(R.id.bluetooth)
    ImageButton mBluetoothButton;
    @BindView(R.id.button_save)
    Button saveButton;
    @BindView(R.id.spinner_wifi_name)
    Spinner mWifiName;
    @BindView(R.id.isConnect)
    RadioGroup radioGroup;
    @BindView(R.id.dialog_radio_connect)
    RadioButton radioConnect;
    @BindView(R.id.dialog_radio_disconnect)
    RadioButton radioDisConnect;
    @BindView(R.id.name)
    ImageButton imageButton;
    @BindView(R.id.wifi_name_text)
    TextView wifiNameText;

    @BindView(R.id.expand)ImageButton buttonExpand;
    @BindView(R.id.content_main_d) RelativeLayout relate;
    @BindView(R.id.contact_num)
    EditText con;

    @BindView(R.id.message) ImageButton messageButton;
    @BindView(R.id.message_text) TextView messageText;
    @BindView(R.id.contact_close) ImageButton contactClose;

    String contactName;

    public static final int PICK_CONTACT = 100;
    String triggerPoint;
    ArrayList<String> names;
    ArrayAdapter<CharSequence> optionsAdapter;
    ArrayAdapter<String> arrayAdapter;
    String isConnect;
    boolean isEdit = false;
    Uri mUri;
    String id;
    String ph_number;

    public static final int REQUEST_SMS = 100;
    public static final int SEND_SMS = 101;


    public EditCreateProfileFragment() {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public static EditCreateProfileFragment newInstance(String triggerPoint, boolean isEdit, Uri uri) {
        EditCreateProfileFragment frag = new EditCreateProfileFragment();
        Bundle args = new Bundle();
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
        return inflater.inflate(R.layout.frag_dialog_drop, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        isConnect = getString(R.string.text_connect);
        triggerPoint = getArguments().getString("title", "Enter Name");
        isEdit = getArguments().getBoolean("isEdit");
        names = new ArrayList<>();
        if (triggerPoint.equals(getString(wifi))) {
            names = ChangeSettings.getConfiguredWifi(getActivity());
            imageButton.setImageResource(R.drawable.ic_wifi_brown);
        }
        if (triggerPoint.equals(getString(R.string.bluetooth))) {
            names = ChangeSettings.getConfiguredBluetooth(getActivity());
            imageButton.setImageResource(R.drawable.ic_bluetooth_brown);
        }
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
        if(savedInstanceState != null){

            if(con.getText().toString().length()>0){
                messageText.setVisibility(View.VISIBLE);
                messageButton.setVisibility(View.VISIBLE);
            }
        }
        //Expand exp
        buttonExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(relate.isShown()) {
                   ChangeSettings.slide_up(getActivity(),relate);
                    relate.setVisibility(View.GONE);
                }
                else{
                    ChangeSettings.slide_down(getActivity(),relate);
                    relate.setVisibility(View.VISIBLE);
                }
            }
        });
        //msg exp
        con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //  checkpermission();
                Intent intent= new Intent(Intent.ACTION_PICK,  ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });

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
                if(progress == 0){
                    Vibrator v = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
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
            case (PICK_CONTACT) :
                Log.v("Inside ddsf","resul ok");
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c =  getContext().getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        contactName = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        id= c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                        if(Integer.parseInt(c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)))>0){
                            checkpermission();
                        }
                        else{
                            Toast.makeText(getActivity(),"No Number associated with this contact.Choose another one",Toast.LENGTH_LONG).show();
                        }


                    }
                }

                break;
        }
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

    @OnClick(R.id.button_save)
    public void onClickSave(View view) {
      /*  if (mWifiName.getSelectedItem()==null && !isEdit) {
            Toast.makeText(getActivity(),"Please turn on the "+triggerPoint+" to enter the name",Toast.LENGTH_LONG).show();
            this.dismiss();
        }
        else {*/
        if (!checkForDuplicates()) {
            insertRecord();
            this.dismiss();

        }
      //  ChangeSettings.sendMessage();
      //  checkpermission();
        // }
    }

    public void checkpermission() {
        Log.v("ooo2","Cheack Permission");
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),android.Manifest.permission.READ_CONTACTS)) {
                    showMessageOKCancel("You need to allow access to Send SMS",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS},
                                                REQUEST_SMS);
                                }
                            });
                    return;
                }
                else{
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
                    checkpermission();
                }
                break;
            }
            case SEND_SMS:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    int hasSMSPermission = ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.SEND_SMS);
                    if (hasSMSPermission == PackageManager.PERMISSION_GRANTED) {
                        // Intent intent= new Intent(Intent.ACTION_PICK,  ContactsContract.Contacts.CONTENT_URI);
                        // startActivityForResult(intent, PICK_CONTACT);
                        Cursor pCur = getContext().getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                                new String[]{id}, null);

                        if(pCur.getCount() == 0){
                            Toast.makeText(getActivity(),"No number",Toast.LENGTH_LONG).show();
                        }
                        else {
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
                    checkpermission();
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

    private void configureViews() {
        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, names);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mWifiName.setAdapter(arrayAdapter);
        setVolumeSeekBar();
        optionsAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.options, android.R.layout.simple_spinner_item);
        optionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mIsBluetoothOn.setAdapter(optionsAdapter);
        mIsWifiOn.setAdapter(optionsAdapter);
        radioConnect.setChecked(true);
        if (triggerPoint.equals(getString(wifi))) {
            mIsWifiOn.setVisibility(View.GONE);
            mWifiButton.setVisibility(View.GONE);
        }
        if (triggerPoint.equals(getString(R.string.bluetooth))) {
            mBluetoothButton.setVisibility(View.GONE);
            mIsBluetoothOn.setVisibility(View.GONE);
        }

    }

    private void configureValues(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, ChangeSettings.TRIGGER_COLUMNS, null, null, null);
        if (cursor.moveToNext()) {
            String triggerPoint = cursor.getString(ChangeSettings.INDEX_TRIGGER_POINT);
            if (triggerPoint.equals(getString(wifi))) {
                mIsWifiOn.setSelection(0);
                mIsWifiOn.setEnabled(false);
            } else if (triggerPoint.equals(getString(R.string.bluetooth))) {
                mIsBluetoothOn.setSelection(0);
                mIsBluetoothOn.setEnabled(false);
            }
            if (cursor.getString(ChangeSettings.INDEX_CONNECT).equals(getString(R.string.text_connect))) {
                radioConnect.setChecked(true);
                radioGroup.setEnabled(false);

            } else {
                radioDisConnect.setChecked(true);
                radioGroup.setEnabled(false);
            }
            mWifiName.setVisibility(View.INVISIBLE);
            wifiNameText.setVisibility(View.VISIBLE);
            radioConnect.setEnabled(false);
            radioDisConnect.setEnabled(false);
            wifiNameText.setText(cursor.getString(ChangeSettings.INDEX_NAME));
            mIsBluetoothOn.setSelection(optionsAdapter.getPosition(cursor.getString(ChangeSettings.INDEX_ISBLUETOOTHON)));
            mIsWifiOn.setSelection(optionsAdapter.getPosition(cursor.getString(ChangeSettings.INDEX_ISWIFION)));
            mediaVolumeBar.setProgress(cursor.getInt(ChangeSettings.INDEX_MEDIAVOL));
            ringVolumeBar.setProgress(cursor.getInt(ChangeSettings.INDEX_RINGVOL));
            notifVolumeBar.setProgress(cursor.getInt(ChangeSettings.INDEX_NOTIFVOL));
            messageText.setText(cursor.getString(ChangeSettings.INDEX_MSGTEXT));
            ph_number = cursor.getString(ChangeSettings.INDEX_PHNUMBER);
            if(ph_number != null) {
                con.setText(ChangeSettings.getDisplayNameByNumber(getActivity(), ph_number));
                messageButton.setVisibility(View.VISIBLE);
                messageText.setVisibility(View.VISIBLE);
            }
            cursor.close();
        }
    }



    private void insertRecord() {
        ContentValues values = new ContentValues();
        Uri uri = TriggerContract.TriggerEntry.CONTENT_URI;
        values.put(TriggerContract.TriggerEntry.COLUMN_MEDIAVOL, mediaVolumeBar.getProgress());
        values.put(TriggerContract.TriggerEntry.COLUMN_RINGVOL, ringVolumeBar.getProgress());
        values.put(TriggerContract.TriggerEntry.COLUMN_NOTIFVOL, notifVolumeBar.getProgress());
        values.put(TriggerContract.TriggerEntry.COLUMN_ISBLUETOOTHON, mIsBluetoothOn.getSelectedItem().toString());
        values.put(TriggerContract.TriggerEntry.COLUMN_ISWIFION, mIsWifiOn.getSelectedItem().toString());
        values.put(TriggerContract.TriggerEntry.COLUMN_PH_NUMBER,ph_number);
        values.put(TriggerContract.TriggerEntry.COLUMN_MSG_TEXT,messageText.getText().toString());
        if (isEdit) {
            values.put(TriggerContract.TriggerEntry.COLUMN_NAME, wifiNameText.getText().toString());
            updateRecord(values);
        } else {
            String name = mWifiName.getSelectedItem().toString();
            values.put(TriggerContract.TriggerEntry.COLUMN_NAME, name);
            ChangeSettings.addWifiNameToSharedPreference(getActivity());
            values.put(TriggerContract.TriggerEntry.COLUMN_TRIGGER_POINT, triggerPoint);
            values.put(TriggerContract.TriggerEntry.COLUMN_TRIGGER_NAME, triggerPoint + "_" + name);
            values.put(TriggerContract.TriggerEntry.COLUMN_CONNECT, isConnect);
            getActivity().getContentResolver().insert(uri, values);
        }
    }


    private boolean checkForDuplicates() {
        if (!isEdit) {
            String name = mWifiName.getSelectedItem().toString();
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

    private void updateRecord(ContentValues values) {
        String where = TriggerContract.TriggerEntry.TABLE_NAME + "." +
                TriggerContract.TriggerEntry._ID + " = ?";
        String[] args = new String[]{Long.toString(TriggerContract.TriggerEntry.getIdFromUri(mUri))};
        getActivity().getContentResolver().update(TriggerContract.TriggerEntry.CONTENT_URI, values, where, args);
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
}
