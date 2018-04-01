package com.hfad.mytimetracker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
                        TaskCreatorFragment.days = which;
                        return true;
                    }
                })
                .positiveText("confirm")
                .negativeText("cancel")
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
                .show();
    }

    public String[] getCategoryList(){
        TimeTrackerDataBaseHelper db = new TimeTrackerDataBaseHelper(getContext());
        SQLiteDatabase read = db.getWritableDatabase();
        Cursor categories = read.query("TASK_CATEGORY_INFO", new String[] {"CATEGORY_NAME"}, null, null, null, null, null, null);
        String[] result = new String[categories.getCount()];
        categories.moveToFirst();
        for(int i =0; i<categories.getCount(); i++){
            String name = categories.getString(0);
            result[i] = name;
            categories.moveToNext();
        }
        return result;
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
        String startDate = constructDateStr(TaskCreatorFragment.startYear, TaskCreatorFragment.startMonth, TaskCreatorFragment.startDate);
        String endDate = constructDateStr(TaskCreatorFragment.endYear, TaskCreatorFragment.endMonth, TaskCreatorFragment.endDay);
        String startTime = TaskCreatorFragment.startHourReocc + "-" + TaskCreatorFragment.startMinuteReocc + "-00";
        String endTime = TaskCreatorFragment.endHourReocc + "-" + TaskCreatorFragment.endMinuteReocc + "-00";
        Log.d("ReoccTest", startDate + " " + endDate);

        Toast.makeText(getActivity(), startDate + " " + endDate, Toast.LENGTH_LONG).show();
        TimeTrackerDataBaseHelper categoryHelper = new TimeTrackerDataBaseHelper(getContext());
        SQLiteDatabase write = categoryHelper.getWritableDatabase();
        SQLiteDatabase read = categoryHelper.getReadableDatabase();
        HashSet<Integer> days = new HashSet<Integer>();
        for(int i=0; i< TaskCreatorFragment.days.length; i++){
            days.add(TaskCreatorFragment.days[i]+1);
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");      //year.month.day
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);

            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTime(start);
            while (!gcal.getTime().after(end)) {
               if(days.contains(gcal.get(Calendar.DAY_OF_WEEK))){
                   //We want to put in the task in task info
                   String dueDate = constructDateStr(gcal.get(Calendar.YEAR), gcal.get(Calendar.MONTH), gcal.get(Calendar.DAY_OF_MONTH));
                   ContentValues recordParamaters = new ContentValues();                   //insert task info, insert task stats
                   recordParamaters.put("TASK_NAME", TaskCreatorFragment.reoccTaskName);
                   recordParamaters.put("DUE_DATE", dueDate);
                   recordParamaters.put("START_TIME", startTime);
                   recordParamaters.put("END_TIME", endTime);
                   //construct date value, start time value, end time value
                   write.insert("TASK_INFORMATION", null, recordParamaters);

                   Cursor id = read.rawQuery("SELECT MAX(_ID) FROM TASK_INFORMATION", null);
                   id.moveToFirst();

                   //We want to put in the task in task stats
                   ContentValues taskStatsParams = new ContentValues();
                   taskStatsParams.put("TASK_NAME", TaskCreatorFragment.reoccTaskName);
                   taskStatsParams.put("CATEGORY_GENERAL", "1");
                   taskStatsParams.put("NOT_COMPLETED", "1");
                   taskStatsParams.put("NOT_ON_TIME", "0");
                   taskStatsParams.put("ON_TIME", "0");
                   taskStatsParams.put("TASK_ID", id.getString(0));
                   taskStatsParams.put("COMPLETED", "0");
                   taskStatsParams.put("DUE_DATE", dueDate);
                   for(int i =0; i<TaskCreatorFragment.categoriesReocc.length; i++){
                       taskStatsParams.put(TaskCreatorFragment.categoriesReocc[i].toString(), "1");
                       write.execSQL("UPDATE TASK_CATEGORY_INFO " +
                               "SET NOT_COMPLETED = NOT_COMPLETED + 1 " +
                               "WHERE CATEGORY_NAME = \"" +
                               TaskCreatorFragment.categoriesReocc[i] + "\"");
                   }
                   write.insert("TASK_STATS", null, taskStatsParams);      //issue here
               }
                gcal.add(Calendar.DATE, 1);
            }
        }
        catch (Exception e){
            Log.d("ReoccTest", "Did not work");
        }
        cleanUpCardViewThree();
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
        TimeTrackerDataBaseHelper categoryHelper = new TimeTrackerDataBaseHelper(getContext());
        SQLiteDatabase read = categoryHelper.getReadableDatabase();
        SQLiteDatabase write = categoryHelper.getWritableDatabase();
        EditText txt = (EditText) getActivity().findViewById(R.id.cat_name);

        String cat = txt.getText().toString();      //paramaters
        TaskCreatorFragment.categoryName=cat;
        Integer color = this.color;
        if(TaskCreatorFragment.color==null){
            Log.d("CategorySQL", "No Color");
            Toast.makeText(getActivity(), "This Category Needs a Color!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TaskCreatorFragment.categoryName.equals("")){
            Log.d("CategorySQL", "this is cat" + cat + "!");
            Toast.makeText(getActivity(), "This Category Needs a Name!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), "This Category Already Exists!", Toast.LENGTH_SHORT).show();
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
            write.execSQL("ALTER TABLE TASK_STATS ADD COLUMN " + cat + " BOOLEAN;");
            ContentValues entry = new ContentValues();
            entry.put("CATEGORY_NAME", cat);
            entry.put("COLOR", color);
            entry.put("COMPLETED", 0);
            entry.put("NOT_COMPLETED", 0);
            entry.put("NOT_ON_TIME", 0);
            entry.put("ON_TIME", 0);
            write.insert("TASK_CATEGORY_INFO", null, entry);
            Log.d("CategorySQL", "We put in DB");
            Toast.makeText(getActivity(), "The Category " + TaskCreatorFragment.categoryName+ " was made", Toast.LENGTH_SHORT).show();
            TaskCreatorFragment.categoryName = null;
            TaskCreatorFragment.color = null;
        }
    }

    private void enterTaskInDB() {
        String dueDate = constructDateStr(TaskCreatorFragment.year, TaskCreatorFragment.month, TaskCreatorFragment.date);
        String startTime = TaskCreatorFragment.startHour + "-" + TaskCreatorFragment.startMinute + "-00";
        String endTime = TaskCreatorFragment.endHour + "-" + TaskCreatorFragment.endMinute + "-00";
        TaskCreatorFragment.categoryName = ((EditText) getActivity().findViewById(R.id.cat_name_task_adder)).getText().toString().toUpperCase();
        TaskCreatorFragment.taskName = ((EditText) getActivity().findViewById(R.id.task_name_task_adder)).getText().toString();
        if(!checkValidityOfTask()){
            return;
        }
        TimeTrackerDataBaseHelper categoryHelper = new TimeTrackerDataBaseHelper(getContext());
        SQLiteDatabase write = categoryHelper.getWritableDatabase();
        SQLiteDatabase read = categoryHelper.getReadableDatabase();

        ContentValues recordParamaters = new ContentValues();                   //insert task info, insert task stats
        recordParamaters.put("TASK_NAME", TaskCreatorFragment.taskName);
        //recordParamaters.put("TASK_CATEGORY", TaskCreatorFragment.taskCategoryName);
        recordParamaters.put("DUE_DATE", dueDate);
        recordParamaters.put("START_TIME", startTime);
        recordParamaters.put("END_TIME", endTime);
        //construct date value, start time value, end time value
        write.insert("TASK_INFORMATION", null, recordParamaters);
        Log.d("InsertTaskTest", "it worked");

        Cursor id = read.rawQuery("SELECT MAX(_ID) FROM TASK_INFORMATION", null);
        id.moveToFirst();
        ContentValues recordParamaterstwo = new ContentValues();
        recordParamaterstwo.put("TASK_NAME", TaskCreatorFragment.taskName);
        recordParamaterstwo.put("CATEGORY_GENERAL", "1");
        recordParamaterstwo.put("TASK_ID", id.getString(0));
        recordParamaterstwo.put("NOT_COMPLETED", "1");
        recordParamaterstwo.put("COMPLETED", "0");
        recordParamaterstwo.put("ON_TIME", "0");
        recordParamaterstwo.put("NOT_ON_TIME", "0");
        recordParamaterstwo.put("DUE_DATE", dueDate);
        Log.d("TestAddTask", TaskCreatorFragment.taskCategoryNames.length + "");
        for(int i =0; i<TaskCreatorFragment.taskCategoryNames.length; i++){
            Log.d("TestAddTask", TaskCreatorFragment.taskCategoryNames[i].toString());
            recordParamaterstwo.put(TaskCreatorFragment.taskCategoryNames[i].toString(), "1");
            write.execSQL("UPDATE TASK_CATEGORY_INFO " +
                    "SET NOT_COMPLETED = NOT_COMPLETED + 1 " +
                    "WHERE CATEGORY_NAME = \"" +
                    TaskCreatorFragment.taskCategoryNames[i] + "\"");

           // write.execSQL("UPDATE TASK_CATEGORY_INFO SET NOT_COMPLETED = NOT_COMPLETED + 1 WHERE CATEGORY_NAME = " + "\"Jul\"");
        }
        write.insert("TASK_STATS", null, recordParamaterstwo);      //issue here
        Log.d("InsertTaskTest", "it worked");
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

    private boolean checkValidityOfReoccTask(){
      //  Log.d("ReoccTest", TaskCreatorFragment.startYear + " " + TaskCreatorFragment.startMonth + " " + TaskCreatorFragment.startDate);
      //  Log.d("ReoccTest", TaskCreatorFragment.endYear + " " + TaskCreatorFragment.endMonth +  " " + TaskCreatorFragment.endDay);
        if(TaskCreatorFragment.reoccTaskName.equals("")){                      //change
            Toast.makeText(getActivity(), "Choose a Task!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TaskCreatorFragment.endYear==null){
            Toast.makeText(getActivity(), "Choose a End Date!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TaskCreatorFragment.startYear==null){
            Toast.makeText(getActivity(), "Choose a Start Date!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TaskCreatorFragment.startHourReocc==null){
            Toast.makeText(getActivity(), "Choose a Start Time!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TaskCreatorFragment.endHourReocc==null){
            Toast.makeText(getActivity(), "Choose a End Time!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TaskCreatorFragment.categoriesReocc==null){
            Toast.makeText(getActivity(), "Choose a Category!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TaskCreatorFragment.days==null){
            Toast.makeText(getActivity(), "Choose Days!", Toast.LENGTH_SHORT).show();
            return false;
        }

        //Check the following three validity cases, if either start date or end date is before the current date, if end date is before start date, and if end time is less than start time(
        //All of them should be implemented, just test again
        Log.d("ReoccTest", TaskCreatorFragment.startHourReocc + " " + TaskCreatorFragment.startMinuteReocc);
        Log.d("ReoccTest", TaskCreatorFragment.endHourReocc + " " + TaskCreatorFragment.endMinuteReocc);
        if(TaskCreatorFragment.endHourReocc<TaskCreatorFragment.startHourReocc || (TaskCreatorFragment.endHourReocc==TaskCreatorFragment.startHourReocc && TaskCreatorFragment.endMinuteReocc<TaskCreatorFragment.startMinuteReocc)){
            Toast.makeText(getActivity(), "This Time is Invalid! Make End Time After Start Time", Toast.LENGTH_SHORT).show();
            return false;
        }
        Calendar today = Calendar.getInstance();
        Integer cday = today.get(Calendar.DAY_OF_MONTH);
        Integer cmonth = today.get(Calendar.MONTH);
        Integer cyear = today.get(Calendar.YEAR);
        if(TaskCreatorFragment.startYear<cyear ||  (TaskCreatorFragment.startYear==cyear && TaskCreatorFragment.startMonth<cmonth) || (TaskCreatorFragment.startYear==cyear && TaskCreatorFragment.startMonth==cmonth && TaskCreatorFragment.startDate<cday) ){
            Toast.makeText(getActivity(), "The Start Date is Invalid! Make The Start Date Today or After", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TaskCreatorFragment.endYear<cyear ||  (TaskCreatorFragment.endYear==cyear && TaskCreatorFragment.endMonth<cmonth) || (TaskCreatorFragment.endYear==cyear && TaskCreatorFragment.endMonth==cmonth && TaskCreatorFragment.endDay<cday) ){
            Toast.makeText(getActivity(), "The End Date is Invalid! Make The Due Date Today or After", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TaskCreatorFragment.endYear<TaskCreatorFragment.startYear ||  (TaskCreatorFragment.endYear==TaskCreatorFragment.startYear && TaskCreatorFragment.endMonth<TaskCreatorFragment.startMonth) || (TaskCreatorFragment.endYear==TaskCreatorFragment.startYear && TaskCreatorFragment.endMonth==TaskCreatorFragment.startMonth && TaskCreatorFragment.endDay<TaskCreatorFragment.startDate) ){
            Toast.makeText(getActivity(), "The End Date  Invalid! Make The End Date The Same Day Or After The EndDate", Toast.LENGTH_SHORT).show();
            return false;
        }
        return  true;
    }


    private boolean checkValidityOfTask(){
        if(TaskCreatorFragment.taskName.equals("")){
            Toast.makeText(getActivity(), "Choose a Task!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TaskCreatorFragment.year==null){
            Toast.makeText(getActivity(), "Choose a Due Date!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TaskCreatorFragment.startHour==null){
            Toast.makeText(getActivity(), "Choose a Start Time!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TaskCreatorFragment.endHour==null){
            Toast.makeText(getActivity(), "Choose a End Time!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TaskCreatorFragment.taskCategoryNames==null){
            Toast.makeText(getActivity(), "Choose a Category!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TaskCreatorFragment.endHour<TaskCreatorFragment.startHour || (TaskCreatorFragment.endHour==TaskCreatorFragment.startHour && TaskCreatorFragment.endMinute<TaskCreatorFragment.startMinute)){
            Toast.makeText(getActivity(), "This Time is Invalid! Make End Time After Start Time", Toast.LENGTH_SHORT).show();
            return false;
        }
        Calendar today = Calendar.getInstance();
        Integer cday = today.get(Calendar.DAY_OF_MONTH);
        Integer cmonth = today.get(Calendar.MONTH);
        Integer cyear = today.get(Calendar.YEAR);
        if(TaskCreatorFragment.year<cyear ||  (TaskCreatorFragment.year==cyear && TaskCreatorFragment.month<cmonth) || (TaskCreatorFragment.year==cyear && TaskCreatorFragment.month==cmonth && TaskCreatorFragment.date<cday) ){
            Toast.makeText(getActivity(), "This Date is Invalid! Make The Due Date Today or After", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
