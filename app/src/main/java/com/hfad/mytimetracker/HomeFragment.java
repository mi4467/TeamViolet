package com.hfad.mytimetracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import net.sqlcipher.DatabaseUtils;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeFragment extends Fragment {
    private static ArrayList<StatsFragment.CategoryStats> completedBarData = null;
    private static ArrayList<StatsFragment.CategoryStats> onTimeBarData = null;

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

//        initBestCompleteBar(layout);        //these functions set up the respective cardviews
//        initBestOnTimeBar(layout);          //as of right now these hold dummy data, in these functions implement the neccessary sql needed to show this
//        initWorstCompleteBar(layout);
          initTaskListView(layout);
//        initWorstOnTimeBar(layout);
          initCompleteLineChart(layout);
          initOnTimeLineChart(layout);
//        initIncompletePieChart(layout);
//        initLatePieChart(layout);

        return layout;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && getView()!=null) {
            // Do your stuff here
            initTaskListView(getView());
            initCompleteLineChart(getView());
            initOnTimeLineChart(getView());
        }

    }

    public void initTaskListView(View layout){
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTime(Calendar.getInstance().getTime());
        String[] daterep = gcal.getTime().toString().split(" ");
        Map<String, Integer> monthMapper = new HashMap<>();
        monthMapper.put("Jan", 0);
        monthMapper.put("Feb", 1);
        monthMapper.put("Mar", 2);
        monthMapper.put("Apr", 3);
        monthMapper.put("May", 4);
        monthMapper.put("Jun", 5);
        monthMapper.put("Jul", 6);
        monthMapper.put("Aug", 7);
        monthMapper.put("Sep", 8);
        monthMapper.put("Oct", 9);
        monthMapper.put("Nov", 10);
        monthMapper.put("Dec", 11);
        Cursor result = SQLfunctionHelper.getTasksGivenDate(getContext(), TaskCreatorFragment.constructDateStr(Integer.parseInt(daterep[5]), monthMapper.get(daterep[1]), Integer.parseInt(daterep[2])));
        Log.d("HomeTodaysListsDebug", android.database.DatabaseUtils.dumpCursorToString(result));

        ArrayList<String> completedTasks = new ArrayList<>();
        ArrayList<String> notCompletedTasks = new ArrayList<>();
        result.moveToFirst();
        String completed="";
        for(int i =0; i< result.getCount(); i++){
            if(result.getInt(1)==1){                //completed
                completedTasks.add(result.getString(0));
                completed = completed +"\n " +result.getString(0);
            }
            result.moveToNext();
        }
        result.moveToFirst();
        String incomplete = "";
        for(int i =0; i< result.getCount(); i++){
            if(result.getInt(2)==1){                //notcompleted
                notCompletedTasks.add(result.getString(0));
                incomplete = incomplete +"\n" +result.getString(0);
            }
            result.moveToNext();
        }

        Log.d("HomeListViewDebug", completed);
        Log.d("HomeListViewDebug", incomplete);

//         completed = new String[completedTasks.size()];
//        String[] notCompleted = new String[notCompletedTasks.size()];
//
//        ArrayAdapter<String> completedAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, completed);
//        ArrayAdapter<String> notCompletedAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, notCompleted);

        TextView completedList = layout.findViewById(R.id.completed_listview);
        TextView notCompletedList = layout.findViewById(R.id.not_completed_listview);

        if(!completed.equals("")){
            completedList.setText("Completed Tasks\n" + completed);
            completedList.setGravity(Gravity.CENTER);
        }
        else{
            completedList.setText("Completed Tasks\n\nNo Tasks Completed Today");
            completedList.setGravity(Gravity.CENTER);
        }
        if(!incomplete.equals("")){
            notCompletedList.setText("Incomplete Tasks\n" + incomplete);
            notCompletedList.setGravity(Gravity.CENTER);
        }
        else{
            notCompletedList.setText("Incomplete Tasks\n\nNo Tasks Left To Do Today");
            notCompletedList.setGravity(Gravity.CENTER);
        }

        //completedList.setAdapter(completedAdapter);
        //notCompletedList.setAdapter(notCompletedAdapter);


    }


//    public void initBestCompleteBar(View layout){
//        ArrayList<StatsFragment.CategoryStats> data = SQLfunctionHelper.getFiveBestCompleteCategories(getContext(), new StatsFragment());
//        for(int i =0; i<data.size(); i++){
//            if(data.get(i).totalTasksWithCompleteStatus<5){
//                data.remove(data.get(i));
//                i--;
//            }
//        }
//
//        data.sort(new BestCompletionComparator());
//
//        //ArrayList<StatsFragment.CategoryStats> data = SQLfunctionHelper.filterBarGraph(categoriesComplete, getContext(), this);
//        if(data.size()==0){
//            return;
//        }
//        completedBarData = new ArrayList<>();
//        for(int i = 0; i<3 && i<data.size(); i++){
//            completedBarData.add(data.get(i));
//        }
//
//        //completedBarData =
//        GraphView completionG = layout.findViewById(R.id.hahaha);
//        completionG.removeAllSeries();
//        DataPoint[] completed = new DataPoint[completedBarData.size()];
//        for(int i =0; i< completed.length; i++){
//            completed[i] = new DataPoint(i, completedBarData.get(i).complete);
//        }
//        BarGraphSeries<DataPoint> completedSeries = new BarGraphSeries<>(completed);
//        completedSeries.setColor(Color.GREEN);
//
//        DataPoint[] incomplete = new DataPoint[completedBarData.size()];
//        for(int i =0; i< incomplete.length; i++){
//            incomplete[i] = new DataPoint(i, completedBarData.get(i).incomplete);
//        }
//        BarGraphSeries<DataPoint> incompleteSeries = new BarGraphSeries<>(incomplete);
//        incompleteSeries.setColor(Color.RED);
//        incompleteSeries.setDataWidth(.8);
//        completedSeries.setDataWidth(.8);
//
//        completionG.addSeries(completedSeries);
//        completionG.addSeries(incompleteSeries);
//        Log.d("HomeBarDebugCompleteBar", data.toString());
//        for(int i =0; i< incomplete.length; i++){
//            Log.d("HomeBarDebugCompleteBar", "Name: " + data.get(i).name + " X-Value: " + i + " Complete#: " + data.get(i).complete + " InComplete#: " + data.get(i).incomplete);
//        }
//        //barFilterComplete.
//        //completionG.getGridLabelRenderer().setNumHorizontalLabels(completedBarData.size()+2);
//        completionG.getGridLabelRenderer().setLabelFormatter( new DefaultLabelFormatter() {
//
//
//            @Override
//            public String formatLabel(double value, boolean isValueX){
//                String label = null;
//                if(isValueX){
////                    if(value<completedBarData.size()) {
////                        Log.d("StatsLabelDebug", "Position: " + value + " Label: " + completedBarData.get(((int) value)).name);
////                        label = completedBarData.get(((int) value)).name;
////                    }
//                    //Log.d("StatsLabelDebug", "Position: " + value + " Label: " + completedBarData.get(((int) value)).name);
//                    if(value==0 && value<completedBarData.size()){
//                        label = completedBarData.get(((int) value)).name;
//                    }
//                    if(value==1 && value<completedBarData.size()){
//                        label = completedBarData.get(((int) value)).name;
//                    }
//                    if(value==2 && value<completedBarData.size()){
//                        label = completedBarData.get(((int) value)).name;
//                    }
//                    return label;
//                }
//                else{
//                    return  super.formatLabel(value, isValueX);
//                }
//
//            }
//        });
//
//        completionG.getViewport().setMinY(0.0);
//        completionG.getViewport().setMinX(-5.0);
//        completionG.getViewport().setMaxX(5.0);
//    }
//
//    public void initWorstCompleteBar(View layout){
//        GraphView completionWG = layout.findViewById(R.id.task_completion_total_graph_worst);
//        ArrayList<StatsFragment.CategoryStats> data = SQLfunctionHelper.getFiveBestCompleteCategories(getContext(), new StatsFragment(), SQLfunctionHelper.getCategoryList(getContext());
//        for(int i =0; i<data.size(); i++){
//            if(data.get(i).totalTasksWithCompleteStatus<5){
//                data.remove(data.get(i));
//                i--;
//            }
//        }
//
//        data.sort(new BestCompletionComparator());
//        Collections.reverse(data);
//
//        //ArrayList<StatsFragment.CategoryStats> data = SQLfunctionHelper.filterBarGraph(categoriesComplete, getContext(), this);
//        if(data.size()==0){
//            return;
//        }
//        completedBarData = new ArrayList<>();
//        for(int i = 0; i<3 && i<data.size(); i++){
//            completedBarData.add(data.get(i));
//        }
//        completionWG.removeAllSeries();
//        DataPoint[] completed = new DataPoint[completedBarData.size()];
//        for(int i =0; i< completed.length; i++){
//            completed[i] = new DataPoint(i, completedBarData.get(i).complete);
//        }
//        BarGraphSeries<DataPoint> completedSeries = new BarGraphSeries<>(completed);
//        completedSeries.setColor(Color.GREEN);
//
//        DataPoint[] incomplete = new DataPoint[completedBarData.size()];
//        for(int i =0; i< incomplete.length; i++){
//            incomplete[i] = new DataPoint(i, completedBarData.get(i).incomplete);
//        }
//        BarGraphSeries<DataPoint> incompleteSeries = new BarGraphSeries<>(incomplete);
//        incompleteSeries.setColor(Color.RED);
//        incompleteSeries.setDataWidth(.8);
//        completedSeries.setDataWidth(.8);
//
//        completionWG.addSeries(completedSeries);
//        completionWG.addSeries(incompleteSeries);
//        Log.d("HomeBarDebugCompleteBarWorst", data.toString());
//        for(int i =0; i< incomplete.length; i++){
//            Log.d("HomeBarDebugCompleteBarWorst", "Name: " + data.get(i).name + " X-Value: " + i + " Complete#: " + data.get(i).complete + " InComplete#: " + data.get(i).incomplete);
//        }
//        //barFilterComplete.
//        //completionG.getGridLabelRenderer().setNumHorizontalLabels(completedBarData.size()+2);
//        completionWG.getGridLabelRenderer().setLabelFormatter( new DefaultLabelFormatter() {
//
//
//            @Override
//            public String formatLabel(double value, boolean isValueX){
//                String label = null;
//                if(isValueX){
////                    if(value<completedBarData.size()) {
////                        Log.d("StatsLabelDebug", "Position: " + value + " Label: " + completedBarData.get(((int) value)).name);
////                        label = completedBarData.get(((int) value)).name;
////                    }
//                    //Log.d("StatsLabelDebug", "Position: " + value + " Label: " + completedBarData.get(((int) value)).name);
//                    if(value==0 && value<completedBarData.size()){
//                        label = completedBarData.get(((int) value)).name;
//                    }
//                    if(value==1 && value<completedBarData.size()){
//                        label = completedBarData.get(((int) value)).name;
//                    }
//                    if(value==2 && value<completedBarData.size()){
//                        label = completedBarData.get(((int) value)).name;
//                    }
//                    return label;
//                }
//                else{
//                    return  super.formatLabel(value, isValueX);
//                }
//
//            }
//        });
//
//        completionWG.getViewport().setMinY(0.0);
//        completionWG.getViewport().setMinX(-5.0);
//        completionWG.getViewport().setMaxX(5.0);
//    }
//
//    public void initBestOnTimeBar(View layout){
//        GraphView onTimeG = layout.findViewById(R.id.task_onTime_total_graph_best);
//        BarGraphSeries<DataPoint> seriestwo = new BarGraphSeries<>(new DataPoint[] {
//                new DataPoint(0, 7),
//                new DataPoint(1, 7),
//                new DataPoint(2, 7),
//                new DataPoint(3, 7),
//                new DataPoint(4, 7)
//        });
//        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
//                new DataPoint(0, -1),
//                new DataPoint(1, 5),
//                new DataPoint(2, 3),
//                new DataPoint(3, 2),
//                new DataPoint(4, 6)
//        });
//        onTimeG.addSeries(series);
//        onTimeG.addSeries(seriestwo);
//        onTimeG.setTitle("Best Task On-Time Total");
//        seriestwo.setSpacing(50);
//
//        TextView onTimeKey = layout.findViewById(R.id.onTime_key_best);
//        onTimeKey.setBackgroundColor(Color.GREEN);
//
//        TextView lateKey = layout.findViewById(R.id.late_key_best);
//        lateKey.setBackgroundColor(Color.RED);
//
//    }
//
//    public void initWorstOnTimeBar(View layout){
//        GraphView onTimeWG = layout.findViewById(R.id.task_onTime_total_graph_worst);
//        BarGraphSeries<DataPoint> seriestwo = new BarGraphSeries<>(new DataPoint[] {
//                new DataPoint(0, 7),
//                new DataPoint(1, 7),
//                new DataPoint(2, 7),
//                new DataPoint(3, 7),
//                new DataPoint(4, 7)
//        });
//        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
//                new DataPoint(0, -1),
//                new DataPoint(1, 5),
//                new DataPoint(2, 3),
//                new DataPoint(3, 2),
//                new DataPoint(4, 6)
//        });
//        onTimeWG.addSeries(series);
//        onTimeWG.setTitle("Worst Task On-Time Total");
//        seriestwo.setSpacing(50);
//
//        TextView onTimeKey = layout.findViewById(R.id.onTime_key_worst);
//        onTimeKey.setBackgroundColor(Color.GREEN);
//
//        TextView lateKey = layout.findViewById(R.id.late_key_worst);
//        lateKey.setBackgroundColor(Color.RED);
//
//    }

    public void initCompleteLineChart(View layout){
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTime(Calendar.getInstance().getTime());

        Map<String, Integer> monthMapper = new HashMap<>();
        monthMapper.put("Jan", 0);
        monthMapper.put("Feb", 1);
        monthMapper.put("Mar", 2);
        monthMapper.put("Apr", 3);
        monthMapper.put("May", 4);
        monthMapper.put("Jun", 5);
        monthMapper.put("Jul", 6);
        monthMapper.put("Aug", 7);
        monthMapper.put("Sep", 8);
        monthMapper.put("Oct", 9);
        monthMapper.put("Nov", 10);
        monthMapper.put("Dec", 11);

        String[] daterep = gcal.getTime().toString().split(" ");
        String date = TaskCreatorFragment.constructDateStr(Integer.parseInt(daterep[5]), monthMapper.get(daterep[1]), Integer.parseInt(daterep[2]));
        final ArrayList<StatsFragment.DayStats> data = SQLfunctionHelper.getWeekOnTimeTasksFilter(getContext(), new StatsFragment(), date);
        Log.d("HomeDebug", data.toString());
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
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTime(Calendar.getInstance().getTime());

        Map<String, Integer> monthMapper = new HashMap<>();
        monthMapper.put("Jan", 0);
        monthMapper.put("Feb", 1);
        monthMapper.put("Mar", 2);
        monthMapper.put("Apr", 3);
        monthMapper.put("May", 4);
        monthMapper.put("Jun", 5);
        monthMapper.put("Jul", 6);
        monthMapper.put("Aug", 7);
        monthMapper.put("Sep", 8);
        monthMapper.put("Oct", 9);
        monthMapper.put("Nov", 10);
        monthMapper.put("Dec", 11);

        String[] daterep = gcal.getTime().toString().split(" ");
        String date = TaskCreatorFragment.constructDateStr(Integer.parseInt(daterep[5]), monthMapper.get(daterep[1]), Integer.parseInt(daterep[2]));
        final ArrayList<StatsFragment.DayStats> data = SQLfunctionHelper.getWeekOnTimeTasksFilter(getContext(), new StatsFragment(), date);
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
            if(100*categoryStats.complete/categoryStats.totalTasksWithCompleteStatus < 100*t1.complete/t1.totalTasksWithCompleteStatus){
                return 1;
            }
            else{
                if(100*categoryStats.complete/categoryStats.totalTasksWithCompleteStatus == 100*t1.complete/t1.totalTasksWithCompleteStatus){
                    if(categoryStats.totalTasksWithCompleteStatus < t1.totalTasksWithCompleteStatus){
                        return 1;
                    }
                    return -1;
                }
                else {
                    return -1;
                }
            }
        }
    }

    public class WorstCompletionComparator implements Comparator<StatsFragment.CategoryStats> {


        @Override
        public int compare(StatsFragment.CategoryStats categoryStats, StatsFragment.CategoryStats t1) {
            if(categoryStats.complete/categoryStats.totalTasksWithCompleteStatus < t1.complete/t1.totalTasksWithCompleteStatus){
                if(categoryStats.complete/categoryStats.totalTasksWithCompleteStatus < t1.complete/t1.totalTasksWithCompleteStatus){
                    return 1;
                }
                else {
                    return 1;
                }
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
//                if(categoryStats.onTime/categoryStats.totalTasksWithOnTimeStatus == t1.onTime/t1.totalTasksWithOnTimeStatus){
//                    if(categoryStats.onTime/)
//                }
//                else {
//                    return -1;
//                }
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
