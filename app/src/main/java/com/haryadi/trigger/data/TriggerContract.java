package com.haryadi.trigger.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


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
        //Column for trigger point
        public static final String COLUMN_TRIGGER_POINT = "trigger_point";
        //Column for custom name of the trigger
        public static final String COLUMN_TRIGGER_NAME = "trigger_name";
        //Column for name of wifi,bluetooth or location
        public static final String COLUMN_NAME = "name";
        //Column for triggerEnable/Disable
        public static final String COLUMN_CONNECT = "isConnect";
        //bluetooth setting
        public static final String COLUMN_ISBLUETOOTHON = "bluetooth";
        //wifi setting
        public static final String COLUMN_ISWIFION = "wifi";
        //Column for setting media volume
        public static final String COLUMN_MEDIAVOL = "media";
        //Column for setting Ring volume
        public static final String COLUMN_RINGVOL = "ring";
        //brightness setting
        public static final String COLUMN_BRIGHTNESS = "brightness";

        //returns uri with appended id
        public static Uri buildTaskUri(long id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
        }

        public static Uri buildUriWithName(String name) {
            return CONTENT_URI.buildUpon().appendPath(name).build();
        }

        public static String getTriggerNameFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

    }
}
