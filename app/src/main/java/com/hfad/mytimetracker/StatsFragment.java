package com.hfad.mytimetracker;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.db.chart.model.Bar;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieDataSet;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import im.dacer.androidcharts.PieHelper;
import im.dacer.androidcharts.PieView;

public class StatsFragment extends Fragment {

    private static CharSequence[] categoriesComplete = null;
    private static CharSequence[] categoriesOnTime = null;

    private static Integer onTimeYear=null;              //use for third cardview
    private static Integer onTimeDay=null;
    private static Integer onTimeMonth=null;
    private static Integer completeYear=null;
    private static Integer completeMonth=null;
    private static Integer completeDay=null;

    private static ArrayList<DayStats> completedLineData = null;
    private static ArrayList<DayStats> onTimeLineData = null;
    private static ArrayList<CategoryStats> completedBarData = null;
    private static ArrayList<CategoryStats> onTimeBarData = null;

    public StatsFragment() {
        // Required empty public constructor
    }



    @SuppressLint("RestrictedAPI")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View layout = inflater.inflate(R.layout.fragment_stats, container, false);

//        initBestCompleteBar(layout);        //these functions set up the respective cardviews
//        initBestOnTimeBar(layout);          //as of right now these hold dummy data, in these functions implement the neccessary sql needed to show this
//        initWorstCompleteBar(layout);
//        initWorstOnTimeBar(layout);
//        initCompleteLineChart(layout);
//        initOnTimeLineChart(layout);
        initIncompletePieChart(layout);
        initLatePieChart(layout);
        initListeners(layout);
        initField();
        initLayout(layout);
        setUpCompletionWeekLineGraph(layout);
        setUpOnTimeWeekLineGraph(layout);
//        PieView pieView = (PieView)layout.findViewById(R.id.late_pie_view);
//        ArrayList<PieHelper> pieHelperArrayList = new ArrayList<PieHelper>();
//        pieHelperArrayList.add(new PieHelper(45, Color.YELLOW));
//        pieHelperArrayList.add(new PieHelper(50, Color.BLUE));
//        pieView.setDate(pieHelperArrayList);
//        pieView.selectedPie(2); //optional
//        //pieView.setOnPieClickListener(listener) //optional
//        pieView.showPercentLabel(true); //optional
//
//        pieView = layout.findViewById(R.id.incomplete_pie_view);
//        pieView.setDate(pieHelperArrayList);
//        pieView.showPercentLabel(true);

    //    initIncompletePieChart(layout);

        //PieChart mChart = layout.findViewById(R.id.chart1);
        return layout;
    }

    public void initLayout(View layout){
        TextView completeBar = layout.findViewById(R.id.completed_key_pick_three);
        completeBar.setBackgroundColor(Color.GREEN);
        TextView incompleteBar = layout.findViewById(R.id.not_completed_key_pick_three);
        incompleteBar.setBackgroundColor(Color.RED);

        TextView onTimeBar = layout.findViewById(R.id.onTime_key_pick_three);
        onTimeBar.setBackgroundColor(Color.GREEN);
        TextView lateBar = layout.findViewById(R.id.late_key_pick_three);
        lateBar.setBackgroundColor(Color.RED);
    }

    public void initListeners(final View layout){
        Button completionFilter = layout.findViewById(R.id.filterCompletionButton);
        completionFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment.flag=0;
                showCompleteTaskCatSelection();
            }
        });

        Button completionFilterBarEnter = layout.findViewById(R.id.filterCompletionEnter);
        completionFilterBarEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpCompletionBarGraph(layout);
            }
        });

        Button onTimeFilter = layout.findViewById(R.id.filterOnTimeButton);
        onTimeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOnTimeTaskCatSelection();
            }
        });

        Button onTimeFilterBarEnter = layout.findViewById(R.id.filterOnTimeButtonEnter);
        onTimeFilterBarEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpOnTimeBarGraph(layout);
            }
        });

        Button completionWeekFilter = layout.findViewById(R.id.filterCompletionWeekButton);
        completionWeekFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment.flag=0;
                showDatePickerDialog(view);
                Log.d("StatsDebug", "Date is: " + completeMonth + "/" + completeDay);
            }
        });

        Button completionWeekEnter = layout.findViewById(R.id.filterCompletionWeekEnter);
        completionWeekEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpCompletionWeekLineGraph(layout);
            }
        });

        Button onTimeWeekFilter = layout.findViewById(R.id.filterOnTimeWeekButton);
        onTimeWeekFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment.flag=1;
                showDatePickerDialog(view);
                //setUpOnTimeLineGraph();
            }
        });

        Button onTimeWeekEnter = layout.findViewById(R.id.filterOnTimeWeekEnter);
        onTimeWeekEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpOnTimeWeekLineGraph(layout);
            }
        });


    }

    public void initField(){
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
        completeYear = Integer.parseInt(daterep[5]);
        completeMonth = monthMapper.get(daterep[1]);
        completeDay = Integer.parseInt(daterep[2]);
        onTimeYear = Integer.parseInt(daterep[5]);
        onTimeMonth = monthMapper.get(daterep[1]);
        onTimeDay = Integer.parseInt(daterep[2]);

    }

    public void setUpCompletionBarGraph(View layout){
        ArrayList<CategoryStats> data = SQLfunctionHelper.filterBarGraph(categoriesComplete, getContext(), this);
        if(data.size()==0){
            return;
        }
        completedBarData = data;
        GraphView barFilterComplete = layout.findViewById(R.id.task_completion_total_graph_pick_three);
        barFilterComplete.removeAllSeries();
        DataPoint[] completed = new DataPoint[data.size()];
        for(int i =0; i< completed.length; i++){
            completed[i] = new DataPoint(i, data.get(i).complete);
        }
        BarGraphSeries<DataPoint> completedSeries = new BarGraphSeries<>(completed);
        completedSeries.setColor(Color.GREEN);

        DataPoint[] incomplete = new DataPoint[data.size()];
        for(int i =0; i< incomplete.length; i++){
            incomplete[i] = new DataPoint(i, data.get(i).incomplete);
        }
        BarGraphSeries<DataPoint> incompleteSeries = new BarGraphSeries<>(incomplete);
        incompleteSeries.setColor(Color.RED);
        incompleteSeries.setDataWidth(.8);
        completedSeries.setDataWidth(.8);

        barFilterComplete.addSeries(completedSeries);
        barFilterComplete.addSeries(incompleteSeries);
        barFilterComplete.getViewport().setMinY(0.0);
        barFilterComplete.getViewport().setMinX(-1.0);
        barFilterComplete.getViewport().setMaxX(3.0);

        for(int i =0; i< incomplete.length; i++){
            Log.d("StatsBarDebug", "Name: " + data.get(i).name + " X-Value: " + i + " Complete#: " + data.get(i).complete + " InComplete#: " + data.get(i).incomplete);
        }
        //barFilterComplete.
        barFilterComplete.getGridLabelRenderer().setNumHorizontalLabels(data.size()+2);
        barFilterComplete.getGridLabelRenderer().setLabelFormatter( new DefaultLabelFormatter() {


            @Override
            public String formatLabel(double value, boolean isValueX){
                String label = null;
                if(isValueX){
                    if(value==0 && value<completedBarData.size()){
                        label = completedBarData.get(((int) value)).name;
                    }
                    if(value==1 && value<completedBarData.size()){
                        label = completedBarData.get(((int) value)).name;
                    }
                    if(value==2 && value<completedBarData.size()){
                        label = completedBarData.get(((int) value)).name;
                    }
                    return label;
                }
                else{
                    return  super.formatLabel(value, isValueX);
                }

            }
        });
    }

    public void setUpOnTimeBarGraph(View layout){
        ArrayList<CategoryStats> data = SQLfunctionHelper.filterBarGraph(categoriesOnTime, getContext(), this);
        if(data.size()==0){
            return;
        }
        onTimeBarData = data;
        GraphView barFilterComplete = layout.findViewById(R.id.task_onTime_total_graph_pick_three);
        barFilterComplete.removeAllSeries();
        DataPoint[] onTime = new DataPoint[data.size()];
        for(int i =0; i< onTime.length; i++){
            onTime[i] = new DataPoint(i, data.get(i).onTime);
        }
        BarGraphSeries<DataPoint> onTimeSeries = new BarGraphSeries<>(onTime);
        onTimeSeries.setColor(Color.GREEN);

        DataPoint[] late = new DataPoint[data.size()];
        for(int i =0; i< late.length; i++){
            late[i] = new DataPoint(i, data.get(i).late);
        }
        BarGraphSeries<DataPoint> lateSeries = new BarGraphSeries<>(late);
        lateSeries.setColor(Color.RED);
        lateSeries.setDataWidth(.8);
        onTimeSeries.setDataWidth(.8);

        barFilterComplete.addSeries(onTimeSeries);
        barFilterComplete.addSeries(lateSeries);
        barFilterComplete.getViewport().setMinY(0.0);
        barFilterComplete.getViewport().setMinX(-1.0);
        barFilterComplete.getViewport().setMaxX(3.0);

        for(int i =0; i< late.length; i++){
            Log.d("StatsBarDebug", "Name: " + data.get(i).name + " X-Value: " + i + " Complete#: " + data.get(i).complete + " InComplete#: " + data.get(i).incomplete);
        }
        //barFilterComplete.
        barFilterComplete.getGridLabelRenderer().setNumHorizontalLabels(data.size()+2);
        barFilterComplete.getGridLabelRenderer().setLabelFormatter( new DefaultLabelFormatter() {


            @Override
            public String formatLabel(double value, boolean isValueX){
                String label = null;
                if(isValueX){
//                    if(value<completedBarData.size()) {
//                        Log.d("StatsLabelDebug", "Position: " + value + " Label: " + completedBarData.get(((int) value)).name);
//                        label = completedBarData.get(((int) value)).name;
//                    }
                    //Log.d("StatsLabelDebug", "Position: " + value + " Label: " + completedBarData.get(((int) value)).name);
                    if(value==0 && value<onTimeBarData.size() ){
                        label = onTimeBarData.get(((int) value)).name;
                    }
                    if(value==1 && value<onTimeBarData.size()){
                        label = onTimeBarData.get(((int) value)).name;
                    }
                    if(value==2 && value<onTimeBarData.size()){
                        label = onTimeBarData.get(((int) value)).name;
                    }
                    return label;
                }
                else{
                    return  super.formatLabel(value, isValueX);
                }

            }
        });

    }

    public void setUpCompletionWeekLineGraph(View layout){
        if(StatsFragment.completeYear==null){
            Toast.makeText(getActivity(), "You Did Not Select A Date. Try Again", Toast.LENGTH_SHORT).show();
            return;
        }
        String date = TaskCreatorFragment.constructDateStr(completeYear, completeMonth, completeDay);
        Log.d("StatsDebug", "The date passed into the data is: " +date);
        ArrayList<DayStats> data = SQLfunctionHelper.getWeekOnTimeTasksFilter(getContext(), this, date);
        completedLineData = data;
        DataPoint[] total = new DataPoint[7];
        for(int i = 0; i<7; i++){
            total[i] = new DataPoint(i, data.get(total.length-i-1).totalTasksWithCompleteStatus);
        }
        final DataPoint[] complete = new DataPoint[7];
        for(int i = 0; i<7; i++){
            complete[i] = new DataPoint(i, data.get(total.length-i-1).complete);
        }
        DataPoint[] incomplete = new DataPoint[7];
        for(int i = 0; i<7; i++){
            incomplete[i] = new DataPoint(i, data.get(total.length-i-1).incomplete);
        }

        GraphView completeWeek = layout.findViewById(R.id.task_complete_week_graph_filter);
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
                String result = null;
                if(isValueX){
                    if(value<7 ) {
                        //return data.get((int)value).date;             //use this to create x axis as category labels
                        String[] date = StatsFragment.completedLineData.get(completedLineData.size()-1-((int) value)).date.split("-");
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

        TextView totalKey = layout.findViewById(R.id.total_complete_key_filter);
        totalKey.setBackgroundColor(Color.YELLOW);

        TextView onTimeKey = layout.findViewById(R.id.complete_key_filter);
        onTimeKey.setBackgroundColor(Color.GREEN);

        TextView lateKey = layout.findViewById(R.id.incomplete_key_filter);
        lateKey.setBackgroundColor(Color.RED);

    }

    public void setUpOnTimeWeekLineGraph(View layout){
        if(StatsFragment.completeYear==null){
            Toast.makeText(getActivity(), "You Did Not Select A Date. Try Again", Toast.LENGTH_SHORT).show();
            return;
        }
        String date = TaskCreatorFragment.constructDateStr(onTimeYear, onTimeMonth, onTimeDay);
        Log.d("StatsDebug", "The date passed into the data is: " +date);
        ArrayList<DayStats> data = SQLfunctionHelper.getWeekOnTimeTasksFilter(getContext(), this, date);
        onTimeLineData = data;
        DataPoint[] total = new DataPoint[7];
        for(int i = 0; i<7; i++){
            total[i] = new DataPoint(i, data.get(total.length-i-1).totalTasksWithOnTimeStatus);
        }
        final DataPoint[] complete = new DataPoint[7];
        for(int i = 0; i<7; i++){
            complete[i] = new DataPoint(i, data.get(total.length-i-1).onTime);
        }
        DataPoint[] incomplete = new DataPoint[7];
        for(int i = 0; i<7; i++){
            incomplete[i] = new DataPoint(i, data.get(total.length-i-1).late);
        }

        GraphView completeWeek = layout.findViewById(R.id.onTime_week_graph_filter);
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
                        String[] date = StatsFragment.onTimeLineData.get(onTimeLineData.size()-1-((int) value)).date.split("-");
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

        TextView totalKey = layout.findViewById(R.id.onTime_complete_key_filter);
        totalKey.setBackgroundColor(Color.YELLOW);

        TextView onTimeKey = layout.findViewById(R.id.onTime_key_filter);
        onTimeKey.setBackgroundColor(Color.GREEN);

        TextView lateKey = layout.findViewById(R.id.late_key_filter);
        lateKey.setBackgroundColor(Color.RED);

    }

//    public void initBestCompleteBar(View layout){
//        ArrayList<CategoryStats> categories = SQLfunctionHelper.getFiveBestCompleteCategories(getContext(), this);
//        final GraphView completionG = layout.findViewById(R.id.task_completion_total_graph_best);
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
//
//
//    }
//
//    public void initWorstCompleteBar(View layout){
//        GraphView completionWG = layout.findViewById(R.id.task_completion_total_graph_worst);
//        BarGraphSeries<DataPoint> seriestwo = new BarGraphSeries<>(new DataPoint[] {
//                new DataPoint(0, 7),
//                new DataPoint(1, 7),
//                new DataPoint(2, 7)
//        });
//        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
//                new DataPoint(0, -1),
//                new DataPoint(1, 5),
//                new DataPoint(2, 3),
//                new DataPoint(3, 2),
//                new DataPoint(4, 6)
//        });
//
//        completionWG.addSeries(series);
//        completionWG.setTitle("Worst Task Completion Total");
//        seriestwo.setSpacing(50);
//
//        TextView onTimeKey = layout.findViewById(R.id.completed_key_worst);
//        onTimeKey.setBackgroundColor(Color.GREEN);
//
//        TextView lateKey = layout.findViewById(R.id.not_completed_key_worst);
//        lateKey.setBackgroundColor(Color.RED);
//
//
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
//
//    public void initCompleteLineChart(View layout){
//        final ArrayList<DayStats> data = SQLfunctionHelper.getWeekOnTimeTasks(getContext(), this);
//        DataPoint[] total = new DataPoint[7];
//        for(int i = 0; i<7; i++){
//            total[i] = new DataPoint(i, data.get(total.length-i-1).totalTasksWithCompleteStatus);
//        }
//        DataPoint[] complete = new DataPoint[7];
//        for(int i = 0; i<7; i++){
//            complete[i] = new DataPoint(i, data.get(total.length-i-1).complete);
//        }
//        DataPoint[] incomplete = new DataPoint[7];
//        for(int i = 0; i<7; i++){
//            incomplete[i] = new DataPoint(i, data.get(total.length-i-1).incomplete);
//        }
//
//        GraphView completeWeek = layout.findViewById(R.id.task_complete_week_graph);
//        completeWeek.addSeries(new LineGraphSeries<DataPoint>(total));
//        completeWeek.addSeries(new LineGraphSeries<DataPoint>(complete));
//        completeWeek.addSeries(new LineGraphSeries<DataPoint>(incomplete));
//
//        LineGraphSeries<DataPoint> totalSeries = new LineGraphSeries<DataPoint>(total);
//        LineGraphSeries<DataPoint> completeSeries = new LineGraphSeries<DataPoint>(complete);////////////////////////////////////////
//        LineGraphSeries<DataPoint> incompleteSeries = new LineGraphSeries<DataPoint>(incomplete);
//        totalSeries.setTitle("Total");
//        totalSeries.setColor(Color.YELLOW);
//        completeWeek.addSeries(totalSeries);
//        completeSeries.setTitle("Complete");
//        completeSeries.setColor(Color.GREEN);
//        completeWeek.addSeries(completeSeries);
//        incompleteSeries.setTitle("Incomplete");
//        incompleteSeries.setColor(Color.RED);
//        completeWeek.addSeries(incompleteSeries);
//        completeWeek.getGridLabelRenderer().setNumHorizontalLabels(7);
//        completeWeek.getGridLabelRenderer().setLabelFormatter( new DefaultLabelFormatter(){
//
//            @Override
//            public String formatLabel(double value, boolean isValueX){
//                String result = "";
//                if(isValueX){
//                    if(value<7) {
//                        //return data.get((int)value).date;             //use this to create x axis as category labels
//                        String[] date = data.get(data.size()-1-((int) value)).date.split("-");
//
//                        return date[1] + "/" + date[2];
//                    }
//                    return null;
//                }
//                else{
//                    return super.formatLabel(value, isValueX);
//                }
//            }
//        });
//        completeWeek.setTitle("On-Time Stats Over Past Week");
//
//        TextView totalKey = layout.findViewById(R.id.total_complete_key);
//        totalKey.setBackgroundColor(Color.YELLOW);
//
//        TextView onTimeKey = layout.findViewById(R.id.complete_key);
//        onTimeKey.setBackgroundColor(Color.GREEN);
//
//        TextView lateKey = layout.findViewById(R.id.incomplete_key);
//        lateKey.setBackgroundColor(Color.RED);
//    }
//
//    public void initOnTimeLineChart(View layout){
//        final ArrayList<DayStats> data = SQLfunctionHelper.getWeekOnTimeTasks(getContext(), this);
//        DataPoint[] total = new DataPoint[7];
//        for(int i = 0; i<7; i++){
//            total[i] = new DataPoint(i, data.get(total.length-i-1).totalTasksWithOnTimeStatus);
//        }
//        DataPoint[] ontime = new DataPoint[7];
//        for(int i = 0; i<7; i++){
//            ontime[i] = new DataPoint(i, data.get(total.length-i-1).onTime);
//        }
//        DataPoint[] late = new DataPoint[7];
//        for(int i = 0; i<7; i++){
//            late[i] = new DataPoint(i, data.get(total.length-i-1).late);
//        }
//
//        GraphView onTimeWeek = layout.findViewById(R.id.task_onTime_week_graph);
//        LineGraphSeries<DataPoint> totalSeries = new LineGraphSeries<DataPoint>(total);
//        LineGraphSeries<DataPoint> onTimeSeries = new LineGraphSeries<DataPoint>(ontime);////////////////////////////////////////
//        LineGraphSeries<DataPoint> lateSeries = new LineGraphSeries<DataPoint>(late);
//        totalSeries.setTitle("Total");
//        totalSeries.setColor(Color.YELLOW);
//        onTimeWeek.addSeries(totalSeries);
//        onTimeSeries.setTitle("On-Time");
//        onTimeSeries.setColor(Color.GREEN);
//        onTimeWeek.addSeries(onTimeSeries);
//        lateSeries.setTitle("Late");
//        lateSeries.setColor(Color.RED);
//        onTimeWeek.addSeries(lateSeries);
//        onTimeWeek.getGridLabelRenderer().setNumHorizontalLabels(7);
//        onTimeWeek.getGridLabelRenderer().setLabelFormatter( new DefaultLabelFormatter(){
//
//            @Override
//            public String formatLabel(double value, boolean isValueX){
//                String result = "";
//                if(isValueX){
//                    if(value<7) {
//                        //return data.get((int)value).date;             //use this to create x axis as category labels
//                        String[] date = data.get(data.size()-1-((int) value)).date.split("-");
//
//                        return date[1] + "/" + date[2];
//                    }
//                    return null;
//                }
//                else{
//                    return super.formatLabel(value, isValueX);
//                }
//            }
//        });
//        onTimeWeek.setTitle("On-Time Stats Over Past Week");
//
//        onTimeWeek.getGridLabelRenderer().setVerticalAxisTitle("Number of Tasks");
//        onTimeWeek.getGridLabelRenderer().setHorizontalAxisTitle("Date");
//
//        TextView totalKey = layout.findViewById(R.id.total_onTime_key);
//        totalKey.setBackgroundColor(Color.YELLOW);
//
//        TextView onTimeKey = layout.findViewById(R.id.onTime_key);
//        onTimeKey.setBackgroundColor(Color.GREEN);
//
//        TextView lateKey = layout.findViewById(R.id.late_onTime_key);
//        lateKey.setBackgroundColor(Color.RED);
//
//    }
//

    public void initIncompletePieChart(View layout){
        PieChart mPieCharttwo = (PieChart) layout.findViewById(R.id.notCompleted_piechart);
        ArrayList<CategoryStats> data = SQLfunctionHelper.getFiveBestCompleteCategories(getContext(), this);
        for(CategoryStats curr : data){
            mPieCharttwo.addPieSlice(new PieModel(curr.name, curr.incomplete, Color.parseColor(String.format("#%06X", (0xFFFFFF & curr.color)))));
        }

    }

    public void initLatePieChart(View layout){
        PieChart mPieCharttwo = (PieChart) layout.findViewById(R.id.late_piechart);
        ArrayList<CategoryStats> data = SQLfunctionHelper.getFiveBestCompleteCategories(getContext(), this);
        for(CategoryStats curr : data){
            mPieCharttwo.addPieSlice(new PieModel(curr.name, curr.late, Color.parseColor(String.format("#%06X", (0xFFFFFF & curr.color)))));
        }
    }

    public void showCompleteTaskCatSelection(){
        new MaterialDialog.Builder(getContext())
                .title("Specify Up To Five Categories")
                .items(SQLfunctionHelper.getCategoryList(getContext()))
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        if (text.length > 5) {
                            Toast.makeText(getActivity(), "You selected too many. Try Again", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            StatsFragment.categoriesComplete = text;
                        }
                        return true;
                    }
                })
                .positiveText("confirm")
                .negativeText("cancel")
                .show();
    }

    public void showOnTimeTaskCatSelection(){
        new MaterialDialog.Builder(getContext())
                .title("Specify Up To Categories")
                .items(SQLfunctionHelper.getCategoryList(getContext()))
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        if (text.length > 5) {
                            Toast.makeText(getActivity(), "You selected too many. Try Again", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            StatsFragment.categoriesOnTime = text;
                        }
                        return true;
                    }
                })
                .inputRange(0,3)
                .positiveText("confirm")
                .negativeText("cancel")
                .show();
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        private static int flag;

        public void onDateSet(DatePicker view, int year, int month, int day) {
            if(DatePickerFragment.flag==0){
                Log.d("StatsDebug", "Flag is 0");
                Log.d("StatsDebug", month + "/" + day);
                StatsFragment.completeYear = year;
                StatsFragment.completeMonth = month;
                StatsFragment.completeDay = day;
            }
            if(DatePickerFragment.flag==1){
                StatsFragment.onTimeYear = year;
                StatsFragment.onTimeMonth = month;
                StatsFragment.onTimeDay = day;
            }
        }
    }

    public class CategoryStats {
        String name;
        int color;
        int complete;
        int incomplete;
        int totalTasksWithCompleteStatus;
        int onTime;
        int late;
        int totalTasksWithOnTimeStatus;

        public CategoryStats(String n, int color, int c, int i, int o, int l){
            name = n;
            this.color = color;
            complete = c;
            incomplete =i;
            totalTasksWithCompleteStatus = c+i;
            onTime = o;
            late = l;
            totalTasksWithOnTimeStatus = o+l;
        }

        @Override
        public String toString(){
            return "NAME = " + this.name + "\n" +
                    "COLOR = " + this.color + "\n" +
                    "Complete # = " + this.complete + "\n" +
                    "InComplete # = " + this.incomplete + "\n" +
                    "onTime # = " + this.onTime + "\n" +
                    "Late # = " + this.late + "\n";
        }

    }

    public class DayStats {
        String date;
        int complete;
        int incomplete;
        int totalTasksWithCompleteStatus;
        int onTime;
        int late;
        int totalTasksWithOnTimeStatus;

        public DayStats(String n, int c, int i, int o, int l){
            date = n;
            complete = c;
            incomplete =i;
            totalTasksWithCompleteStatus = c+i;
            onTime = o;
            late = l;
            totalTasksWithOnTimeStatus = l+o;
        }

        @Override
        public String toString(){
            return "DATE = " + this.date + "\n" +
                    "Complete # = " + this.complete + "\n" +
                    "InComplete # = " + this.incomplete + "\n" +
                    "onTime # = " + this.onTime + "\n" +
                    "Late # = " + this.late + "\n";
        }

    }


}
