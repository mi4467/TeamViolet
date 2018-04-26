package com.hfad.mytimetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SQLfunctionHelper {

    public static String[] getCategoryList(Context c){
        TimeTrackerDataBaseHelper db = TimeTrackerDataBaseHelper.getInstance(c);
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

    public static void deleteTask(int id, Context c){
        TimeTrackerDataBaseHelper categoryHelper = TimeTrackerDataBaseHelper.getInstance(c);
        SQLiteDatabase writableDatabase = categoryHelper.getWritableDatabase();
        SQLiteDatabase readableDatabase = categoryHelper.getReadableDatabase();
        Cursor temp = readableDatabase.rawQuery("SELECT * FROM TASK_STATS WHERE TASK_ID = " + id, null);
        temp.moveToFirst();
        boolean completed = temp.getInt(4)==1;
        boolean matters = !(temp.getInt(7) == temp.getInt(8));
        boolean ontime = temp.getInt(8)==1;
        Log.d("DeleteDebug", temp.getColumnCount()+"");
        Log.d("DeleteDebug", completed+" " + matters + " " + ontime);
        for(int i =9; i<temp.getColumnCount(); i++){
            Log.d("DeleteDebug", "Column " + i + " is: " + temp.getColumnName(i));
            Log.d("DeleteDebug", temp.getPosition() + " out of " + temp.getColumnCount());
            Log.d("DeleteDebug", DatabaseUtils.dumpCursorToString(temp));
            if(temp.getInt(i)==1){
                if(completed){
                    writableDatabase.execSQL("UPDATE TASK_CATEGORY_INFO " +
                            "SET COMPLETED = COMPLETED - 1 " +
                            "WHERE CATEGORY_NAME = \"" +
                            temp.getColumnName(i) + "\"");
                }
                else {
                    writableDatabase.execSQL("UPDATE TASK_CATEGORY_INFO " +
                            "SET NOT_COMPLETED = NOT_COMPLETED - 1 " +
                            "WHERE CATEGORY_NAME = \"" +
                            temp.getColumnName(i) + "\"");
                }
                if(matters){
                    if(ontime){
                        writableDatabase.execSQL("UPDATE TASK_CATEGORY_INFO " +
                                "SET ON_TIME = ON_TIME - 1 " +
                                "WHERE CATEGORY_NAME = \"" +
                                temp.getColumnName(i) + "\"");
                    }
                    else{
                        writableDatabase.execSQL("UPDATE TASK_CATEGORY_INFO " +
                                "SET NOT_ON_TIME = NOT_ON_TIME - 1 " +
                                "WHERE CATEGORY_NAME = \"" +
                                temp.getColumnName(i) + "\"");
                    }
                }
            }
        }
        deleteNotif(c, readableDatabase.rawQuery("SELECT * FROM TASK_INFORMATION WHERE _ID = " + id, null));
        writableDatabase.delete("TASK_STATS", "TASK_ID = ?", new String[]{id+""});
        writableDatabase.delete("TASK_INFORMATION", "_ID = ?", new String[]{id+""});
    }

    public static void markComplete(int id, TimeTrackerDataBaseHelper helper, boolean onTime){
        SQLiteDatabase readableDatabase = helper.getReadableDatabase();
        SQLiteDatabase writableDatabase = helper.getWritableDatabase();
        //Something here is broken
        //ADD a check for the general category
        ContentValues completeValue = new ContentValues();
        completeValue.put("COMPLETED", 1);
        completeValue.put("NOT_COMPLETED", 0);
        //writableDatabase = (new TimeTrackerDataBaseHelper(getActivity())).getWritableDatabase();
        Cursor temp = readableDatabase.rawQuery("SELECT * FROM TASK_STATS WHERE TASK_ID = " + id, null);
        temp.moveToFirst();
        int completed = temp.getInt(4);
        if(completed==1){       //check to make sure we can't mark complete twice
            return;
        }
        else{
            //add points for completing a task here, make a call to that function
        }
        //we have to check to see if it's on time or not, mark those values in TASK_STATS
        Log.d("MarkCompleteDebug", "Is it on time: " + onTime);
        boolean alreadyMarkedLate = temp.getInt(7)==1;

        if(onTime){
            addToScore(helper, true);
            completeValue.put("ON_TIME", 1);
            completeValue.put("NOT_ON_TIME", 0);
            writableDatabase.update("TASK_STATS", completeValue, "TASK_ID = ?", new String[] {id + ""});
            for(int i =9; i<temp.getColumnCount(); i++){
                //UPDATE THE ONTIME AND COMPLETED FOR THAT CATEGORY
                Log.d("MarkCompleteDebug", "Column " + i + " is: " + temp.getColumnName(i));
                if(temp.getInt(i)==1){
                    Log.d("MarkCompleteDebug", "We are entering data for: " + temp.getColumnName(i));
                    writableDatabase.execSQL("UPDATE TASK_CATEGORY_INFO " +
                            "SET COMPLETED = COMPLETED + 1 " +
                            "WHERE CATEGORY_NAME = \"" +
                            temp.getColumnName(i) + "\"");      //Mark it complete

                    writableDatabase.execSQL("UPDATE TASK_CATEGORY_INFO " +
                            "SET NOT_COMPLETED = NOT_COMPLETED - 1 " +
                            "WHERE CATEGORY_NAME = \"" +
                            temp.getColumnName(i) + "\"");

                    writableDatabase.execSQL("UPDATE TASK_CATEGORY_INFO " +
                            "SET ON_TIME = ON_TIME + 1 " +
                            "WHERE CATEGORY_NAME = \"" +
                            temp.getColumnName(i) + "\"");
                }
            }
        }
        else{
            addToScore(helper,false);
            completeValue.put("ON_TIME", 0);
            completeValue.put("NOT_ON_TIME", 1);
            writableDatabase.update("TASK_STATS", completeValue, "TASK_ID = ?", new String[] {id + ""});
            for(int i =9; i<temp.getColumnCount(); i++){
                //UPDATE THE NOT_ONTIME AND COMPLETED FOR THAT CATEGORY
                Log.d("MarkCompleteDebug", "Column " + i + " is: " + temp.getColumnName(i));
                if(temp.getInt(i)==1){
                    Log.d("MarkCompleteDebug", "We are entering data for: " + temp.getColumnName(i));
                    writableDatabase.execSQL("UPDATE TASK_CATEGORY_INFO " +
                            "SET COMPLETED = COMPLETED + 1 " +
                            "WHERE CATEGORY_NAME = \"" +
                            temp.getColumnName(i) + "\"");      //Mark it complete

                    writableDatabase.execSQL("UPDATE TASK_CATEGORY_INFO " +
                            "SET NOT_COMPLETED = NOT_COMPLETED - 1 " +
                            "WHERE CATEGORY_NAME = \"" +
                            temp.getColumnName(i) + "\"");
                    if(!alreadyMarkedLate) {                                        //makes sure we don't up the late count if the chron job already marks it late
                        writableDatabase.execSQL("UPDATE TASK_CATEGORY_INFO " +
                                "SET NOT_ON_TIME = NOT_ON_TIME + 1 " +
                                "WHERE CATEGORY_NAME = \"" +
                                temp.getColumnName(i) + "\"");
                    }
                }
            }
        }
        //Then iterate through category information and update the values for the categories the task belongs to

    }

    public static void enterReoccTasksInDB(Context c, Integer startYear, Integer startMonth, Integer startDay,
                                           Integer endYear, Integer endMonth, Integer endDay, Integer startHourReocc, Integer startMinuteReocc,
                                            Integer endHourReocc, Integer endMinuteReocc, Integer[] daysList, String reoccTaskName, CharSequence[] categoriesReocc)  {
        String startDate = TaskCreatorFragment.constructDateStr(startYear, startMonth, startDay);
        String endDate = TaskCreatorFragment.constructDateStr(endYear, endMonth, endDay);
        String startTime = startHourReocc + "-" + startMinuteReocc + "-00";
        String endTime = endHourReocc + "-" + endMinuteReocc + "-00";
        Log.d("ReoccTest", startDate + " " + endDate);
        Toast.makeText(c, startDate + " " + endDate, Toast.LENGTH_LONG).show();
        TimeTrackerDataBaseHelper categoryHelper = TimeTrackerDataBaseHelper.getInstance(c);
        SQLiteDatabase write = categoryHelper.getWritableDatabase();
        SQLiteDatabase read = categoryHelper.getReadableDatabase();
        HashSet<Integer> days = new HashSet<Integer>();
        for(int i=0; i< daysList.length; i++){
            days.add(daysList[i]+1);
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
                    String dueDate = TaskCreatorFragment.constructDateStr(gcal.get(Calendar.YEAR), gcal.get(Calendar.MONTH), gcal.get(Calendar.DAY_OF_MONTH));
                    ContentValues recordParamaters = new ContentValues();                   //insert task info, insert task stats
                    recordParamaters.put("TASK_NAME", reoccTaskName);
                    recordParamaters.put("DUE_DATE", dueDate);
                    recordParamaters.put("START_TIME", startTime);
                    recordParamaters.put("END_TIME", endTime);
                    recordParamaters.put("NOTIFICATION", 1);
                    //construct date value, start time value, end time value
                    write.insert("TASK_INFORMATION", null, recordParamaters);

//                    Cursor id = read.rawQuery("SELECT MAX(_ID) FROM TASK_INFORMATION", null);
//                    id.moveToFirst();

                    Cursor id = read.rawQuery("SELECT MAX(_ID) FROM TASK_INFORMATION", null);
                    id.moveToFirst();
                    Cursor notifCursor = read.rawQuery("SELECT * FROM TASK_INFORMATION WHERE _ID = " + id.getInt(0), null);
                    createNotif(c, notifCursor);

                    //We want to put in the task in task stats
                    ContentValues taskStatsParams = new ContentValues();
                    taskStatsParams.put("TASK_NAME", reoccTaskName);
                    taskStatsParams.put("CATEGORY_GENERAL", "1");
                    taskStatsParams.put("NOT_COMPLETED", "1");
                    taskStatsParams.put("NOT_ON_TIME", "0");
                    taskStatsParams.put("ON_TIME", "0");
                    taskStatsParams.put("TASK_ID", id.getString(0));
                    taskStatsParams.put("COMPLETED", "0");
                    taskStatsParams.put("DUE_DATE", dueDate);
                    for(int i =0; i<categoriesReocc.length; i++){
                        taskStatsParams.put(categoriesReocc[i].toString(), "1");
                        write.execSQL("UPDATE TASK_CATEGORY_INFO " +
                                "SET NOT_COMPLETED = NOT_COMPLETED + 1 " +
                                "WHERE CATEGORY_NAME = \"" +
                                categoriesReocc[i] + "\"");
                    }
                    write.insert("TASK_STATS", null, taskStatsParams);      //issue here
                }
                gcal.add(Calendar.DATE, 1);
            }
        }
        catch (Exception e){
            Log.d("ReoccTest", "Did not work");
        }
    }

    public static  boolean enterCatInDB(Context c, String categoryName, Integer color) {
        TimeTrackerDataBaseHelper helper = TimeTrackerDataBaseHelper.getInstance(c);
        SQLiteDatabase read = helper.getReadableDatabase();
        SQLiteDatabase write = helper.getWritableDatabase();
        Cursor check = read.query("TASK_CATEGORY_INFO", new String[] {"CATEGORY_NAME"}, null, null, null, null, null);
        boolean exists = false;
        check.moveToFirst();
        for(int i =0; i<check.getCount(); i++){
            String name = check.getString(0);
            Log.d("CategorySQL", name);
            if(name.equalsIgnoreCase(categoryName)){
                //break and send toast or some shit
                //Toast.makeText(c, "This Category Already Exists!", Toast.LENGTH_SHORT).show();
                Log.d("CategorySQL", "It was in DB already");
                exists = true;
                break;
            }
            check.moveToNext();
        }
        if(exists){
            return false;
        }
        else{
            write.execSQL("ALTER TABLE TASK_STATS ADD COLUMN " + categoryName + " BOOLEAN;");
            ContentValues entry = new ContentValues();
            entry.put("CATEGORY_NAME", categoryName);
            entry.put("COLOR", color);
            entry.put("COMPLETED", 0);
            entry.put("NOT_COMPLETED", 0);
            entry.put("NOT_ON_TIME", 0);
            entry.put("ON_TIME", 0);
            write.insert("TASK_CATEGORY_INFO", null, entry);
            Log.d("CategorySQL", "We put in DB");
            //Toast.makeText(c, "The Category " + categoryName+ " was made", Toast.LENGTH_SHORT).show();
            return true;
            //TaskCreatorFragment.categoryName = null;
            //TaskCreatorFragment.color = null;
        }
    }

    public static void enterTaskInDB(Context c, String taskName, String dueDate, String startTime, String endTime, CharSequence[] taskCategoryNames) {
        TimeTrackerDataBaseHelper categoryHelper = new TimeTrackerDataBaseHelper(c);
        SQLiteDatabase write = categoryHelper.getWritableDatabase();
        SQLiteDatabase read = categoryHelper.getReadableDatabase();

        ContentValues recordParamaters = new ContentValues();                   //insert task info, insert task stats
        recordParamaters.put("TASK_NAME", taskName);
        //recordParamaters.put("TASK_CATEGORY", TaskCreatorFragment.taskCategoryName);
        recordParamaters.put("DUE_DATE", dueDate);
        recordParamaters.put("START_TIME", startTime);
        recordParamaters.put("END_TIME", endTime);
        recordParamaters.put("NOTIFICATION", 1);

        //construct date value, start time value, end time value
        write.insert("TASK_INFORMATION", null, recordParamaters);
        Log.d("InsertTaskTest", "it worked");

        Cursor id = read.rawQuery("SELECT MAX(_ID) FROM TASK_INFORMATION", null);
        id.moveToFirst();
        Cursor notifCursor = read.rawQuery("SELECT * FROM TASK_INFORMATION WHERE _ID = " + id.getInt(0), null);
        createNotif(c, notifCursor);

        id.moveToFirst();
        ContentValues recordParamaterstwo = new ContentValues();
        recordParamaterstwo.put("TASK_NAME", taskName);
        recordParamaterstwo.put("CATEGORY_GENERAL", "1");
        recordParamaterstwo.put("TASK_ID", id.getString(0));
        recordParamaterstwo.put("NOT_COMPLETED", "1");
        recordParamaterstwo.put("COMPLETED", "0");
        recordParamaterstwo.put("ON_TIME", "0");
        recordParamaterstwo.put("NOT_ON_TIME", "0");
        recordParamaterstwo.put("DUE_DATE", dueDate);
        Log.d("TestAddTask", taskCategoryNames.length + "");
        for(int i =0; i<taskCategoryNames.length; i++){
            Log.d("TestAddTask", taskCategoryNames[i].toString());
            recordParamaterstwo.put(taskCategoryNames[i].toString(), "1");
            write.execSQL("UPDATE TASK_CATEGORY_INFO " +
                    "SET NOT_COMPLETED = NOT_COMPLETED + 1 " +
                    "WHERE CATEGORY_NAME = \"" +
                    taskCategoryNames[i] + "\"");
            // write.execSQL("UPDATE TASK_CATEGORY_INFO SET NOT_COMPLETED = NOT_COMPLETED + 1 WHERE CATEGORY_NAME = " + "\"Jul\"");
        }
        write.insert("TASK_STATS", null, recordParamaterstwo);      //issue here
        Log.d("InsertTaskTest", "it worked");
    }

    public static Cursor getTasksGivenDate(Context c, String date){
        //Cursor data = SQLfunctionHelper.getTasksGivenDate(getContext(), TaskCreatorFragment.constructDateStr(Integer.parseInt(daterep[5]), monthMapper.get(daterep[1]), Integer.parseInt(daterep[2])));
        TimeTrackerDataBaseHelper helper = TimeTrackerDataBaseHelper.getInstance(c);
        SQLiteDatabase read = helper.getReadableDatabase();
        Cursor result;
        result = read.query("TASK_STATS", new String[] {"TASK_NAME", "COMPLETED", "NOT_COMPLETED"}, "DUE_DATE = ?", new String[] {date}, null, null, null);
        return result;
    }

    public static ArrayList<StatsFragment.DayStats> getWeekOnTimeTasksFilter(Context c, StatsFragment frag, String d){
        GregorianCalendar gcal = new GregorianCalendar();
        DateFormat formatter = new SimpleDateFormat("yy-MM-dd");
        Date endDate = null;
        try {
            endDate = formatter.parse(d);
        } catch (ParseException e) {
            Log.d("StatsLineFilter", "We have a fucking exception");
            endDate = Calendar.getInstance().getTime();
        }
        Log.d("StatsLineFilter", endDate.toString());
        gcal.setTime(endDate);
        TimeTrackerDataBaseHelper helper = TimeTrackerDataBaseHelper.getInstance(c);
        SQLiteDatabase read = helper.getReadableDatabase();
        ArrayList<StatsFragment.DayStats> data = new ArrayList<>();
        Map<String, Integer> monthMapper = new HashMap<>();
        monthMapper.put("Jan", 0);
        monthMapper.put("Feb", 1);
        monthMapper.put("Mar", 2);
        monthMapper.put("Apr", 3);
        monthMapper.put("May", 4);
        monthMapper.put("Jun", 5);
        monthMapper.put("Jul", 6);
        monthMapper.put("Aug", 7);
        monthMapper.put("Sep", 8);
        monthMapper.put("Oct", 9);
        monthMapper.put("Nov", 10);
        monthMapper.put("Dec", 11);
        for(int i =0; i<7; i++){
            Log.d("StatsTimeDebug", gcal.getTime().toString());     //split into word array, then collect indexes 1,2,5 month/day/year into 5,1,2 for sql
            String[] daterep = gcal.getTime().toString().split(" ");
            String date = TaskCreatorFragment.constructDateStr(Integer.parseInt(daterep[5]), monthMapper.get(daterep[1]), Integer.parseInt(daterep[2]));
            Log.d("DayStatsDebug","SELECT * FROM TASK_STATS WHERE DUE_DATE = " + date + " AND COMPLETED = 1" );
            Log.d("DayStatsDebug","SELECT * FROM TASK_STATS WHERE DUE_DATE = " + date + " AND NOT_COMPLETED = 1");
            Log.d("DayStatsDebug","SELECT * FROM TASK_STATS WHERE DUE_DATE = " + date + " AND ON_TIME = 1");
            Log.d("DayStatsDebug", "SELECT * FROM TASK_STATS WHERE DUE_DATE = " + date + " AND NOT_ON_TIME = 1");
            Cursor dayQuery = read.rawQuery("SELECT * FROM TASK_STATS WHERE DUE_DATE = " + date + " AND COMPLETED = 1", null);
            int completed = dayQuery.getCount();
            dayQuery = read.rawQuery("SELECT * FROM TASK_STATS WHERE DUE_DATE = '" + date + "' AND NOT_COMPLETED = 1", null);
            int incompleted = dayQuery.getCount();
            dayQuery = read.rawQuery("SELECT * FROM TASK_STATS WHERE DUE_DATE = '" + date + "' AND ON_TIME = 1", null);
            int onTime = dayQuery.getCount();
            dayQuery = read.rawQuery("SELECT * FROM TASK_STATS WHERE DUE_DATE = '" + date + "' AND NOT_ON_TIME = 1", null);
            int late = dayQuery.getCount();
            data.add(frag.new DayStats(date, completed, incompleted, onTime, late));
            gcal.add(Calendar.DATE, -1);
        }
        Log.d("StatsCatDateDebug", data.toString());
        return data;
    }

    public static ArrayList<StatsFragment.CategoryStats> filterBarGraph(CharSequence[] categories, Context c, StatsFragment frag){
        String command = "SELECT * FROM TASK_CATEGORY_INFO WHERE ";
        for(int i =0; i<categories.length; i++){
            command += "CATEGORY_NAME = '" + categories[i].toString() + "' OR ";
        }
        command = command.substring(0, command.length()-3);
        Log.d("StatsBarDebug", command);
        TimeTrackerDataBaseHelper helper = TimeTrackerDataBaseHelper.getInstance(c);
        SQLiteDatabase read = helper.getReadableDatabase();
        Cursor categoryStats = read.rawQuery(command, null);
        categoryStats.moveToFirst();
        ArrayList<StatsFragment.CategoryStats> data = new ArrayList<>();
        for(int i =0; i< categoryStats.getCount(); i++){
            StatsFragment.CategoryStats temp = frag.new CategoryStats(categoryStats.getString(1), categoryStats.getInt(2), categoryStats.getInt(3), categoryStats.getInt(4), categoryStats.getInt(6), categoryStats.getInt(5));
            data.add(temp);
            categoryStats.moveToNext();
        }
        Log.d("StatsBarDebug", data.toString());     //we return the correct data, but do not sort it
        return data;
    }

    public static Cursor getTaskInfo(Context c, int id){
        TimeTrackerDataBaseHelper helper = TimeTrackerDataBaseHelper.getInstance(c);
        SQLiteDatabase read = helper.getReadableDatabase();
        return read.rawQuery("SELECT * FROM TASK_INFORMATION WHERE _ID = " + id, null);
    }

    public static Cursor getTaskStats(Context c, int id){
        TimeTrackerDataBaseHelper helper = TimeTrackerDataBaseHelper.getInstance(c);
        SQLiteDatabase read = helper.getReadableDatabase();
        return read.rawQuery("SELECT * FROM TASK_STATS WHERE TASK_ID = " + id, null);
    }

    public static void changeNotification(Context c, int id, boolean state){
        TimeTrackerDataBaseHelper helper = TimeTrackerDataBaseHelper.getInstance(c);            //test
        SQLiteDatabase write = helper.getReadableDatabase();
        Integer val;
        if(state){
            val = 1;
        }
        else{
            val = 0;
        }
        write.execSQL("UPDATE TASK_INFORMATION SET NOTIFICATION = " + val + " WHERE _ID = " + id);
        return;
    }

    public static void createNotif(Context c, Cursor id){
       // NotificationHelper.createTaskNotif(c, id);

    }

    public static void deleteNotif(Context c, Cursor id){

    }


    public static void addCatTaskActivity(Context c, CharSequence[] categories, int id){
        SQLiteDatabase writableDatabase = TimeTrackerDataBaseHelper.getInstance(c).getWritableDatabase();
        SQLiteDatabase readableDatabase = TimeTrackerDataBaseHelper.getInstance(c).getReadableDatabase();
        ContentValues data = new ContentValues();
        Cursor valuesOfTask = readableDatabase.rawQuery("SELECT * FROM TASK_STATS WHERE TASK_ID = " + id, null);
        valuesOfTask.moveToFirst();
        for(int i =0; i<categories.length; i++){
            Cursor temp = readableDatabase.rawQuery("SELECT " + categories[i].toString() + " FROM TASK_STATS WHERE TASK_ID = " + id, null);
            temp.moveToFirst();
            data.put(categories[i].toString(), 1);
            if(temp.getInt(0)==0){
                writableDatabase.execSQL("UPDATE TASK_CATEGORY_INFO SET COMPLETED = COMPLETED + " + valuesOfTask.getInt(4) + " WHERE CATEGORY_NAME = '" + categories[i] + "'");
                writableDatabase.execSQL("UPDATE TASK_CATEGORY_INFO SET NOT_COMPLETED = NOT_COMPLETED + " + valuesOfTask.getInt(6) + " WHERE CATEGORY_NAME = '" + categories[i] + "'");
                writableDatabase.execSQL("UPDATE TASK_CATEGORY_INFO SET NOT_ON_TIME = NOT_ON_TIME + " + valuesOfTask.getInt(7) + " WHERE CATEGORY_NAME = '" + categories[i] + "'");
                writableDatabase.execSQL("UPDATE TASK_CATEGORY_INFO SET ON_TIME = ON_TIME + " + valuesOfTask.getInt(8) + " WHERE CATEGORY_NAME = '" + categories[i] + "'");
            }
        }
        writableDatabase.update("TASK_STATS", data, "TASK_ID = " + id, null);
    }

    public static void removeCatTaskActivity(Context c, CharSequence[] categories, int id){
        SQLiteDatabase writableDatabase = TimeTrackerDataBaseHelper.getInstance(c).getWritableDatabase();
        SQLiteDatabase readableDatabase = TimeTrackerDataBaseHelper.getInstance(c).getReadableDatabase();
        Cursor valuesOfTask = readableDatabase.rawQuery("SELECT * FROM TASK_STATS WHERE TASK_ID = " + id, null);
        valuesOfTask.moveToFirst();
        ContentValues data = new ContentValues();
        for(int i =0; i<categories.length; i++){
            Cursor temp = readableDatabase.rawQuery("SELECT " + categories[i].toString() + " FROM TASK_STATS WHERE TASK_ID = " + id, null);
            temp.moveToFirst();
            data.put(categories[i].toString(), 0);
            if(temp.getInt(0)==1){
                writableDatabase.execSQL("UPDATE TASK_CATEGORY_INFO SET COMPLETED = COMPLETED - " + valuesOfTask.getInt(4) + " WHERE CATEGORY_NAME = '" + categories[i] + "'");
                writableDatabase.execSQL("UPDATE TASK_CATEGORY_INFO SET NOT_COMPLETED = NOT_COMPLETED - " + valuesOfTask.getInt(6) + " WHERE CATEGORY_NAME = '" + categories[i] + "'");
                writableDatabase.execSQL("UPDATE TASK_CATEGORY_INFO SET NOT_ON_TIME = NOT_ON_TIME - " + valuesOfTask.getInt(7) + " WHERE CATEGORY_NAME = '" + categories[i] + "'");
                writableDatabase.execSQL("UPDATE TASK_CATEGORY_INFO SET ON_TIME = ON_TIME - " + valuesOfTask.getInt(8) + " WHERE CATEGORY_NAME = '" + categories[i] + "'");
            }
        }
        writableDatabase.update("TASK_STATS", data, "TASK_ID = " + id, null);
    }

    public static void changeTimeData(Context c, int id, String dueDate, String startTime, String endTime){
        TimeTrackerDataBaseHelper categoryHelper = new TimeTrackerDataBaseHelper(c);
        SQLiteDatabase write = categoryHelper.getWritableDatabase();
        SQLiteDatabase read = categoryHelper.getReadableDatabase();

//        ContentValues taskInfo = new ContentValues();                   //insert task info, insert task stats
//        //recordParamaters.put("TASK_CATEGORY", TaskCreatorFragment.taskCategoryName);
//        taskInfo.put("DUE_DATE", dueDate);
//        taskInfo.put("START_TIME", startTime);
//        taskInfo.put("END_TIME", endTime);

        //write.update("TASK_INFORMATION", taskInfo, "_ID = " + id, null);
        write.execSQL("UPDATE TASK_INFORMATION SET DUE_DATE = '" + dueDate +"', START_TIME = '" + startTime + "', END_TIME = '" + endTime + "' WHERE _ID = " + id);
        Log.d("TaskActivityTimeDebug", "UPDATE TASK_INFORMATION SET DUE_DATE = '" + dueDate +"', START_TIME = '" + startTime + "', END_TIME = '" + endTime + "' WHERE _ID = " + id);
        Log.d("TaskActivityTimeDebug", "UPDATE TASK_STATS SET DUE_DATE = '" + dueDate + "' WHERE TASK_ID = " + id);
//        ContentValues taskStats = new ContentValues();
//        taskStats.put("DUE_DATE", dueDate);
//        write.update("TASK_STATS", taskStats, "TASK_ID = " + id, null);
        write.execSQL("UPDATE TASK_STATS SET DUE_DATE = '" + dueDate + "' WHERE TASK_ID = " + id);

    }

    public static void addToScore(TimeTrackerDataBaseHelper c, boolean onTime){
        //award fifty points if it's just complete, award 100 if its onTime as well
        //add to the today score and for the total score
        SQLiteDatabase write = c.getWritableDatabase();
        if(onTime){
            write.execSQL("UPDATE USER_STATS SET TODAY_SCORE = TODAY_SCORE + 10");
            write.execSQL("UPDATE USER_STATS SET TOTAL_SCORE = TOTAL_SCORE + 10");
        }
        else {
            write.execSQL("UPDATE USER_STATS SET TODAY_SCORE = TODAY_SCORE +5");
            write.execSQL("UPDATE USER_STATS SET TOTAL_SCORE = TOTAL_SCORE +5");
        }

    }

    public static void removeFromScore(TimeTrackerDataBaseHelper c){
        //take away a hundred points if it's late
        //subtract to the today score and for the total score
        SQLiteDatabase write = c.getWritableDatabase();
        write.execSQL("UPDATE USER_STATS SET TODAY_SCORE = TODAY_SCORE - 10");
        write.execSQL("UPDATE USER_STATS SET TOTAL_SCORE = TOTAL_SCORE - 10");
    }

}
