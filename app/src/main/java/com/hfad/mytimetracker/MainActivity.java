package com.hfad.mytimetracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.facebook.stetho.Stetho;
import java.util.Calendar;

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
        helper.setUpChronJob();
        helper.setUpViewPager();
        NotificationHelper notifHelp = new NotificationHelper();
        notifHelp.addDigestNotif(this);
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private class InitalizerHelper {
        public InitalizerHelper() {

        }

        private void setUpViewPager(){
            ViewPager viewPager = findViewById(R.id.pager);
            viewPager.setCurrentItem(2);
        }

        private void setUpChronJob(){
            Intent intent = new Intent(MainActivity.this, ScoreUpdaterAndLateMarkerChronJob.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager manager = (AlarmManager)MainActivity.this.getSystemService(Context.ALARM_SERVICE);
            manager.setRepeating(manager.RTC, getCalendarForChron().getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }

        private Calendar getCalendarForChron(){
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 11);
            calendar.set(Calendar.MINUTE, 45);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return  calendar;
        }


        public void initalizeDatabase (Context context) {
            SQLiteDatabase mydatabase = openOrCreateDatabase("MyTimeTrackerDB",MODE_PRIVATE,null);
            TimeTrackerDataBaseHelper yo = TimeTrackerDataBaseHelper.getInstance(MainActivity.this);
            initializeStetho(context);
        }

        public void initializeStetho(Context context){
            Stetho.initialize(Stetho.newInitializerBuilder(context)
                    .enableDumpapp(Stetho.defaultDumperPluginsProvider(context))
                    .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
                    .build());
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
                                    break;
                                case R.id.task_viewer:
                                    viewPager.setCurrentItem(1);
                                    break;
                                case R.id.home:
                                    viewPager.setCurrentItem(2);
                                    break;
                                case R.id.task_creator:
                                    viewPager.setCurrentItem(3);
                                    break;
                                case R.id.stats_viewer:
                                    viewPager.setCurrentItem(4);
                                    break;
                            }
                            return true;
                        }
                    }
            );
            initalizePagerAdapter(bottomNavigationView);
        }

        public void initalizeToolBar(){
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

        }

        public void initalizePagerAdapter(BottomNavigationView bnv) {
            final BottomNavigationView bottomNavigationView = bnv;
            SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            ViewPager pager = (ViewPager) findViewById(R.id.pager);
            pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if(prevMenuItem != null){
                        prevMenuItem.setChecked(false);
                    }
                    else{
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
                    return new CalendarFragment();
                case 1:
                    return new TasksListFragment();
                case 2:
                    return new HomeFragment();
                case 3:
                    bottomNavigationView.getMenu().findItem(R.id.stats_viewer).setChecked(false);
                    return new TaskCreatorFragment();
                case 4:
                    bottomNavigationView.getMenu().findItem(R.id.stats_viewer).setChecked(true);
                    return new StatsFragment();
            }
            return null;
        }
    }



}