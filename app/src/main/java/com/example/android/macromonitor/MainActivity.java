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
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.macromonitor.data.MacroContract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, MacroConstants{

    private static final int MACRO_LOADER_ONE = 0;
    private static final int MACRO_LOADER_TWO = 1;
    private static final int MACRO_LOADER_THREE = 2;
    private static final int MACRO_LOADER_FOUR = 3;
    private TextView textView;

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

        textView = findViewById(R.id.quardrant_one_text);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MacroContract.MacroEntry.COLUMN_MACRO_NAME, MACRO_SUGAR_DB_NAME);
                    contentValues.put(MacroContract.MacroEntry.COLUMN_INTAKE_VALUE, 1);
                    contentValues.put(MacroContract.MacroEntry.COLUMN_INTAKE_DATE, "20171108");
                    getContentResolver().insert(MacroContract.MacroEntry.CONTENT_URI,contentValues);
            }
        });
        getSupportLoaderManager().initLoader(MACRO_LOADER_ONE, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader;
        switch (id) {
            case 0:  loader = new CursorLoader(this,
                    MacroContract.MacroEntry.CONTENT_URI,
                    MACRO_COLUMNS,
                    null,
                    null,
                    null);
                break;
            case 1: loader = new CursorLoader(this,
                    MacroContract.MacroEntry.CONTENT_URI,
                    MACRO_COLUMNS,
                    "macro_name=" + MACRO_FAT_DB_NAME,
                    null,
                    null);
                break;
            case 2: loader = new CursorLoader(this,
                    MacroContract.MacroEntry.CONTENT_URI,
                    MACRO_COLUMNS,
                    "macro_name=" + MACRO_CARB_DB_NAME,
                    null,
                    null);
                break;
            case 3: loader = new CursorLoader(this,
                    MacroContract.MacroEntry.CONTENT_URI,
                    MACRO_COLUMNS,
                    "macro_name=" + MACRO_SUGAR_DB_NAME,
                    null,
                    null);
                break;
            default: loader = null;
                break;
        }

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.e("MainActivity", "Ben in onLoadFinished");
        if(data == null || (data != null && data.getCount() == 0)){
            Toast.makeText(this, "No results",
                    Toast.LENGTH_SHORT).show();
        }else{
            loader.getId();
            Log.e("Ben", "data getCount:" + data.getCount());
//            data.moveToFirst();
//            String name = data.getString(COL_MACRO_NAME);
//            int value = data.getInt(COL_INTAKE_VALUE);
//            String date = data.getString(COL_INTAKE_DATE);
//            textView.setText("Name:" + name + " Value:" + value + " date: " + date);
            //Toasty.success(getApplicationContext(), "NAME:" + name + " VALUE:" + value + " DATE:" + date, Toast.LENGTH_LONG, true).show();
//            String names[] = data.getColumnNames();
//
//            for(int i=0; i<names.length; i++){
//                Log.e("Ben", "col_name:" + names[i]);
//                Log.e("Ben", "col_index:" + data.getColumnIndex(names[i]));
//            }
            //Toasty.info(getApplicationContext(), "Column Count:" + data.getColumnCount(), Toast.LENGTH_LONG, true).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
