package com.hfad.mytimetracker;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

public class TaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        Intent intent = getIntent();
        Integer id = intent.getIntExtra("TASK_ID", 80);
        Log.d("TaskDebug", "Task id is: " + id);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        GraphView completionG = findViewById(R.id.test);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, -1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });

        for(int i =5; i<50; i++){
            //series.appendData();
            series.appendData(new DataPoint(i, 2*i), false, 1);
            //series.appendData(i, 2*i);
        }
        completionG.addSeries(series);
        completionG.getViewport().setScrollable(true);
        completionG.getViewport().setScrollableY(true);

        completionG.setTitle("Task Completion Total");
        series.setSpacing(50);
    }
}
