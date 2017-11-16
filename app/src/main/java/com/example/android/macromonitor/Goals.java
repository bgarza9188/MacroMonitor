package com.example.android.macromonitor;

import java.util.Collections;
import java.util.Map;

/**
 * Created by Ben on 11/15/2017.
 */
public class Goals implements MacroConstants{
    private static final Map<String, String> goalMacroMap;
    static {
        Map<String, String> aMap = null;
        aMap.put(MACRO_FAT_DB_NAME, "32 grams");
        aMap.put(MACRO_PROTEIN_DB_NAME, "12 grams");
        aMap.put(MACRO_WATER_DB_NAME,"64oz");
        aMap.put(MACRO_SUGAR_DB_NAME,"28 grams");
        aMap.put(MACRO_CALORIES_DB_NAME, "2000 calories");
        aMap.put(MACRO_SODIUM_DB_NAME, "45 milligrams");
        aMap.put(MACRO_CARB_DB_NAME, "79 grams");
        goalMacroMap = Collections.unmodifiableMap(aMap);
    }
}
