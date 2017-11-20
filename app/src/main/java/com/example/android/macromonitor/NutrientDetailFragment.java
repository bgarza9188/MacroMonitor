package com.example.android.macromonitor;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * A fragment representing a single Nutrient detail screen.
 * This fragment is either contained in a {@link NutrientListActivity}
 * in two-pane mode (on tablets) or a {@link NutrientDetailActivity}
 * on handsets.
 */
public class NutrientDetailFragment extends Fragment  implements MacroConstants{

    private XYPlot plot;
    private XYSeries series;
    private BarFormatter bf;
    private ResponseReceiver receiver;
    private IntentFilter intentFilter;

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_MACRO_ID = "macro_id";

    public static String MACRO_NAME;
    private static String macroDisplayName;

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
            MACRO_NAME = getArguments().getString(ARG_MACRO_ID);
            macroDisplayName = getMacroDisplayName(MACRO_NAME);
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(macroDisplayName);
            }
        }
        intentFilter = new IntentFilter(ResponseReceiver.ACTION_RESP);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        getActivity().registerReceiver(receiver, intentFilter);
    }

    private String getMacroDisplayName(String macroName) {
        String[] macroStringArray = getResources().getStringArray(R.array.pref_macro_list_titles);
        String[] macroDBStringArray = getResources().getStringArray(R.array.pref_macro_list_values);
        for(int i=0;i<macroDBStringArray.length;i++){
            if(macroDBStringArray[i].equalsIgnoreCase(macroName)){
                return macroStringArray[i];
            }
        }
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.nutrient_detail, container, false);
        plot = rootView.findViewById(R.id.bar_plot);

        //Week dates logics
        Calendar calender = Calendar.getInstance();
        int dayOfWeek = calender.get(Calendar.DAY_OF_WEEK);
        calender.add(Calendar.DATE,(-dayOfWeek+1));//Day of week numbers start at 1
        Date date = calender.getTime();
        DateFormat df = new SimpleDateFormat(DATE_FORMAT_DB_PATTERN);
        DateFormat readableDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String readableStartDate = readableDateFormat.format(date);
        String weekStartDate = df.format(date);

        Number[] intakeValues = new Number[7];
        MacroIntentService service = new MacroIntentService();
        service.startActionFetchWeek(getActivity(), MACRO_NAME, weekStartDate);

        // create a bar formatter with a red fill color and a black outline:
        bf = new BarFormatter(Color.RED, Color.BLACK);
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                // obj contains the raw Number value representing the position of the label being drawn.
                //The switch statement will convert the labels to Day names
                int i = Math.round(((Number) obj).floatValue());
                String dayOfWeek;
                switch (i){
                    case 0: dayOfWeek = getActivity().getString(R.string.sunday);
                        break;
                    case 1: dayOfWeek = getActivity().getString(R.string.monday);
                        break;
                    case 2: dayOfWeek = getActivity().getString(R.string.tuesday);
                        break;
                    case 3: dayOfWeek = getActivity().getString(R.string.wednesday);
                        break;
                    case 4: dayOfWeek = getActivity().getString(R.string.thursday);
                        break;
                    case 5: dayOfWeek = getActivity().getString(R.string.friday);
                        break;
                    case 6: dayOfWeek = getActivity().getString(R.string.saturady);
                        break;
                    default: dayOfWeek = getActivity().getString(R.string.day);
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
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(new DecimalFormat("0.0"));
        //Set Title
        plot.setTitle(macroDisplayName + getActivity().getString(R.string.intake));
        //Sets the values to the specified Androidplot series
        series = new SimpleXYSeries(  Arrays.asList(intakeValues),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, getActivity().getString(R.string.daily_intake));
        //Add series to the plot
        plot.addSeries(series, bf);
        //This will divide the domain values into 7 distinct sections(Mon-Sun)
        plot.setDomainStep(StepMode.SUBDIVIDE, 7);
        //Sets the upper and lower bondries for the domain values 0-6 for (Mon-Sun)
        plot.setDomainBoundaries(0,6, BoundaryMode.FIXED);
        //This allows the Range values to start at 0 and not change
        plot.setRangeLowerBoundary(0, BoundaryMode.FIXED);
        //attempt to make the range values make sense
        plot.setRangeStep(StepMode.SUBDIVIDE, 5);
        //Gives a fixed width to the bars that are drawn
        BarRenderer barRenderer = plot.getRenderer(BarRenderer.class);
        barRenderer.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_WIDTH, PixelUtils.dpToPix(10));

        // Show the content as text in a TextView.
        String descriptionText = getActivity().getString(R.string.week_starting_on) + readableStartDate;
        ((TextView) rootView.findViewById(R.id.nutrient_detail)).setText(descriptionText);

        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();
        //registering receiver
        getActivity().registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        //unregister receiver
        getActivity().unregisterReceiver(receiver);
    }

    public class ResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP =
                "com.example.android.macromonitor.DATA_PROCESSED";

        @Override
        public void onReceive(Context context, Intent intent) {
            plot.removeSeries(series);
            int[] responseData = intent.getIntArrayExtra(MacroIntentService.FETCHED_DATA);
            Number[] intakeValues = new Number[7];
            for(int i=0;i<7;i++){
                intakeValues[i] = responseData[i];
            }
            series = new SimpleXYSeries(  Arrays.asList(intakeValues),
                    SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, getActivity().getString(R.string.daily_intake));
            //Add series to the plot
            plot.addSeries(series, bf);
            plot.redraw();
        }
    }
}
