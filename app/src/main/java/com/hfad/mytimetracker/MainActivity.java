package com.hfad.mytimetracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amitshekhar.DebugDB;
import com.facebook.stetho.Stetho;

import junit.framework.Test;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    MenuItem prevMenuItem;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitalizerHelper helper = new InitalizerHelper();
        helper.initalizeDatabase(this);
        helper.initalizeBottomNavagationBar();
        helper.initalizeToolBar();
        ViewPager viewPager = findViewById(R.id.pager);
        viewPager.setCurrentItem(2);

        Calendar calendar = Calendar.getInstance();

        Date date = new Date(System.currentTimeMillis());

        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 45);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Log.d("ChronDebug", calendar.getTime().toString());

        Intent intent = new Intent(this, ScoreUpdaterAndLateMarkerChronJob.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager manager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(manager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

    }

    private void setUpChronJob(){
        ComponentName componentName = new ComponentName(this, ScoreUpdaterAndLateMarkerChronJob.class);
//        JobInfo jobInfor = new JobInfo.Builder(12, componentName)
//                                .setPeriodic(21600000)

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbarmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.settings:
                Intent intent = new Intent(this, TestMapScroll.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        //return true; //put in fragment for settings

//        SettingsFragment settings = new SettingsFragment();
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
//        viewPager.setCurrentItem(3);

//        ft.replace(viewPager.getCurrentItem(), settings);
//        ft.addToBackStack(null);
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//        ft.commit();
        // return  super.onOptionsItemSelected(item);
    }


    private class InitalizerHelper {
        public InitalizerHelper() {

        }

        public void initalizeDatabase (Context context) {
            SQLiteDatabase mydatabase = openOrCreateDatabase("MyTimeTrackerDB",MODE_PRIVATE,null);
            TimeTrackerDataBaseHelper yo = new TimeTrackerDataBaseHelper(context);
            //yo.getReadableDatabase();

            Stetho.initialize(Stetho.newInitializerBuilder(context)
                    .enableDumpapp(Stetho.defaultDumperPluginsProvider(context))
                    .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
                    .build());
            Cursor c = null;

            try{
                c = mydatabase.query("TASK_CATEGORY_INFO", null, null, null, null, null, null);
                Log.d("TableTest", "Table exists");
            }
            catch(Exception e ){
                Log.d("TableTest", " shit is broke");
            }
            Log.d("yolo", mydatabase.isOpen() + " hello " + DebugDB.getAddressLog());
        }

        public void initalizeBottomNavagationBar() {
            final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
            BottomNavigationHelper.removeShiftMode(bottomNavigationView);
            bottomNavigationView.setOnNavigationItemSelectedListener(
                    new BottomNavigationView.OnNavigationItemSelectedListener(){

                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            ViewPager viewPager = findViewById(R.id.pager);
                            switch(item.getItemId()) {
                                case R.id.task_calendar:
                                    viewPager.setCurrentItem(0);
                                    Log.d("Manual Selection", "Task Calender");
                                    break;
                                case R.id.task_viewer:
                                    viewPager.setCurrentItem(1);
                                    Log.d("Manual Selection", "Task Viewer");
                                    break;
                                case R.id.home:
                                    viewPager.setCurrentItem(2);
                                    Log.d("Manual Selection", "Home");
                                    break;
                                case R.id.task_creator:
                                    viewPager.setCurrentItem(3);
                                    Log.d("Manual Selection", "Task Creator");
                                    break;
                                case R.id.stats_viewer:
                                    viewPager.setCurrentItem(4);
                                    Log.d("Manual Selection", "Stats Viewer");
                                    break;
                            }
                            return true;
                        }
                    }
            );

            SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            ViewPager pager = (ViewPager) findViewById(R.id.pager);
            pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (prevMenuItem != null) {
                        prevMenuItem.setChecked(false);
                    }
                    else
                    {
                        bottomNavigationView.getMenu().getItem(0).setChecked(false);
                    }

                    bottomNavigationView.getMenu().getItem(position).setChecked(true);
                    prevMenuItem = bottomNavigationView.getMenu().getItem(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            pager.setAdapter(pagerAdapter);
        }

        public void initalizeToolBar(){
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

        }

        public void initalizePagerAdapter(BottomNavigationView bottomNavigationView) {
        }

    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Fragment getItem(int position) {
            BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
            switch (position){
                case 0:
                    //bottomNavigationView.getItem(R.id.task_calendar);
                    return new CalendarFragment();
                case 1:
                    // bottomNavigationView.setSelectedItemId(R.id.task_viewer);
                    return new TasksListFragment();
                case 2:
                    // bottomNavigationView.setSelectedItemId(R.id.home);
                    return new HomeFragment();
                case 3:
                    // bottomNavigationView.setSelectedItemId(R.id.task_creator);
                    bottomNavigationView.getMenu().findItem(R.id.stats_viewer).setChecked(false);
                    return new TaskCreatorFragment();
                case 4:
                    // bottomNavigationView.setSelectedItemId(R.id.stats_viewer);
                    bottomNavigationView.getMenu().findItem(R.id.stats_viewer).setChecked(true);
                    return new StatsFragment();
            }
            return null;
        }
    }



}