package com.hfad.mytimetracker.taskcreator;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.database.sqlite.SQLiteDatabase;
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

import com.hfad.mytimetracker.R;
import com.hfad.mytimetracker.SQLfunctionHelper;
import com.hfad.mytimetracker.TimeTrackerDataBaseHelper;

import java.util.Calendar;

public class TaskCreatorTaskFragment extends Fragment implements View.OnClickListener {

    private static Integer color = null;                  //use for first card view
    private static String categoryName = null;            //use for first cardView

    private static Integer year;                   //use for second cardView
    private static Integer month;
    private static Integer date;

    private static String taskName = null;                 //use for second
    private static CharSequence[] taskCategoryNames = null;
    private static Integer startMinute = null;
    private static Integer startHour = null;
    private static Integer endMinute = null;
    private static Integer endHour = null;

    public EditText editTextDueDate;

    public TaskCreatorTaskFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_task_creator_single, container, false);

        EditText startTime = (EditText) layout.findViewById(R.id.edittext_task_startime);
        EditText endTime = (EditText) layout.findViewById(R.id.edittext_task_endtime);
        EditText dueDate = (EditText) layout.findViewById(R.id.edittext_task_duedate);
        editTextDueDate = dueDate;

        startTime.setOnClickListener(this);
        endTime.setOnClickListener(this);
        dueDate.setOnClickListener(this);

        return layout;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edittext_task_startime:
                TaskCreatorTaskFragment.TimePickerFragment.flag = 0;
                showTimePickerDialog(view);
                break;
            case R.id.edittext_task_endtime:
                TaskCreatorTaskFragment.TimePickerFragment.flag = 1;
                showTimePickerDialog(view);
                break;
            case R.id.edittext_task_duedate:
                showDatePickerDialog(view);
                break;
            case R.id.submit_task:
                enterTaskInDB();
                break;

        }
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TaskCreatorTaskFragment.TimePickerFragment();
        Log.d("time picker test", "it entered the showTimePickerDialog");
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new TaskCreatorTaskFragment.DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    private void enterTaskInDB() {
        String dueDate = constructDateStr(TaskCreatorTaskFragment.year, TaskCreatorTaskFragment.month, TaskCreatorTaskFragment.date);
        String startTime = TaskCreatorTaskFragment.startHour + "-" + TaskCreatorTaskFragment.startMinute + "-00";
        String endTime = TaskCreatorTaskFragment.endHour + "-" + TaskCreatorTaskFragment.endMinute + "-00";
        TaskCreatorTaskFragment.categoryName = ((EditText) getActivity().findViewById(R.id.cat_name_task_adder)).getText().toString().toUpperCase();
        TaskCreatorTaskFragment.taskName = ((EditText) getActivity().findViewById(R.id.task_name_task_adder)).getText().toString();
        if (!checkValidityOfTask()) {
            return;
        }
        TimeTrackerDataBaseHelper categoryHelper = new TimeTrackerDataBaseHelper(getContext());
        SQLiteDatabase write = categoryHelper.getWritableDatabase();
        SQLiteDatabase read = categoryHelper.getReadableDatabase();
        SQLfunctionHelper.enterTaskInDB(getActivity(), taskName, dueDate, startTime, endTime, taskCategoryNames);

        cleanUpCardViewTwo();
    }

    public static String constructDateStr(int y, int m, int d) {
        m = m + 1;
        StringBuilder result = new StringBuilder();
        result.append(y + "-");
        if (m < 10) {
            result.append("0");
        }
        result.append(m + "-");
        if (d < 10) {
            result.append("0");
        }
        result.append(d + "");
        return new String(result);
    }

    private boolean checkValidityOfTask() {
        if (TaskCreatorTaskFragment.taskName.equals("")) {
            Toast.makeText(getActivity(), "Choose a Task!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TaskCreatorTaskFragment.year == null) {
            Toast.makeText(getActivity(), "Choose a Due Date!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TaskCreatorTaskFragment.startHour == null) {
            Toast.makeText(getActivity(), "Choose a Start Time!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TaskCreatorTaskFragment.endHour == null) {
            Toast.makeText(getActivity(), "Choose a End Time!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TaskCreatorTaskFragment.taskCategoryNames == null) {
            Toast.makeText(getActivity(), "Choose a Category!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TaskCreatorTaskFragment.endHour < TaskCreatorTaskFragment.startHour || (TaskCreatorTaskFragment.endHour == TaskCreatorTaskFragment.startHour && TaskCreatorTaskFragment.endMinute < TaskCreatorTaskFragment.startMinute)) {
            Toast.makeText(getActivity(), "This Time is Invalid! Make End Time After Start Time", Toast.LENGTH_SHORT).show();
            return false;
        }
        Calendar today = Calendar.getInstance();
        Integer cday = today.get(Calendar.DAY_OF_MONTH);
        Integer cmonth = today.get(Calendar.MONTH);
        Integer cyear = today.get(Calendar.YEAR);
        return true;
    }

    private void cleanUpCardViewTwo() {
        TaskCreatorTaskFragment.year = null;
        TaskCreatorTaskFragment.month = null;
        TaskCreatorTaskFragment.date = null;
        TaskCreatorTaskFragment.startMinute = null;
        TaskCreatorTaskFragment.startHour = null;
        TaskCreatorTaskFragment.endMinute = null;
        TaskCreatorTaskFragment.endHour = null;
        TaskCreatorTaskFragment.taskCategoryNames = null;
        TaskCreatorTaskFragment.taskName = null;
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
             if (hourOfDay > 12) {
                 hourOfDay = hourOfDay - 12;
                 timePeriod = "PM";
             }
             if (hourOfDay == 0) {
                 hourOfDay = 12;
                 timePeriod = "AM";
             }
            if (flag == 0) {
                TaskCreatorTaskFragment.startMinute = minute;
                TaskCreatorTaskFragment.startHour = hourOfDay;

                EditText dueDate = (EditText) getActivity().findViewById(R.id.edittext_task_startime);
                dueDate.setText(hourOfDay + ":" + minute + " " + timePeriod, TextView.BufferType.EDITABLE);
            }
            if (flag == 1) {
                TaskCreatorTaskFragment.endMinute = minute;
                TaskCreatorTaskFragment.endHour = hourOfDay;

                EditText dueDate = (EditText) getActivity().findViewById(R.id.edittext_task_endtime);
                dueDate.setText(hourOfDay + ":" + minute + " " + timePeriod, TextView.BufferType.EDITABLE);
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
            TaskCreatorTaskFragment.year = year;
            TaskCreatorTaskFragment.month = month;
            TaskCreatorTaskFragment.date = day;

            EditText dueDate = (EditText) getActivity().findViewById(R.id.edittext_task_duedate);
            dueDate.setText(month + " - " + day + " - " + year, TextView.BufferType.EDITABLE);
        }
    }

}
