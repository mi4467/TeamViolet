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
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
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
        initInompletePieChart(layout);
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
                    return "pizza";             //use this to create x axis as category labels
                }
                else{
                    return super.formatLabel(value, isValueX);
                }
            }
        });

        completionG.addSeries(series);
        completionG.getViewport().setScrollable(true);
        completionG.getViewport().setScalable(true);
        completionG.setTitle("Best Task Completion Total");
        series.setSpacing(50);

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

    }

    public void initCompleteLineChart(View layout){
        SQLfunctionHelper.getWeekOnTimeTasks(getContext(), this);
        LineGraphSeries<DataPoint> s1 = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 5),
                new DataPoint(1, 6),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 7)

        });
        LineGraphSeries<DataPoint> s2 = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, -1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        GraphView completeWeek = layout.findViewById(R.id.task_complete_week_graph);
        completeWeek.addSeries(s1);
        completeWeek.addSeries(s2);
        completeWeek.setTitle("Completion Stats Over Past Week");
    }

    public void initOnTimeLineChart(View layout){
        SQLfunctionHelper.getWeekOnTimeTasks(getContext(), this);
        LineGraphSeries<DataPoint> s1 = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 5),
                new DataPoint(1, 6),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 7)

        });
        LineGraphSeries<DataPoint> s2 = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, -1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        GraphView onTimeWeek = layout.findViewById(R.id.task_onTime_week_graph);
        onTimeWeek.addSeries(s1);
        onTimeWeek.addSeries(s2);
        onTimeWeek.setTitle("On-Time Stats Over Past Week");
    }

    public void initInompletePieChart(View layout){
        PieChart mPieChart = (PieChart) layout.findViewById(R.id.notCompleted_piechart);
        mPieChart.addPieSlice(new PieModel("Freetime", 15, Color.parseColor("#FE6DA8")));
        mPieChart.addPieSlice(new PieModel("Sleep", 25, Color.parseColor("#56B7F1")));
        mPieChart.addPieSlice(new PieModel("Work", 35, Color.parseColor("#CDA67F")));
        mPieChart.addPieSlice(new PieModel("Eating", 9, Color.parseColor("#FED70E")));

    }

    public void initLatePieChart(View layout){
        PieChart mPieCharttwo = (PieChart) layout.findViewById(R.id.late_piechart);
        mPieCharttwo.addPieSlice(new PieModel("Freetime", 15, Color.parseColor("#FE6DA8")));
        mPieCharttwo.addPieSlice(new PieModel("Sleep", 25, Color.parseColor("#56B7F1")));
        mPieCharttwo.addPieSlice(new PieModel("Work", 35, Color.parseColor("#CDA67F")));
        mPieCharttwo.addPieSlice(new PieModel("Eating", 9, Color.parseColor("#FED70E")));
    }

    public class PieSlice {
        String name;
        int percentage;
        int color;

        public PieSlice(String s, int perc, int col){
            name = s;
            percentage = perc;
            color = col;
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
