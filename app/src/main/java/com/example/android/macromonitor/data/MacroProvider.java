package com.example.android.macromonitor.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by jebus on 11/7/2017.
 */

public class MacroProvider extends ContentProvider{
    private final String LOG_TAG = MacroProvider.class.getSimpleName();

    private MacroDbHelper mOpenHelper;

    private static final SQLiteQueryBuilder sMacroQueryBuilder;

    static{
        sMacroQueryBuilder = new SQLiteQueryBuilder();

        sMacroQueryBuilder.setTables(MacroContract.MacroEntry.TABLE_NAME);
    }

    @Override
    public boolean onCreate() {
        Log.e(LOG_TAG, "Ben in onCreate");
        mOpenHelper = new MacroDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        Cursor retCursor = mOpenHelper.getReadableDatabase().query(
                MacroContract.MacroEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return MacroContract.MacroEntry.CONTENT_TYPE;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;

        long _id = db.insert(MacroContract.MacroEntry.TABLE_NAME, null, values);
        if ( _id > 0 )
            returnUri = MacroContract.MacroEntry.buildMacroUri(_id);
        else
            throw new android.database.SQLException("Failed to insert row into " + uri);


        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted

        rowsDeleted = db.delete(
                MacroContract.MacroEntry.TABLE_NAME, selection, selectionArgs);

        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated;

        rowsUpdated = db.update(MacroContract.MacroEntry.TABLE_NAME, values, selection,
                selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        int returnCount = 0;

        try {
            for (ContentValues value : values) {

                long _id = db.insert(MacroContract.MacroEntry.TABLE_NAME, null, value);
                if (_id != -1) {
                    returnCount++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnCount;
    }
}
