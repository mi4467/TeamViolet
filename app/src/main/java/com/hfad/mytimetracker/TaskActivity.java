package com.hfad.mytimetracker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.RectF;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.framgia.library.calendardayview.CalendarDayView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import junit.framework.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static java.security.AccessController.getContext;


public class TaskActivity extends AppCompatActivity implements View.OnClickListener{
    private Integer taskID = 0;
    private Cursor stats = null;
    private Cursor info = null;

    private static Integer year;                   //use for dueDate in third cardview
    private static Integer month;
    private static Integer date;
    private static Integer startMinute=null;
    private static Integer startHour=null;
    private static Integer endMinute=null;
    private static Integer endHour=null;


    private static CharSequence[] addCategoryNames;     //use for add/remove cats
    private static CharSequence[] removeCategoryNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        Intent intent = getIntent();
        taskID = intent.getIntExtra("TASK_ID", -1);
        Log.d("TaskDebug", "Task id is: " + taskID);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        info = SQLfunctionHelper.getTaskInfo(this, taskID);
        stats = SQLfunctionHelper.getTaskStats(this, taskID);
        initTimeInformation();
        initOrgInformation();
        initStats();
        initListeners();


        // Get a reference for the week view in the layout.
//        WeekView mWeekView = (WeekView) findViewById(R.id.weekView);
//
//// Set an action when any event is clicked.
//        mWeekView.setOnEventClickListener(this);
//
//// The week view has infinite scrolling horizontally. We have to provide the events of a
//// month every time the month changes on the week view.
//        mWeekView.setMonthChangeListener(this);
//
//// Set long press listener for events.
//        mWeekView.setEventLongPressListener(this);
//

//        MonthLoader.MonthChangeListener mMonthChangeListener = new MonthLoader.MonthChangeListener() {
//            @Override
//            public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
//                // Populate the week view with some events.
//                List<WeekViewEvent> events = getEvents(newYear, newMonth);
//                return events;
//            }
//        };


    }

    private boolean verifyOnTime(int id){
        SQLiteDatabase readableDatabase = TimeTrackerDataBaseHelper.getInstance(this).getReadableDatabase();
        Cursor check = readableDatabase.rawQuery("SELECT * FROM TASK_INFORMATION WHERE _ID = " +id, null);
        check.moveToFirst();
        String[] dueDate = check.getString(2).split("-");
        int dueYear = Integer.parseInt(dueDate[0]);
        int dueDay = Integer.parseInt(dueDate[2]);
        int dueMonth = Integer.parseInt(dueDate[1]);
        String[] dueTime = check.getString(4).split("-");
        int dueHour = Integer.parseInt(dueTime[0]);
        int dueMin = Integer.parseInt(dueTime[1]);
        Log.d("MarkCompleteDebug", Arrays.toString(dueDate));
        Log.d("MarkCompleteDebug", Arrays.toString(dueTime));
        Calendar today = Calendar.getInstance();
        int current_hour = today.get(Calendar.HOUR);
        int current_minute = today.get(Calendar.MINUTE);
        int cday = today.get(Calendar.DAY_OF_MONTH);
        int cmonth = today.get(Calendar.MONTH)+1;
        int cyear = today.get(Calendar.YEAR);
        Log.d("MarkCompleteDebug" , "Year: " + cyear + " Month: " + cmonth + " Day: " + cday);
        Log.d("MarkCompleteDebug" , " DUE DATE IS: Year: " + dueYear + " Month: " + dueMonth + " Day: " + dueDay);
        Log.d("MarkCompleteDebug", ((dueYear==cyear))  +"");
        Log.d("MarkCompleteDebug", ((dueMonth==cmonth))  +"");
        Log.d("MarkCompleteDebug", ((dueDay<cday))  +"");


        Log.d("MarkCompleteDebug", ((dueYear==cyear && dueMonth==cmonth && dueDay<cday))  +"");
        if(dueYear<cyear ||  (dueYear==cyear && dueMonth<cmonth) || (dueYear==cyear && dueMonth==cmonth && dueDay<cday) ){
            return false;
        }
        else{
            if(dueDay>cday){
                return true;
            }
            if(current_hour<dueHour || (current_hour==dueHour && current_minute<dueMin)){
                //Toast.makeText(getActivity(), "This Time is Invalid! Make End Time After Start Time", Toast.LENGTH_SHORT).show();
                return true;
            }
            else{
                return false;
            }
        }
    }

    private void initListeners(){
        Button addCat = findViewById(R.id.task_act_addCat);
        addCat.setOnClickListener(this);
        Button removeCat = findViewById(R.id.task_act_removeCat);
        removeCat.setOnClickListener(this);

        Button markComp = findViewById(R.id.task_act_mark_complete);
        markComp.setOnClickListener(this);
        Button delete = findViewById(R.id.task_act_delete);
        delete.setOnClickListener(this);
        ToggleButton notification = findViewById(R.id.task_act_notification_toggle);
        notification.setOnClickListener(this);

        Button startTime = findViewById(R.id.task_act_start_time);
        startTime.setOnClickListener(this);
        Button endTime = findViewById(R.id.task_act_end_time);
        endTime.setOnClickListener(this);
        Button dueDate = findViewById(R.id.task_act_due_date);
        dueDate.setOnClickListener(this);
        Button enter = findViewById(R.id.task_act_date_enter);
        enter.setOnClickListener(this);

    }

    private void initOrgInformation(){
        info.moveToFirst();
        stats.moveToFirst();
        String name = info.getString(1);
        TextView taskName = findViewById(R.id.name_record);
        taskName.setText(taskName.getText() + name);

        String categories = "\n";
        for(int i =9; i<stats.getColumnCount(); i++){
            //UPDATE THE ONTIME AND COMPLETED FOR THAT CATEGORY
            Log.d("TaskActivityDebug", "Column " + i + " is: " + stats.getColumnName(i));
            if(stats.getInt(i)==1){
                categories +="\n\t\t\t" + "\u2022 " + stats.getColumnName(i) + " \n";
            }
        }
        categories = categories.substring(0, categories.length()-2);
        Log.d("TaskActivityDebug", "Column List is: " + categories);

        TextView catRecord = findViewById(R.id.category_record);
        catRecord.setText(catRecord.getText() + categories);
    }

    private void initTimeInformation(){
        info.moveToFirst();
        String[] dateRep = info.getString(2).split("-");
        String date = dateRep[1] + "/" + dateRep[2] + "/" + dateRep[0];
        Log.d("TaskActivityDebug", DatabaseUtils.dumpCursorToString(info));
        TextView dueDate = findViewById(R.id.due_date_record);
        dueDate.setText(dueDate.getText() + date);
        String[] startTimeRep = info.getString(3).split("-");
        String startTime = "";
        if(Integer.parseInt(startTimeRep[0])%12==0){
            startTime += 12 + ":";
        }
        else {
            startTime += Integer.parseInt(startTimeRep[0]) % 12 + ":";
        }
        startTime += Integer.parseInt(startTimeRep[1]) + " ";
        if(Integer.parseInt(startTimeRep[0])<12){
            startTime += "AM";
        }
        else{
            startTime += "PM";
        }
        TextView startTimeView = findViewById(R.id.start_time_record);
        startTimeView.setText(startTimeView.getText() + startTime);

        String[] endTimeRep = info.getString(4).split("-");
        String endTime = "";
        if(Integer.parseInt(endTimeRep[0])%12==0){
            endTime += 12 + ":";
        }
        else {
            endTime += Integer.parseInt(endTimeRep[0]) % 12 + ":";
        }
        endTime += Integer.parseInt(endTimeRep[1]) + " ";
        if(Integer.parseInt(endTimeRep[0])<12){
            endTime += "AM";
        }
        else{
            endTime += "PM";
        }
        TextView endTimeView = findViewById(R.id.endt_time_record);
        endTimeView.setText(endTimeView.getText() + endTime);

        //the above is for the time info
    }

    private void initStats(){
        stats.moveToFirst();

        int completed = stats.getInt(4);
        int onTime = stats.getInt(8);

        TextView completedView = findViewById(R.id.completed_record);
        TextView notCompletedView = findViewById(R.id.incomplete_record);
        TextView onTimeView = findViewById(R.id.onTime_record);
        TextView lateView = findViewById(R.id.late_record);

        if(completed==1){
            completedView.setText(completedView.getText() + "Yes");
            notCompletedView.setText(notCompletedView.getText() + "No");
        }
        else{
            completedView.setText(completedView.getText() + "No");
            notCompletedView.setText(notCompletedView.getText() + "Yes");
        }
        if(onTime==1){
            onTimeView.setText(onTimeView.getText() + "Yes");
            lateView.setText(lateView.getText() + "No");
        }
        else{
            onTimeView.setText(onTimeView.getText() + "No");
            lateView.setText(lateView.getText() + "Yes");
        }
    }


    public void showAddCategorySelectionDialog(){
        new MaterialDialog.Builder(this)
                .title("Specify Categories to Add")
                .items(SQLfunctionHelper.getCategoryList(this))
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        addCategoryNames =  text;
                        return true;
                    }
                })
                .positiveText("confirm")
                .negativeText("cancel")
                .show();
    }

    public void showRemoveCatSelection(){
        new MaterialDialog.Builder(this)
                .title("Specify Categories to Remove")
                .items(SQLfunctionHelper.getCategoryList(this))
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        removeCategoryNames = text;
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
            case R.id.task_act_addCat:
                showAddCategorySelectionDialog();
                Log.d("TaskListenerDebug", "AddCategory Was Clicked");
                break;
            case R.id.task_act_removeCat:
                showRemoveCatSelection();
                Log.d("TaskListenerDebug", "RemoveCategory Was Clicked");
                break;
            case R.id.task_act_mark_complete:
                markTaskComplete();
                Log.d("TaskListenerDebug", "MarkComplete Was Clicked");
                break;
            case R.id.task_act_delete:
                deleteTask();
                Log.d("TaskListenerDebug", "Delete Was Clicked");
                break;
            case R.id.task_act_notification_toggle:
                adjustNotification(view);
                Log.d("TaskListenerDebug", "NOtificationToggle Was Clicked");
                break;
            case R.id.task_act_start_time:
                TaskActivity.TimePickerFragment.flag=0;
                showTimePickerDialog(view);
                Log.d("TaskListenerDebug", "Start Time Was Clicked");
                break;
            case R.id.task_act_end_time:
                TaskActivity.TimePickerFragment.flag=1;
                showTimePickerDialog(view);
                Log.d("TaskListenerDebug", "End Time Was Clicked");
                break;
            case R.id.task_act_due_date:
                showDatePickerDialog(view);
                Log.d("TaskListenerDebug", "Due Date Was Clicked");
                break;
            case R.id.task_act_date_enter:
                //changeTaskDate();
                Log.d("TaskListenerDebug", "Enter Was Clicked");
                break;
        }
    }



//    public void changeTaskDate(){           //not finished
//        //Log.d("TaskActivityDebug", "What We Entered: Year = " + year + " Month =  ")
//        //this is where we check all of the information, and if valid, change the properties, and the respective fields
//        String dueDate = TaskCreatorFragment.constructDateStr(year, month, date);
//        String startTime = startHour + "-" + startMinute + "-00";
//        String endTime = endHour + "-" + endMinute + "-00";
//        //TaskCreatorFragment.categoryName = ((EditText) getActivity().findViewById(R.id.cat_name_task_adder)).getText().toString().toUpperCase();
//        //TaskCreatorFragment.taskName = ((EditText) getActivity().findViewById(R.id.task_name_task_adder)).getText().toString();
//        if(!checkValidityOfTask()){
//            return;
//        }
//        TimeTrackerDataBaseHelper categoryHelper = new TimeTrackerDataBaseHelper(getContext());
//        SQLiteDatabase write = categoryHelper.getWritableDatabase();
//        SQLiteDatabase read = categoryHelper.getReadableDatabase();
//        SQLfunctionHelper.enterTaskInDB(this, taskName, dueDate, startTime, endTime, taskCategoryNames);
//    }

    public void markTaskComplete(){
        TimeTrackerDataBaseHelper helper = TimeTrackerDataBaseHelper.getInstance(this);
        SQLfunctionHelper.markComplete(taskID, helper.getReadableDatabase(), helper.getWritableDatabase(), verifyOnTime(taskID));
        return;
    }

    public void deleteTask(){                               //test
        TimeTrackerDataBaseHelper helper = TimeTrackerDataBaseHelper.getInstance(this);
        SQLfunctionHelper.deleteTask(taskID, this);
        return;
    }

    public void adjustNotification(View button){
        ToggleButton not = (ToggleButton) button;
        boolean param = false;
        if(not.getText().equals("Notification Off")){
            param = false;
        }
        else{
            param = true;
        }
        SQLfunctionHelper.changeNotification(this, taskID, param);

    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TaskCreatorFragment.TimePickerFragment();
        Log.d("time picker test", "it entered the showTimePickerDialog");
        newFragment.show(getSupportFragmentManager(), "timePicker");
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
                TaskActivity.startMinute = minute;
                TaskActivity.startHour = hourOfDay;
            }
            if(flag==1){
                TaskActivity.endMinute = minute;
                TaskActivity.endHour = hourOfDay;
            }
        }
    }



    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new TaskCreatorFragment.DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
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
            TaskActivity.year = year;
            TaskActivity.month = month;
            TaskActivity.date = day;
        }
    }
}
