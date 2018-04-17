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

    public static void deleteTask(int id, SQLiteDatabase readableDatabase, SQLiteDatabase writableDatabase){
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
        writableDatabase.delete("TASK_STATS", "TASK_ID = ?", new String[]{id+""});
        writableDatabase.delete("TASK_INFORMATION", "_ID = ?", new String[]{id+""});
    }

    public static void markComplete(int id, SQLiteDatabase readableDatabase, SQLiteDatabase writableDatabase, boolean onTime){
        //ADD a check for the general category
        ContentValues completeValue = new ContentValues();
        completeValue.put("COMPLETED", 1);
        completeValue.put("NOT_COMPLETED", 0);
        //writableDatabase = (new TimeTrackerDataBaseHelper(getActivity())).getWritableDatabase();
        Cursor temp = readableDatabase.rawQuery("SELECT * FROM TASK_STATS WHERE TASK_ID = " + id, null);
        temp.moveToFirst();
        //we have to check to see if it's on time or not, mark those values in TASK_STATS
        Log.d("MarkCompleteDebug", "Is it on time: " + onTime);
        if(onTime){
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

                    writableDatabase.execSQL("UPDATE TASK_CATEGORY_INFO " +
                            "SET NOT_ON_TIME = NOT_ON_TIME + 1 " +
                            "WHERE CATEGORY_NAME = \"" +
                            temp.getColumnName(i) + "\"");
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
                    //construct date value, start time value, end time value
                    write.insert("TASK_INFORMATION", null, recordParamaters);

                    Cursor id = read.rawQuery("SELECT MAX(_ID) FROM TASK_INFORMATION", null);
                    id.moveToFirst();

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

    public static  boolean enterCatInDB(Context c, SQLiteDatabase read, SQLiteDatabase write, String cat, String categoryName, Integer color) {
        Cursor check = read.query("TASK_CATEGORY_INFO", new String[] {"CATEGORY_NAME"}, null, null, null, null, null);
        boolean exists = false;
        check.moveToFirst();
        for(int i =0; i<check.getCount(); i++){
            String name = check.getString(0);
            Log.d("CategorySQL", name);
            if(name.equalsIgnoreCase(cat)){
                //break and send toast or some shit
                Toast.makeText(c, "This Category Already Exists!", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(c, "The Category " + categoryName+ " was made", Toast.LENGTH_SHORT).show();
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
        //construct date value, start time value, end time value
        write.insert("TASK_INFORMATION", null, recordParamaters);
        Log.d("InsertTaskTest", "it worked");

        Cursor id = read.rawQuery("SELECT MAX(_ID) FROM TASK_INFORMATION", null);
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

    public static ArrayList<StatsFragment.CategoryStats> getFiveBestCompleteCategories(Context c, StatsFragment frag){
        TimeTrackerDataBaseHelper helper = TimeTrackerDataBaseHelper.getInstance(c);
        SQLiteDatabase read = helper.getReadableDatabase();
        Cursor categoryStats = read.rawQuery("SELECT * FROM TASK_CATEGORY_INFO", null);
        categoryStats.moveToFirst();
        ArrayList<StatsFragment.CategoryStats> data = new ArrayList<>();
        for(int i =0; i< categoryStats.getCount(); i++){
            StatsFragment.CategoryStats temp = frag.new CategoryStats(categoryStats.getString(1), categoryStats.getInt(2), categoryStats.getInt(3), categoryStats.getInt(4), categoryStats.getInt(5), categoryStats.getInt(6));
            data.add(temp);
            categoryStats.moveToNext();
        }
        Log.d("StatsCategoriesDebug", data.toString());     //we return the correct data
        return data;
    }

    public static ArrayList<StatsFragment.CategoryStats> getFiveWorstCompleteCategories(Context c, StatsFragment frag){
        TimeTrackerDataBaseHelper helper = TimeTrackerDataBaseHelper.getInstance(c);
        SQLiteDatabase read = helper.getReadableDatabase();
        Cursor categoryStats = read.rawQuery("SELECT * FROM TASK_CATEGORY_INFO", null);
        categoryStats.moveToFirst();
        ArrayList<StatsFragment.CategoryStats> data = new ArrayList<>();
        for(int i =0; i< categoryStats.getCount(); i++){
            StatsFragment.CategoryStats temp = frag.new CategoryStats(categoryStats.getString(1), categoryStats.getInt(2), categoryStats.getInt(3), categoryStats.getInt(4), categoryStats.getInt(5), categoryStats.getInt(6));
            data.add(temp);
            categoryStats.moveToNext();
        }
        Log.d("StatsCategoriesDebug", data.toString());     //we return the correct data
        return data;
    }

    public static ArrayList<StatsFragment.CategoryStats> getFiveBestOnTimeCategories(Context c, StatsFragment frag){
        TimeTrackerDataBaseHelper helper = TimeTrackerDataBaseHelper.getInstance(c);
        SQLiteDatabase read = helper.getReadableDatabase();
        Cursor categoryStats = read.rawQuery("SELECT * FROM TASK_CATEGORY_INFO", null);
        categoryStats.moveToFirst();
        ArrayList<StatsFragment.CategoryStats> data = new ArrayList<>();
        for(int i =0; i< categoryStats.getCount(); i++){
            StatsFragment.CategoryStats temp = frag.new CategoryStats(categoryStats.getString(1), categoryStats.getInt(2), categoryStats.getInt(3), categoryStats.getInt(4), categoryStats.getInt(5), categoryStats.getInt(6));
            data.add(temp);
            categoryStats.moveToNext();
        }
        Log.d("StatsCategoriesDebug", data.toString());     //we return the correct data
        return data;
    }

    public static ArrayList<StatsFragment.CategoryStats> getFiveWorstOnTimeCategories(Context c, StatsFragment frag){
        TimeTrackerDataBaseHelper helper = TimeTrackerDataBaseHelper.getInstance(c);
        SQLiteDatabase read = helper.getReadableDatabase();
        Cursor categoryStats = read.rawQuery("SELECT * FROM TASK_CATEGORY_INFO", null);
        categoryStats.moveToFirst();
        ArrayList<StatsFragment.CategoryStats> data = new ArrayList<>();
        for(int i =0; i< categoryStats.getCount(); i++){
            StatsFragment.CategoryStats temp = frag.new CategoryStats(categoryStats.getString(1), categoryStats.getInt(2), categoryStats.getInt(3), categoryStats.getInt(4), categoryStats.getInt(5), categoryStats.getInt(6));
            data.add(temp);
            categoryStats.moveToNext();
        }
        Log.d("StatsCategoriesDebug", data.toString());     //we return the correct data, but do not sort it
        return data;
    }

    public static ArrayList<StatsFragment.DayStats> getWeekOnTimeTasks(Context c, StatsFragment frag){
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTime(Calendar.getInstance().getTime());
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
            Cursor dayQuery = read.rawQuery("SELECT * FROM TASK_STATS WHERE DUE_DATE = " + date + " AND COMPLETED = 1", null);
            int completed = dayQuery.getCount();
            dayQuery = read.rawQuery("SELECT * FROM TASK_STATS WHERE DUE_DATE = " + date + " AND NOT_COMPLETED = 1", null);
            int incompleted = dayQuery.getCount();
            dayQuery = read.rawQuery("SELECT * FROM TASK_STATS WHERE DUE_DATE = " + date + " AND ON_TIME = 1", null);
            int onTime = dayQuery.getCount();
            dayQuery = read.rawQuery("SELECT * FROM TASK_STATS WHERE DUE_DATE = " + date + " AND NOT_ON_TIME = 1", null);
            int late = dayQuery.getCount();
            data.add(frag.new DayStats(date, completed, incompleted, onTime, late));
            gcal.add(Calendar.DATE, -1);
        }
        Log.d("StatsCatDateDebug", data.toString());
        return data;
    }

    public static ArrayList<StatsFragment.DayStats> getWeekCompleteTasks(Context c, StatsFragment frag){
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTime(Calendar.getInstance().getTime());
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
            Cursor dayQuery = read.rawQuery("SELECT * FROM TASK_STATS WHERE DUE_DATE = " + date + " AND COMPLETED = 1", null);
            int completed = dayQuery.getCount();
            dayQuery = read.rawQuery("SELECT * FROM TASK_STATS WHERE DUE_DATE = " + date + " AND NOT_COMPLETED = 1", null);
            int incompleted = dayQuery.getCount();
            dayQuery = read.rawQuery("SELECT * FROM TASK_STATS WHERE DUE_DATE = " + date + " AND ON_TIME = 1", null);
            int onTime = dayQuery.getCount();
            dayQuery = read.rawQuery("SELECT * FROM TASK_STATS WHERE DUE_DATE = " + date + " AND NOT_ON_TIME = 1", null);
            int late = dayQuery.getCount();
            data.add(frag.new DayStats(date, completed, incompleted, onTime, late));
            gcal.add(Calendar.DATE, -1);
        }
        Log.d("StatsCatDateDebug", data.toString());
        return data;
    }

    public static ArrayList<StatsFragment.CategoryStats> getLateTasks(Context c, StatsFragment frag){
        TimeTrackerDataBaseHelper helper = TimeTrackerDataBaseHelper.getInstance(c);
        SQLiteDatabase read = helper.getReadableDatabase();
        Cursor categoryStats = read.rawQuery("SELECT * FROM TASK_CATEGORY_INFO", null);
        categoryStats.moveToFirst();
        ArrayList<StatsFragment.CategoryStats> data = new ArrayList<>();
        for(int i =0; i< categoryStats.getCount(); i++){
            StatsFragment.CategoryStats temp = frag.new CategoryStats(categoryStats.getString(1), categoryStats.getInt(2), categoryStats.getInt(3), categoryStats.getInt(4), categoryStats.getInt(5), categoryStats.getInt(6));
            data.add(temp);
            categoryStats.moveToNext();
        }
        Log.d("StatsCategoriesDebug", data.toString());     //we return the correct data, but do not sort it
        return data;
    }

    public static ArrayList<StatsFragment.CategoryStats> getInCompleteTasks(Context c, StatsFragment frag){
        TimeTrackerDataBaseHelper helper = TimeTrackerDataBaseHelper.getInstance(c);
        SQLiteDatabase read = helper.getReadableDatabase();
        Cursor categoryStats = read.rawQuery("SELECT * FROM TASK_CATEGORY_INFO", null);
        categoryStats.moveToFirst();
        ArrayList<StatsFragment.CategoryStats> data = new ArrayList<>();
        for(int i =0; i< categoryStats.getCount(); i++){
            StatsFragment.CategoryStats temp = frag.new CategoryStats(categoryStats.getString(1), categoryStats.getInt(2), categoryStats.getInt(3), categoryStats.getInt(4), categoryStats.getInt(5), categoryStats.getInt(6));
            data.add(temp);
            categoryStats.moveToNext();
        }
        Log.d("StatsCategoriesDebug", data.toString());     //we return the correct data, but do not sort it
        return data;
    }

}
