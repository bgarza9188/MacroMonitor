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

import com.example.android.macromonitor.data.MacroContract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, MacroConstants{

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int MACRO_LOADER_ONE = 1;
    private static final int MACRO_LOADER_TWO = 2;
    private static final int MACRO_LOADER_THREE = 3;
    private static final int MACRO_LOADER_FOUR = 4;
    private TextView textView_quadrant_one;
    private TextView textView_quadrant_two;
    private TextView textView_quadrant_three;
    private TextView textView_quadrant_four;
    private Button button_quadrant_one;
    private Button button_quadrant_two;
    private Button button_quadrant_three;
    private Button button_quadrant_four;

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

        textView_quadrant_one = findViewById(R.id.quadrant_one_text);
        button_quadrant_one = findViewById(R.id.quadrant_one_button);

        textView_quadrant_two = findViewById(R.id.quadrant_two_text);
        button_quadrant_two = findViewById(R.id.quadrant_two_button);

        textView_quadrant_three = findViewById(R.id.quadrant_three_text);
        button_quadrant_three = findViewById(R.id.quadrant_three_button);

        textView_quadrant_four = findViewById(R.id.quadrant_four_text);
        button_quadrant_four = findViewById(R.id.quadrant_four_button);

        //TODO need to call insert to insert new rows for each new day
        //initDBInserts();
        //This will init a loader that will pull all rows
        //getSupportLoaderManager().initLoader(MACRO_LOADER_ONE, null, this);
        Bundle bundle = new Bundle();
        bundle.putString(LOADER_ARG_BUNDLE_KEY_SELECTION, LOADER_DB_NAME_ARG + MACRO_WATER_DB_NAME + LOADER_ARG_CLOSE_QUOTE);
        getSupportLoaderManager().initLoader(MACRO_LOADER_ONE, bundle, this);

        bundle.putString(LOADER_ARG_BUNDLE_KEY_SELECTION, LOADER_DB_NAME_ARG + MACRO_FAT_DB_NAME + LOADER_ARG_CLOSE_QUOTE);
        getSupportLoaderManager().initLoader(MACRO_LOADER_TWO, bundle, this);

        bundle.putString(LOADER_ARG_BUNDLE_KEY_SELECTION, LOADER_DB_NAME_ARG + MACRO_CARB_DB_NAME + LOADER_ARG_CLOSE_QUOTE);
        getSupportLoaderManager().initLoader(MACRO_LOADER_THREE, bundle, this);

        bundle.putString(LOADER_ARG_BUNDLE_KEY_SELECTION, LOADER_DB_NAME_ARG + MACRO_SUGAR_DB_NAME + LOADER_ARG_CLOSE_QUOTE);
        getSupportLoaderManager().initLoader(MACRO_LOADER_FOUR, bundle, this);
    }

    private void initDBInserts() {
//        ContentValues contentValues = new ContentValues();
//
//        contentValues.put(MacroContract.MacroEntry.COLUMN_MACRO_NAME, MACRO_WATER_DB_NAME);
//        contentValues.put(MacroContract.MacroEntry.COLUMN_INTAKE_VALUE, 0);
//        contentValues.put(MacroContract.MacroEntry.COLUMN_INTAKE_DATE, "20171108");
//        getContentResolver().insert(MacroContract.MacroEntry.CONTENT_URI,contentValues);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = new CursorLoader(this,
                    MacroContract.MacroEntry.CONTENT_URI,
                    MACRO_COLUMNS,
                    args.getString(LOADER_ARG_BUNDLE_KEY_SELECTION),
                    null,
                    SORT_ORDER_LATEST_RECORD);

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.e(LOG_TAG, "Ben in onLoadFinished, loader ID:" + loader.getId());
        if(data == null || (data != null && data.getCount() == 0)){
            Log.e(LOG_TAG, "Ben on results from loader");
        }else{
            Log.e(LOG_TAG, "Ben data getCount:" + data.getCount());
            data.moveToFirst();
            do{
                updateUI(loader.getId(), data);
                Log.e(LOG_TAG, "Ben macro names:" + data.getString(COL_MACRO_NAME) + " record Date:" + data.getString(COL_INTAKE_DATE) + " record ID:" + data.getInt(COL_MACRO_ENTRY_ID) + " value:" + data.getInt(COL_INTAKE_VALUE));
            }while(data.moveToNext());

        }
    }

    private void updateUI(int id, final Cursor data) {
        data.moveToFirst();//sanity check
        String name = data.getString(COL_MACRO_NAME);
        final int value = data.getInt(COL_INTAKE_VALUE);
        if(id == 1){
            textView_quadrant_one.setText("Name:" + name + ", value:" + value);
            button_quadrant_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Make an update call to DB
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MacroContract.MacroEntry.COLUMN_MACRO_NAME, MACRO_WATER_DB_NAME);
                    contentValues.put(MacroContract.MacroEntry.COLUMN_INTAKE_VALUE, value+1);
                    //Ideally the date set here will be today's date
                    contentValues.put(MacroContract.MacroEntry.COLUMN_INTAKE_DATE, "20171108");
                    //The 'macro_name' in the where clause should match the quadrant and the update value
                    //The date here should also ideally be today's date
                    getContentResolver().update(MacroContract.MacroEntry.CONTENT_URI,contentValues, "macro_name = 'WATER' AND intake_date = '20171108'", null);
                }
            });
        } else if(id == 2) {
            textView_quadrant_two.setText("Name:" + name + ", value:" + value);
            button_quadrant_two.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Make an update call to DB
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MacroContract.MacroEntry.COLUMN_MACRO_NAME, MACRO_FAT_DB_NAME);
                    //just doing '+1' for now
                    contentValues.put(MacroContract.MacroEntry.COLUMN_INTAKE_VALUE, value+1);
                    //Ideally the date set here will be today's date
                    contentValues.put(MacroContract.MacroEntry.COLUMN_INTAKE_DATE, "20171108");
                    //The 'macro_name' in the where clause should match the quadrant and the update value
                    //The date here should also ideally be today's date
                    getContentResolver().update(MacroContract.MacroEntry.CONTENT_URI,contentValues, "macro_name = 'FAT' AND intake_date = '20171108'", null);
                }
            });
        } else if(id == 3) {
            textView_quadrant_three.setText("Name:" + name + ", value:" + value);
            button_quadrant_three.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Make an update call to DB
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MacroContract.MacroEntry.COLUMN_MACRO_NAME, MACRO_CARB_DB_NAME);
                    //just doing '+1' for now
                    contentValues.put(MacroContract.MacroEntry.COLUMN_INTAKE_VALUE, value+1);
                    //Ideally the date set here will be today's date
                    contentValues.put(MacroContract.MacroEntry.COLUMN_INTAKE_DATE, "20171108");
                    //The 'macro_name' in the where clause should match the quadrant and the update value
                    //The date here should also ideally be today's date
                    getContentResolver().update(MacroContract.MacroEntry.CONTENT_URI,contentValues, "macro_name = 'CARBOHYDRATES' AND intake_date = '20171108'", null);
                }
            });
        } else if(id == 4) {
            textView_quadrant_four.setText("Name:" + name + ", value:" + value);
            button_quadrant_four.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Make an update call to DB
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MacroContract.MacroEntry.COLUMN_MACRO_NAME, MACRO_SUGAR_DB_NAME);
                    //just doing '+1' for now
                    contentValues.put(MacroContract.MacroEntry.COLUMN_INTAKE_VALUE, value+1);
                    //Ideally the date set here will be today's date
                    contentValues.put(MacroContract.MacroEntry.COLUMN_INTAKE_DATE, "20171108");
                    //The 'macro_name' in the where clause should match the quadrant and the update value
                    //The date here should also ideally be today's date
                    getContentResolver().update(MacroContract.MacroEntry.CONTENT_URI,contentValues, "macro_name = 'SUGAR' AND intake_date = '20171108'", null);
                }
            });
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
