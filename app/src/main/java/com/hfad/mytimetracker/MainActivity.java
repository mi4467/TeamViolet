package com.hfad.mytimetracker;

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

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SQLiteDatabase mydatabase = openOrCreateDatabase("MyTimeTrackerDB",MODE_PRIVATE,null);
        TimeTrackerDataBaseHelper yo = new TimeTrackerDataBaseHelper(this);
        //yo.getReadableDatabase();

        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
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
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
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
        toolBarSetUp();

        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(pagerAdapter);
    }

    private void toolBarSetUp(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                Intent intent = new Intent(this, SettingsActivity.class);
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
            switch (position){
                case 0:
                    return new CalendarFragment();
                case 1:
                    return new TasksListFragment();
                case 2:
                    return new HomeFragment();
                case 3:
                    return new TaskCreatorFragment();
                case 4:
                    return new StatsFragment();
            }
            return null;
        }
    }



}
