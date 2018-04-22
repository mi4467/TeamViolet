package com.hfad.mytimetracker;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SQLFunctionHelperTest {

    Context instrumentationCtx;

    @Before
    public void setup() {
        instrumentationCtx = InstrumentationRegistry.getTargetContext();
        SQLiteDatabase write = TimeTrackerDataBaseHelper.getInstance(instrumentationCtx).getWritableDatabase();
        write.execSQL("DELETE FROM TASK_INFORMATION");
        write.execSQL("DROP TABLE TASK_STATS");
        write.execSQL("CREATE TABLE TASK_STATS ("
                + "_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "TASK_NAME TEXT, "
                + "TASK_ID INTEGER,"
                + "CATEGORY_GENERAL BOOLEAN, "
                + "COMPLETED BOOLEAN, "
                + "DUE_DATE DATE, "
                + "NOT_COMPLETED BOOLEAN, "
                + "NOT_ON_TIME BOOLEAN, "
                + "ON_TIME BOOLEAN);");
        write.execSQL("DELETE FROM TASK_CATEGORY_INFO");

    }


    @Test
    public void addCategoriesTest() {
        TimeTrackerDataBaseHelper helper = TimeTrackerDataBaseHelper.getInstance(instrumentationCtx);
        SQLiteDatabase read = helper.getReadableDatabase();
        //SQLiteDatabase write = helper.getWritableDatabase();


        //test for if it gets put in if it doesn't exist
        //--then test to see if it adds a column to task stats
        //test for if it doesn't get put in if it exists
        String testName = "TestEnterCat";      //paramater
        Integer testColor = 5;         //paramater
        Integer values = 0;            //a result to check against
        SQLfunctionHelper.enterCatInDB(instrumentationCtx, testName, testColor);
        Cursor result = read.rawQuery("SELECT * FROM TASK_CATEGORY_INFO WHERE CATEGORY_NAME = '" + testName + "'", null);

        result.moveToFirst();
        assertTrue(result.getCount()==1);           //checks size of successfull add
        assertTrue(result.getString(1).equals(testName)); //checks if we have the correct values
        assertTrue(result.getInt(2)==(testColor));
        assertTrue(result.getInt(3)==values);
        assertTrue(result.getInt(4)==values);
        assertTrue(result.getInt(5)==values);

        //tests to see if columns were made properly to represent category
        Cursor testStatsColumn = read.rawQuery("SELECT * FROM TASK_STATS", null);
        testStatsColumn.moveToFirst();
        assertTrue(testStatsColumn.getColumnName(9).equals(testName));
        assertTrue(testStatsColumn.getColumnIndex(testName)==9);

        SQLfunctionHelper.enterCatInDB(instrumentationCtx, testName + "oo", testColor);
        testStatsColumn = read.rawQuery("SELECT * FROM TASK_STATS", null);
        testStatsColumn.moveToFirst();
        assertTrue(testStatsColumn.getColumnCount()==10);

        assertFalse(SQLfunctionHelper.enterCatInDB(instrumentationCtx, testName, testColor));   //checks to make sure no duplicates are allowed

    }

    @Test
    public void getCategoriesTest(){
        //test to see if for any arbitrary categories, will retrieve all categories
        String[] testCat = new String[]{"HelLo", "HELLO", "hello", "hElLo"};
        TimeTrackerDataBaseHelper helper = TimeTrackerDataBaseHelper.getInstance(instrumentationCtx);
        HashSet<String> check = new HashSet<>();
        for(int i = 0; i< testCat.length; i++){
            SQLfunctionHelper.enterCatInDB(instrumentationCtx, testCat[i], i);
            check.add(testCat[i]);
        }
        String[] result = SQLfunctionHelper.getCategoryList(instrumentationCtx);
        for(int i =0; i<result.length; i++){
            assertTrue(check.contains(result[i]));
        }
    }

    @Test
    public void enterTaskInDB(){
        //Test if task_information puts in correct values
        //Test if task_stats has the correct information
        //Test if task_category has the proper values

        TimeTrackerDataBaseHelper helper = TimeTrackerDataBaseHelper.getInstance(instrumentationCtx);
        SQLiteDatabase read = helper.getReadableDatabase();         //set up categories
        SQLiteDatabase write = helper.getWritableDatabase();
        String[] testCat = new String[]{"HelLo", "HELLO1", "hello2", "hElLo3"};
        for(int i = 0; i< testCat.length; i++){
            SQLfunctionHelper.enterCatInDB(instrumentationCtx, testCat[i], i);
        }
        //assertTrue(read.rawQuery("SELECT * FROM TASK_CATEGORY_INFO", null).getCount()==4);
        //Log.d("TestDebug"," this is" +  DatabaseUtils.dumpCursorToString(read.rawQuery("SELECT * FROM TASK_CATEGORY_INFO", null)));
        String taskName = "TestTask";
        //test enterTaskInDB first
        SQLfunctionHelper.enterTaskInDB(instrumentationCtx, taskName, "2018-12-12","12-00-00", "13-00-00", new String[]{"HelLo", "HELLO1", "hello2"});
        Cursor getFromTaskInfo = read.rawQuery("SELECT * FROM TASK_INFORMATION WHERE TASK_NAME = '" + taskName + "'", null);
        getFromTaskInfo.moveToFirst();

        //test that information has the proper values
        assertEquals(taskName, getFromTaskInfo.getString(1));
        assertEquals("2018-12-12", getFromTaskInfo.getString(2));
        assertEquals("12-00-00", getFromTaskInfo.getString(3));
        assertEquals(1, getFromTaskInfo.getInt(4));
        assertEquals("13-00-00", getFromTaskInfo.getString(5));



        //test to see if it was put into stats properly and that a connection was formed
        Integer id = getFromTaskInfo.getInt(0);
        Cursor getFromStats = read.rawQuery("SELECT * FROM TASK_STATS WHERE TASK_ID = " + id, null);
        getFromStats.moveToFirst();
        Log.d("TestDebug", DatabaseUtils.dumpCursorToString(getFromStats));
        assertTrue(getFromStats.getInt(2)==id);     //checks for id linke
        for(int i =0; i< testCat.length-1; i++){                //checks for marked category
            Cursor temp = read.rawQuery("SELECT " + testCat[i] + " FROM TASK_STATS WHERE TASK_ID = " + id, null);
            temp.moveToFirst();
            assertTrue(1==temp.getInt(0));
        }
        Cursor temp = read.rawQuery("SELECT " + testCat[3] + " FROM TASK_STATS WHERE TASK_ID = " + id, null);
        temp.moveToFirst();
        assertTrue(0==temp.getInt(0));

        //test if category values were updated correctly
        Cursor getFromCat = read.rawQuery("SELECT * FROM TASK_CATEGORY_INFO", null);
        getFromCat.moveToFirst();
        assertTrue(getFromCat.getColumnName(3).equals("COMPLETED"));
        for(int i =0; i<testCat.length-1; i++){

            assertEquals(0, getFromCat.getInt(3));
            assertEquals(1, getFromCat.getInt(4));
            assertEquals(0, getFromCat.getInt(5));
            assertEquals(0, getFromCat.getInt(6));
            getFromCat.moveToNext();
        }
        assertEquals(0, getFromCat.getInt(3));
        assertEquals(0, getFromCat.getInt(4));
        assertEquals(0, getFromCat.getInt(5));
        assertEquals(0, getFromCat.getInt(6));


    }

    @Test
    public void markTaskCompleteTest(){
        //to do
    }

    @Test
    public void addReoccTaskTest(){
        //to do
    }

    @Test
    public void deleteTaskTest(){
        //four test cases
        //--Delete a task that is complete/onTime, complete and late, incomplete/late
        TimeTrackerDataBaseHelper helper = TimeTrackerDataBaseHelper.getInstance(instrumentationCtx);
        SQLiteDatabase read = helper.getReadableDatabase();         //set up categories
        SQLiteDatabase write = helper.getWritableDatabase();
        String[] testCat = new String[]{"HelLo", "HELLO1", "hello2", "hElLo3"};
        for(int i = 0; i< testCat.length; i++){
            SQLfunctionHelper.enterCatInDB(instrumentationCtx, testCat[i], i);
        }
        //We are actually going to have to test mark complete

    }

    @Test
    public void filterLineChart(){
        TimeTrackerDataBaseHelper helper = TimeTrackerDataBaseHelper.getInstance(instrumentationCtx);
        SQLiteDatabase read = helper.getReadableDatabase();         //set up categories
        SQLiteDatabase write = helper.getWritableDatabase();
        String[] testCat = new String[]{"HelLo", "HELLO1", "hello2", "hElLo3"};
        for(int i = 0; i< testCat.length; i++){
            SQLfunctionHelper.enterCatInDB(instrumentationCtx, testCat[i], i);
        }
        //Tests to see if it pulls out the correct days
        ArrayList<StatsFragment.DayStats> test = SQLfunctionHelper.getWeekOnTimeTasksFilter(instrumentationCtx, new StatsFragment(), "2018-11-01");
        assertEquals("2018-11-01", test.get(0).date);
        assertEquals("2018-10-31", test.get(1).date);
        assertEquals("2018-10-30", test.get(2).date);
        assertEquals("2018-10-29", test.get(3).date);
        assertEquals("2018-10-28", test.get(4).date);
        assertEquals("2018-10-27", test.get(5).date);
        assertEquals("2018-10-26", test.get(6).date);
    }

    @Test
    public void filterBarGraphTest(){
        TimeTrackerDataBaseHelper helper = TimeTrackerDataBaseHelper.getInstance(instrumentationCtx);
        SQLiteDatabase read = helper.getReadableDatabase();         //set up categories
        SQLiteDatabase write = helper.getWritableDatabase();
        String[] testCat = new String[]{"HelLo", "HELLO1", "hello2", "hElLo3"};
        for(int i = 0; i< testCat.length; i++){
            SQLfunctionHelper.enterCatInDB(instrumentationCtx, testCat[i], i);
        }
        String[] cat = SQLfunctionHelper.getCategoryList(instrumentationCtx);
        assertEquals(4, SQLfunctionHelper.filterBarGraph(SQLfunctionHelper.getCategoryList(instrumentationCtx), instrumentationCtx, new StatsFragment()).size());
        assertEquals(3, SQLfunctionHelper.filterBarGraph(new String[]{"HelLo", "HELLO1", "hello2"}, instrumentationCtx, new StatsFragment()).size());
    }

    @Test
    public void getTaskGivenDateTest(){
        TimeTrackerDataBaseHelper helper = TimeTrackerDataBaseHelper.getInstance(instrumentationCtx);
        SQLiteDatabase read = helper.getReadableDatabase();         //set up categories
        SQLiteDatabase write = helper.getWritableDatabase();
        String[] testCat = new String[]{"HelLo", "HELLO1", "hello2", "hElLo3"};
        for(int i = 0; i< testCat.length; i++){
            SQLfunctionHelper.enterCatInDB(instrumentationCtx, testCat[i], i);
        }
        //assertTrue(read.rawQuery("SELECT * FROM TASK_CATEGORY_INFO", null).getCount()==4);
        //Log.d("TestDebug"," this is" +  DatabaseUtils.dumpCursorToString(read.rawQuery("SELECT * FROM TASK_CATEGORY_INFO", null)));
        String taskName = "TestTask";
        //test enterTaskInDB first
        SQLfunctionHelper.enterTaskInDB(instrumentationCtx, taskName, "2018-12-12","12-00-00", "13-00-00", new String[]{"HelLo", "HELLO1", "hello2"});
        SQLfunctionHelper.enterTaskInDB(instrumentationCtx, taskName + "later", "2018-12-15","12-00-00", "13-00-00", new String[]{"HelLo", "HELLO1", "hello2"});
        //above was prep, below is to make two different dates and make sure the other doesn't appear in the wrong spot

        Cursor early = SQLfunctionHelper.getTasksGivenDate(instrumentationCtx, "2018-12-12");
        early.moveToFirst();
        Cursor later = SQLfunctionHelper.getTasksGivenDate(instrumentationCtx, "2018-12-15");
        later.moveToFirst();
        assertTrue(early.getCount()==later.getCount() && early.getCount()==1);
        assertEquals(taskName, early.getString(0));
        assertEquals(taskName + "later", later.getString(0));
    }


    @Test
    public void getTaskInfoTest(){
        TimeTrackerDataBaseHelper helper = TimeTrackerDataBaseHelper.getInstance(instrumentationCtx);
        SQLiteDatabase read = helper.getReadableDatabase();         //set up categories
        SQLiteDatabase write = helper.getWritableDatabase();
        String[] testCat = new String[]{"HelLo", "HELLO1", "hello2", "hElLo3"};
        for(int i = 0; i< testCat.length; i++){
            SQLfunctionHelper.enterCatInDB(instrumentationCtx, testCat[i], i);
        }
        //assertTrue(read.rawQuery("SELECT * FROM TASK_CATEGORY_INFO", null).getCount()==4);
        //Log.d("TestDebug"," this is" +  DatabaseUtils.dumpCursorToString(read.rawQuery("SELECT * FROM TASK_CATEGORY_INFO", null)));
        String taskName = "TestTask";
        //test enterTaskInDB first
        SQLfunctionHelper.enterTaskInDB(instrumentationCtx, taskName, "2018-12-12","12-00-00", "13-00-00", new String[]{"HelLo", "HELLO1", "hello2"});
        Cursor compare = read.rawQuery("SELECT * FROM TASK_INFORMATION WHERE TASK_NAME = '" + taskName + "'", null);
        compare.moveToFirst();
        Integer id = compare.getInt(0);

        Cursor getFromTaskInfo = SQLfunctionHelper.getTaskInfo(instrumentationCtx, id);
        getFromTaskInfo.moveToFirst();
        assertEquals(taskName, getFromTaskInfo.getString(1));
        assertEquals("2018-12-12", getFromTaskInfo.getString(2));
        assertEquals("12-00-00", getFromTaskInfo.getString(3));
        assertEquals("13-00-00", getFromTaskInfo.getString(5));
    }

    @Test
    public void getTaskStatsTest(){
        TimeTrackerDataBaseHelper helper = TimeTrackerDataBaseHelper.getInstance(instrumentationCtx);
        SQLiteDatabase read = helper.getReadableDatabase();         //set up categories
        SQLiteDatabase write = helper.getWritableDatabase();
        String[] testCat = new String[]{"HelLo", "HELLO1", "hello2", "hElLo3"};
        for(int i = 0; i< testCat.length; i++){
            SQLfunctionHelper.enterCatInDB(instrumentationCtx, testCat[i], i);
        }
        //assertTrue(read.rawQuery("SELECT * FROM TASK_CATEGORY_INFO", null).getCount()==4);
        //Log.d("TestDebug"," this is" +  DatabaseUtils.dumpCursorToString(read.rawQuery("SELECT * FROM TASK_CATEGORY_INFO", null)));
        String taskName = "TestTask";
        //test enterTaskInDB first
        SQLfunctionHelper.enterTaskInDB(instrumentationCtx, taskName, "2018-12-12","12-00-00", "13-00-00", new String[]{"HelLo", "HELLO1", "hello2"});
        Cursor compare = read.rawQuery("SELECT * FROM TASK_INFORMATION WHERE TASK_NAME = '" + taskName + "'", null);
        compare.moveToFirst();
        Integer id = compare.getInt(0);

        Cursor getFromTaskStats = SQLfunctionHelper.getTaskStats(instrumentationCtx, id);
        getFromTaskStats.moveToFirst();
        assertEquals(1, getFromTaskStats.getInt(9));
        assertEquals(1, getFromTaskStats.getInt(10));
        assertEquals(1, getFromTaskStats.getInt(11));
        assertEquals(0, getFromTaskStats.getInt(12));

    }

    @Test
    public void changeNotificationTest(){
        TimeTrackerDataBaseHelper helper = TimeTrackerDataBaseHelper.getInstance(instrumentationCtx);
        SQLiteDatabase read = helper.getReadableDatabase();
        SQLiteDatabase write = helper.getWritableDatabase();
        String[] testCat = new String[]{"HelLo", "HELLO", "hello", "hElLo"};
        for(int i = 0; i< testCat.length; i++){
            SQLfunctionHelper.enterCatInDB(instrumentationCtx, testCat[i], i);
        }
        String taskName = "TestTask";

        //test enterTaskInDB first
        SQLfunctionHelper.enterTaskInDB(instrumentationCtx, taskName, "2018-12-12","12-00-00", "13-00-00", testCat);
        Cursor getId = read.rawQuery("SELECT TASK_ID FROM TASK_STATS WHERE TASK_NAME = '" + taskName + "'", null);
        getId.moveToFirst();
        Integer id = getId.getInt(0);
        //above is prep work

        //below tests if we can change the notification field
        SQLfunctionHelper.changeNotification(instrumentationCtx, id, false);
        Cursor result = read.rawQuery("SELECT NOTIFICATION FROM TASK_INFORMATION WHERE _ID = " + id, null);
        result.moveToFirst();
        assertEquals(0, result.getInt(0));
        SQLfunctionHelper.changeNotification(instrumentationCtx, id, true);
        result = read.rawQuery("SELECT NOTIFICATION FROM TASK_INFORMATION WHERE _ID = " + id, null);
        result.moveToFirst();
        assertEquals(1,result.getInt(0));

    }
}
