package com.hfad.mytimetracker;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;

public class TestMapScroll extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_map_scroll);
        ArrayList<StatsFragment.CategoryStats> data = SQLfunctionHelper.getFiveBestCompleteCategories(this, new StatsFragment());
        PieChart mPieChart = (PieChart) findViewById(R.id.test_piechart);
        for(StatsFragment.CategoryStats curr : data){
            mPieChart.addPieSlice(new PieModel(curr.name, curr.incomplete, Color.parseColor(String.format("#%06X", (0xFFFFFF & curr.color)))));
        }
    }
}
