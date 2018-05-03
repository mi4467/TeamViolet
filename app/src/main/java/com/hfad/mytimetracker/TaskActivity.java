package com.hfad.mytimetracker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.afollestad.materialdialogs.MaterialDialog;
import java.util.Calendar;
import es.dmoral.toasty.Toasty;

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

    private String dueDateText = "\nDue Date:   ";
    private String startTimeText = "\nStart Time: ";
    private String endTimeText = "\nEnd Time:   ";

    private String taskHeader = "\nTitle: ";
    private String catHeader = "\nTagged Categories: ";

    private String completeHeader = "\nCompleted:        ";
    private String incompleteHeader = "\nNot Completed: ";
    private String onTimeHeader = "\nOn Time: ";
    private String lateHeader = "\nLate:        ";

    Context currentActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        extractIntentInfo();
        initBars();
        initCursors();
        initTimeInformation();
        initOrgInformation();
        initStats();
        initListeners();
        setUpToast();
    }

    private void extractIntentInfo(){
        Intent intent = getIntent();
        taskID = intent.getIntExtra("TASK_ID", -1);
        Log.d("TaskDebug", "Task id is: " + taskID);
    }

    private void initBars(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Task Creator Center");
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setTitle("Task Center");
    }

    private void initCursors(){
        info = SQLfunctionHelper.getTaskInfo(this, taskID);
        stats = SQLfunctionHelper.getTaskStats(this, taskID);
    }

    private boolean verifyOnTime(int id){
        Cursor check = SQLfunctionHelper.queryWithString(this, "SELECT * FROM TASK_INFORMATION WHERE _ID = " +id);
        check.moveToFirst();
        String[] dueDate = check.getString(2).split("-");
        int dueYear = Integer.parseInt(dueDate[0]);
        int dueDay = Integer.parseInt(dueDate[2]);
        int dueMonth = Integer.parseInt(dueDate[1]);
        String[] dueTime = check.getString(5).split("-");
        int dueHour = Integer.parseInt(dueTime[0]);
        int dueMin = Integer.parseInt(dueTime[1]);
        Calendar today = Calendar.getInstance();
        int current_hour = today.get(Calendar.HOUR);
        int current_minute = today.get(Calendar.MINUTE);
        int cday = today.get(Calendar.DAY_OF_MONTH);
        int cmonth = today.get(Calendar.MONTH)+1;
        int cyear = today.get(Calendar.YEAR);
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

    private boolean nullCheck(){
        if(year==null){
            Toasty.error(this, "You didn't choose a date. Choose a date!", Toast.LENGTH_LONG, true).show();
            return false;
        }
        if(startHour==null){
            Toasty.error(this, "You didn't choose a date. Choose a date!", Toast.LENGTH_LONG, true).show();
            return false;
        }
        if(endHour==null){
            Toasty.error(this, "No Date Selected!", Toast.LENGTH_LONG, true).show();
            return false;
        }
        if(endHour<startHour || (endHour==startHour && endMinute<startMinute)){
            Toasty.error(this, "Invalid End Time!", Toast.LENGTH_LONG, true).show();
            return false;
        }
        return true;
    }

    private boolean checkValidityOfTask(){
        if(!nullCheck()){
            return false;
        }
        Calendar today = Calendar.getInstance();
        Integer cday = today.get(Calendar.DAY_OF_MONTH);
        Integer cmonth = today.get(Calendar.MONTH);
        Integer cyear = today.get(Calendar.YEAR);
        if(year<cyear ||  (year==cyear && month<cmonth) || (year==cyear && month==cmonth && date<cday) ){
            Toasty.error(this, "Invalid Date!", Toast.LENGTH_LONG, true).show();
            return false;
        }
        Toasty.success(TaskActivity.this, "Time Successfully Changed!", Toast.LENGTH_LONG, true).show();
        return true;
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
        taskName.setText(taskHeader + name);
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
        catRecord.setText(catHeader + categories);
    }

    private String getTimeString(String[] timeArray){
        String time = "";
        if(Integer.parseInt(timeArray[0])%12==0){
            time += 12 + ":";
        }
        else {
            time += Integer.parseInt(timeArray[0]) % 12 + ":";
        }
        if(Integer.parseInt(timeArray[1])<10){
            time += "0";
        }
        time += Integer.parseInt(timeArray[1]) + " ";
        if(Integer.parseInt(timeArray[0])<12){
            time += "AM";
        }
        else{
            time += "PM";
        }
        return time;
    }

    private void initTimeInformation(){
        info.moveToFirst();
        String[] dateRep = info.getString(2).split("-");
        String date = dateRep[1] + "/" + dateRep[2] + "/" + dateRep[0];
        Log.d("TaskActivityDebug", DatabaseUtils.dumpCursorToString(info));
        TextView dueDate = findViewById(R.id.due_date_record);
        dueDate.setText(dueDateText + date);
        String[] startTimeRep = info.getString(3).split("-");
        String startTime = getTimeString(startTimeRep);
        TextView startTimeView = findViewById(R.id.start_time_record);
        startTimeView.setText(startTimeText + startTime);
        String[] endTimeRep = info.getString(5).split("-");
        String endTime = getTimeString(endTimeRep);
        TextView endTimeView = findViewById(R.id.endt_time_record);
        endTimeView.setText(endTimeText + endTime);
    }

    private void initStats(){
        stats.moveToFirst();
        int completed = stats.getInt(4);
        int onTime = stats.getInt(8);
        int late = stats.getInt(7);
        Log.d("DynamicStatsTest", "Completed is: " + completed);
        TextView completedView = findViewById(R.id.completed_record);
        TextView notCompletedView = findViewById(R.id.incomplete_record);
        TextView onTimeView = findViewById(R.id.onTime_record);
        TextView lateView = findViewById(R.id.late_record);
        if(completed==1){
            completedView.setText(completeHeader + "Yes");
            notCompletedView.setText(incompleteHeader + "No");
        }
        else{
            completedView.setText(completeHeader + "No");
            notCompletedView.setText(incompleteHeader + "Yes");
        }
        if(onTime==1){
            onTimeView.setText(onTimeHeader + "Yes");
            lateView.setText(lateHeader + "No");
        }
        else{
            onTimeView.setText(onTimeHeader + "No");
            if(late==1) {
                lateView.setText(lateHeader + "Yes");
            }
            else{
                lateView.setText(lateHeader + "N/A");
            }
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
                        SQLfunctionHelper.addCatTaskActivity(currentActivity, addCategoryNames, taskID);
                        stats = SQLfunctionHelper.getTaskStats(TaskActivity.this, taskID);
                        initOrgInformation();
                        Toasty.success(TaskActivity.this, "Categories Successfully Added!", Toast.LENGTH_LONG, true).show();
                        return true;
                    }
                })
                .positiveText("confirm")
                .negativeText("cancel")
                .backgroundColor(Color.parseColor("#263238"))
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
                        SQLfunctionHelper.removeCatTaskActivity(currentActivity, removeCategoryNames, taskID);
                        stats = SQLfunctionHelper.getTaskStats(TaskActivity.this, taskID);
                        initOrgInformation();
                        Toasty.success(TaskActivity.this, "Categories Successfully Removed!", Toast.LENGTH_LONG, true).show();
                        return true;
                    }
                })
                .positiveText("confirm")
                .negativeText("cancel")
                .backgroundColor(Color.parseColor("#263238"))
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
                initStats();
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
                changeTaskDate();
                Log.d("TaskListenerDebug", "Enter Was Clicked");
                break;
        }
    }


    public void changeTaskDate(){
        if(!checkValidityOfTask()){
            return;
        }
        SQLfunctionHelper.deleteNotif(this, info);
        String dueDate = TaskCreatorFragment.constructDateStr(year, month, date);
        String startTime = startHour + "-" + startMinute + "-00";
        String endTime = endHour + "-" + endMinute + "-00";
        Log.d("TaskActivityTimeDebug", "We are about to enter the change time data");
        SQLfunctionHelper.changeTimeData(this, taskID, dueDate, startTime, endTime);
        info = SQLfunctionHelper.getTaskInfo(this, taskID);
        SQLfunctionHelper.createNotif(this, info);
        initTimeInformation();
    }

    public void markTaskComplete(){
        boolean result = SQLfunctionHelper.markComplete(taskID, TimeTrackerDataBaseHelper.getInstance(this), verifyOnTime(taskID));
        stats = SQLfunctionHelper.getTaskStats(this, taskID);
        stats.moveToFirst();
        if(result){
            Toasty.success(this, "Marked Complete!", Toast.LENGTH_LONG, true).show();
        }
        else{
            Toasty.error(this, "Already Marked Complete!", Toast.LENGTH_LONG, true).show();
        }
        return;
    }

    public void setUpToast(){
        Toasty.Config.getInstance()
                .setErrorColor(Color.parseColor("#B71C1C"))
                .setSuccessColor(Color.parseColor("#1B5E20"))
                .setTextColor(Color.WHITE)
                .apply();
    }

    public void deleteTask(){                               //test
        TimeTrackerDataBaseHelper helper = TimeTrackerDataBaseHelper.getInstance(this);
        SQLfunctionHelper.deleteTask(taskID, this);
        super.finish();
        return;
    }

    public void adjustNotification(View button){
        ToggleButton not = (ToggleButton) button;
        boolean param = false;
        Log.d("NotificationDebug", "Is it false: " + not.isChecked());
        if(!not.isChecked()){
            Log.d("NotificationDebug", "We should be canceling the notification");
            param = false;      //basically when it says notification off
            SQLfunctionHelper.deleteNotif(this, info);
        }
        else{
            param = true;
            SQLfunctionHelper.createNotif(this, info);
        }
        SQLfunctionHelper.changeNotification(this, taskID, param);
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
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
        DialogFragment newFragment = new DatePickerFragment();
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
            Log.d("TaskActivityDebug", TaskActivity.year + "/" + TaskActivity.month + "/" + TaskActivity.date);
        }
    }
}
