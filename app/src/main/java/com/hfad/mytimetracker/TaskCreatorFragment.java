package com.hfad.mytimetracker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;

import de.mateware.snacky.Snacky;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 *
 * NOTES FOR LATER:
 * Try and split up the sql writes and reads in a non conflicting way, and in turn make these synchronized methods
 * --Also eventually try and set up ASYNC tasks to further parralize and lighten the load on the main thread
 * All of these fields could possible be better kept in some sort of interface file, and we then have this fragment implement this interface
 * At the end, implement the state saving methods for the fragments and fields
 */
public class TaskCreatorFragment extends Fragment implements  View.OnClickListener {
    private static Integer color = null;                  //use for first card view
    private static String categoryName = null;            //use for first cardView

    private static Integer year;                   //use for second cardView
    private static Integer month;
    private static Integer date;

    private static Integer startYear=null;              //use for third cardview
    private static Integer startDate=null;
    private static Integer startMonth=null;
    private static Integer endYear=null;
    private static Integer endMonth=null;
    private static Integer endDay=null;
    private static Integer startMinuteReocc=null;         // for third cardview
    private static Integer startHourReocc=null;
    private static Integer endMinuteReocc=null;
    private static Integer endHourReocc=null;
    private static Integer[] days=null;

    private static CharSequence[] categoriesReocc = null;
    private static String  reoccTaskName = null;

    private static String taskName=null;                 //use for second
    private static CharSequence[] taskCategoryNames=null;
    private static Integer startMinute=null;
    private static Integer startHour=null;
    private static Integer endMinute=null;
    private static Integer endHour=null;


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
        Button pickColor = (Button) layout.findViewById(R.id.color_button);                             //for first card view
        Button submitCat = (Button) layout.findViewById(R.id.submit_cat);

        Button pickStartTime = (Button) layout.findViewById(R.id.start_time_reocc_picker);              //for the third card view
        Button pickEndTime = (Button) layout.findViewById(R.id.end_time_reocc_picker);
        Button pickDate = (Button) layout.findViewById(R.id.date_picker);
        Button submitReocc = (Button) layout.findViewById(R.id.submit_reocc);
        Button pickStartDate = (Button) layout.findViewById(R.id.start_date);
        Button pickEndDate = (Button) layout.findViewById(R.id.end_date);

        Button pickCatTask = (Button) layout.findViewById(R.id.pick_cat_task_adder);
        Button pickStartTimeTask = (Button) layout.findViewById(R.id.start_time_taskadder);             //for the second card view
        Button pickEndTimeTask = (Button) layout.findViewById(R.id.end_time_taskadder);
        Button pickDateTask = (Button) layout.findViewById(R.id.add_task_date_picker);
        Button pickOpions = (Button)   layout.findViewById(R.id.reocc_cat_list);
        Button submitTask = (Button) layout.findViewById(R.id.submit_task);

        pickColor.setOnClickListener(this);                                                             //add listeners
        submitCat.setOnClickListener(this);

        pickCatTask.setOnClickListener(this);
        pickStartTimeTask.setOnClickListener(this);
        pickEndTimeTask.setOnClickListener(this);
        pickDateTask.setOnClickListener(this);
        submitTask.setOnClickListener(this);

        pickStartTime.setOnClickListener(this);
        pickOpions.setOnClickListener(this);
        pickEndTime.setOnClickListener(this);
        pickDate.setOnClickListener(this);
        submitReocc.setOnClickListener(this);
        pickStartDate.setOnClickListener(this);
        pickEndDate.setOnClickListener(this);
        setUpToast();
        return layout;
    }

    public void setUpToast(){
        Toasty.Config.getInstance()
                .setErrorColor(Color.parseColor("#B71C1C"))
                .setSuccessColor(Color.parseColor("#1B5E20"))
                .setTextColor(Color.WHITE)
                .apply();
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
                        //TextView yolo = (TextView) getActivity().findViewById(R.id.colorbox);
                        color = selectedColor;
                        //yolo.setBackgroundColor(selectedColor);
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
                        TaskCreatorFragment.days = which;
                        return true;
                    }
                })
                .positiveText("confirm")
                .negativeText("cancel")
                .backgroundColor(Color.parseColor("#263238"))
                .show();
    }

    public void showCategorySelectionDialog(){
        new MaterialDialog.Builder(getContext())
                .title("Specify Categories")
                .items(getCategoryList())
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        TaskCreatorFragment.taskCategoryNames =  text;
                        return true;
                    }
                })
                .positiveText("confirm")
                .negativeText("cancel")
                .backgroundColor(Color.parseColor("#263238"))
                .show();
    }

    public void showReoccCatSelection(){
        new MaterialDialog.Builder(getContext())
                .title("Specify Categories")
                .items(getCategoryList())
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        TaskCreatorFragment.categoriesReocc = text;
                        return true;
                    }
                })
                .positiveText("confirm")
                .negativeText("cancel")
                .backgroundColor(Color.parseColor("#263238"))
                .show();
    }

    public String[] getCategoryList(){
        return SQLfunctionHelper.getCategoryList(getContext());
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.color_button:
                showColorWheelDialog(view);
                break;
            case R.id.start_time_reocc_picker:
                Log.d("ReoccTest", "was called");
                TimePickerFragment.flag=2;
                showTimePickerDialog(view);
                break;
            case R.id.end_time_reocc_picker:
                Log.d("ReoccTest", "was called as well");
                TimePickerFragment.flag=3;
                showTimePickerDialog(view);
                break;
            case R.id.date_picker:
                showDaysSelectionDialog();
                break;
            case R.id.pick_cat_task_adder:
                showCategorySelectionDialog();
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
            case R.id.start_date:
                DatePickerFragment.flag=1;
                showDatePickerDialog(view);
                break;
            case R.id.end_date:
                DatePickerFragment.flag=2;
                showDatePickerDialog(view);
                break;
            case R.id.reocc_cat_list:
                showReoccCatSelection();
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
    }

    private void enterReoccTasksInDB()  {
          TaskCreatorFragment.reoccTaskName = ((EditText) getActivity().findViewById(R.id.reocc_task_name)).getText().toString();
          if(!checkValidityOfReoccTask()){
              return;
          }
          SQLfunctionHelper.enterReoccTasksInDB(getContext(), startYear, startMonth, startDate, endYear, endMonth, endDay, startHourReocc, startMinuteReocc,
                  endHourReocc, endMinuteReocc, days, reoccTaskName, categoriesReocc);
        cleanUpCardViewThree();
        Snacky.builder().setActivity(getActivity())
                .setText(" Already Marked Complete!")
                .setDuration(Snacky.LENGTH_INDEFINITE)
                .setActionText(android.R.string.ok)
                .setBackgroundColor(Color.parseColor("#B71C1C"))
                .error().show();
    }

    public static String constructDateStr(int y, int m, int d){
        m = m+1;
        StringBuilder result = new StringBuilder();
        result.append(y+"-");
        if(m<10){
            result.append("0");
        }
        result.append(m +"-");
        if(d<10){
            result.append("0");
        }
        result.append(d + "");
        return new String(result);
    }

    private void enterCatInDB() {
        EditText txt = (EditText) getActivity().findViewById(R.id.cat_name);
        String cat = txt.getText().toString();      //paramaters
        TaskCreatorFragment.categoryName=cat;
        Integer color = this.color;
        if(TaskCreatorFragment.color==null){
            Log.d("CategorySQL", "No Color");
            Toasty.error(getContext(), "Pick A Color!", Toast.LENGTH_LONG, true).show();
            return;
        }
        if(TaskCreatorFragment.categoryName.equals("")){
            Log.d("CategorySQL", "this is cat" + cat + "!");
            Toasty.error(getContext(), "Give A Name!", Toast.LENGTH_LONG, true).show();
            return;
        }
        TimeTrackerDataBaseHelper categoryHelper = TimeTrackerDataBaseHelper.getInstance(getActivity());
        SQLiteDatabase read = categoryHelper.getReadableDatabase();
        SQLiteDatabase write = categoryHelper.getWritableDatabase();
        if(SQLfunctionHelper.enterCatInDB(getActivity(), categoryName, color)){
            TaskCreatorFragment.categoryName = null;
            TaskCreatorFragment.color = null;
            Toasty.success(getContext(), "Successfully Added!", Toast.LENGTH_LONG, true).show();
        }
        else{
            Toasty.error(getContext(), "Already Exists!", Toast.LENGTH_LONG, true).show();
            return;
        }
    }

    private void enterTaskInDB() {
        TaskCreatorFragment.taskName = ((EditText) getActivity().findViewById(R.id.task_name_task_adder)).getText().toString();
        if(!checkValidityOfTask()){
            return;
        }
        String dueDate = constructDateStr(TaskCreatorFragment.year, TaskCreatorFragment.month, TaskCreatorFragment.date);
        String startTime = TaskCreatorFragment.startHour + "-" + TaskCreatorFragment.startMinute + "-00";
        String endTime = TaskCreatorFragment.endHour + "-" + TaskCreatorFragment.endMinute + "-00";
        TimeTrackerDataBaseHelper categoryHelper = new TimeTrackerDataBaseHelper(getContext());
        SQLiteDatabase write = categoryHelper.getWritableDatabase();
        SQLiteDatabase read = categoryHelper.getReadableDatabase();
        SQLfunctionHelper.enterTaskInDB(getActivity(), taskName, dueDate, startTime, endTime, taskCategoryNames);
        cleanUpCardViewTwo();
    }

    private void cleanUpCardViewTwo(){
        TaskCreatorFragment.year=null;
        TaskCreatorFragment.month=null;
        TaskCreatorFragment.date=null;
        TaskCreatorFragment.startMinute=null;
        TaskCreatorFragment.startHour=null;
        TaskCreatorFragment.endMinute=null;
        TaskCreatorFragment.endHour=null;
        TaskCreatorFragment.taskCategoryNames=null;
        TaskCreatorFragment.taskName=null;
    }

    private void cleanUpCardViewThree(){
        TaskCreatorFragment.startYear=null;              //use for third cardview
        TaskCreatorFragment.startDate=null;
        TaskCreatorFragment.startMonth=null;
        TaskCreatorFragment.endYear=null;
        TaskCreatorFragment.endMonth=null;
        TaskCreatorFragment.endDay=null;
        TaskCreatorFragment.startMinuteReocc=null;         // for third cardview
        TaskCreatorFragment.startHourReocc=null;
        TaskCreatorFragment.endMinuteReocc=null;
        TaskCreatorFragment.endHourReocc=null;
        TaskCreatorFragment.days=null;
        TaskCreatorFragment.reoccTaskName=null;
        TaskCreatorFragment.categoriesReocc=null;
    }

    public boolean checkValidityOfReoccTask(){
      //  Log.d("ReoccTest", TaskCreatorFragment.startYear + " " + TaskCreatorFragment.startMonth + " " + TaskCreatorFragment.startDate);
      //  Log.d("ReoccTest", TaskCreatorFragment.endYear + " " + TaskCreatorFragment.endMonth +  " " + TaskCreatorFragment.endDay);
        if(TaskCreatorFragment.reoccTaskName == null || TaskCreatorFragment.reoccTaskName.equals("") ){                      //change
            Toasty.error(getContext(), "Make A Task!", Toast.LENGTH_LONG, true).show();
            return false;
        }
        if(TaskCreatorFragment.endYear==null){
            Toasty.error(getContext(), "Choose An End Date!", Toast.LENGTH_LONG, true).show();
            return false;
        }
        if(TaskCreatorFragment.startYear==null){
            Toasty.error(getContext(), "Choose A Start Date!", Toast.LENGTH_LONG, true).show();
            return false;
        }
        if(TaskCreatorFragment.startHourReocc==null){
            Toasty.error(getContext(), "Choose A Start Time!", Toast.LENGTH_LONG, true).show();
            return false;
        }
        if(TaskCreatorFragment.endHourReocc==null){
            Toasty.error(getContext(), "Choose An End Time!", Toast.LENGTH_LONG, true).show();
            return false;
        }
        if(TaskCreatorFragment.categoriesReocc==null){
            Toasty.error(getContext(), "Choose a Category!", Toast.LENGTH_LONG, true).show();
            return false;
        }
        if(TaskCreatorFragment.days==null){
            Toasty.error(getContext(), "Choose Days!", Toast.LENGTH_LONG, true).show();
            return false;
        }

        //Check the following three validity cases, if either start date or end date is before the current date, if end date is before start date, and if end time is less than start time(
        //All of them should be implemented, just test again
        Log.d("ReoccTest", TaskCreatorFragment.startHourReocc + " " + TaskCreatorFragment.startMinuteReocc);
        Log.d("ReoccTest", TaskCreatorFragment.endHourReocc + " " + TaskCreatorFragment.endMinuteReocc);
        if(TaskCreatorFragment.endHourReocc<TaskCreatorFragment.startHourReocc || (TaskCreatorFragment.endHourReocc==TaskCreatorFragment.startHourReocc && TaskCreatorFragment.endMinuteReocc<TaskCreatorFragment.startMinuteReocc)){
            Toasty.error(getContext(), "Invalid Times!", Toast.LENGTH_LONG, true).show();
            return false;
        }
        Calendar today = Calendar.getInstance();
        Integer cday = today.get(Calendar.DAY_OF_MONTH);
        Integer cmonth = today.get(Calendar.MONTH);
        Integer cyear = today.get(Calendar.YEAR);
        if(TaskCreatorFragment.startYear<cyear ||  (TaskCreatorFragment.startYear==cyear && TaskCreatorFragment.startMonth<cmonth) || (TaskCreatorFragment.startYear==cyear && TaskCreatorFragment.startMonth==cmonth && TaskCreatorFragment.startDate<cday) ){
            Toasty.error(getContext(), "Invalid Start Date!", Toast.LENGTH_LONG, true).show();
            return false;
        }
        if(TaskCreatorFragment.endYear<cyear ||  (TaskCreatorFragment.endYear==cyear && TaskCreatorFragment.endMonth<cmonth) || (TaskCreatorFragment.endYear==cyear && TaskCreatorFragment.endMonth==cmonth && TaskCreatorFragment.endDay<cday) ){
            Toasty.error(getContext(), "Invalid End Date!", Toast.LENGTH_LONG, true).show();
            return false;
        }
        if(TaskCreatorFragment.endYear<TaskCreatorFragment.startYear ||  (TaskCreatorFragment.endYear==TaskCreatorFragment.startYear && TaskCreatorFragment.endMonth<TaskCreatorFragment.startMonth) || (TaskCreatorFragment.endYear==TaskCreatorFragment.startYear && TaskCreatorFragment.endMonth==TaskCreatorFragment.startMonth && TaskCreatorFragment.endDay<TaskCreatorFragment.startDate) ){
            Toasty.error(getContext(), "Invalid End Date!", Toast.LENGTH_LONG, true).show();
            return false;
        }
        Toasty.success(getContext(), "Successfully Added!", Toast.LENGTH_LONG, true).show();
        return  true;
    }


    public boolean checkValidityOfTask(){
        if(TaskCreatorFragment.taskName.equals("")){
            Toasty.error(getContext(), "Make A Task!", Toast.LENGTH_LONG, true).show();
            return false;
        }
        if(TaskCreatorFragment.year==null){
            Toasty.error(getContext(), "Choose A Due Date!", Toast.LENGTH_LONG, true).show();
            return false;
        }
        if(TaskCreatorFragment.startHour==null){
            Toasty.error(getContext(), "Choose A Start Time!", Toast.LENGTH_LONG, true).show();
            return false;
        }
        if(TaskCreatorFragment.endHour==null){
            Toasty.error(getContext(), "Choose An End Time", Toast.LENGTH_LONG, true).show();
            return false;
        }
        if(TaskCreatorFragment.taskCategoryNames==null){
            Toasty.error(getContext(), "Choose Categories!", Toast.LENGTH_LONG, true).show();
            return false;
        }
        if(TaskCreatorFragment.endHour<TaskCreatorFragment.startHour || (TaskCreatorFragment.endHour==TaskCreatorFragment.startHour && TaskCreatorFragment.endMinute<TaskCreatorFragment.startMinute)){
            Toasty.error(getContext(), "Invalid End Time!", Toast.LENGTH_LONG, true).show();
            return false;
        }
        Calendar today = Calendar.getInstance();
        Integer cday = today.get(Calendar.DAY_OF_MONTH);
        Integer cmonth = today.get(Calendar.MONTH);
        Integer cyear = today.get(Calendar.YEAR);
        if(TaskCreatorFragment.year<cyear ||  (TaskCreatorFragment.year==cyear && TaskCreatorFragment.month<cmonth) || (TaskCreatorFragment.year==cyear && TaskCreatorFragment.month==cmonth && TaskCreatorFragment.date<cday) ){
            Toasty.error(getContext(), "Invalid Due Date!", Toast.LENGTH_LONG, true).show();
            return false;
        }
        Toasty.success(getContext(), "Successfully Added!", Toast.LENGTH_LONG, true).show();
        return true;
    }


    public static void setColor(Integer color) {
        TaskCreatorFragment.color = color;
    }

    public static void setCategoryName(String categoryName) {
        TaskCreatorFragment.categoryName = categoryName;
    }

    public static void setYear(Integer year) {
        TaskCreatorFragment.year = year;
    }

    public static void setMonth(Integer month) {
        TaskCreatorFragment.month = month;
    }

    public static void setDate(Integer date) {
        TaskCreatorFragment.date = date;
    }

    public static void setStartYear(Integer startYear) {
        TaskCreatorFragment.startYear = startYear;
    }

    public static void setStartDate(Integer startDate) {
        TaskCreatorFragment.startDate = startDate;
    }

    public static void setStartMonth(Integer startMonth) {
        TaskCreatorFragment.startMonth = startMonth;
    }

    public static void setEndYear(Integer endYear) {
        TaskCreatorFragment.endYear = endYear;
    }

    public static void setEndMonth(Integer endMonth) {
        TaskCreatorFragment.endMonth = endMonth;
    }

    public static void setEndDay(Integer endDay) {
        TaskCreatorFragment.endDay = endDay;
    }

    public static void setStartMinuteReocc(Integer startMinuteReocc) {
        TaskCreatorFragment.startMinuteReocc = startMinuteReocc;
    }

    public static void setStartHourReocc(Integer startHourReocc) {
        TaskCreatorFragment.startHourReocc = startHourReocc;
    }

    public static void setEndMinuteReocc(Integer endMinuteReocc) {
        TaskCreatorFragment.endMinuteReocc = endMinuteReocc;
    }

    public static void setEndHourReocc(Integer endHourReocc) {
        TaskCreatorFragment.endHourReocc = endHourReocc;
    }

    public static void setDays(Integer[] days) {
        TaskCreatorFragment.days = days;
    }

    public static void setCategoriesReocc(CharSequence[] categoriesReocc) {
        TaskCreatorFragment.categoriesReocc = categoriesReocc;
    }

    public static void setReoccTaskName(String reoccTaskName) {
        TaskCreatorFragment.reoccTaskName = reoccTaskName;
    }

    public static void setTaskName(String taskName) {
        TaskCreatorFragment.taskName = taskName;
    }

    public static void setTaskCategoryNames(CharSequence[] taskCategoryNames) {
        TaskCreatorFragment.taskCategoryNames = taskCategoryNames;
    }

    public static void setStartMinute(Integer startMinute) {
        TaskCreatorFragment.startMinute = startMinute;
    }

    public static void setStartHour(Integer startHour) {
        TaskCreatorFragment.startHour = startHour;
    }

    public static void setEndMinute(Integer endMinute) {
        TaskCreatorFragment.endMinute = endMinute;
    }

    public static void setEndHour(Integer endHour) {
        TaskCreatorFragment.endHour = endHour;
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

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Log.d("ReoccTest", flag + "");
            if(flag==0){
                TaskCreatorFragment.startMinute = minute;
                TaskCreatorFragment.startHour = hourOfDay;
            }
            if(flag==1){
                TaskCreatorFragment.endMinute = minute;
                TaskCreatorFragment.endHour = hourOfDay;
            }
            if(flag==2){
                TaskCreatorFragment.startHourReocc = hourOfDay;
                TaskCreatorFragment.startMinuteReocc = minute;
            }
            if(flag==3){
                TaskCreatorFragment.endHourReocc = hourOfDay;
                TaskCreatorFragment.endMinuteReocc = minute;
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

        public void onDateSet(DatePicker view, int year, int month, int day) {
            if(flag==0){
                TaskCreatorFragment.year = year;
                TaskCreatorFragment.month = month;
                TaskCreatorFragment.date = day;
            }
            if(flag==1){
                TaskCreatorFragment.startYear = year;
                TaskCreatorFragment.startMonth = month;
                TaskCreatorFragment.startDate = day;
            }
            if(flag==2){
                TaskCreatorFragment.endDay = day;
                TaskCreatorFragment.endMonth = month;
                TaskCreatorFragment.endYear = year;
            }

        }
    }

}
