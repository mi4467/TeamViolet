package com.example.periodictest;

import android.app.Application;

import com.evernote.android.job.JobManager;

/**
 * Created by Paris on 3/19/2018.
 */

public class MainApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JobManager.create(this).addJobCreator(new DemoJobCreator());
        JobManager.instance().getConfig().setAllowSmallerIntervalsForMarshmallow(true); // Don't use this in production
    }
}