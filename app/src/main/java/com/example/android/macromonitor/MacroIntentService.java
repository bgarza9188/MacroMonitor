package com.example.android.macromonitor;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.example.android.macromonitor.data.MacroContract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.example.android.macromonitor.NutrientListActivity.COL_INTAKE_VALUE;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class MacroIntentService extends IntentService implements MacroConstants{

    private static final String ACTION_FETCH = "com.example.android.macromonitor.action.FETCH";
    public static final String FETCHED_DATA = "fetched_data";

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

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FETCH.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_MACRO_NAME);
                final String param2 = intent.getStringExtra(EXTRA_START_DATE);
                handleActionFetchWeek(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFetchWeek(String macroName, String weekDayDate) {

        int[] intakeValues = new int[7];
        Calendar calender = Calendar.getInstance();
        int dayOfWeek = calender.get(Calendar.DAY_OF_WEEK);
        DateFormat df = new SimpleDateFormat(DATE_FORMAT_DB_PATTERN);
        calender.add(Calendar.DATE,(-dayOfWeek+1));
        Cursor cursor = null;
        for(int i=0;i<7;i++) {
            if(i != 0) {
                //Initial parameter should be a formatted proper date.
                calender.add(Calendar.DATE, 1);
                Date date = calender.getTime();
                weekDayDate = df.format(date);
            }
            cursor = getContentResolver().query(MacroContract.MacroEntry.CONTENT_URI,
                    NutrientListActivity.MACRO_COLUMNS,
                    LOADER_DB_NAME_ARG + macroName + LOADER_ARG_CLOSE_QUOTE + " AND intake_date = '" + weekDayDate + "'",
                    null,
                    SORT_ORDER_LATEST_RECORD);
            if (cursor == null || cursor.getCount() == 0) {
                intakeValues[i] = 0;
            } else {
                cursor.moveToFirst();
                do {
                    intakeValues[i] = cursor.getInt(COL_INTAKE_VALUE);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(NutrientDetailFragment.ResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(FETCHED_DATA, intakeValues);
        sendBroadcast(broadcastIntent);
    }
}
