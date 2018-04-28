package com.hfad.mytimetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class TaskFilterMultiCategoryTests {

    Context context;
    TimeTrackerDataBaseHelper helper;
    SQLiteDatabase read;
    SQLiteDatabase write;

    boolean completed;
    boolean notCompleted;
    boolean onTime;
    boolean notOnTime;

    String sqlCommand;

    String categoryName1;
    String categoryName2;
    String categoryName3;
    String categoryName4;
    CharSequence[] testCategories = new CharSequence[2];

    @Before
    public void deleteDatabaseTasks() {
        context = InstrumentationRegistry.getTargetContext();
        helper = new TimeTrackerDataBaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("DELETE FROM TASK_STATS");
        db.execSQL("DELETE FROM TASK_INFORMATION");
        db.execSQL("DELETE FROM TASK_CATEGORY_INFO");
        db.execSQL("DROP TABLE TASK_STATS");
        db.execSQL("CREATE TABLE TASK_STATS ("
                + "_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "TASK_NAME TEXT, "
                + "TASK_ID INTEGER,"
                + "CATEGORY_GENERAL BOOLEAN, "
                + "COMPLETED BOOLEAN, "
                + "DUE_DATE DATE, "
                + "NOT_COMPLETED BOOLEAN, "
                + "NOT_ON_TIME BOOLEAN, "
                + "ON_TIME BOOLEAN);");

        read = helper.getReadableDatabase();
        write = helper.getReadableDatabase();

        categoryName1 = "newcategory85";
        categoryName2 = "newcategory86";
        categoryName3 = "newcategory87";
        categoryName4 = "newcategory88";
        SQLfunctionHelper.enterCatInDB(context, categoryName1, 1);
        SQLfunctionHelper.enterCatInDB(context, categoryName2, 1);
        SQLfunctionHelper.enterCatInDB(context, categoryName3, 1);
        SQLfunctionHelper.enterCatInDB(context, categoryName4, 1);

        //These are the tasks the tests are looking for
        testCategories[0] = categoryName2; testCategories[1] = categoryName4;
        insertTaskStats(testCategories, "Task Punctual Complete", "0", "1", "1", "0");
        insertTaskStats(testCategories, "Task Punctual Incomplete", "1", "0", "1", "0");
        insertTaskStats(testCategories, "Task Late Complete", "0", "1", "0", "1");
        insertTaskStats(testCategories, "Task Late Incomplete", "1", "0", "0", "1");

        //These are tasks we are not looking for.
        CharSequence[] misleadingCategory1 = {categoryName1};
        CharSequence[] misleadingCategory2 = {categoryName3};
        insertTaskStats(misleadingCategory1, "Wrong Task Category 1", "0", "1", "1", "0");
        insertTaskStats(misleadingCategory1, "Wrong Task Category 1", "1", "0", "1", "0");
        insertTaskStats(misleadingCategory1, "Wrong Task Category 1", "0", "1", "0", "1");
        insertTaskStats(misleadingCategory1, "Wrong Task Category 1", "1", "0", "0", "1");

        insertTaskStats(misleadingCategory2, "Wrong Task Category 3", "0", "1", "1", "0");
        insertTaskStats(misleadingCategory2, "Wrong Task Category 3", "1", "0", "1", "0");
        insertTaskStats(misleadingCategory2, "Wrong Task Category 3", "0", "1", "0", "1");
        insertTaskStats(misleadingCategory2, "Wrong Task Category 3", "1", "0", "0", "1");
    }

    //Pass
    @Test
    public void allChoices() {
        completed = true; notCompleted = true; onTime = true; notOnTime = true;

        Cursor data = constructCommand(testCategories);
        ArrayList<String> tasks = new ArrayList<>();
        if (data.moveToFirst()) {
            do {
                tasks.add(data.getString(data.getColumnIndex("TASK_NAME")));
            } while(data.moveToNext());
        }

        assertEquals(4, tasks.size());
        assertTrue(tasks.contains("Task Punctual Complete"));
        assertTrue(tasks.contains("Task Punctual Incomplete"));
        assertTrue(tasks.contains("Task Late Complete"));
        assertTrue(tasks.contains("Task Late Incomplete"));
    }

    //Pass
    @Test
    public void punctualCompleteNonComplete() {
        completed = true; notCompleted = true; onTime = true; notOnTime = false;

        Cursor data = constructCommand(testCategories);
        ArrayList<String> tasks = new ArrayList<>();
        if (data.moveToFirst()) {
            do {
                tasks.add(data.getString(data.getColumnIndex("TASK_NAME")));
            } while(data.moveToNext());
        }

        assertEquals(2, tasks.size());
        assertTrue(tasks.contains("Task Punctual Complete"));
        assertTrue(tasks.contains("Task Punctual Incomplete"));
    }

    //Pass
    @Test
    public void lateCompleteNonComplete() {
        completed = true; notCompleted = true; onTime = false; notOnTime = true;

        Cursor data = constructCommand(testCategories);
        ArrayList<String> tasks = new ArrayList<>();
        if (data.moveToFirst()) {
            do {
                tasks.add(data.getString(data.getColumnIndex("TASK_NAME")));
            } while(data.moveToNext());
        }

        assertEquals(2, tasks.size());
        assertTrue(tasks.contains("Task Late Complete"));
        assertTrue(tasks.contains("Task Late Incomplete"));
    }

    //Pass
    @Test
    public void completeAnyTime() {
        completed = true; notCompleted = false; onTime = true; notOnTime = true;

        Cursor data = constructCommand(testCategories);
        ArrayList<String> tasks = new ArrayList<>();
        if (data.moveToFirst()) {
            do {
                tasks.add(data.getString(data.getColumnIndex("TASK_NAME")));
            } while(data.moveToNext());
        }

        assertEquals(2, tasks.size());
        assertTrue(tasks.contains("Task Punctual Complete"));
        assertTrue(tasks.contains("Task Late Complete"));
    }

    //Pass
    @Test
    public void incompleteAnyTime() {
        completed = false; notCompleted = true; onTime = true; notOnTime = true;

        Cursor data = constructCommand(testCategories);
        ArrayList<String> tasks = new ArrayList<>();
        if (data.moveToFirst()) {
            do {
                tasks.add(data.getString(data.getColumnIndex("TASK_NAME")));
            } while(data.moveToNext());
        }

        assertEquals(2, tasks.size());
        assertTrue(tasks.contains("Task Punctual Incomplete"));
        assertTrue(tasks.contains("Task Late Incomplete"));
    }

    //Pass
    @Test
    public void punctualComplete() {
        completed = true; notCompleted = false; onTime = true; notOnTime = false;

        Cursor data = constructCommand(testCategories);
        ArrayList<String> tasks = new ArrayList<>();
        if (data.moveToFirst()) {
            do {
                tasks.add(data.getString(data.getColumnIndex("TASK_NAME")));
            } while(data.moveToNext());
        }

        assertEquals(1, tasks.size());
        assertTrue(tasks.contains("Task Punctual Complete"));
    }

    //Pass
    @Test
    public void PunctualIncomplete() {
        completed = false; notCompleted = true; onTime = true; notOnTime = false;

        Cursor data = constructCommand(testCategories);
        ArrayList<String> tasks = new ArrayList<>();
        if (data.moveToFirst()) {
            do {
                tasks.add(data.getString(data.getColumnIndex("TASK_NAME")));
            } while(data.moveToNext());
        }

        assertEquals(1, tasks.size());
        assertTrue(tasks.contains("Task Punctual Incomplete"));
    }

    //Pass
    @Test
    public void lateComplete() {
        completed = true; notCompleted = false; onTime = false; notOnTime = true;

        Cursor data = constructCommand(testCategories);
        ArrayList<String> tasks = new ArrayList<>();
        if (data.moveToFirst()) {
            do {
                tasks.add(data.getString(data.getColumnIndex("TASK_NAME")));
            } while(data.moveToNext());
        }

        assertEquals(1, tasks.size());
        assertTrue(tasks.contains("Task Late Complete"));
    }

    //Pass
    @Test
    public void lateIncomplete() {
        completed = false; notCompleted = true; onTime = false; notOnTime = true;

        Cursor data = constructCommand(testCategories);
        ArrayList<String> tasks = new ArrayList<>();
        if (data.moveToFirst()) {
            do {
                tasks.add(data.getString(data.getColumnIndex("TASK_NAME")));
            } while(data.moveToNext());
        }

        assertEquals(1, tasks.size());
        assertTrue(tasks.contains("Task Late Incomplete"));
    }

    //Pass
    @Test
    public void lateOnly() {
        completed = false; notCompleted = false; onTime = false; notOnTime = true;

        Cursor data = constructCommand(testCategories);
        ArrayList<String> tasks = new ArrayList<>();
        if (data.moveToFirst()) {
            do {
                tasks.add(data.getString(data.getColumnIndex("TASK_NAME")));
            } while(data.moveToNext());
        }

        assertEquals(2, tasks.size());
        assertTrue(tasks.contains("Task Late Complete"));
        assertTrue(tasks.contains("Task Late Incomplete"));
    }

    //Pass
    @Test
    public void punctualOnly() {
        completed = false; notCompleted = false; onTime = true; notOnTime = false;

        Cursor data = constructCommand(testCategories);
        ArrayList<String> tasks = new ArrayList<>();
        if (data.moveToFirst()) {
            do {
                tasks.add(data.getString(data.getColumnIndex("TASK_NAME")));
            } while(data.moveToNext());
        }

        assertEquals(2, tasks.size());
        assertTrue(tasks.contains("Task Punctual Complete"));
        assertTrue(tasks.contains("Task Punctual Incomplete"));
    }

    //Failed
    @Test
    public void incompleteOnly() {
        completed = false; notCompleted = true; onTime = false; notOnTime = false;

        Cursor data = constructCommand(testCategories);
        ArrayList<String> tasks = new ArrayList<>();
        if (data.moveToFirst()) {
            do {
                tasks.add(data.getString(data.getColumnIndex("TASK_NAME")));
            } while(data.moveToNext());
        }

        assertEquals(2, tasks.size());
        assertTrue(tasks.contains("Task Punctual Incomplete"));
        assertTrue(tasks.contains("Task Late Incomplete"));
    }

    //Failed
    @Test
    public void completeOnly() {
        completed = true; notCompleted = false; onTime = false; notOnTime = false;

        Cursor data = constructCommand(testCategories);
        ArrayList<String> tasks = new ArrayList<>();
        if (data.moveToFirst()) {
            do {
                tasks.add(data.getString(data.getColumnIndex("TASK_NAME")));
            } while(data.moveToNext());
        }

        assertEquals(2, tasks.size());
        assertTrue(tasks.contains("Task Punctual Complete"));
        assertTrue(tasks.contains("Task Late Complete"));
    }

    private void insertTaskStats(CharSequence[] taskCategoryNames, String taskName, String incomplete, String completed, String punctual, String late) {
        Cursor id = read.rawQuery("SELECT MAX(_ID) FROM TASK_INFORMATION", null);
        id.moveToFirst();
        ContentValues recordParamaterstwo = new ContentValues();
        recordParamaterstwo.put("TASK_NAME", taskName);
        recordParamaterstwo.put("CATEGORY_GENERAL", "1");
        recordParamaterstwo.put("TASK_ID", id.getString(0));
        recordParamaterstwo.put("NOT_COMPLETED", incomplete);
        recordParamaterstwo.put("COMPLETED", completed);
        recordParamaterstwo.put("ON_TIME", punctual);
        recordParamaterstwo.put("NOT_ON_TIME", late);
        recordParamaterstwo.put("DUE_DATE", "12-00-00");
        for(int i =0; i<taskCategoryNames.length; i++){
            recordParamaterstwo.put(taskCategoryNames[i].toString(), "1");
            write.execSQL("UPDATE TASK_CATEGORY_INFO " +
                    "SET NOT_COMPLETED = NOT_COMPLETED + 1 " +
                    "WHERE CATEGORY_NAME = \"" +
                    taskCategoryNames[i] + "\"");
            // write.execSQL("UPDATE TASK_CATEGORY_INFO SET NOT_COMPLETED = NOT_COMPLETED + 1 WHERE CATEGORY_NAME = " + "\"Jul\"");
        }
        write.insert("TASK_STATS", null, recordParamaterstwo);      //issue here
    }

    private Cursor constructCommand(CharSequence[] taskCategoryNames) {
        if (completed == false && notCompleted == false) {
            completed = true;
            notCompleted = true;
        }
        if (onTime == false && notOnTime == false) {
            onTime = true;
            notCompleted = true;
        }
        StringBuilder cmd = new StringBuilder();
        cmd.append("SELECT * FROM TASK_STATS WHERE ( ");
        for (int i = 0; i < taskCategoryNames.length - 1; i++) {
            cmd.append(taskCategoryNames[i].toString() + " = 1 OR");
        }
        cmd.append(" " + taskCategoryNames[taskCategoryNames.length - 1] + "=1 ) ");
        if (completed) {
            cmd.append("AND ( COMPLETED = 1 ");
        } else {
            cmd.append("AND ( COMPLETED = 0 ");
        }
        if (notCompleted) {
            cmd.append(" OR NOT_COMPLETED = 1 ) ");
        } else {
            cmd.append("OR NOT_COMPLETED = 0 ) ");
        }
        if (onTime) {
            cmd.append("AND ( ON_TIME = 1 ");
        } else {
            cmd.append("AND ( ON_TIME = 0 ");
        }
        if (notOnTime) {
            cmd.append(" OR NOT_ON_TIME = 1 )");
        } else {
            cmd.append("OR NOT_ON_TIME = 0 )");
        }
        sqlCommand = new String(cmd);
        Cursor result = read.rawQuery(sqlCommand, null);
        Log.d("FilterDebug", sqlCommand);
        return result;

    }
}
