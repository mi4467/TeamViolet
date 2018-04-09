package com.example.notificationbutton;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        b = (Button) findViewById(R.id.notifButton);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    public void getNotification(View view){
        final int NOTIFY_ID = 1002;
        NotificationManager notificationmanager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        String id = "my_package_channel_1";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        String name = "my_package_channel";
        String description = "my_package_first_channel";
        NotificationChannel mChannel = notificationmanager.getNotificationChannel(id);
        if(mChannel == null){
            mChannel = new NotificationChannel(id, name, importance);
            mChannel.setDescription(description);
            mChannel.enableVibration(true);
            notificationmanager.createNotificationChannel(mChannel);
        }

        Notification notif = new NotificationCompat.Builder(this, id)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Notification test")
                .setContentText("notification test text ")
                .build();

        notificationmanager.notify(NOTIFY_ID,notif);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
