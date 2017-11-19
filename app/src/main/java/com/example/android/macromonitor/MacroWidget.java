package com.example.android.macromonitor;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.android.macromonitor.data.MacroContract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

/**
 * Implementation of App Widget functionality.
 */
public class MacroWidget extends AppWidgetProvider implements MacroConstants {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        Log.e("ben","updateAppWidget");
        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.macro_widget);
        Intent configIntent = new Intent(context, NutrientListActivity.class);
        PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);
        String today;
        DateFormat df = new SimpleDateFormat(DATE_FORMAT_DB_PATTERN);
        today = df.format(new Date());
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> selections = sharedPref.getStringSet(MACRO_PREF_LIST,null);
        Cursor cursor = context.getContentResolver().query(MacroContract.MacroEntry.CONTENT_URI,
                NutrientListActivity.MACRO_COLUMNS,
                LOADER_DB_NAME_ARG + selections.toArray()[0] + LOADER_ARG_CLOSE_QUOTE + " AND intake_date = '" + today + "'",
                null,
                SORT_ORDER_LATEST_RECORD);
        if (cursor == null || (cursor != null && cursor.getCount() == 0)) {
            Log.e("ben", "cursor was null :/");
        } else {
            Log.e("ben", "cursor not null!");
            cursor.moveToFirst();
            StringBuilder builder = new StringBuilder();
            String[] macroStringArray = context.getResources().getStringArray(R.array.pref_macro_list_titles);
            String[] macroDBStringArray = context.getResources().getStringArray(R.array.pref_macro_list_values);
            String[] goalStringArray = context.getResources().getStringArray(R.array.macro_goal_values);
            for(int i=0;i<macroDBStringArray.length;i++){
                if(macroDBStringArray[i].equalsIgnoreCase(cursor.getString(NutrientListActivity.COL_MACRO_NAME))){
                    builder.append(macroStringArray[i]);
                    builder.append(context.getString(R.string.colon_text));
                    builder.append("\n");
                    builder.append(cursor.getString(NutrientListActivity.COL_INTAKE_VALUE));
                    builder.append(context.getString(R.string.of_text));
                    builder.append(goalStringArray[i]);
                }
            }
            widgetText = builder.toString();
        }
        views.setTextViewText(R.id.appwidget_text, widgetText);
        views.setOnClickPendingIntent(R.id.widget, configPendingIntent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.e("ben","onUpdate");
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


}

