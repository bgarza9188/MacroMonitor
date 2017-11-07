package com.example.android.macromonitor.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by jebus on 11/7/2017.
 */

public class MacroContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.macromonitor";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MACRO = "nutrients";

    /* Inner class that defines the table contents of the Nutrients table */
    public static final class MacroEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MACRO).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MACRO;

        public static final String TABLE_NAME = "nutrients";

        //The name of the macro nutrient
        public static final String COLUMN_MACRO_NAME = "macro_name";
        //The intake value that was recorded for the day
        public static final String COLUMN_INTAKE_VALUE = "intake_value";
        //The day the record represents
        public static final String COLUMN_INTAKE_DATE = "intake_date";

        public static Uri buildMacroUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
