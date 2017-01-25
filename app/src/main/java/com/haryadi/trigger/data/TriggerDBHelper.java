package com.haryadi.trigger.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class TriggerDBHelper extends SQLiteOpenHelper {
    //Database version
    private static final int DATABASE_VERSION = 1;

    //Database name
    static final String DATABASE_NAME = "trigger.db";

    public TriggerDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Create table SQL statement
        final String SQL_CREATE_TABLE = "CREATE TABLE " + TriggerContract.TriggerEntry.TABLE_NAME + " (" +

                TriggerContract.TriggerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TriggerContract.TriggerEntry.COLUMN_TRIGGER_NAME + " TEXT NOT NULL," +
                TriggerContract.TriggerEntry.COLUMN_NAME + " TEXT NOT NULL," +
                TriggerContract.TriggerEntry.COLUMN_ISBLUETOOTHON + " TEXT," +
                TriggerContract.TriggerEntry.COLUMN_BRIGHTNESS + " TEXT," +
                TriggerContract.TriggerEntry.COLUMN_SOUNDSETTING + " TEXT," +
                TriggerContract.TriggerEntry.COLUMN_ISWIFION + " TEXT );";
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        //OnUpgrade drop the table and create new table
        //----TEMPORARY CHANGE ONCE YOU HAVE THE UPDATE
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TriggerContract.TriggerEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
