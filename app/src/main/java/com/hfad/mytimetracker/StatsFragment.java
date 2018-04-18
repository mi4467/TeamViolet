package com.hfad.mytimetracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;

public class StatsFragment extends Fragment {


    public StatsFragment() {
        // Required empty public constructor
    }



    @SuppressLint("RestrictedAPI")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View layout = inflater.inflate(R.layout.fragment_stats, container, false);

        initBestCompleteBar(layout);        //these functions set up the respective cardviews
        initBestOnTimeBar(layout);          //as of right now these hold dummy data, in these functions implement the neccessary sql needed to show this
        initWorstCompleteBar(layout);
        initWorstOnTimeBar(layout);
        initCompleteLineChart(layout);
        initOnTimeLineChart(layout);
        initIncompletePieChart(layout);
        initLatePieChart(layout);

        return layout;
    }

    public void initBestCompleteBar(View layout){
        ArrayList<CategoryStats> categories = SQLfunctionHelper.getFiveBestCompleteCategories(getContext(), this);
        GraphView completionG = layout.findViewById(R.id.task_completion_total_graph_best);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, -1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });

        completionG.getGridLabelRenderer().setLabelFormatter( new DefaultLabelFormatter(){

            @Override
            public String formatLabel(double value, boolean isValueX){
                String result = "";
                if(isValueX){
                    //return "pizza";             //use this to create x axis as category labels
                    return super.formatLabel(value, isValueX);
                }
                else{
                    return "pizza";
                }
            }
        });

        completionG.addSeries(series);
        completionG.getViewport().setScrollable(true);
        completionG.getViewport().setScalable(true);
        completionG.setTitle("Best Task Completion Total");
        series.setSpacing(50);

        TextView onTimeKey = layout.findViewById(R.id.completed_key_best);
        onTimeKey.setBackgroundColor(Color.GREEN);

        TextView lateKey = layout.findViewById(R.id.not_completed_key_best);
        lateKey.setBackgroundColor(Color.RED);


    }

    public void initWorstCompleteBar(View layout){
        GraphView completionWG = layout.findViewById(R.id.task_completion_total_graph_worst);
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
        final ArrayList<DayStats> data = SQLfunctionHelper.getWeekOnTimeTasks(getContext(), this);
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
        final ArrayList<DayStats> data = SQLfunctionHelper.getWeekOnTimeTasks(getContext(), this);
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

        TextView totalKey = layout.findViewById(R.id.total_onTime_key);
        totalKey.setBackgroundColor(Color.YELLOW);

        TextView onTimeKey = layout.findViewById(R.id.onTime_key);
        onTimeKey.setBackgroundColor(Color.GREEN);

        TextView lateKey = layout.findViewById(R.id.late_onTime_key);
        lateKey.setBackgroundColor(Color.RED);

    }

    public void initIncompletePieChart(View layout){
        ArrayList<CategoryStats> data = SQLfunctionHelper.getFiveBestCompleteCategories(getContext(), this);
        PieChart mPieChart = (PieChart) layout.findViewById(R.id.notCompleted_piechart);
        for(CategoryStats curr : data){
            mPieChart.addPieSlice(new PieModel(curr.name, curr.incomplete, Color.parseColor(String.format("#%06X", (0xFFFFFF & curr.color)))));
        }

    }

    public void initLatePieChart(View layout){
        PieChart mPieCharttwo = (PieChart) layout.findViewById(R.id.late_piechart);
        ArrayList<CategoryStats> data = SQLfunctionHelper.getFiveBestCompleteCategories(getContext(), this);
        for(CategoryStats curr : data){
            mPieCharttwo.addPieSlice(new PieModel(curr.name, curr.late, Color.parseColor(String.format("#%06X", (0xFFFFFF & curr.color)))));
        }
    }

//    public class PieSlice {
//        String name;
//        int percentage;
//        int color;
//
//        public PieSlice(String s, int perc, int col){
//            name = s;
//            percentage = perc;
//            color = col;
//        }
//
//    }

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
