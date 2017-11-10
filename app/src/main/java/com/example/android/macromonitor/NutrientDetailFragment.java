package com.example.android.macromonitor;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.example.android.macromonitor.data.MacroContract;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static com.example.android.macromonitor.NutrientListActivity.COL_INTAKE_DATE;
import static com.example.android.macromonitor.NutrientListActivity.COL_INTAKE_VALUE;

/**
 * A fragment representing a single Nutrient detail screen.
 * This fragment is either contained in a {@link NutrientListActivity}
 * in two-pane mode (on tablets) or a {@link NutrientDetailActivity}
 * on handsets.
 */
public class NutrientDetailFragment extends Fragment  implements MacroConstants{

    private XYPlot plot;

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_MACRO_ID = "macro_id";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NutrientDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_MACRO_ID)) {
            // Load the content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            //getArguments().getString(ARG_MACRO_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle("TITLE");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.nutrient_detail, container, false);
        plot = rootView.findViewById(R.id.bar_plot);

        // create a bar formatter with a red fill color and a black outline:
        BarFormatter bf = new BarFormatter(Color.RED, Color.BLACK);
        XYSeries series;
        Cursor cursor = getActivity().getContentResolver().query(MacroContract.MacroEntry.CONTENT_URI,
                NutrientListActivity.MACRO_COLUMNS,
                LOADER_DB_NAME_ARG + MACRO_WATER_DB_NAME + LOADER_ARG_CLOSE_QUOTE,
                null,
                SORT_ORDER_LATEST_SEVEN_RECORDS);
        if(cursor != null) {
            Log.e("ben", "cursor not null!");
            cursor.moveToFirst();
            do{
                Log.e("ben", " cursor date:" + cursor.getString(COL_INTAKE_DATE) + " cursor Value:" + cursor.getInt(COL_INTAKE_VALUE));
            }while(cursor.moveToNext());
        } else {
            Log.e("ben","cursor was null :/");
        }

        Calendar calender = Calendar.getInstance();
        calender.add(Calendar.DATE,-7);
        Date date = calender.getTime();
        Log.e("ben","Current WEEK OF YEAR:" + calender.get(Calendar.WEEK_OF_YEAR));
        Log.e("ben", "Current DAY OF WEEK:" + calender.get(Calendar.DAY_OF_WEEK));
        Log.e("ben", "Current Day plus one:" + date.toString());
        Number[] intakeValues = {2, 4, 25, 20, 17, 4, 9};
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                // obj contains the raw Number value representing the position of the label being drawn.
                //The switch statement will convert the labels to Day names
                int i = Math.round(((Number) obj).floatValue());
                String dayOfWeek;
                switch (i){
                    case 0: dayOfWeek = "Sun.";
                        break;
                    case 1: dayOfWeek = "Mon.";
                        break;
                    case 2: dayOfWeek = "Tue.";
                        break;
                    case 3: dayOfWeek = "Wed.";
                        break;
                    case 4: dayOfWeek = "Thu.";
                        break;
                    case 5: dayOfWeek = "Fri.";
                        break;
                    case 6: dayOfWeek = "Sat.";
                        break;
                    default: dayOfWeek = "Day";
                        break;
                }
                return toAppendTo.append(dayOfWeek);
            }

            @Override
            public Object parseObject(String s, @NonNull ParsePosition parsePosition) {
                return null;
            }
        });
        //Displays the Range values as whole numbers instead of decimals
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(new DecimalFormat("0"));
        //Sets the values to the specified Androidplot series
        series = new SimpleXYSeries(  Arrays.asList(intakeValues),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Daily Intake");
        //Add series to the plot
        plot.addSeries(series, bf);
        //This will divide the domain values into 7 distinct sections(Mon-Sun)
        plot.setDomainStep(StepMode.SUBDIVIDE, 7);
        //Sets the upper and lower bondries for the domain values 0-6 for (Mon-Sun)
        plot.setDomainBoundaries(0,6, BoundaryMode.FIXED);
        //This allows the Range values to start at 0 and not change
        plot.setRangeLowerBoundary(0, BoundaryMode.FIXED);
        //Gives a fixed width to the bars that are drawn
        BarRenderer barRenderer = plot.getRenderer(BarRenderer.class);
        barRenderer.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_WIDTH, PixelUtils.dpToPix(10));
        // Show the content as text in a TextView.
        ((TextView) rootView.findViewById(R.id.nutrient_detail)).setText("DETAILS");


        return rootView;
    }
}
