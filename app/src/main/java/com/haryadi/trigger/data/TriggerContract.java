package com.haryadi.trigger.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by aharyadi on 12/26/16.
 */

public class TriggerContract {

    //Authority for contentprovider which is the package name
    public static final String CONTENT_AUTHORITY = "com.haryadi.trigger";

    //Base content uri which contains the content Authority
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //path to be added to Uri of content provider
    public static final String PATH_TRIGGER = "trigger";

    public static final class TriggerEntry implements BaseColumns {


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRIGGER).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRIGGER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRIGGER;

        public static final String TABLE_NAME = "trigger";
        //Column for custom name of the trigger
        public static final String COLUMN_TRIGGER_NAME = "trigger_name";
        //Column for name of wifi,bluetooth or location
        public static final String COLUMN_NAME = "name";
        //bluetooth setting
        public static final String COLUMN_ISBLUETOOTHON = "bluetooth";
        //sound setting
        public static final String COLUMN_SOUNDSETTING = "sound";
        //wifi setting
        public static final String COLUMN_ISWIFION = "wifi";
        //brightness setting
        public static final String COLUMN_BRIGHTNESS = "brightness";

        //returns uri with appended id
        public static Uri buildTaskUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildUriWithName(String name) {
            return CONTENT_URI.buildUpon().appendPath(name).build();
        }

  /*      public static Uri buildUriWithDate(long date) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(date)).build();
        }*/

        public static String getTriggerNameFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

  /*      public static long getDateFromUriWithTaskName(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static long getDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }*/

    }
}
