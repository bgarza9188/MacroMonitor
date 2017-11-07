package com.example.android.macromonitor;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.macromonitor.data.MacroContract;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int MACRO_LOADER = 0;


    private static final String[] MACRO_COLUMNS = {
            MacroContract.MacroEntry.TABLE_NAME + "." + MacroContract.MacroEntry._ID,
            MacroContract.MacroEntry.COLUMN_MACRO_NAME,
            MacroContract.MacroEntry.COLUMN_INTAKE_VALUE,
            MacroContract.MacroEntry.COLUMN_INTAKE_DATE
    };

    public static final int COL_MACRO_ENTRY_ID = 0;
    public static final int COL_MACRO_NAME = 1;
    public static final int COL_INTAKE_VALUE = 2;
    public static final int COL_INTAKE_DATE = 3;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MacroContract.MacroEntry.COLUMN_MACRO_NAME, "FAT");
                    contentValues.put(MacroContract.MacroEntry.COLUMN_INTAKE_VALUE, 2);
                    contentValues.put(MacroContract.MacroEntry.COLUMN_INTAKE_DATE, "20171231");
                    getContentResolver().insert(MacroContract.MacroEntry.CONTENT_URI,contentValues);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSupportLoaderManager().initLoader(MACRO_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                MacroContract.MacroEntry.CONTENT_URI,
                MACRO_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.e("MainActivity", "Ben in onLoadFinished");
        if(data == null || (data != null && data.getCount() == 0)){
            Toast.makeText(this, "No results",
                    Toast.LENGTH_SHORT).show();
        }else{
            data.moveToFirst();
            String name = data.getString(COL_MACRO_NAME);
            int value = data.getInt(COL_INTAKE_VALUE);
            String date = data.getString(COL_INTAKE_DATE);
            Toasty.success(getApplicationContext(), "NAME:" + name + " VALUE:" + value + " DATE:" + date, Toast.LENGTH_LONG, true).show();
            String names[] = data.getColumnNames();

            for(int i=0; i<names.length; i++){
                Log.e("Ben", "col_name:" + names[i]);
                Log.e("Ben", "col_index:" + data.getColumnIndex(names[i]));
            }
            Toasty.info(getApplicationContext(), "Column Count:" + data.getColumnCount(), Toast.LENGTH_LONG, true).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
