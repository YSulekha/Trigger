package com.haryadi.trigger.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


public class TriggerProvider extends ContentProvider {
    private static final int TASK = 100;
    private static final int TASK_WITH_NAME = 101;
    private static final int TASK_WITH_ID = 102;
    //private static final int TASK_WITH_NAME_AND_DATE = 102;
    private static final String sIdSelection = TriggerContract.TriggerEntry.TABLE_NAME + "." + TriggerContract.TriggerEntry._ID + " = ?";
    private static final String sTriigerSelection = TriggerContract.TriggerEntry.TABLE_NAME + "." + TriggerContract.TriggerEntry.COLUMN_TRIGGER_NAME + " = ?";
  /*  private static final String sDateAndTaskNameSelection = TriggerContract.TriggerEntry.TABLE_NAME + "." +
            TriggerContract.TriggerEntry.COLUMN_TASK_DATE + " = ?" +
            " AND " + TriggerContract.TriggerEntry.TABLE_NAME + "." +
            TriggerContract.TriggerEntry.COLUMN_TASK_NAME + "= ?";*/


    public static final UriMatcher sUriMatcher = buildUriMatcher();

    public TriggerDBHelper mDbHelper;


    static UriMatcher buildUriMatcher() {
        UriMatcher urimatcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = TriggerContract.CONTENT_AUTHORITY;

        urimatcher.addURI(authority, TriggerContract.PATH_TRIGGER, TASK);
        urimatcher.addURI(authority, TriggerContract.PATH_TRIGGER + "/#", TASK_WITH_ID);
        urimatcher.addURI(authority, TriggerContract.PATH_TRIGGER + "/*", TASK_WITH_NAME);
       // urimatcher.addURI(authority, TriggerContract.PATH_TASK + "/*/#", TASK_WITH_NAME_AND_DATE);


        return urimatcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new TriggerDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String s, String[] strings1, String sort) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (match) {
            case TASK:
                retCursor = db.query(TriggerContract.TriggerEntry.TABLE_NAME, projection, s, strings1, null, null, sort);
                break;

            case TASK_WITH_ID:
                long id1 = TriggerContract.TriggerEntry.getIdFromUri(uri);
                String[] dateArgs = new String[]{Long.toString(id1)};
                retCursor = db.query(TriggerContract.TriggerEntry.TABLE_NAME, projection, sIdSelection, dateArgs, null, null, sort);
                break;

            case TASK_WITH_NAME:
                String triggerName = TriggerContract.TriggerEntry.getTriggerNameFromUri(uri);
                String[] selectionArgs = new String[]{triggerName};
                retCursor = db.query(TriggerContract.TriggerEntry.TABLE_NAME, projection, sTriigerSelection, selectionArgs, null, null, sort);
                break;



       /*     case TASK_WITH_NAME_AND_DATE:
                long dateValue = TriggerContract.TriggerEntry.getDateFromUriWithTaskName(uri);
                String name = TriggerContract.TriggerEntry.getTaskNameFromUri(uri);
                String[] args = new String[]{Long.toString(dateValue), name};
                retCursor = db.query(TriggerContract.TriggerEntry.TABLE_NAME, projection, sDateAndTaskNameSelection, args, null, null, sort);
                break;*/

            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case TASK:
                return TriggerContract.TriggerEntry.CONTENT_DIR_TYPE;
            case TASK_WITH_NAME:
                return TriggerContract.TriggerEntry.CONTENT_DIR_TYPE;
            case TASK_WITH_ID:
                return TriggerContract.TriggerEntry.CONTENT_ITEM_TYPE;
           /* case TASK_WITH_NAME_AND_DATE:
                return TriggerContract.TriggerEntry.CONTENT_ITEM_TYPE;*/
            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri retUri;
        switch (match) {
            case TASK:
                long id = db.insert(TriggerContract.TriggerEntry.TABLE_NAME, null, contentValues);

                if (id > 0) {
                    retUri = TriggerContract.TriggerEntry.buildTaskUri(id);
                } else {
                    throw new SQLException("Error inserting record");
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return retUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        int retValue;
        switch (match) {
            case TASK:
                retValue = db.delete(TriggerContract.TriggerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);
        }
        if (retValue != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return retValue;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int retValue;
        switch (match) {
            case TASK:
                retValue = db.update(TriggerContract.TriggerEntry.TABLE_NAME, contentValues, whereClause, whereArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return retValue;
    }
}
