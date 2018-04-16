package com.hfad.mytimetracker.taskcreator;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hfad.mytimetracker.R;
import com.hfad.mytimetracker.SQLfunctionHelper;

import java.util.Calendar;

public class TaskCreatorReoccurringTaskFragment extends Fragment implements  View.OnClickListener {
    private static Integer color = null;                  //use for first card view
    private static String categoryName = null;            //use for first cardView

    private static Integer startYear = null;              //use for third cardview
    private static Integer startMonth = null;
    private static Integer startDay = null;
    private static Integer endYear = null;
    private static Integer endMonth = null;
    private static Integer endDay = null;
    private static Integer startMinute = null;         // for third cardview
    private static Integer startHour = null;
    private static Integer endMinute = null;
    private static Integer endHour = null;
    private static Integer[] days = null;
    private static CharSequence[] categoriesReocc = null;
    private static String  reoccTaskName = null;

    public TaskCreatorReoccurringTaskFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_task_creator_reoccurring_task, container, false);

        EditText starttime = (EditText) layout.findViewById(R.id.edittext_reoccurringtask_starttime);
        EditText endtime = (EditText) layout.findViewById(R.id.edittext_reoccurringtask_endtime);
        EditText days = (EditText) layout.findViewById(R.id.edittext_reoccurringtask_days);
        EditText startdate = (EditText) layout.findViewById(R.id.edittext_reoccurringtask_startdate);
        EditText enddate = (EditText) layout.findViewById(R.id.edittext_reoccurringtask_enddate);

        starttime.setOnClickListener(this);
        endtime.setOnClickListener(this);
        days.setOnClickListener(this);
        startdate.setOnClickListener(this);
        enddate.setOnClickListener(this);

        return layout;
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.edittext_reoccurringtask_starttime:
                TaskCreatorReoccurringTaskFragment.TimePickerFragment.flag=0;
                showTimePickerDialog(view);
                break;
            case R.id.edittext_reoccurringtask_endtime:
                TaskCreatorReoccurringTaskFragment.TimePickerFragment.flag=1;
                showTimePickerDialog(view);
                break;
            case R.id.edittext_reoccurringtask_days:
                showDaysSelectionDialog();
                break;
            case R.id.edittext_reoccurringtask_startdate:
                TaskCreatorReoccurringTaskFragment.DatePickerFragment.flag=0;
                showDatePickerDialog(view);
                break;
            case R.id.edittext_reoccurringtask_enddate:
                TaskCreatorReoccurringTaskFragment.DatePickerFragment.flag=1;
                showDatePickerDialog(view);
                break;
            case R.id.submit_reoccurringtask:
                enterReoccTasksInDB();
                break;

        }
    }
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TaskCreatorReoccurringTaskFragment.TimePickerFragment();
        Log.d("time picker test", "it entered the showTimePickerDialog");
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new TaskCreatorReoccurringTaskFragment.DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void showDaysSelectionDialog(){
        new MaterialDialog.Builder(getContext())
                .title(R.string.days_dialog_title)
                .items(R.array.days_list)
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] days, CharSequence[] text) {
                        if (days.length == 0) {
                            return true;
                        }
                        TaskCreatorReoccurringTaskFragment.days = days;
                        String daysString = "";
                        for (int index = 0; index < text.length - 1; index++) {
                            daysString = daysString + text[index] + ", ";
                        }
                        daysString = daysString + text[text.length - 1];
                        EditText dueDate = (EditText) getActivity().findViewById(R.id.edittext_reoccurringtask_days);
                        dueDate.setText(daysString, TextView.BufferType.EDITABLE);

                        return true;
                    }
                })
                .positiveText("confirm")
                .negativeText("cancel")
                .show();
    }


    private boolean checkValidityOfReoccTask(){
        //  Log.d("ReoccTest", TaskCreatorFragment.startYear + " " + TaskCreatorFragment.startMonth + " " + TaskCreatorFragment.startDate);
        //  Log.d("ReoccTest", TaskCreatorFragment.endYear + " " + TaskCreatorFragment.endMonth +  " " + TaskCreatorFragment.endDay);
        if(TaskCreatorReoccurringTaskFragment.reoccTaskName.equals("")){                      //change
            Toast.makeText(getActivity(), "Choose a Task!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TaskCreatorReoccurringTaskFragment.endYear==null){
            Toast.makeText(getActivity(), "Choose a End Date!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TaskCreatorReoccurringTaskFragment.startYear==null){
            Toast.makeText(getActivity(), "Choose a Start Date!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TaskCreatorReoccurringTaskFragment.startHour ==null){
            Toast.makeText(getActivity(), "Choose a Start Time!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TaskCreatorReoccurringTaskFragment.endHour ==null){
            Toast.makeText(getActivity(), "Choose a End Time!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TaskCreatorReoccurringTaskFragment.categoriesReocc==null){
            Toast.makeText(getActivity(), "Choose a Category!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TaskCreatorReoccurringTaskFragment.days==null){
            Toast.makeText(getActivity(), "Choose Days!", Toast.LENGTH_SHORT).show();
            return false;
        }

        //Check the following three validity cases, if either start date or end date is before the current date, if end date is before start date, and if end time is less than start time(
        //All of them should be implemented, just test again
        Log.d("ReoccTest", TaskCreatorReoccurringTaskFragment.startHour + " " + TaskCreatorReoccurringTaskFragment.startMinute);
        Log.d("ReoccTest", TaskCreatorReoccurringTaskFragment.endHour + " " + TaskCreatorReoccurringTaskFragment.endMinute);
        if(TaskCreatorReoccurringTaskFragment.endHour <TaskCreatorReoccurringTaskFragment.startHour || (TaskCreatorReoccurringTaskFragment.endHour ==TaskCreatorReoccurringTaskFragment.startHour && TaskCreatorReoccurringTaskFragment.endMinute <TaskCreatorReoccurringTaskFragment.startMinute)){
            Toast.makeText(getActivity(), "This Time is Invalid! Make End Time After Start Time", Toast.LENGTH_SHORT).show();
            return false;
        }
        Calendar today = Calendar.getInstance();
        Integer cday = today.get(Calendar.DAY_OF_MONTH);
        Integer cmonth = today.get(Calendar.MONTH);
        Integer cyear = today.get(Calendar.YEAR);
        if(TaskCreatorReoccurringTaskFragment.startYear<cyear ||  (TaskCreatorReoccurringTaskFragment.startYear==cyear && TaskCreatorReoccurringTaskFragment.startMonth<cmonth) || (TaskCreatorReoccurringTaskFragment.startYear==cyear && TaskCreatorReoccurringTaskFragment.startMonth==cmonth && TaskCreatorReoccurringTaskFragment.startDay<cday) ){
            Toast.makeText(getActivity(), "The Start Date is Invalid! Make The Start Date Today or After", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TaskCreatorReoccurringTaskFragment.endYear<cyear ||  (TaskCreatorReoccurringTaskFragment.endYear==cyear && TaskCreatorReoccurringTaskFragment.endMonth<cmonth) || (TaskCreatorReoccurringTaskFragment.endYear==cyear && TaskCreatorReoccurringTaskFragment.endMonth==cmonth && TaskCreatorReoccurringTaskFragment.endDay<cday) ){
            Toast.makeText(getActivity(), "The End Date is Invalid! Make The Due Date Today or After", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TaskCreatorReoccurringTaskFragment.endYear<TaskCreatorReoccurringTaskFragment.startYear ||  (TaskCreatorReoccurringTaskFragment.endYear==TaskCreatorReoccurringTaskFragment.startYear && TaskCreatorReoccurringTaskFragment.endMonth<TaskCreatorReoccurringTaskFragment.startMonth) || (TaskCreatorReoccurringTaskFragment.endYear==TaskCreatorReoccurringTaskFragment.startYear && TaskCreatorReoccurringTaskFragment.endMonth==TaskCreatorReoccurringTaskFragment.startMonth && TaskCreatorReoccurringTaskFragment.endDay<TaskCreatorReoccurringTaskFragment.startDay) ){
            Toast.makeText(getActivity(), "The End Date  Invalid! Make The End Date The Same Day Or After The EndDate", Toast.LENGTH_SHORT).show();
            return false;
        }
        return  true;
    }

    private void enterReoccTasksInDB()  {
        TaskCreatorReoccurringTaskFragment.reoccTaskName = ((EditText) getActivity().findViewById(R.id.reocc_task_name)).getText().toString();
        if(!checkValidityOfReoccTask()){
            return;
        }
        SQLfunctionHelper.enterReoccTasksInDB(getContext(), startYear, startMonth, startDay, endYear, endMonth, endDay, startHour, startMinute,
                endHour, endMinute, days, reoccTaskName, categoriesReocc);
        cleanUpCardViewThree();
    }

    private void cleanUpCardViewThree(){
        TaskCreatorReoccurringTaskFragment.startYear=null;              //use for third cardview
        TaskCreatorReoccurringTaskFragment.startDay=null;
        TaskCreatorReoccurringTaskFragment.startMonth=null;
        TaskCreatorReoccurringTaskFragment.endYear=null;
        TaskCreatorReoccurringTaskFragment.endMonth=null;
        TaskCreatorReoccurringTaskFragment.endDay=null;
        TaskCreatorReoccurringTaskFragment.startMinute =null;         // for third cardview
        TaskCreatorReoccurringTaskFragment.startHour =null;
        TaskCreatorReoccurringTaskFragment.endMinute =null;
        TaskCreatorReoccurringTaskFragment.endHour =null;
        TaskCreatorReoccurringTaskFragment.days=null;
        TaskCreatorReoccurringTaskFragment.reoccTaskName=null;
        TaskCreatorReoccurringTaskFragment.categoriesReocc=null;
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
            String timePeriod = "AM";
            int fixedHour = hourOfDay;
            if (fixedHour > 12) {
                fixedHour = fixedHour - 12;
                timePeriod = "PM";
            }
            if (fixedHour == 0) {
                fixedHour = 12;
                timePeriod = "AM";
            }
            if (flag == 0) {
                TaskCreatorReoccurringTaskFragment.startMinute = minute;
                TaskCreatorReoccurringTaskFragment.startHour = hourOfDay;

                EditText dueDate = (EditText) getActivity().findViewById(R.id.edittext_reoccurringtask_starttime);
                dueDate.setText(fixedHour + ":" + minute + " " + timePeriod, TextView.BufferType.EDITABLE);
            }
            if (flag == 1) {
                TaskCreatorReoccurringTaskFragment.endMinute = minute;
                TaskCreatorReoccurringTaskFragment.endHour = hourOfDay;

                EditText dueDate = (EditText) getActivity().findViewById(R.id.edittext_reoccurringtask_endtime);
                dueDate.setText(fixedHour + ":" + minute + " " + timePeriod, TextView.BufferType.EDITABLE);
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
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            return datePickerDialog;
        }

        private static int flag;

        public void onDateSet(DatePicker view, int year, int month, int day) {
            if (flag == 0) {
                TaskCreatorReoccurringTaskFragment.startYear = year;
                TaskCreatorReoccurringTaskFragment.startMonth = month;
                TaskCreatorReoccurringTaskFragment.startDay = day;

                EditText dueDate = (EditText) getActivity().findViewById(R.id.edittext_reoccurringtask_startdate);
                dueDate.setText(month + " - " + day + " - " + year, TextView.BufferType.EDITABLE);
            } else {
                TaskCreatorReoccurringTaskFragment.endYear = year;
                TaskCreatorReoccurringTaskFragment.endMonth = month;
                TaskCreatorReoccurringTaskFragment.endDay = day;

                EditText dueDate = (EditText) getActivity().findViewById(R.id.edittext_reoccurringtask_enddate);
                dueDate.setText(month + " - " + day + " - " + year, TextView.BufferType.EDITABLE);
            }
        }
    }


}
