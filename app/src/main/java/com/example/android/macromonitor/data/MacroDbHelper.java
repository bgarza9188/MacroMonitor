package com.example.android.macromonitor.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jebus on 11/7/2017.
 */

public class MacroDbHelper extends SQLiteOpenHelper{
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "macro.db";

    public MacroDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MacroContract.MacroEntry.TABLE_NAME + " (" +
                MacroContract.MacroEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the Movie entry associated with this movie data
                MacroContract.MacroEntry.COLUMN_MACRO_NAME + " TEXT NOT NULL, " +
                MacroContract.MacroEntry.COLUMN_INTAKE_VALUE + " INTEGER NOT NULL, " +
                MacroContract.MacroEntry.COLUMN_INTAKE_DATE + " TEXT NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MacroContract.MacroEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
