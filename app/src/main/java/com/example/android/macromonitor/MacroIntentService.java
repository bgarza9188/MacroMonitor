package com.example.android.macromonitor;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.example.android.macromonitor.data.MacroContract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.example.android.macromonitor.NutrientListActivity.COL_INTAKE_DATE;
import static com.example.android.macromonitor.NutrientListActivity.COL_INTAKE_VALUE;
import static com.example.android.macromonitor.NutrientListActivity.COL_MACRO_ENTRY_ID;
import static com.example.android.macromonitor.NutrientListActivity.COL_MACRO_NAME;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MacroIntentService extends IntentService implements MacroConstants{

    private static final String ACTION_FETCH = "com.example.android.macromonitor.action.FETCH";
    private static final String ACTION_BAZ = "com.example.android.macromonitor.action.BAZ";

    private static final String EXTRA_MACRO_NAME = "com.example.android.macromonitor.extra.NAME";
    private static final String EXTRA_START_DATE = "com.example.android.macromonitor.extra.DATE";

    public MacroIntentService() {
        super("MacroIntentService");
    }

    /**
     * Starts this service to perform action Fetch with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionFetchWeek(Context context, String name, String weekDate) {
        Intent intent = new Intent(context, MacroIntentService.class);
        intent.setAction(ACTION_FETCH);
        intent.putExtra(EXTRA_MACRO_NAME, name);
        intent.putExtra(EXTRA_START_DATE, weekDate);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MacroIntentService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_MACRO_NAME, param1);
        intent.putExtra(EXTRA_START_DATE, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FETCH.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_MACRO_NAME);
                final String param2 = intent.getStringExtra(EXTRA_START_DATE);
                handleActionFetchWeek(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_MACRO_NAME);
                final String param2 = intent.getStringExtra(EXTRA_START_DATE);
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFetchWeek(String macroName, String weekDayDate) {

        Number[] intakeValues = new Number[7];
        Calendar calender = Calendar.getInstance();
        int dayOfWeek = calender.get(Calendar.DAY_OF_WEEK);
        DateFormat df = new SimpleDateFormat(DATE_FORMAT_DB_PATTERN);
        calender.add(Calendar.DATE,(-dayOfWeek+1));

        for(int i=0;i<7;i++) {
            if(i != 0) {
                //Initial parameter should be a formatted proper date.
                calender.add(Calendar.DATE, 1);
                Date date = calender.getTime();
                weekDayDate = df.format(date);
            }
            Cursor cursor = getContentResolver().query(MacroContract.MacroEntry.CONTENT_URI,
                    NutrientListActivity.MACRO_COLUMNS,
                    LOADER_DB_NAME_ARG + macroName + LOADER_ARG_CLOSE_QUOTE + " AND intake_date = '" + weekDayDate + "'",
                    null,
                    SORT_ORDER_LATEST_RECORD);
            Log.e("ben","weekDayDate:" + weekDayDate);
            if (cursor == null || (cursor != null && cursor.getCount() == 0)) {
                Log.e("ben", "cursor was null :/");
                intakeValues[i] = 0;
            } else {
                Log.e("ben", "cursor not null!");
                cursor.moveToFirst();
                do {
                    Log.e("ben", "cursor ID: " + cursor.getInt(COL_MACRO_ENTRY_ID) + " cursor macro name:" + cursor.getString(COL_MACRO_NAME) + " cursor date:" + cursor.getString(COL_INTAKE_DATE) + " cursor Value:" + cursor.getInt(COL_INTAKE_VALUE));
                    intakeValues[i] = cursor.getInt(COL_INTAKE_VALUE);
                } while (cursor.moveToNext());
            }
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
