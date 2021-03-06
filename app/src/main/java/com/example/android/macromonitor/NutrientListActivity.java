package com.example.android.macromonitor;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.macromonitor.data.MacroContract;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import es.dmoral.toasty.Toasty;

/**
 * An activity representing a list of Nutrients. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link NutrientDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class NutrientListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,MacroConstants,UpdateNutrientDialogFragment.NoticeDialogListener {

    private static final String LOG_TAG = NutrientListActivity.class.getSimpleName();

    private static final int RC_SIGN_IN = 86;
    private String[] macro_pref_array = new String[4];
    private TextView textView_quadrant_one;
    private TextView textView_quadrant_two;
    private TextView textView_quadrant_three;
    private TextView textView_quadrant_four;
    private Button update_button_quadrant_one;
    private Button update_button_quadrant_two;
    private Button update_button_quadrant_three;
    private Button update_button_quadrant_four;

    public static final String[] MACRO_COLUMNS = {
            MacroContract.MacroEntry.TABLE_NAME + "." + MacroContract.MacroEntry._ID,
            MacroContract.MacroEntry.COLUMN_MACRO_NAME,
            MacroContract.MacroEntry.COLUMN_INTAKE_VALUE,
            MacroContract.MacroEntry.COLUMN_INTAKE_DATE
    };

    public static final int COL_MACRO_ENTRY_ID = 0;
    public static final int COL_MACRO_NAME = 1;
    public static final int COL_INTAKE_VALUE = 2;
    public static final int COL_INTAKE_DATE = 3;
    private String currentDate;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private AdView mAdView;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton signInButton;
    private SharedPreferences sharedPref;

    @Override
    protected void onStart(){
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null){
            signInButton.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrient_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        DateFormat df = new SimpleDateFormat(DATE_FORMAT_DB_PATTERN);
        currentDate = df.format(new Date());
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                }
            }

        });

        if (findViewById(R.id.nutrient_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
            .addTestDevice("9C6380BFD6954AC62ACF1EE1D64BA0A1")
            .build();
        mAdView.loadAd(adRequest);
        textView_quadrant_one = findViewById(R.id.quadrant_one_text);
        update_button_quadrant_one = findViewById(R.id.quadrant_one_update_button);

        textView_quadrant_two = findViewById(R.id.quadrant_two_text);
        update_button_quadrant_two = findViewById(R.id.quadrant_two_update_button);

        textView_quadrant_three = findViewById(R.id.quadrant_three_text);
        update_button_quadrant_three = findViewById(R.id.quadrant_three_update_button);

        textView_quadrant_four = findViewById(R.id.quadrant_four_text);
        update_button_quadrant_four = findViewById(R.id.quadrant_four_update_button);

        Bundle bundle = new Bundle();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> selections = sharedPref.getStringSet("macro_pref_list",null);

        macro_pref_array = selections.toArray(macro_pref_array);
        if(macro_pref_array != null) {
            for (int loaderId = 0; loaderId < 4; loaderId++) {
                bundle.putString(LOADER_ARG_BUNDLE_KEY_SELECTION, LOADER_DB_NAME_ARG + macro_pref_array[loaderId] + LOADER_ARG_CLOSE_QUOTE);
                getSupportLoaderManager().initLoader(loaderId, bundle, this);
            }
        }
    }

    private void initDBInserts(int loaderId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MacroContract.MacroEntry.COLUMN_MACRO_NAME, macro_pref_array[loaderId]);
        contentValues.put(MacroContract.MacroEntry.COLUMN_INTAKE_VALUE, 0);
        contentValues.put(MacroContract.MacroEntry.COLUMN_INTAKE_DATE, currentDate);
        getContentResolver().insert(MacroContract.MacroEntry.CONTENT_URI,contentValues);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            String[] displayName = account.getDisplayName().split("\\s+");

            // Signed in successfully, show authenticated UI.
            if(displayName[0] != null && !displayName[0].isEmpty()) {
                Toasty.success(this, getString(R.string.successful_signin) + displayName[0] + getString(R.string.exclamation), Toast.LENGTH_LONG).show();
            } else {
                Toasty.success(this, getString(R.string.welcome), Toast.LENGTH_LONG).show();
            }
            signInButton.setVisibility(View.GONE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("display_name_text", displayName[0]);
            editor.apply();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(LOG_TAG, "signInResult:failed code=" + e.getStatusCode());
            Toasty.error(this, getString(R.string.friendly_err_msg)).show();
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
        if(data == null ||  data.getCount() == 0){
            initDBInserts(loader.getId());
        }else{
            data.moveToFirst();
            do{
                if(!data.getString(COL_INTAKE_DATE).equalsIgnoreCase(currentDate)){
                    //Need to Insert a new record for today
                    initDBInserts(loader.getId());
                    continue;
                }
                updateUI(loader.getId(), data.getString(COL_MACRO_NAME), data.getInt(COL_INTAKE_VALUE));
            }while(data.moveToNext());
        }
    }

    private void updateUI(final int loaderId, String name, final int value) {
        switch (loaderId) {
            case 0: textView_quadrant_one.setText(getNutrientText(name, value));
                textView_quadrant_one.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mTwoPane) {
                            Bundle arguments = new Bundle();
                            arguments.putString(NutrientDetailFragment.ARG_MACRO_ID, macro_pref_array[loaderId]);
                            NutrientDetailFragment fragment = new NutrientDetailFragment();
                            fragment.setArguments(arguments);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.nutrient_detail_container, fragment)
                                    .commit();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), NutrientDetailActivity.class);
                            intent.putExtra(NutrientDetailFragment.ARG_MACRO_ID, macro_pref_array[loaderId]);
                            startActivity(intent);
                        }
                    }});
                update_button_quadrant_one.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UpdateNutrientDialogFragment.newInstance(loaderId).show(getSupportFragmentManager(), "Update Alert");
                    }});
                break;
            case 1: textView_quadrant_two.setText(getNutrientText(name, value));
                textView_quadrant_two.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mTwoPane) {
                            Bundle arguments = new Bundle();
                            arguments.putString(NutrientDetailFragment.ARG_MACRO_ID, macro_pref_array[loaderId]);
                            NutrientDetailFragment fragment = new NutrientDetailFragment();
                            fragment.setArguments(arguments);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.nutrient_detail_container, fragment)
                                    .commit();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), NutrientDetailActivity.class);
                            intent.putExtra(NutrientDetailFragment.ARG_MACRO_ID, macro_pref_array[loaderId]);
                            startActivity(intent);
                        }
                    }});
                update_button_quadrant_two.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UpdateNutrientDialogFragment.newInstance(loaderId).show(getSupportFragmentManager(), "Update Alert");
                    }
                });
                break;
            case 2: textView_quadrant_three.setText(getNutrientText(name, value));
                textView_quadrant_three.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mTwoPane) {
                            Bundle arguments = new Bundle();
                            arguments.putString(NutrientDetailFragment.ARG_MACRO_ID, macro_pref_array[loaderId]);
                            NutrientDetailFragment fragment = new NutrientDetailFragment();
                            fragment.setArguments(arguments);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.nutrient_detail_container, fragment)
                                    .commit();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), NutrientDetailActivity.class);
                            intent.putExtra(NutrientDetailFragment.ARG_MACRO_ID, macro_pref_array[loaderId]);
                            startActivity(intent);
                        }
                    }});
                update_button_quadrant_three.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UpdateNutrientDialogFragment.newInstance(loaderId).show(getSupportFragmentManager(), "Update Alert");
                    }
                });
                break;
            case 3: textView_quadrant_four.setText(getNutrientText(name, value));
                textView_quadrant_four.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mTwoPane) {
                            Bundle arguments = new Bundle();
                            arguments.putString(NutrientDetailFragment.ARG_MACRO_ID, macro_pref_array[loaderId]);
                            NutrientDetailFragment fragment = new NutrientDetailFragment();
                            fragment.setArguments(arguments);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.nutrient_detail_container, fragment)
                                    .commit();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), NutrientDetailActivity.class);
                            intent.putExtra(NutrientDetailFragment.ARG_MACRO_ID, macro_pref_array[loaderId]);
                            startActivity(intent);
                        }
                    }});
                update_button_quadrant_four.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UpdateNutrientDialogFragment.newInstance(loaderId).show(getSupportFragmentManager(), "Update Alert");
                    }
                });
                break;
        }

    }

    private String getNutrientText(String name, int value) {
        StringBuilder builder = new StringBuilder();
        String[] macroStringArray = getResources().getStringArray(R.array.pref_macro_list_titles);
        String[] macroDBStringArray = getResources().getStringArray(R.array.pref_macro_list_values);
        String[] goalStringArray = getResources().getStringArray(R.array.macro_goal_values);
        for(int i=0;i<macroDBStringArray.length;i++){
            if(macroDBStringArray[i].equalsIgnoreCase(name)){
                builder.append(macroStringArray[i]);
                builder.append(getString(R.string.colon_text));
                builder.append("\n");
                builder.append(value);
                builder.append(getString(R.string.of_text));
                builder.append(goalStringArray[i]);
            }
        }
        return builder.toString();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int value, int loaderId) {
        updateDBIntakeValue(loaderId, value);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }

    private void updateDBIntakeValue(int loaderId, int value){
        //Make an update call to DB
        ContentValues contentValues = new ContentValues();
        String whereClauseNameCondition;

        contentValues.put(MacroContract.MacroEntry.COLUMN_MACRO_NAME, macro_pref_array[loaderId]);
        whereClauseNameCondition = LOADER_DB_NAME_ARG + macro_pref_array[loaderId] + LOADER_ARG_CLOSE_QUOTE;

        contentValues.put(MacroContract.MacroEntry.COLUMN_INTAKE_VALUE, value);
        //Ideally the date set here will be today's date
        contentValues.put(MacroContract.MacroEntry.COLUMN_INTAKE_DATE, currentDate);
        //The 'macro_name' in the where clause should match the quadrant and the update value
        //The date here should also ideally be today's date
        getContentResolver().update(MacroContract.MacroEntry.CONTENT_URI,contentValues, whereClauseNameCondition + " AND intake_date = '" + currentDate + LOADER_ARG_CLOSE_QUOTE, null);
        //Update the Widget-
        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), MacroWidget.class));
        MacroWidget myWidget = new MacroWidget();
        myWidget.onUpdate(this, AppWidgetManager.getInstance(this),ids);
    }
}
