package com.hfad.mytimetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SQLfunctionHelper {

    public static Cursor queryWithString(Context c, String cmd){
        TimeTrackerDataBaseHelper helper = TimeTrackerDataBaseHelper.getInstance(c);
        SQLiteDatabase readableDatabase = helper.getReadableDatabase();
        Log.d("TaskFilterDebug", "This is command: " + cmd);
        return readableDatabase.rawQuery(cmd, null);
    }

    public static Cursor getTaskListLineGraph(String date, boolean green, Context c){
        TimeTrackerDataBaseHelper helper = TimeTrackerDataBaseHelper.getInstance(c);
        SQLiteDatabase readableDatabase = helper.getReadableDatabase();
        if(green){
            Log.d("TaskListDebug," ,"SELECT TASK_NAME FROM TASK_STATS WHERE DUE_DATE=\"" + date + "\" AND ON_TIME=1");
            return readableDatabase.rawQuery("SELECT TASK_NAME FROM TASK_STATS WHERE DUE_DATE = \"" + date + "\" AND ON_TIME = 1", null);
        }
        else{
            Log.d("TaskListDebug," ,"SELECT TASK_NAME FROM TASK_STATS WHERE DUE_DATE=\"" + date + "\" AND NOT_ON_TIME=1");
            return readableDatabase.rawQuery("SELECT TASK_NAME FROM TASK_STATS WHERE DUE_DATE = \"" + date + "\" AND NOT_ON_TIME = 1", null);
        }
    }

    public static Cursor queryWithParams(Context c, String table, String[] fields, String ques, String[] cond){
        TimeTrackerDataBaseHelper db = TimeTrackerDataBaseHelper.getInstance(c);
        SQLiteDatabase read = db.getWritableDatabase();
        return read.query(table, fields, ques, cond, null, null, null);
    }


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
        for(int i =9; i<temp.getColumnCount(); i++){
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
                        removeFromScore(categoryHelper);
                    }
                }
            }
        }
        deleteNotif(c, readableDatabase.rawQuery("SELECT * FROM TASK_INFORMATION WHERE _ID = " + id, null));
        writableDatabase.delete("TASK_STATS", "TASK_ID = ?", new String[]{id+""});
        writableDatabase.delete("TASK_INFORMATION", "_ID = ?", new String[]{id+""});
    }

    public static boolean markComplete(int id, TimeTrackerDataBaseHelper helper, boolean onTime){
        SQLiteDatabase readableDatabase = helper.getReadableDatabase();
        SQLiteDatabase writableDatabase = helper.getWritableDatabase();
        ContentValues completeValue = new ContentValues();
        completeValue.put("COMPLETED", 1);
        completeValue.put("NOT_COMPLETED", 0);
        Cursor temp = readableDatabase.rawQuery("SELECT * FROM TASK_STATS WHERE TASK_ID = " + id, null);
        temp.moveToFirst();
        int completed = temp.getInt(4);
        if(completed==1){       //check to make sure we can't mark complete twice
            return false;
        }
        boolean alreadyMarkedLate = temp.getInt(7)==1;
        if(onTime){
            addToScore(helper, true);
            completeValue.put("ON_TIME", 1);
            completeValue.put("NOT_ON_TIME", 0);
            writableDatabase.update("TASK_STATS", completeValue, "TASK_ID = ?", new String[] {id + ""});
            for(int i =9; i<temp.getColumnCount(); i++){
                if(temp.getInt(i)==1){
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
                if(temp.getInt(i)==1){
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
        return true;
    }

    public static void enterReoccTasksInDB(Context c, Integer startYear, Integer startMonth, Integer startDay,
                                           Integer endYear, Integer endMonth, Integer endDay, Integer startHourReocc, Integer startMinuteReocc,
                                            Integer endHourReocc, Integer endMinuteReocc, Integer[] daysList, String reoccTaskName, CharSequence[] categoriesReocc)  {
        String startDate = TaskCreatorFragment.constructDateStr(startYear, startMonth, startDay);
        String endDate = TaskCreatorFragment.constructDateStr(endYear, endMonth, endDay);
        String startTime = startHourReocc + "-" + startMinuteReocc + "-00";
        String endTime = endHourReocc + "-" + endMinuteReocc + "-00";
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
                    String dueDate = TaskCreatorFragment.constructDateStr(gcal.get(Calendar.YEAR), gcal.get(Calendar.MONTH), gcal.get(Calendar.DAY_OF_MONTH));
                    ContentValues recordParamaters = new ContentValues();                   //insert task info, insert task stats
                    recordParamaters.put("TASK_NAME", reoccTaskName);
                    recordParamaters.put("DUE_DATE", dueDate);
                    recordParamaters.put("START_TIME", startTime);
                    recordParamaters.put("END_TIME", endTime);
                    recordParamaters.put("NOTIFICATION", 1);
                    write.insert("TASK_INFORMATION", null, recordParamaters);
                    Cursor id = read.rawQuery("SELECT MAX(_ID) FROM TASK_INFORMATION", null);
                    id.moveToFirst();
                    Cursor notifCursor = read.rawQuery("SELECT * FROM TASK_INFORMATION WHERE _ID = " + id.getInt(0), null);
                    createNotif(c, notifCursor);
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
            if(name.equalsIgnoreCase(categoryName)){
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
            return true;
        }
    }

    public static void enterTaskInDB(Context c, String taskName, String dueDate, String startTime, String endTime, CharSequence[] taskCategoryNames) {
        TimeTrackerDataBaseHelper categoryHelper = TimeTrackerDataBaseHelper.getInstance(c);
        SQLiteDatabase write = categoryHelper.getWritableDatabase();
        SQLiteDatabase read = categoryHelper.getReadableDatabase();
        ContentValues recordParamaters = new ContentValues();                   //insert task info, insert task stats
        recordParamaters.put("TASK_NAME", taskName);
        recordParamaters.put("DUE_DATE", dueDate);
        recordParamaters.put("START_TIME", startTime);
        recordParamaters.put("END_TIME", endTime);
        recordParamaters.put("NOTIFICATION", 1);
        write.insert("TASK_INFORMATION", null, recordParamaters);
        Cursor id = read.rawQuery("SELECT MAX(_ID) FROM TASK_INFORMATION", null);
        id.moveToFirst();
        Integer lastID = id.getInt(0);
        id = read.rawQuery("SELECT * FROM TASK_INFORMATION WHERE _ID = " + lastID, null);
        createNotif(c, id);
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
            recordParamaterstwo.put(taskCategoryNames[i].toString(), "1");
            write.execSQL("UPDATE TASK_CATEGORY_INFO " +
                    "SET NOT_COMPLETED = NOT_COMPLETED + 1 " +
                    "WHERE CATEGORY_NAME = \"" +
                    taskCategoryNames[i] + "\"");
        }
        write.insert("TASK_STATS", null, recordParamaterstwo);      //issue here
    }

    public static Cursor getTasksGivenDate(Context c, String date){
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
            endDate = Calendar.getInstance().getTime();
        }
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
            String[] daterep = gcal.getTime().toString().split(" ");
            String date = TaskCreatorFragment.constructDateStr(Integer.parseInt(daterep[5]), monthMapper.get(daterep[1]), Integer.parseInt(daterep[2]));
            Cursor dayQuery = read.rawQuery("SELECT * FROM TASK_STATS WHERE DUE_DATE = '" + date + "' AND COMPLETED = 1", null);
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
        return data;
    }

    public static ArrayList<StatsFragment.CategoryStats> filterBarGraph(CharSequence[] categories, Context c, StatsFragment frag){
        String command = "SELECT * FROM TASK_CATEGORY_INFO WHERE ";
        for(int i =0; i<categories.length; i++){
            command += "CATEGORY_NAME = '" + categories[i].toString() + "' OR ";
        }
        command = command.substring(0, command.length()-3);
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
        NotificationHelper.createTaskNotif(c, id);
    }

    public static void deleteNotif(Context c, Cursor id){
        NotificationHelper.deleteTaskNotif(c, id);
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
        TimeTrackerDataBaseHelper categoryHelper = TimeTrackerDataBaseHelper.getInstance(c);
        SQLiteDatabase write = categoryHelper.getWritableDatabase();
        write.execSQL("UPDATE TASK_INFORMATION SET DUE_DATE = '" + dueDate +"', START_TIME = '" + startTime + "', END_TIME = '" + endTime + "' WHERE _ID = " + id);
        write.execSQL("UPDATE TASK_STATS SET DUE_DATE = '" + dueDate + "' WHERE TASK_ID = " + id);
    }

    public static void addToScore(TimeTrackerDataBaseHelper c, boolean onTime){
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
        SQLiteDatabase write = c.getWritableDatabase();
        write.execSQL("UPDATE USER_STATS SET TODAY_SCORE = TODAY_SCORE - 10");
        write.execSQL("UPDATE USER_STATS SET TOTAL_SCORE = TOTAL_SCORE - 10");
    }

}
