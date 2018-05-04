package com.hfad.mytimetracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class TaskActivityTest {

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
    public void verifyOnTimeTest(){

    }

}