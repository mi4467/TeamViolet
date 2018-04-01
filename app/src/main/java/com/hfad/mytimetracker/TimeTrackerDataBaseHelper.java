package com.hfad.mytimetracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by Fuffy on 3/16/2018.
 */

public class TimeTrackerDataBaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "starbuzz"; // the name of our database
    private static final int DB_VERSION = 1;

    public TimeTrackerDataBaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
       initTaskInfoTable(sqLiteDatabase);
       initTaskStatsTable(sqLiteDatabase);
       initTaskCatStatsTable(sqLiteDatabase);
       Log.d(TAG, "onCreate: we made the database");
    }

    protected void initTaskCatStatsTable(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE TASK_CATEGORY_INFO ("
                        + "_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "CATEGORY_NAME TEXT, "
                        + "COLOR INTEGER, "
                        + "COMPLETED INTEGER, "
                        + "NOT_COMPLETED INTEGER, "
                        + "NOT_ON_TIME INTEGER, "
                        + "ON_TIME INTEGER);");
    }

    private void initTaskStatsTable(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE TASK_STATS ("
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

    private void initTaskInfoTable(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE TASK_INFORMATION ("
                + "_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "TASK_NAME TEXT, "
                + "DUE_DATE DATE, "
                + "START_TIME TIME, "
                + "END_TIME TIME);");
    }

    private void addTaskCategory(SQLiteDatabase sqLiteDatabase, String category){
        category = category.trim();
        category = category.replace(" ", "_");
        //category = category.toUpperCase();
        sqLiteDatabase.execSQL("ALTER TABLE TASK_INFORMATION ADD COLUMN " + category + " BOOLEAN;");
        sqLiteDatabase.execSQL("ALTER TABLE TASK_STATS ADD COLUMN " + category + " BOOLEAN;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        initTaskInfoTable(sqLiteDatabase);
        initTaskStatsTable(sqLiteDatabase);
        initTaskCatStatsTable(sqLiteDatabase);
    }
}
