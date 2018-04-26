package com.hfad.mytimetracker;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

public class ScoreUpdaterAndLateMarkerChronJob extends JobService {


    private String getDate(){
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        String date = TaskCreatorFragment.constructDateStr(year, month, day);
        return date;
    }

    private void updateTotalStreak(SQLiteDatabase read, SQLiteDatabase write){
        Cursor userStats = read.rawQuery("SELECT * FROM USER_STATS", null);
        int totalStreak = userStats.getInt(1);
        int allTimeStreak = userStats.getInt(2);
        if(totalStreak>allTimeStreak){
            write.execSQL("UPDATE USER_STATS SET TOTAL_STREAK = TOTAL_STREAK + 1");
        }
    }

    //@Override
    public void onReceive(Context context, Intent intent) {
//        TimeTrackerDataBaseHelper helper = TimeTrackerDataBaseHelper.getInstance(context);
//        SQLiteDatabase readableDatabase = helper.getReadableDatabase();
//        SQLiteDatabase writableDatabase = helper.getWritableDatabase();
//        String date = getDate();
//        Cursor streak = readableDatabase.rawQuery("SELECT * FROM TASK_STATS WHERE DUE_DATE = '" + date + "'", null);
//        streak.moveToFirst();
//        Integer onTimeAndComplete =0;
//        Cursor punctual = streak;
//        for(int i =0; i< punctual.getCount(); i++){             //for each task of that day
//            if(punctual.getInt(4)==0){                          //if the task is incomplete by the end of the day
//                ContentValues makeLate = new ContentValues();
//                makeLate.put("NOT_ON_TIME", 1);
//                if(punctual.getInt(7)==0) {                                        //and it hasn't been marked late already
//                    writableDatabase.update("TASK_STATS", makeLate, "TASK_ID = " + punctual.getInt(0),null);
//                    for (int j = 9; j < punctual.getColumnCount(); j++) {                //then for every column
//                        //UPDATE THE NOT_ONTIME AND COMPLETED FOR THAT CATEGORY
//                        Log.d("MarkCompleteDebug", "Column " + j + " is: " + punctual.getColumnName(j));
//                        if (punctual.getInt(j) == 1) {                                          //if it is tagged with a category
//                            Log.d("MarkCompleteDebug", "We are entering data for: " + punctual.getColumnName(j));
//                            writableDatabase.execSQL("UPDATE TASK_CATEGORY_INFO " +
//                                    "SET NOT_ON_TIME = NOT_ON_TIME + 1 " +
//                                    "WHERE CATEGORY_NAME = \"" +
//                                    punctual.getColumnName(j) + "\"");
//                        }
//                    }
//                    SQLfunctionHelper.removeFromScore(helper);  //decrement user score here with some call to SQLfunctionHelper
//                }
//            }
//            else{
//                onTimeAndComplete += punctual.getInt(7);        //otherwise we keep a total of punctual and complete
//            }
//            punctual.moveToNext();
//        }
//        if(onTimeAndComplete>0){
//            writableDatabase.execSQL("UPDATE USER_STATS SET CURRENT_STREAK = CURRENT_STREAK + 1");
//            //update streak accordingly in response to having completed some task on time
//            updateTotalStreak(readableDatabase, writableDatabase);
//        }
//        else{
//            writableDatabase.execSQL("UPDATE USER_STATS SET CURRENT_STREAK = 0"); //Reset current streak to zero
//        }
//        writableDatabase.execSQL("UPDATE USER_STATS SET TODAY_SCORE = 0");      // reset the today score paramater
        Toast.makeText(context, "We are calculating streak/score", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Toast.makeText(this, "We are calculating streak/score", Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
