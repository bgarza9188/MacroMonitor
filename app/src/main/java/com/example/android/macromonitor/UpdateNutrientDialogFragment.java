package com.example.android.macromonitor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import es.dmoral.toasty.Toasty;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UpdateNutrientDialogFragment.NoticeDialogListener} interface
 * to handle interaction events.
 * Use the {@link UpdateNutrientDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateNutrientDialogFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int loaderId;

    private NoticeDialogListener mListener;

    public UpdateNutrientDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.fragment_update_nutrient_dialog, null);

        final NumberPicker numberPicker = rootView.findViewById(R.id.number_picker);
        //Populate NumberPicker values from minimum and maximum value range
        //Set the minimum value of NumberPicker
        numberPicker.setMinValue(1);
        //Specify the maximum value/number of NumberPicker
        numberPicker.setMaxValue(5000);

        //Gets whether the selector wheel wraps when reaching the min/max value.
        numberPicker.setWrapSelectorWheel(true);

        builder.setTitle("Update Nutrient Intake");

        builder.setMessage("Select a number:");

        builder.setView(rootView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toasty.info(getActivity(), "User OK value:" + numberPicker.getValue()).show();
                        mListener.onDialogPositiveClick(UpdateNutrientDialogFragment.this, numberPicker.getValue(), loaderId);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toasty.info(getActivity(), "User Cancelled").show();
                        mListener.onDialogNegativeClick(UpdateNutrientDialogFragment.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param loaderId Parameter 1.
     * @return A new instance of fragment UpdateNutrientDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpdateNutrientDialogFragment newInstance(int loaderId) {
        UpdateNutrientDialogFragment fragment = new UpdateNutrientDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, loaderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            loaderId = getArguments().getInt(ARG_PARAM1);
            Log.e("diag. args","ben, loaderId:" + loaderId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Returning null here since the layout gets inflated with the Alert Dialog
        return null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NoticeDialogListener ) {
            mListener = (NoticeDialogListener ) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface NoticeDialogListener  {
        public void onDialogPositiveClick(DialogFragment dialog, int value, int loaderId);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
}
