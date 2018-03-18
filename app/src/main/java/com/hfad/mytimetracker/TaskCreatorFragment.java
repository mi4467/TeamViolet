package com.hfad.mytimetracker;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Arrays.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class TaskCreatorFragment extends Fragment implements  View.OnClickListener {
    private Integer color;
    public TextView txt;

    public TaskCreatorFragment() {
        // Required empty public constructor
    }



    //@Override
    //public void onCreate(Bundle savedInstanceState){

    //}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_task_creator, container, false);
//        BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigation);
//        bottomNavigationView.getMenu().getItem(4).setChecked(true);
        Button pickColor = (Button) layout.findViewById(R.id.color_button);
        Button submitCat = (Button) layout.findViewById(R.id.submit_cat);

        Button pickStartTime = (Button) layout.findViewById(R.id.start_time_reocc_picker);              //for the third card view
        Button pickEndTime = (Button) layout.findViewById(R.id.end_time_reocc_picker);
        Button pickDate = (Button) layout.findViewById(R.id.date_picker);
        Button submitReocc = (Button) layout.findViewById(R.id.submit_reocc);

        Button pickStartTimeTask = (Button) layout.findViewById(R.id.start_time_taskadder);
        Button pickEndTimeTask = (Button) layout.findViewById(R.id.end_time_taskadder);
        Button pickDateTask = (Button) layout.findViewById(R.id.add_task_date_picker);



         txt = (TextView) layout.findViewById(R.id.colorbox);
       // txt.setBackgroundColor(Color.BLUE);
        //Log.d("time picker test", pickTime.hasOnClickListeners() +" it entered before creating the  listener");

        pickColor.setOnClickListener(this);
        submitCat.setOnClickListener(this);

        pickStartTimeTask.setOnClickListener(this);
        pickEndTimeTask.setOnClickListener(this);
        pickDateTask.setOnClickListener(this);

        pickStartTime.setOnClickListener(this);
        pickEndTime.setOnClickListener(this);
        pickDate.setOnClickListener(this);
        submitReocc.setOnClickListener(this);
        return layout;
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        Log.d("time picker test", "it entered the showTimePickerDialog");
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void showColorWheelDialog(View v) {
        ColorPickerDialogBuilder
                .with(getContext())
                .setTitle("Choose color")
                .initialColor(0xffffffff)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        // toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        //EditText yolo = (EditText) getActivity().findViewById(R.id.colorbox);
                         Log.d("ColorTest", "Color selected is: " + selectedColor + "");
                        //txt.getBackground().setColorFilter(selectedColor, PorterDuff.Mode.SRC_ATOP);
                        txt.setBackgroundColor(selectedColor);
                       // txt.setBackgroundColor(Color.parseColor(allColors[selectedColor]+ ""));
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    public void showDaysSelectionDialog(){
        new MaterialDialog.Builder(getContext())
                .title(R.string.days_dialog_title)
                .items(R.array.days_list)
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {

                        Log.d("Days test",  Arrays.toString(which));
                        Log.d("Days test",  Arrays.toString(text));
                        return true;
                    }
                })
                .positiveText("confirm")
                .negativeText("cancel")
                .show();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.color_button:
                showColorWheelDialog(view);
                break;
            case R.id.start_time_reocc_picker:
                showTimePickerDialog(view);
                break;
            case R.id.end_time_reocc_picker:
                showTimePickerDialog(view);
                break;
            case R.id.date_picker:
                showDaysSelectionDialog();
                break;
            case R.id.add_task_date_picker:
                showDatePickerDialog(view);
                break;
            case R.id.start_time_taskadder:
                showTimePickerDialog(view);
                break;
            case R.id.end_time_taskadder:
                showTimePickerDialog(view);
                break;

        }


        //showTimePickerDialog(view);
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
        }
    }


    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
        }
    }

}
