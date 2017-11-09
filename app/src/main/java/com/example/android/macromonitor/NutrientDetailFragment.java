package com.example.android.macromonitor;

import android.app.Activity;
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

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;

/**
 * A fragment representing a single Nutrient detail screen.
 * This fragment is either contained in a {@link NutrientListActivity}
 * in two-pane mode (on tablets) or a {@link NutrientDetailActivity}
 * on handsets.
 */
public class NutrientDetailFragment extends Fragment {

    private XYPlot plot;

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NutrientDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            //getArguments().getString(ARG_ITEM_ID));

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
        Number[] intakeValues = {2, 4, 25, 20, 17, 4, 9};
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                // obj contains the raw Number value representing the position of the label being drawn.
                //The switch statement will convert the labels to Day names
                int i = Math.round(((Number) obj).floatValue());
                String dayOfWeek;
                switch (i){
                    case 0: dayOfWeek = "Monday";
                        break;
                    case 1: dayOfWeek = "Tuesday";
                        break;
                    case 2: dayOfWeek = "Wednesday";
                        break;
                    case 3: dayOfWeek = "Thursday";
                        break;
                    case 4: dayOfWeek = "Friday";
                        break;
                    case 5: dayOfWeek = "Saturday";
                        break;
                    case 6: dayOfWeek = "Sunday";
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
