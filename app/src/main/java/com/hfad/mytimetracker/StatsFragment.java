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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
        initListeners(layout);
        initField();
        initLayout(layout);
        setUpCompletionWeekLineGraph(layout);
        setUpOnTimeWeekLineGraph(layout);
        setUpCompletePieGraph(layout);
        setUpLatePieGraph(layout);
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && getView()!=null) {
            // Do your stuff here
            View layout = getView();
            initListeners(layout);
            initField();
            initLayout(layout);
            setUpCompletionWeekLineGraph(layout);
            setUpOnTimeWeekLineGraph(layout);
        }

    }

    public void initLayout(View layout){
//        TextView completeBar = layout.findViewById(R.id.completed_key_pick_three);
//        completeBar.setBackgroundColor(Color.GREEN);
//        TextView incompleteBar = layout.findViewById(R.id.not_completed_key_pick_three);
//        incompleteBar.setBackgroundColor(Color.RED);
//
//        TextView onTimeBar = layout.findViewById(R.id.onTime_key_pick_three);
//        onTimeBar.setBackgroundColor(Color.GREEN);
//        TextView lateBar = layout.findViewById(R.id.late_key_pick_three);
//        lateBar.setBackgroundColor(Color.RED);
//        categoriesComplete = SQLfunctionHelper.getCategoryList(getContext());
//        setUpCompletionBarGraph(layout);
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
                Log.d("Penis", "It should be cleared");
//                GraphView completeWeek = layout.findViewById(R.id.task_complete_week_graph_filter);
//                completeWeek.removeAllSeries();
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

    public void setUpCompletePieGraph(View layout){
        ArrayList<CategoryStats> data = SQLfunctionHelper.filterBarGraph(SQLfunctionHelper.getCategoryList(getContext()), getContext(), this);
        if(data.size()==0){
            return;
        }
        ArrayList<Integer> colors = new ArrayList<>();
        ArrayList<PieEntry> slices = new ArrayList<>();
        Integer divisor =0;
        for(CategoryStats curr: data){
            divisor += curr.incomplete;
        }
        if(divisor==0){
            return;
        }
        final Integer hole = divisor;
        for(int i =0 ; i<data.size(); i++){
            slices.add(new PieEntry(((float) data.get(i).incomplete)*100/divisor, data.get(i).name));
            colors.add(data.get(i).color);
        }
        PieDataSet set = new PieDataSet(slices, "Incompletion Per Category");
        set.setColors(colors);
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(12f);
        PieData results = new PieData(set);
        final PieChart chart = layout.findViewById(R.id.incomplete_pie_chart);
        chart.setData(results);
        chart.invalidate();
        chart.setDrawSliceText(false);
        chart.getLegend().setForm(Legend.LegendForm.CIRCLE);
        chart.getLegend().setTextColor(Color.WHITE);
        chart.getLegend().setOrientation(Legend.LegendOrientation.VERTICAL);
        chart.getDescription().setEnabled(false);
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                PieEntry slice = (PieEntry) e;
                chart.setCenterText(slice.getLabel() + ": " + (int) e.getY()*hole/100 + " tasks");

            }

            @Override
            public void onNothingSelected() {
                chart.setCenterText("Total Incomplete Tasks: " + hole);
            }
        });
    }

    public void setUpLatePieGraph(View layout){
        ArrayList<CategoryStats> data = SQLfunctionHelper.filterBarGraph(SQLfunctionHelper.getCategoryList(getContext()), getContext(), this);
        if(data.size()==0){
            return;
        }
        ArrayList<Integer> colors = new ArrayList<>();
        ArrayList<PieEntry> slices = new ArrayList<>();
        Integer divisor =0;
        for(CategoryStats curr: data){
            divisor += curr.late;
        }
        final Integer hole = divisor;
        if(divisor==0){
            return;
        }
        for(int i =0 ; i<data.size(); i++){
            slices.add(new PieEntry(((float) data.get(i).late)*100/divisor, data.get(i).name));
            colors.add(data.get(i).color);
        }
        PieDataSet set = new PieDataSet(slices, "Late Per Category");
        set.setColors(colors);
        set.setColors(colors);
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(12f);
        PieData results = new PieData(set);
        final PieChart chart = layout.findViewById(R.id.late_pie_chart);
        chart.setData(results);
        chart.invalidate();
        chart.setDrawSliceText(false);
        chart.getLegend().setForm(Legend.LegendForm.CIRCLE);
        chart.getLegend().setTextColor(Color.WHITE);
        chart.getLegend().setOrientation(Legend.LegendOrientation.VERTICAL);
        chart.getDescription().setEnabled(false);
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                PieEntry slice = (PieEntry) e;
                chart.setCenterText(slice.getLabel() + ": " + e.getY()*hole/100 + " tasks");

            }

            @Override
            public void onNothingSelected() {
                chart.setCenterText("Total Late Tasks: " + hole);
            }
        });
    }

    public void setUpOnTimeBarGraph(View layout){
        ArrayList<CategoryStats> data = SQLfunctionHelper.filterBarGraph(categoriesOnTime, getContext(), this);
        if(data.size()==0){
            return;
        }
        onTimeBarData = data;
        ArrayList<BarEntry> complete = new ArrayList<>();
        for(int i =0; i<onTimeBarData.size(); i++){
            complete.add(new BarEntry((float) i, (float) onTimeBarData.get(i).onTime));
        }
        ArrayList<BarEntry> incomplete = new ArrayList<>();
        for(int i =0; i<onTimeBarData.size(); i++){
            incomplete.add(new BarEntry((float) i, (float) onTimeBarData.get(i).late));
        }
        //above is collecting the data

        BarDataSet completedTasks = new BarDataSet(complete, "On-time Tasks");
        completedTasks.setColor(Color.parseColor("#689F38"));
        completedTasks.setValueTextColor(Color.WHITE);
        BarDataSet incompleteTasks = new BarDataSet(incomplete, "Late Tasks");
        incompleteTasks.setColor(Color.parseColor("#D32F2F"));
        incompleteTasks.setValueTextColor(Color.WHITE);
        float groupSpace = .06f;
        float barSpace = .02f;
        float barWidth = .45f;

//        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
//        dataSets.add(completedTasks);
//        dataSets.add(incompleteTasks);

        final String[] cat = new String[data.size()];
        for(int i =0; i<cat.length; i++){
            cat[i] = onTimeBarData.get(i).name;
        }
        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                // String[] date = data.get(data.size()-1-((int) value)).date.split("-");
                //
                //                        return date[1] + "/" + date[2];
                //String[] dateRep = days[(int) value].split("-");
                if((int) value>=cat.length || value <0){
                    return "";
                }
                return cat[Math.round(value)];
            }

            // we don't draw numbers, so no decimal digits needed
        };

        BarData barData = new BarData(completedTasks, incompleteTasks);
        barData.setBarWidth(barWidth);
        BarChart chart = layout.findViewById(R.id.onTime_cat_barchart);

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);       //interval
        xAxis.setValueFormatter(formatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setCenterAxisLabels(true);
        xAxis.setAxisMaximum((float) onTimeBarData.size());
        chart.getLegend().setTextColor(Color.WHITE);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.setNoDataText("No Categories Were Selected. Choose Up To Three Categories!");
        chart.setData(barData);
        chart.groupBars(0f, groupSpace, barSpace);
        chart.setFitBars(true);
        chart.getDescription().setEnabled(false);
        chart.setScaleEnabled(false);
        chart.invalidate();
    }

    public void setUpCompletionBarGraph(View layout){
        ArrayList<CategoryStats> data = SQLfunctionHelper.filterBarGraph(categoriesComplete, getContext(), this);
        if(data.size()==0){
            return;
        }
        completedBarData = data;
        ArrayList<BarEntry> complete = new ArrayList<>();
        for(int i =0; i<completedBarData.size(); i++){
            complete.add(new BarEntry((float) i, (float) completedBarData.get(i).complete));
        }
        ArrayList<BarEntry> incomplete = new ArrayList<>();
        for(int i =0; i<completedBarData.size(); i++){
            incomplete.add(new BarEntry((float) i, (float) completedBarData.get(i).incomplete));
        }
        //above is collecting the data

        BarDataSet completedTasks = new BarDataSet(complete, "Completed Tasks");
        completedTasks.setColor(Color.parseColor("#689F38"));
        completedTasks.setValueTextColor(Color.WHITE);
        BarDataSet incompleteTasks = new BarDataSet(incomplete, "Incomplete Tasks");
        incompleteTasks.setColor(Color.parseColor("#D32F2F"));
        incompleteTasks.setValueTextColor(Color.WHITE);
        float groupSpace = .06f;
        float barSpace = .02f;
        float barWidth = .45f;

//        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
//        dataSets.add(completedTasks);
//        dataSets.add(incompleteTasks);

        final String[] cat = new String[data.size()];
        for(int i =0; i<cat.length; i++){
            cat[i] = completedBarData.get(i).name;
        }
        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                // String[] date = data.get(data.size()-1-((int) value)).date.split("-");
                //
                //                        return date[1] + "/" + date[2];
                //String[] dateRep = days[(int) value].split("-");
                if((int) value>=cat.length || value <0){
                    return "";
                }
                return cat[Math.round(value)];
            }

            // we don't draw numbers, so no decimal digits needed
        };

        BarData barData = new BarData(completedTasks, incompleteTasks);
        barData.setBarWidth(barWidth);
        BarChart chart = layout.findViewById(R.id.complete_cat_barchart);

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);       //interval
        xAxis.setValueFormatter(formatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setCenterAxisLabels(true);
        xAxis.setAxisMaximum((float) completedBarData.size());
        chart.getLegend().setTextColor(Color.WHITE);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.setNoDataText("No Categories Were Selected. Choose Up To Three Categories!");
        chart.setData(barData);
        chart.groupBars(0f, groupSpace, barSpace);
        chart.setFitBars(true);
        chart.getDescription().setEnabled(false);
        chart.setScaleEnabled(false);
        chart.invalidate();
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
        Collections.reverse(completedLineData);

        ArrayList<Entry> totalWithCompleteStatus = new ArrayList<>();
        for(int i =0; i< completedLineData.size(); i++){
            totalWithCompleteStatus.add(new Entry((float) i, (float) completedLineData.get(i).totalTasksWithCompleteStatus));
        }
        ArrayList<Entry> complete = new ArrayList<>();
        for(int i =0; i< completedLineData.size(); i++){
            complete.add(new Entry((float) i, (float) completedLineData.get(i).complete));
        }
        ArrayList<Entry> incomplete = new ArrayList<>();
        for(int i =0; i< completedLineData.size(); i++){
            incomplete.add(new Entry((float) i, (float) completedLineData.get(i).incomplete));
        }
        LineDataSet total = new LineDataSet(totalWithCompleteStatus, "Total Tasks");
        total.setAxisDependency(YAxis.AxisDependency.LEFT);
        total.setColor(Color.parseColor("#AFB42B"));    //Dark Yellow
        total.setCircleColor(Color.parseColor("#827717"));
        total.setCircleColorHole(Color.parseColor("#EEFF41"));
        total.setValueTextColor(Color.WHITE);
        LineDataSet completeTasks = new LineDataSet(complete, "Completed Tasks");
        completeTasks.setAxisDependency(YAxis.AxisDependency.LEFT);
        completeTasks.setColor(Color.parseColor("#689F38"));    //Dark Green
        completeTasks.setCircleColor(Color.parseColor("#33691E"));
        completeTasks.setCircleColorHole(Color.parseColor("#64DD17"));
        completeTasks.setValueTextColor(Color.WHITE);
        LineDataSet incompleteTasks = new LineDataSet(incomplete, "Incomplete Tasks");
        incompleteTasks.setAxisDependency(YAxis.AxisDependency.LEFT);
        incompleteTasks.setColor(Color.parseColor("#D32F2F"));    //Dark Red
        incompleteTasks.setCircleColor(Color.parseColor("#B71C1C"));
        incompleteTasks.setCircleColorHole(Color.parseColor("#FF5252"));
        incompleteTasks.setValueTextColor(Color.WHITE);

        //above is creating entry lists and their data sets
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(total);
        dataSets.add(completeTasks);
        dataSets.add(incompleteTasks);

        LineData chartData = new LineData(dataSets);

        LineChart chart = layout.findViewById(R.id.complete_line_chart_filter);
        chart.setData(chartData);
        chart.invalidate();

        final String[] days = new String[7];
        for(int i =0; i<days.length; i++){
            days[i] = completedLineData.get(i).date;
        }
        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                // String[] date = data.get(data.size()-1-((int) value)).date.split("-");
                //
                //                        return date[1] + "/" + date[2];
                String[] dateRep = days[(int) value].split("-");
                return dateRep[1] + "/" + dateRep[2];
            }

            // we don't draw numbers, so no decimal digits needed
        };

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);       //interval
        xAxis.setValueFormatter(formatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);

        chart.setScaleEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getAxisLeft().setGranularity(1f);
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setTextColor(Color.WHITE);
        chart.getLegend().setForm(Legend.LegendForm.CIRCLE);

    }

    public void setUpOnTimeWeekLineGraph(View layout){
        if(StatsFragment.onTimeYear==null){
            Toast.makeText(getActivity(), "You Did Not Select A Date. Try Again", Toast.LENGTH_SHORT).show();
            return;
        }
        String date = TaskCreatorFragment.constructDateStr(onTimeYear, onTimeMonth, onTimeDay);
        Log.d("StatsDebug", "The date passed into the data is: " +date);
        ArrayList<DayStats> data = SQLfunctionHelper.getWeekOnTimeTasksFilter(getContext(), this, date);
        onTimeLineData = data;
        Collections.reverse(onTimeLineData);

        ArrayList<Entry> totalWithLateStatus = new ArrayList<>();
        for(int i =0; i< onTimeLineData.size(); i++){
            totalWithLateStatus.add(new Entry((float) i, (float) onTimeLineData.get(i).totalTasksWithOnTimeStatus));
        }
        ArrayList<Entry> onTime = new ArrayList<>();
        for(int i =0; i< onTimeLineData.size(); i++){
            onTime.add(new Entry((float) i, (float) onTimeLineData.get(i).onTime));
        }
        ArrayList<Entry> late = new ArrayList<>();
        for(int i =0; i< onTimeLineData.size(); i++){
            late.add(new Entry((float) i, (float) onTimeLineData.get(i).late));
        }
        LineDataSet total = new LineDataSet(totalWithLateStatus, "Total Tasks");
        total.setAxisDependency(YAxis.AxisDependency.LEFT);
        total.setColor(Color.parseColor("#AFB42B"));    //Dark Yellow
        total.setCircleColor(Color.parseColor("#827717"));
        total.setCircleColorHole(Color.parseColor("#EEFF41"));
        total.setValueTextColor(Color.WHITE);
        LineDataSet onTimeTasks = new LineDataSet(onTime, "On-time Tasks");
        onTimeTasks.setAxisDependency(YAxis.AxisDependency.LEFT);
        onTimeTasks.setColor(Color.parseColor("#689F38"));    //Dark Green
        onTimeTasks.setCircleColor(Color.parseColor("#33691E"));
        onTimeTasks.setCircleColorHole(Color.parseColor("#64DD17"));
        onTimeTasks.setValueTextColor(Color.WHITE);
        LineDataSet lateTasks = new LineDataSet(late, "Late Tasks");
        lateTasks.setAxisDependency(YAxis.AxisDependency.LEFT);
        lateTasks.setColor(Color.parseColor("#D32F2F"));    //Dark Red
        lateTasks.setCircleColor(Color.parseColor("#B71C1C"));
        lateTasks.setCircleColorHole(Color.parseColor("#FF5252"));
        lateTasks.setValueTextColor(Color.WHITE);

        //above is creating entry lists and their data sets
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(total);
        dataSets.add(onTimeTasks);
        dataSets.add(lateTasks);

        LineData chartData = new LineData(dataSets);

        LineChart chart = layout.findViewById(R.id.onTime_line_chart_filter);
        chart.setData(chartData);
        chart.invalidate();

        final String[] days = new String[7];
        for(int i =0; i<days.length; i++){
            days[i] = onTimeLineData.get(i).date;
        }
        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                String[] dateRep = days[(int) value].split("-");
                return dateRep[1] + "/" + dateRep[2];
            }

            // we don't draw numbers, so no decimal digits needed
        };
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);       //interval
        xAxis.setValueFormatter(formatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        chart.getDescription().setEnabled(false);
        chart.getAxisLeft().setGranularity(1f);
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getAxisRight().setEnabled(false);
        chart.setScaleEnabled(false);
        chart.getLegend().setTextColor(Color.WHITE);
        chart.getLegend().setForm(Legend.LegendForm.CIRCLE);

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
                    "Late # = " + this.late + "\n" +
                    "totalC = " + this.totalTasksWithCompleteStatus + "\n" +
                    "totalO = " + this.totalTasksWithOnTimeStatus;
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
                    "TotalWithComplete # = " + this.totalTasksWithCompleteStatus + "\n" +
                    "onTime # = " + this.onTime + "\n" +
                    "Late # = " + this.late + "\n" +
                    "TotalWithPunct # = " + this.totalTasksWithOnTimeStatus;
        }

    }


}
