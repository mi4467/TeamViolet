package com.example.periodictest;

/**
 * Created by Paris on 3/19/2018.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;


import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class PeriodicAlarm {

    public static void setReminder(Context context, Class<?> cls, int hour, int min)
    {
        Calendar calendar = Calendar.getInstance();
        Calendar setcalendar = Calendar.getInstance();
        setcalendar.set(Calendar.HOUR_OF_DAY, hour);
        setcalendar.set(Calendar.MINUTE, min);
        setcalendar.set(Calendar.SECOND, 0);
        // cancel already scheduled reminders
       // cancelReminder(context,cls);

        if(setcalendar.before(calendar))
            setcalendar.add(Calendar.DATE,1);

        // Enable a receiver
        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent1 = new Intent(context, cls);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
//                DAILY_REMINDER_REQUEST_CODE, intent1,
//                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
//        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, setcalendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

}
