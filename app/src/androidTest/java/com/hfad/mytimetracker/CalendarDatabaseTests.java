package com.hfad.mytimetracker;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class CalendarDatabaseTests {

    Context context;
    TimeTrackerDataBaseHelper helper;
    SQLiteDatabase read;
    SQLiteDatabase write;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void deleteDatabaseTasks() {
        context = InstrumentationRegistry.getTargetContext();
        helper = new TimeTrackerDataBaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from TASK_STATS");
        db.execSQL("delete from TASK_INFORMATION");
        db.execSQL("delete from TASK_CATEGORY_INFO");
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
    }

    /**
     * Adds a task to the calendar and asserts the task name pulled from the database to
     * display onto the calendar.
     */
    @Test
    public void calendarTaskNameTest() {
        read = helper.getReadableDatabase();
        write = helper.getReadableDatabase();

        String categoryName = "newcategory50";
        SQLfunctionHelper.enterCatInDB(context, categoryName, 1);

        CharSequence[] taskCategoryNames = {categoryName};
        SQLfunctionHelper.enterTaskInDB(context, "Test Task", "2018-04-22", "12-00-00", "12-00-00", taskCategoryNames);


        Cursor stats = read.rawQuery("SELECT * FROM TASK_STATS", null);
        Cursor data = read.query("TASK_INFORMATION", new String[] {"_ID", "TASK_NAME", "DUE_DATE", "START_TIME", "END_TIME"}, "DUE_DATE = ?", new String[]{ "2018-04-22"}, null, null, null);

        String taskName = null;
        if (data.moveToFirst()) {
            do {
                taskName = data.getString(data.getColumnIndex("TASK_NAME"));
            } while(data.moveToNext());
        }

        assertEquals("Test Task", taskName);
    }

    /**
     * Puts 50 tasks into the database and asserts that 50 tasks are pulled from the database
     * to display on the calendar for a given day.
     */
    @Test
    public void calendarTaskAmountTest() {
        read = helper.getReadableDatabase();
        write = helper.getReadableDatabase();

        String categoryName = "newcategory52";
        SQLfunctionHelper.enterCatInDB(context, categoryName, 1);

        CharSequence[] taskCategoryNames = {categoryName};
        for (int taskCount = 0; taskCount < 50; taskCount++) {
            String taskName = "Test Task " + taskCount;
            SQLfunctionHelper.enterTaskInDB(context, taskName, "2018-04-22", "12-00-00", "12-00-00", taskCategoryNames);
        }

        Cursor stats = read.rawQuery("SELECT * FROM TASK_STATS", null);
        Cursor data = read.query("TASK_INFORMATION", new String[] {"_ID", "TASK_NAME", "DUE_DATE", "START_TIME", "END_TIME"}, "DUE_DATE = ?", new String[]{ "2018-04-22"}, null, null, null);

        String taskName = null;
        ArrayList<String> taskList = new ArrayList<>();
        if (data.moveToFirst()) {
            do {
                taskList.add(data.getString(data.getColumnIndex("TASK_NAME")));
            } while(data.moveToNext());
        }

        assertEquals(50, taskList.size());
    }
}
