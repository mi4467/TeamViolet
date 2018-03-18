package com.hfad.mytimetracker;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Arrays.*;

import static com.amitshekhar.utils.Constants.NULL;


/**
 * A simple {@link Fragment} subclass.
 */
public class TaskCreatorFragment extends Fragment implements  View.OnClickListener {
    private Integer color;                  //use for first card view
    private String categoryName;            //use for first cardView



    private static Integer year;                   //use for second cardView
    private static Integer month;
    private static Integer date;

    private Integer startYear;              //use for third cardview
    private Integer startDate;
    private Integer startMonth;
    private Integer endYear;
    private Integer endMonth;
    private Integer startDay;
    private Integer endDay;
    private int[] days;                      //use for third cardview

    private static String taskName;                 //use for second and third cardview
    private static String taskCategoryName;         //use for second and third cardView
    private static Integer startMinute;
    private static Integer startHour;
    private static Integer endMinute;
    private static Integer endHour;


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
        Button pickColor = (Button) layout.findViewById(R.id.color_button);                             //for first card view
        Button submitCat = (Button) layout.findViewById(R.id.submit_cat);

        Button pickStartTime = (Button) layout.findViewById(R.id.start_time_reocc_picker);              //for the third card view
        Button pickEndTime = (Button) layout.findViewById(R.id.end_time_reocc_picker);
        Button pickDate = (Button) layout.findViewById(R.id.date_picker);
        Button submitReocc = (Button) layout.findViewById(R.id.submit_reocc);

        Button pickStartTimeTask = (Button) layout.findViewById(R.id.start_time_taskadder);             //for the second card view
        Button pickEndTimeTask = (Button) layout.findViewById(R.id.end_time_taskadder);
        Button pickDateTask = (Button) layout.findViewById(R.id.add_task_date_picker);
        Button submitTask = (Button) layout.findViewById(R.id.submit_task);



       // txt = (TextView) layout.findViewById(R.id.colorbox);
       // txt.setBackgroundColor(Color.BLUE);
        // Log.d("time picker test", pickTime.hasOnClickListeners() +" it entered before creating the  listener");

        pickColor.setOnClickListener(this);                                                             //add listeners
        submitCat.setOnClickListener(this);

        pickStartTimeTask.setOnClickListener(this);
        pickEndTimeTask.setOnClickListener(this);
        pickDateTask.setOnClickListener(this);
        submitTask.setOnClickListener(this);

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
                        TextView yolo = (TextView) getActivity().findViewById(R.id.colorbox);
                         Log.d("ColorTest", "Color selected is: " + selectedColor + "");
                        //txt.getBackground().setColorFilter(selectedColor, PorterDuff.Mode.SRC_ATOP);
                        color = selectedColor;
                        yolo.setBackgroundColor(selectedColor);
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
                DatePickerFragment.flag=0;
                showDatePickerDialog(view);
                break;
            case R.id.start_time_taskadder:
                TimePickerFragment.flag=0;
                showTimePickerDialog(view);
                break;
            case R.id.end_time_taskadder:
                TimePickerFragment.flag=1;
                showTimePickerDialog(view);
                break;
            case R.id.submit_task:
                enterTaskInDB();
                break;
            case R.id.submit_cat:
                enterCatInDB();
                break;
            case R.id.submit_reocc:
                enterReoccTasksInDB();
                break;

        }


        //showTimePickerDialog(view);
    }

    private void enterReoccTasksInDB() {

    }

    private void enterCatInDB() {
        TimeTrackerDataBaseHelper categoryHelper = new TimeTrackerDataBaseHelper(getContext());
        SQLiteDatabase read = categoryHelper.getReadableDatabase();
        SQLiteDatabase write = categoryHelper.getWritableDatabase();
        EditText txt = (EditText) getActivity().findViewById(R.id.cat_name);

        String cat = txt.getText().toString();      //paramaters
        Integer color = this.color;
        if(color==null){
            Log.d("CategorySQL", "No Color");
            return;
        }
        Cursor check = read.query("TASK_CATEGORY_INFO", new String[] {"CATEGORY_NAME"}, null, null, null, null, null);
        boolean exists = false;
        check.moveToFirst();
        for(int i =0; i<check.getCount(); i++){
            String name = check.getString(0);
            Log.d("CategorySQL", name);
            if(name.equalsIgnoreCase(cat)){
                //break and send toast or some shit
                Log.d("CategorySQL", "It was in DB already");
                exists = true;
                break;
            }
            check.moveToNext();
        }
        if(exists){
            return;
        }
        else{
            write.execSQL("ALTER TABLE TASK_STATS ADD COLUMN " + cat.toUpperCase() + " BOOLEAN;");
            ContentValues entry = new ContentValues();
            entry.put("CATEGORY_NAME", cat);
            entry.put("COLOR", color);
            write.insert("TASK_CATEGORY_INFO", null, entry);
            Log.d("CategorySQL", "We put in DB");

            Cursor c = null;

            try{
                c = read.query("TASK_CATEGORY_INFO", null, null, null, null, null, null);
                Log.d("TableTest", "Table exists");
                Log.d("TableTest", read.getPath());
            }
            catch(Exception e ){
                Log.d("TableTest", " shit is broke");
            }
        }
    }

    private void enterTaskInDB() {
        String dueDate = TaskCreatorFragment.year + "-" + TaskCreatorFragment.month + "-" + TaskCreatorFragment.date;
        String startTime = TaskCreatorFragment.startHour + "-" + TaskCreatorFragment.startMinute + "-00";
        String endTime = TaskCreatorFragment.endHour + "-" + TaskCreatorFragment.endMinute + "-00";
        TaskCreatorFragment.taskCategoryName = ((EditText) getActivity().findViewById(R.id.cat_name_task_adder)).getText().toString();
        TaskCreatorFragment.taskName = ((EditText) getActivity().findViewById(R.id.task_name_task_adder)).getText().toString();
        TimeTrackerDataBaseHelper categoryHelper = new TimeTrackerDataBaseHelper(getContext());
        SQLiteDatabase write = categoryHelper.getWritableDatabase();
        ContentValues recordParamaters = new ContentValues();
        recordParamaters.put("TASK_NAME", TaskCreatorFragment.taskName);
        recordParamaters.put("TASK_CATEGORY", TaskCreatorFragment.taskCategoryName);
        recordParamaters.put("DUE_DATE", dueDate);
        recordParamaters.put("START_TIME", startTime);
        recordParamaters.put("END_TIME", endTime);
        //construct date value, start time value, end time value
        write.insert("TASK_INFORMATION", null, recordParamaters);
        Log.d("InsertTaskTest", "it worked");

    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        private static int flag;
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

        public void setFlag(int c){
            flag =c;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if(flag==0){
                TaskCreatorFragment.startMinute = minute;
                TaskCreatorFragment.startHour = hourOfDay;
            }
            if(flag==1){
                TaskCreatorFragment.endMinute = minute;
                TaskCreatorFragment.endHour = hourOfDay;

            }
            if(flag==2){

            }
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

        private static int flag;

        public void setFlag(int c){
            flag =c;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            if(flag==0){
                TaskCreatorFragment.year = year;
                TaskCreatorFragment.month = month;
                TaskCreatorFragment.date = day;
            }
            if(flag==1){

            }
            if(flag==3){

            }

        }
    }

}
