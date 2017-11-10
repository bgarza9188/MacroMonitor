package com.example.android.macromonitor;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

    private final String LOG_TAG = NutrientListActivity.class.getSimpleName();

    private static final int RC_SIGN_IN = 86;
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
    private static final String TAG = NutrientListActivity.class.getSimpleName();
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private AdView mAdView;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onStart(){
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null){
            Toasty.info(this, "User is signed in !").show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrient_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                    // ...
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

//        View recyclerView = findViewById(R.id.nutrient_list);
//        assert recyclerView != null;
//        setupRecyclerView((RecyclerView) recyclerView);
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
            .addTestDevice("9C6380BFD6954AC62ACF1EE1D64BA0A1")
            .build();
        mAdView.loadAd(adRequest);
        textView_quadrant_one = findViewById(R.id.quadrant_one_text);
        button_quadrant_one = findViewById(R.id.quadrant_one_button);

        textView_quadrant_two = findViewById(R.id.quadrant_two_text);
        button_quadrant_two = findViewById(R.id.quadrant_two_button);

        textView_quadrant_three = findViewById(R.id.quadrant_three_text);
        button_quadrant_three = findViewById(R.id.quadrant_three_button);

        textView_quadrant_four = findViewById(R.id.quadrant_four_text);
        button_quadrant_four = findViewById(R.id.quadrant_four_button);



        //TODO need to call insert to insert new rows for each new day
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String currentDate = df.format(new Date());
        Log.e("Date",currentDate);
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
        ContentValues contentValues = new ContentValues();

        contentValues.put(MacroContract.MacroEntry.COLUMN_MACRO_NAME, MACRO_WATER_DB_NAME);
        contentValues.put(MacroContract.MacroEntry.COLUMN_INTAKE_VALUE, 0);
        contentValues.put(MacroContract.MacroEntry.COLUMN_INTAKE_DATE, "20171110");
        getContentResolver().insert(MacroContract.MacroEntry.CONTENT_URI,contentValues);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "Ben Inside 'onActivityResult', requestCode:" + requestCode);
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

            // Signed in successfully, show authenticated UI.
            Toasty.success(this, "Successful sign in!").show();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
            Toasty.error(this, "Api Exception :(").show();
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
        Log.e(LOG_TAG, "Ben in onLoadFinished, loader ID:" + loader.getId());
        if(data == null || (data != null && data.getCount() == 0)){
            Log.e(LOG_TAG, "Ben on results from loader");
        }else{
            Log.e(LOG_TAG, "Ben data getCount:" + data.getCount());
            data.moveToFirst();
            do{
                updateUI(loader.getId(), data.getString(COL_MACRO_NAME), data.getInt(COL_INTAKE_VALUE));
                Log.e(LOG_TAG, "Ben macro names:" + data.getString(COL_MACRO_NAME) + " record Date:" + data.getString(COL_INTAKE_DATE) + " record ID:" + data.getInt(COL_MACRO_ENTRY_ID) + " value:" + data.getInt(COL_INTAKE_VALUE));
            }while(data.moveToNext());
        }
    }

    private void updateUI(final int id, String name, final int value) {
        if(id == 1){
            textView_quadrant_one.setText("Name:" + name + ", value:" + value);
            button_quadrant_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    //Make an update call to DB
//                    ContentValues contentValues = new ContentValues();
//                    contentValues.put(MacroContract.MacroEntry.COLUMN_MACRO_NAME, MACRO_WATER_DB_NAME);
//                    contentValues.put(MacroContract.MacroEntry.COLUMN_INTAKE_VALUE, value+1);
//                    //Ideally the date set here will be today's date
//                    contentValues.put(MacroContract.MacroEntry.COLUMN_INTAKE_DATE, "20171108");
//                    //The 'macro_name' in the where clause should match the quadrant and the update value
//                    //The date here should also ideally be today's date
//                    getContentResolver().update(MacroContract.MacroEntry.CONTENT_URI,contentValues, "macro_name = 'WATER' AND intake_date = '20171108'", null);

                    UpdateNutrientDialogFragment fragment = new UpdateNutrientDialogFragment();
                    fragment.show(getSupportFragmentManager(), "Update Alert");

                    //TODO this will be for when the user wants to launch the detail screen
//                    if (mTwoPane) {
//                        Bundle arguments = new Bundle();
//                        arguments.putInt(NutrientDetailFragment.ARG_ITEM_ID, id);
//                        NutrientDetailFragment fragment = new NutrientDetailFragment();
//                        fragment.setArguments(arguments);
//                        getSupportFragmentManager().beginTransaction()
//                                .replace(R.id.nutrient_detail_container, fragment)
//                                .commit();
//                    } else {
//                        Intent intent = new Intent(getApplicationContext(), NutrientDetailActivity.class);
//                        intent.putExtra(NutrientDetailFragment.ARG_ITEM_ID, id);
//                        startActivity(intent);
//                    }
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

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int value) {
        Log.e(LOG_TAG,"ben in onDialogPositiveClick, value is...:" + value);

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Log.e(LOG_TAG,"ben in onDialogNegativeClick");
    }


//    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
//        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, DummyContent.ITEMS, mTwoPane));
//    }

//    public static class SimpleItemRecyclerViewAdapter
//            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
//
//        private final NutrientListActivity mParentActivity;
//        private final List<DummyContent.DummyItem> mValues;
//        private final boolean mTwoPane;
//        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();
//                if (mTwoPane) {
//                    Bundle arguments = new Bundle();
//                    arguments.putString(NutrientDetailFragment.ARG_ITEM_ID, item.id);
//                    NutrientDetailFragment fragment = new NutrientDetailFragment();
//                    fragment.setArguments(arguments);
//                    mParentActivity.getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.nutrient_detail_container, fragment)
//                            .commit();
//                } else {
//                    Context context = view.getContext();
//                    Intent intent = new Intent(context, NutrientDetailActivity.class);
//                    intent.putExtra(NutrientDetailFragment.ARG_ITEM_ID, item.id);
//
//                    context.startActivity(intent);
//                }
//            }
//        };
//
//        SimpleItemRecyclerViewAdapter(NutrientListActivity parent,
//                                      List<DummyContent.DummyItem> items,
//                                      boolean twoPane) {
//            mValues = items;
//            mParentActivity = parent;
//            mTwoPane = twoPane;
//        }
//
//        @Override
//        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.nutrient_list_content, parent, false);
//            return new ViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(final ViewHolder holder, int position) {
//            holder.mIdView.setText(mValues.get(position).id);
//            holder.mContentView.setText(mValues.get(position).content);
//
//            holder.itemView.setTag(mValues.get(position));
//            holder.itemView.setOnClickListener(mOnClickListener);
//        }
//
//        @Override
//        public int getItemCount() {
//            return mValues.size();
//        }
//
//        class ViewHolder extends RecyclerView.ViewHolder {
//            final TextView mIdView;
//            final TextView mContentView;
//
//            ViewHolder(View view) {
//                super(view);
//                mIdView = (TextView) view.findViewById(R.id.id_text);
//                mContentView = (TextView) view.findViewById(R.id.content);
//            }
//        }
//    }
}
