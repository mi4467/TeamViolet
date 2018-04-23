package com.hfad.mytimetracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.Comparator;


public class HomeFragment extends Fragment {

    public HomeFragment(){

    }

    @SuppressLint("RestrictedAPI")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigation);
//        bottomNavigationView.getMenu().getItem(3).setChecked(true);
        final View layout = inflater.inflate(R.layout.fragment_home, container, false);

        initBestCompleteBar(layout);        //these functions set up the respective cardviews
        initBestOnTimeBar(layout);          //as of right now these hold dummy data, in these functions implement the neccessary sql needed to show this
        initWorstCompleteBar(layout);
        initWorstOnTimeBar(layout);
        initCompleteLineChart(layout);
        initOnTimeLineChart(layout);
//        initIncompletePieChart(layout);
//        initLatePieChart(layout);

        return layout;
    }


    public void initBestCompleteBar(View layout){
        ArrayList<StatsFragment.CategoryStats> categories = SQLfunctionHelper.getFiveBestCompleteCategories(getContext(), new StatsFragment());
        final GraphView completionG = layout.findViewById(R.id.task_completion_total_graph_best);
        //for()
//        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
//                new DataPoint(0, -1),
//                new DataPoint(1, 5),
//                new DataPoint(2, 3),
//                new DataPoint(3, 2),
//                new DataPoint(4, 6)
//        });
//
//        completionG.getGridLabelRenderer().setLabelFormatter( new DefaultLabelFormatter(){
//
//            @Override
//            public String formatLabel(double value, boolean isValueX){
//                String result = "";
//                if(isValueX){
//                    //return "pizza";             //use this to create x axis as category labels
//                    return super.formatLabel(value, isValueX);
//                }
//                else{
//                    return super.formatLabel(value, isValueX);
//                }
//            }
//        });
//
//        completionG.addSeries(series);
//        completionG.getViewport().setScrollable(true);
//        completionG.getViewport().setScalable(true);
//        completionG.setTitle("Best Task Completion Total");
//        series.setSpacing(50);
//        completionG.getViewport().setMaxX(3);
//        completionG.getGridLabelRenderer().setHorizontalAxisTitle("Category");
//        //completionG.getGridLabelRenderer().setVerticalAxisTitle("Number of Tasks");
//
//        TextView onTimeKey = layout.findViewById(R.id.completed_key_best);
//        onTimeKey.setBackgroundColor(Color.GREEN);
//
//        TextView lateKey = layout.findViewById(R.id.not_completed_key_best);
//        lateKey.setBackgroundColor(Color.RED);
//
////        Button left = layout.findViewById(R.id.scroll_left_complete_best);
////        Button right = layout.findViewById(R.id.scroll_right_complete_best);
////
////        left.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                completionG.getViewport().scrollToEnd();
////                completionG.getViewport().
////            }
////        });


    }

    public void initWorstCompleteBar(View layout){
        GraphView completionWG = layout.findViewById(R.id.task_completion_total_graph_worst);
        BarGraphSeries<DataPoint> seriestwo = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 7),
                new DataPoint(1, 7),
                new DataPoint(2, 7)
        });
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, -1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });

        completionWG.addSeries(series);
        completionWG.setTitle("Worst Task Completion Total");
        seriestwo.setSpacing(50);

        TextView onTimeKey = layout.findViewById(R.id.completed_key_worst);
        onTimeKey.setBackgroundColor(Color.GREEN);

        TextView lateKey = layout.findViewById(R.id.not_completed_key_worst);
        lateKey.setBackgroundColor(Color.RED);


    }

    public void initBestOnTimeBar(View layout){
        GraphView onTimeG = layout.findViewById(R.id.task_onTime_total_graph_best);
        BarGraphSeries<DataPoint> seriestwo = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 7),
                new DataPoint(1, 7),
                new DataPoint(2, 7),
                new DataPoint(3, 7),
                new DataPoint(4, 7)
        });
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, -1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        onTimeG.addSeries(series);
        onTimeG.addSeries(seriestwo);
        onTimeG.setTitle("Best Task On-Time Total");
        seriestwo.setSpacing(50);

        TextView onTimeKey = layout.findViewById(R.id.onTime_key_best);
        onTimeKey.setBackgroundColor(Color.GREEN);

        TextView lateKey = layout.findViewById(R.id.late_key_best);
        lateKey.setBackgroundColor(Color.RED);

    }

    public void initWorstOnTimeBar(View layout){
        GraphView onTimeWG = layout.findViewById(R.id.task_onTime_total_graph_worst);
        BarGraphSeries<DataPoint> seriestwo = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 7),
                new DataPoint(1, 7),
                new DataPoint(2, 7),
                new DataPoint(3, 7),
                new DataPoint(4, 7)
        });
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, -1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        onTimeWG.addSeries(series);
        onTimeWG.setTitle("Worst Task On-Time Total");
        seriestwo.setSpacing(50);

        TextView onTimeKey = layout.findViewById(R.id.onTime_key_worst);
        onTimeKey.setBackgroundColor(Color.GREEN);

        TextView lateKey = layout.findViewById(R.id.late_key_worst);
        lateKey.setBackgroundColor(Color.RED);

    }

    public void initCompleteLineChart(View layout){
        final ArrayList<StatsFragment.DayStats> data = SQLfunctionHelper.getWeekOnTimeTasks(getContext(), new StatsFragment());
        DataPoint[] total = new DataPoint[7];
        for(int i = 0; i<7; i++){
            total[i] = new DataPoint(i, data.get(total.length-i-1).totalTasksWithCompleteStatus);
        }
        DataPoint[] complete = new DataPoint[7];
        for(int i = 0; i<7; i++){
            complete[i] = new DataPoint(i, data.get(total.length-i-1).complete);
        }
        DataPoint[] incomplete = new DataPoint[7];
        for(int i = 0; i<7; i++){
            incomplete[i] = new DataPoint(i, data.get(total.length-i-1).incomplete);
        }

        GraphView completeWeek = layout.findViewById(R.id.task_complete_week_graph);
        completeWeek.addSeries(new LineGraphSeries<DataPoint>(total));
        completeWeek.addSeries(new LineGraphSeries<DataPoint>(complete));
        completeWeek.addSeries(new LineGraphSeries<DataPoint>(incomplete));

        LineGraphSeries<DataPoint> totalSeries = new LineGraphSeries<DataPoint>(total);
        LineGraphSeries<DataPoint> completeSeries = new LineGraphSeries<DataPoint>(complete);////////////////////////////////////////
        LineGraphSeries<DataPoint> incompleteSeries = new LineGraphSeries<DataPoint>(incomplete);
        totalSeries.setTitle("Total");
        totalSeries.setColor(Color.YELLOW);
        completeWeek.addSeries(totalSeries);
        completeSeries.setTitle("Complete");
        completeSeries.setColor(Color.GREEN);
        completeWeek.addSeries(completeSeries);
        incompleteSeries.setTitle("Incomplete");
        incompleteSeries.setColor(Color.RED);
        completeWeek.addSeries(incompleteSeries);
        completeWeek.getGridLabelRenderer().setNumHorizontalLabels(7);
        completeWeek.getGridLabelRenderer().setLabelFormatter( new DefaultLabelFormatter(){

            @Override
            public String formatLabel(double value, boolean isValueX){
                String result = "";
                if(isValueX){
                    if(value<7) {
                        //return data.get((int)value).date;             //use this to create x axis as category labels
                        String[] date = data.get(data.size()-1-((int) value)).date.split("-");

                        return date[1] + "/" + date[2];
                    }
                    return null;
                }
                else{
                    return super.formatLabel(value, isValueX);
                }
            }
        });
        completeWeek.setTitle("On-Time Stats Over Past Week");

        TextView totalKey = layout.findViewById(R.id.total_complete_key);
        totalKey.setBackgroundColor(Color.YELLOW);

        TextView onTimeKey = layout.findViewById(R.id.complete_key);
        onTimeKey.setBackgroundColor(Color.GREEN);

        TextView lateKey = layout.findViewById(R.id.incomplete_key);
        lateKey.setBackgroundColor(Color.RED);
    }

    public void initOnTimeLineChart(View layout){
        final ArrayList<StatsFragment.DayStats> data = SQLfunctionHelper.getWeekOnTimeTasks(getContext(), new StatsFragment());
        DataPoint[] total = new DataPoint[7];
        for(int i = 0; i<7; i++){
            total[i] = new DataPoint(i, data.get(total.length-i-1).totalTasksWithOnTimeStatus);
        }
        DataPoint[] ontime = new DataPoint[7];
        for(int i = 0; i<7; i++){
            ontime[i] = new DataPoint(i, data.get(total.length-i-1).onTime);
        }
        DataPoint[] late = new DataPoint[7];
        for(int i = 0; i<7; i++){
            late[i] = new DataPoint(i, data.get(total.length-i-1).late);
        }

        GraphView onTimeWeek = layout.findViewById(R.id.task_onTime_week_graph);
        LineGraphSeries<DataPoint> totalSeries = new LineGraphSeries<DataPoint>(total);
        LineGraphSeries<DataPoint> onTimeSeries = new LineGraphSeries<DataPoint>(ontime);////////////////////////////////////////
        LineGraphSeries<DataPoint> lateSeries = new LineGraphSeries<DataPoint>(late);
        totalSeries.setTitle("Total");
        totalSeries.setColor(Color.YELLOW);
        onTimeWeek.addSeries(totalSeries);
        onTimeSeries.setTitle("On-Time");
        onTimeSeries.setColor(Color.GREEN);
        onTimeWeek.addSeries(onTimeSeries);
        lateSeries.setTitle("Late");
        lateSeries.setColor(Color.RED);
        onTimeWeek.addSeries(lateSeries);
        onTimeWeek.getGridLabelRenderer().setNumHorizontalLabels(7);
        onTimeWeek.getGridLabelRenderer().setLabelFormatter( new DefaultLabelFormatter(){

            @Override
            public String formatLabel(double value, boolean isValueX){
                String result = "";
                if(isValueX){
                    if(value<7) {
                        //return data.get((int)value).date;             //use this to create x axis as category labels
                        String[] date = data.get(data.size()-1-((int) value)).date.split("-");

                        return date[1] + "/" + date[2];
                    }
                    return null;
                }
                else{
                    return super.formatLabel(value, isValueX);
                }
            }
        });
        onTimeWeek.setTitle("On-Time Stats Over Past Week");

        onTimeWeek.getGridLabelRenderer().setVerticalAxisTitle("Number of Tasks");
        onTimeWeek.getGridLabelRenderer().setHorizontalAxisTitle("Date");

        TextView totalKey = layout.findViewById(R.id.total_onTime_key);
        totalKey.setBackgroundColor(Color.YELLOW);

        TextView onTimeKey = layout.findViewById(R.id.onTime_key);
        onTimeKey.setBackgroundColor(Color.GREEN);

        TextView lateKey = layout.findViewById(R.id.late_onTime_key);
        lateKey.setBackgroundColor(Color.RED);

    }

    public class BestCompletionComparator implements Comparator<StatsFragment.CategoryStats> {


        @Override
        public int compare(StatsFragment.CategoryStats categoryStats, StatsFragment.CategoryStats t1) {
            if(t1.totalTasksWithCompleteStatus<5){
                return -1;
            }
            if(categoryStats.totalTasksWithCompleteStatus<5){
                return 1;
            }
            if(categoryStats.complete/categoryStats.totalTasksWithCompleteStatus > t1.complete/t1.totalTasksWithCompleteStatus){
                return 1;
            }
            else{
                return -1;
            }
        }
    }

    public class WorstCompletionComparator implements Comparator<StatsFragment.CategoryStats> {


        @Override
        public int compare(StatsFragment.CategoryStats categoryStats, StatsFragment.CategoryStats t1) {
            if(t1.totalTasksWithCompleteStatus<5){
                return -1;
            }
            if(categoryStats.totalTasksWithCompleteStatus<5){
                return 1;
            }
            if(categoryStats.complete/categoryStats.totalTasksWithCompleteStatus < t1.complete/t1.totalTasksWithCompleteStatus){
                return 1;
            }
            else{
                return -1;
            }
        }
    }

    public class BestOnTimeComparator implements Comparator<StatsFragment.CategoryStats> {


        @Override
        public int compare(StatsFragment.CategoryStats categoryStats, StatsFragment.CategoryStats t1) {
            if(t1.totalTasksWithOnTimeStatus<5){
                return -1;
            }
            if(categoryStats.totalTasksWithOnTimeStatus<5){
                return 1;
            }
            if(categoryStats.onTime/categoryStats.totalTasksWithOnTimeStatus > t1.onTime/t1.totalTasksWithOnTimeStatus){
                return 1;
            }
            else{
                return -1;
            }
        }
    }

    public class WorstOnTimeComparator implements Comparator<StatsFragment.CategoryStats> {


        @Override
        public int compare(StatsFragment.CategoryStats categoryStats, StatsFragment.CategoryStats t1) {
            if(t1.totalTasksWithOnTimeStatus<5){
                return -1;
            }
            if(categoryStats.totalTasksWithOnTimeStatus<5){
                return 1;
            }
            if(categoryStats.onTime/categoryStats.totalTasksWithOnTimeStatus < t1.onTime/t1.totalTasksWithOnTimeStatus){
                return 1;
            }
            else{
                return -1;
            }
        }
    }




}