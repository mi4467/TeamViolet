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

public class TaskFilterSingleCategoryTests {

    Context context;
    TimeTrackerDataBaseHelper helper;
    SQLiteDatabase read;
    SQLiteDatabase write;

    boolean completed;
    boolean notCompleted;
    boolean onTime;
    boolean notOnTime;

    CharSequence[] taskCategoryNames = new CharSequence[1];
    String sqlCommand;

    @Before
    public void deleteDatabaseTasks() {
        context = InstrumentationRegistry.getTargetContext();
        helper = new TimeTrackerDataBaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from TASK_STATS");
        db.execSQL("delete from TASK_INFORMATION");
        db.execSQL("delete from TASK_CATEGORY_INFO");

        read = helper.getReadableDatabase();
        write = helper.getReadableDatabase();

        String categoryName = "newcategory80";
        taskCategoryNames[0] = categoryName;
        SQLfunctionHelper.enterCatInDB(context, categoryName, 1);

        insertTaskStats("Task Punctual Complete", "0", "1", "1", "0");
        insertTaskStats("Task Punctual Incomplete", "1", "0", "1", "0");
        insertTaskStats("Task Late Complete", "0", "1", "0", "1");
        insertTaskStats("Task Late Incomplete", "1", "0", "0", "1");
    }

    @Test
    public void allChoices() {
        completed = true; notCompleted = true; onTime = true; notOnTime = true;

        Cursor data = constructCommand();
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

    @Test
    public void punctualCompleteNonComplete() {
        completed = true; notCompleted = true; onTime = true; notOnTime = false;

        Cursor data = constructCommand();
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

    @Test
    public void lateCompleteNonComplete() {
        completed = true; notCompleted = true; onTime = false; notOnTime = true;

        Cursor data = constructCommand();
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

    @Test
    public void completeAnyTime() {
        completed = true; notCompleted = false; onTime = true; notOnTime = true;

        Cursor data = constructCommand();
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

    @Test
    public void incompleteAnyTime() {
        completed = false; notCompleted = true; onTime = true; notOnTime = true;

        Cursor data = constructCommand();
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

    @Test
    public void punctualComplete() {
        completed = true; notCompleted = false; onTime = true; notOnTime = false;

        Cursor data = constructCommand();
        ArrayList<String> tasks = new ArrayList<>();
        if (data.moveToFirst()) {
            do {
                tasks.add(data.getString(data.getColumnIndex("TASK_NAME")));
            } while(data.moveToNext());
        }

        assertEquals(1, tasks.size());
        assertTrue(tasks.contains("Task Punctual Complete"));
    }

    @Test
    public void PunctualIncomplete() {
        completed = false; notCompleted = true; onTime = true; notOnTime = false;

        Cursor data = constructCommand();
        ArrayList<String> tasks = new ArrayList<>();
        if (data.moveToFirst()) {
            do {
                tasks.add(data.getString(data.getColumnIndex("TASK_NAME")));
            } while(data.moveToNext());
        }

        assertEquals(1, tasks.size());
        assertTrue(tasks.contains("Task Punctual Incomplete"));
    }

    @Test
    public void lateComplete() {
        completed = true; notCompleted = false; onTime = false; notOnTime = true;

        Cursor data = constructCommand();
        ArrayList<String> tasks = new ArrayList<>();
        if (data.moveToFirst()) {
            do {
                tasks.add(data.getString(data.getColumnIndex("TASK_NAME")));
            } while(data.moveToNext());
        }

        assertEquals(1, tasks.size());
        assertTrue(tasks.contains("Task Late Complete"));
    }

    @Test
    public void lateIncomplete() {
        completed = false; notCompleted = true; onTime = false; notOnTime = true;

        Cursor data = constructCommand();
        ArrayList<String> tasks = new ArrayList<>();
        if (data.moveToFirst()) {
            do {
                tasks.add(data.getString(data.getColumnIndex("TASK_NAME")));
            } while(data.moveToNext());
        }

        assertEquals(1, tasks.size());
        assertTrue(tasks.contains("Task Late Incomplete"));
    }

    @Test
    public void lateOnly() {
        completed = false; notCompleted = false; onTime = false; notOnTime = true;

        Cursor data = constructCommand();
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

    @Test
    public void punctualOnly() {
        completed = false; notCompleted = false; onTime = true; notOnTime = false;

        Cursor data = constructCommand();
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

    @Test
    public void incompleteOnly() {
        completed = false; notCompleted = true; onTime = false; notOnTime = false;

        Cursor data = constructCommand();
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

    @Test
    public void completeOnly() {
        completed = true; notCompleted = false; onTime = false; notOnTime = false;

        Cursor data = constructCommand();
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

    private void insertTaskStats(String taskName, String incomplete, String completed, String punctual, String late) {
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

    private Cursor constructCommand() {
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
