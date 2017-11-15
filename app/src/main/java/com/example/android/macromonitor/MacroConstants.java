package com.example.android.macromonitor;

import com.example.android.macromonitor.data.MacroContract;

/**
 * Created by jebus on 11/8/2017.
 */

public interface MacroConstants {
    String MACRO_WATER_DB_NAME = "WATER";
    String MACRO_FAT_DB_NAME = "FAT";
    String MACRO_CARB_DB_NAME = "CARBOHYDRATES";
    String MACRO_SUGAR_DB_NAME = "SUGAR";
    String MACRO_PROTEIN_DB_NAME = "PROTEIN";
    String MACRO_CALORIES_DB_NAME = "CALORIES";
    String MACRO_SODIUM_DB_NAME = "SODIUM";
    String LOADER_DB_NAME_ARG = MacroContract.MacroEntry.COLUMN_MACRO_NAME + " = '";
    String LOADER_ARG_CLOSE_QUOTE = "'";
    String LOADER_ARG_BUNDLE_KEY_SELECTION = "SELECTION";
    String SORT_ORDER_LATEST_RECORD = MacroContract.MacroEntry.COLUMN_INTAKE_DATE + " DESC LIMIT 1";
    String SORT_ORDER_LATEST_SEVEN_RECORDS = MacroContract.MacroEntry.COLUMN_INTAKE_DATE + " DESC LIMIT 7";
    String DATE_FORMAT_DB_PATTERN = "yyyyMMdd";
}
