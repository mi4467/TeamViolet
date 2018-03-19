package com.example.periodictest;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/**
 * Created by Paris on 3/19/2018.
 */

class DemoJobCreator implements JobCreator {

    @Override
    public Job create(String tag) {
        switch (tag) {
            case ShowNotificationJob.TAG:
                return new ShowNotificationJob();
            default:
                return null;
        }
    }
}
