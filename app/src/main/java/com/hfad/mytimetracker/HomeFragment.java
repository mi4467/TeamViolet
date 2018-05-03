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

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
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

import lecho.lib.hellocharts.model.Axis;


public class HomeFragment extends Fragment {
    private static String currentStr = "Current Streak: ";
    private static String todayScr = "\nToday's Score:  ";
    private static String longestStr = "Longest Streak: ";
    private static String totlScr = "\nTotal Score:       " ;
    private static Map<String, Integer> monthMapper;

    public HomeFragment(){

    }

    @SuppressLint("RestrictedAPI")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.fragment_home, container, false);
        initBestCompleteBar(layout);        //these functions set up the respective cardviews
        initWorstCompleteBar(layout);
        initTaskListView(layout);
        initCompleteLineChart(layout);
        initOnTimeLineChart(layout);
        initUserStats(layout);
        initMonthMapper();
        return layout;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && getView()!=null) {
            initTaskListView(getView());
            initCompleteLineChart(getView());
            initOnTimeLineChart(getView());
            initBestCompleteBar(getView());
            initWorstCompleteBar(getView());
            initUserStats(getView());
        }

    }

    private void initMonthMapper(){
        monthMapper = new HashMap<>();
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
    }
    public void initUserStats(View layout){
        Cursor userStats = TimeTrackerDataBaseHelper.getInstance(getContext()).getReadableDatabase().rawQuery("SELECT * FROM USER_STATS", null);
        userStats.moveToFirst();
        TextView currentString = layout.findViewById(R.id.current_streak);
        TextView tdayScr = layout.findViewById(R.id.today_score);
        TextView longStr = layout.findViewById(R.id.longest_streak);
        TextView totalScr = layout.findViewById(R.id.total_score);
        currentString.setText(currentStr + userStats.getInt(0));
        longStr.setText(longestStr + userStats.getInt(1));
        tdayScr.setText(todayScr + userStats.getInt(2));
        totalScr.setText(totlScr + userStats.getInt(3));
    }


    public void initTaskListView(View layout){
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTime(Calendar.getInstance().getTime());
        String[] daterep = gcal.getTime().toString().split(" ");
        initMonthMapper();
        Cursor result = SQLfunctionHelper.getTasksGivenDate(getContext(), TaskCreatorFragment.constructDateStr(Integer.parseInt(daterep[5]), monthMapper.get(daterep[1]), Integer.parseInt(daterep[2])));
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
        setUpTaskViews(layout, completed, incomplete);
    }

    private void setUpTaskViews(View layout, String completed, String incomplete ){
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
    }


    private StringBuilder getHelpfulTipString(ArrayList<StatsFragment.CategoryStats> data){
        int i = 0;
        StringBuilder s = new StringBuilder();
        for(i = 0; i<5 && i<data.size(); i++){
            if(data.get(i).totalTasksWithOnTimeStatus!=0) {
                s.append("\n" + data.get(i).name + "\n" + "\t\t\t\t\u2022" + "\t\tCompleted:\t" + data.get(i).complete * 100 / data.get(i).totalTasksWithCompleteStatus + "%\n" + "\t\t\t\t\u2022" + "\t\tOn-Time: \t\t\t" + data.get(i).onTime * 100 / data.get(i).totalTasksWithOnTimeStatus + "%\n");
            }
            else{
                s.append("\n" + data.get(i).name + "\n" + "\t\t\t\t\u2022" + "\t\tCompleted:\t" + data.get(i).complete * 100 / data.get(i).totalTasksWithCompleteStatus + "%\n" + "\t\t\t\t\u2022" + "\t\tOn-Time: \t\t\t" + "N/A" + "\n");
            }
        }
        if(i==5){
            s.append("....\n");
        }
        if(i==0){
            s.append("\nNo Qualifying Data\n");
        }
        return s;
    }

    public void initBestCompleteBar(View layout){
        ArrayList<StatsFragment.CategoryStats> data = SQLfunctionHelper.filterBarGraph(SQLfunctionHelper.getCategoryList(getContext()), getContext(), new StatsFragment());
        for(int i =0; i<data.size(); i++){
            if(data.get(i).totalTasksWithCompleteStatus<5 && data.get(i).totalTasksWithOnTimeStatus<5){
                data.remove(data.get(i));
                i--;
            }
        }
        data.sort(new BestCompletionComparator());
        if(data.size()==0){
            return;
        }
        StringBuilder s = getHelpfulTipString(data);
        TextView text = layout.findViewById(R.id.best_categories);
        text.setText(s.substring(1));
    }

    public void initWorstCompleteBar(View layout){
        ArrayList<StatsFragment.CategoryStats> data = SQLfunctionHelper.filterBarGraph(SQLfunctionHelper.getCategoryList(getContext()), getContext(), new StatsFragment());
        for(int i =0; i<data.size(); i++){
            if(data.get(i).totalTasksWithCompleteStatus<5 && data.get(i).totalTasksWithOnTimeStatus<5){
                data.remove(data.get(i));
                i--;
            }
        }
        data.sort(new BestCompletionComparator());
        Collections.reverse(data);
        if(data.size()==0){
            return;
        }
        StringBuilder s = getHelpfulTipString(data);
        TextView text = layout.findViewById(R.id.worst_categories);
        text.setText(s.substring(1));
    }

    private void setChartProperties(LineChart chart, IAxisValueFormatter formatter){
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);       //interval
        xAxis.setValueFormatter(formatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        chart.getDescription().setEnabled(false);
        chart.getAxisLeft().setGranularity(1f);
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setTextColor(Color.WHITE);
        chart.getLegend().setForm(Legend.LegendForm.CIRCLE);

    }

    public void lineColorsInit(LineDataSet yellow, LineDataSet green, LineDataSet red){
        yellow.setAxisDependency(YAxis.AxisDependency.LEFT);
        yellow.setColor(Color.parseColor("#AFB42B"));    //Dark Yellow
        yellow.setCircleColor(Color.parseColor("#827717"));
        yellow.setCircleColorHole(Color.parseColor("#EEFF41"));
        yellow.setValueTextColor(Color.WHITE);
        green.setAxisDependency(YAxis.AxisDependency.LEFT);
        green.setColor(Color.parseColor("#689F38"));    //Dark Green
        green.setCircleColor(Color.parseColor("#33691E"));
        green.setCircleColorHole(Color.parseColor("#64DD17"));
        green.setValueTextColor(Color.WHITE);
        red.setAxisDependency(YAxis.AxisDependency.LEFT);
        red.setColor(Color.parseColor("#D32F2F"));    //Dark Red
        red.setCircleColor(Color.parseColor("#B71C1C"));
        red.setCircleColorHole(Color.parseColor("#FF5252"));
        red.setValueTextColor(Color.WHITE);
    }

    public String getDate(){
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTime(Calendar.getInstance().getTime());
        String[] daterep = gcal.getTime().toString().split(" ");
        initMonthMapper();
        return TaskCreatorFragment.constructDateStr(Integer.parseInt(daterep[5]), monthMapper.get(daterep[1]), Integer.parseInt(daterep[2]));
    }


    public void initCompleteLineChart(View layout){
        String date = getDate();
        final ArrayList<StatsFragment.DayStats> data = SQLfunctionHelper.getWeekOnTimeTasksFilter(getContext(), new StatsFragment(), date);
        Collections.reverse(data);
        ArrayList<Entry> totalWithCompleteStatus = new ArrayList<>();
        for(int i =0; i< data.size(); i++){
            totalWithCompleteStatus.add(new Entry((float) i, (float) data.get(i).totalTasksWithCompleteStatus));
        }
        ArrayList<Entry> complete = new ArrayList<>();
        for(int i =0; i< data.size(); i++){
            complete.add(new Entry((float) i, (float) data.get(i).complete));
        }
        ArrayList<Entry> incomplete = new ArrayList<>();
        for(int i =0; i< data.size(); i++){
            incomplete.add(new Entry((float) i, (float) data.get(i).incomplete));
        }
        LineDataSet total = new LineDataSet(totalWithCompleteStatus, "Total Tasks");
        LineDataSet completeTasks = new LineDataSet(complete, "Completed Tasks");
        LineDataSet incompleteTasks = new LineDataSet(incomplete, "Incomplete Tasks");
        lineColorsInit(total, completeTasks, incompleteTasks);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(total);
        dataSets.add(completeTasks);
        dataSets.add(incompleteTasks);
        LineData chartData = new LineData(dataSets);
        LineChart chart = layout.findViewById(R.id.complete_line_chart);
        chart.setData(chartData);
        chart.invalidate();
        final String[] days = new String[7];
        for(int i =0; i<days.length; i++){
            days[i] = data.get(i).date;
        }
        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                String[] dateRep = days[(int) value].split("-");
                return dateRep[1] + "/" + dateRep[2];
            }

            // we don't draw numbers, so no decimal digits needed
        };
        setChartProperties(chart, formatter);
    }

    public void initOnTimeLineChart(View layout){
        String date = getDate();
        final ArrayList<StatsFragment.DayStats> data = SQLfunctionHelper.getWeekOnTimeTasksFilter(getContext(), new StatsFragment(), date);
        Collections.reverse(data);
        ArrayList<Entry> totalWithCompleteStatus = new ArrayList<>();
        for(int i =0; i< data.size(); i++){
            totalWithCompleteStatus.add(new Entry((float) i, (float) data.get(i).totalTasksWithOnTimeStatus));
        }
        ArrayList<Entry> complete = new ArrayList<>();
        for(int i =0; i< data.size(); i++){
            complete.add(new Entry((float) i, (float) data.get(i).onTime));
        }
        ArrayList<Entry> incomplete = new ArrayList<>();
        for(int i =0; i< data.size(); i++){
            incomplete.add(new Entry((float) i, (float) data.get(i).late));
        }
        LineDataSet total = new LineDataSet(totalWithCompleteStatus, "Total Qualifying Tasks");
        LineDataSet onTime = new LineDataSet(complete, "On-Time Tasks");
        LineDataSet late = new LineDataSet(incomplete, "Late Tasks");
        lineColorsInit(total, onTime, late);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(total);
        dataSets.add(onTime);
        dataSets.add(late);
        LineData chartData = new LineData(dataSets);
        LineChart chart = layout.findViewById(R.id.onTime_line_chart);
        chart.setData(chartData);
        chart.invalidate();
        final String[] days = new String[7];
        for(int i =0; i<days.length; i++){
            days[i] = data.get(i).date;
        }
        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                String[] dateRep = days[(int) value].split("-");
                return dateRep[1] + "/" + dateRep[2];
            }

            // we don't draw numbers, so no decimal digits needed
        };
        setChartProperties(chart, formatter);
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
}
