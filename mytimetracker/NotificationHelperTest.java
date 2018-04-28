package com.hfad.mytimetracker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static android.support.test.InstrumentationRegistry.getContext;
import static org.junit.Assert.*;

/**
 * Created by Paris on 4/23/2018.
 */
public class NotificationHelperTest {

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
    public void addDigestNotifTest(){
        NotificationHelper.addDigestNotif(InstrumentationRegistry.getInstrumentation().getTargetContext());

        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), com.hfad.mytimetracker.DigestPublisher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(InstrumentationRegistry.getInstrumentation().getTargetContext(), -3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        boolean alarmCreated = (pendingIntent != null);
        assertTrue(alarmCreated);


    }

    @Test
    public void createTaskNotifTest(){


        //set up a task which will create notif
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

        //recreate intent to see if I can get the notification that way and test the name with getExtra?
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2018);
        calendar.set(Calendar.MONTH, 12);
        calendar.set(Calendar.DATE, 12);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.MINUTE, -15);

        Intent intent = new Intent(instrumentationCtx, NotificationPublisher.class);
        intent.putExtra("TASK_NAME", taskName);
        intent.putExtra("ID", 0);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(instrumentationCtx, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        //assert null because FLAG_NO_CREATE will return
        String s = NotificationPublisher.class.getName() + '@' + Integer.toHexString(pendingIntent.hashCode());
        assertEquals(s, pendingIntent.toString());


    }

    @Test
    public void deleteTaskNotifTest(){
    }

}