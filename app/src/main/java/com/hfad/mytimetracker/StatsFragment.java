package com.hfad.mytimetracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.github.mikephil.charting.charts.Chart;
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

import es.dmoral.toasty.Toasty;
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
        final View layout = inflater.inflate(R.layout.fragment_stats, container, false);
        initListeners(layout);
        initField();
        setUpCompletionWeekLineGraph(layout);
        setUpOnTimeWeekLineGraph(layout);
        setUpCompletePieGraph(layout);
        setUpLatePieGraph(layout);
        setUpCategoryOnTime(layout);
        setUpCategoryStatsCompletion(layout);
        setUpToast();
        return layout;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && getView()!=null) {
            View layout = getView();
            initListeners(layout);
            initField();
            setUpCompletionWeekLineGraph(layout);
            setUpOnTimeWeekLineGraph(layout);
            setUpLatePieGraph(layout);
            setUpCompletePieGraph(layout);
        }

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

    public boolean filterCategoryStats(ArrayList<CategoryStats> data, boolean completion) {
        if(data.size() == 0){
            return false;
        }
        for(int i =0; i< data.size(); i++){
            Log.d("PieDebug", "I is: " + i);
            if(data.size() == 0){
                return false;
            }
            if(data.get(i).incomplete==0 && completion){        //add null checks
                data.remove(i);
                i--;
                Log.d("PieDebug", "I is: " + i);
                continue;
            }
            if(data.size() == 0){
                return false;
            }
            if(data.get(i).late==0 && !completion){
                data.remove(i);
                i--;
                continue;
            }
        }
        if(data.size()==0){
            return false;
        }
        return true;
    }

    public void setChartLegend(Chart chart){
        chart.getLegend().setForm(Legend.LegendForm.CIRCLE);
        chart.getLegend().setTextColor(Color.WHITE);
        chart.getLegend().setOrientation(Legend.LegendOrientation.HORIZONTAL);
        chart.getLegend().setWordWrapEnabled(true);
    }

    public void adjustXAxis(Chart chart, IAxisValueFormatter formatter){
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);       //interval
        xAxis.setValueFormatter(formatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
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

    public void setUpCompletePieGraph(View layout){
        ArrayList<CategoryStats> data = SQLfunctionHelper.filterBarGraph(SQLfunctionHelper.getCategoryList(getContext()), getContext(), this);
        if(!filterCategoryStats(data, true)){
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
        PieDataSet set = new PieDataSet(slices, "");
        set.setColors(colors);
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(12f);
        PieData results = new PieData(set);
        final PieChart chart = layout.findViewById(R.id.incomplete_pie_chart);
        chart.setData(results);
        chart.invalidate();
        setChartLegend(chart);
        chart.getDescription().setEnabled(false);
        chart.setDrawEntryLabels(false);
        chart.setCenterTextColor(Color.WHITE);
        chart.setHoleColor(Color.parseColor("#607D8B"));
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
        //Log.d("PieGraph", data.toString());
        if(!filterCategoryStats(data, false)){
            Toasty.error(getContext(), "Exited at First If", Toast.LENGTH_LONG, true).show();
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
            Toasty.error(getContext(), "Exited at Second If", Toast.LENGTH_LONG, true).show();
            return;
        }
        for(int i =0 ; i<data.size(); i++){
            slices.add(new PieEntry(((float) data.get(i).late)*100/divisor, data.get(i).name));
            colors.add(data.get(i).color);
        }
        PieDataSet set = new PieDataSet(slices, "");
        set.setColors(colors);
        set.setColors(colors);
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(12f);
        PieData results = new PieData(set);
        final PieChart chart = layout.findViewById(R.id.late_pie_chart);
        chart.setData(results);
        chart.invalidate();
        setChartLegend(chart);
        chart.getDescription().setEnabled(false);
        chart.setDrawEntryLabels(false);
        chart.setHoleColor(Color.parseColor("#607D8B"));
        chart.setCenterTextColor(Color.WHITE);
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
        BarDataSet completedTasks = new BarDataSet(complete, "On-time Tasks");
        completedTasks.setColor(Color.parseColor("#689F38"));
        completedTasks.setValueTextColor(Color.WHITE);
        BarDataSet incompleteTasks = new BarDataSet(incomplete, "Late Tasks");
        incompleteTasks.setColor(Color.parseColor("#D32F2F"));
        incompleteTasks.setValueTextColor(Color.WHITE);
        float groupSpace = .06f;
        float barSpace = .02f;
        float barWidth = .45f;
        final String[] cat = new String[data.size()];
        for(int i =0; i<cat.length; i++){
            cat[i] = onTimeBarData.get(i).name;
        }
        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
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
        adjustXAxis(chart, formatter);
        XAxis xAxis = chart.getXAxis();
        xAxis.setCenterAxisLabels(true);
        xAxis.setAxisMaximum((float) onTimeBarData.size());
        setChartLegend(chart);
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

    public void setUpCategoryStatsCompletion(View layout){
        String[] categoryList = SQLfunctionHelper.getCategoryList(getContext());
        if(categoryList.length<5){
            categoriesComplete = categoryList;
        }
        else{
            categoriesComplete = new String[5];
            for(int i =0; i<5; i++){
                categoriesComplete[i] = categoryList[i];
            }
        }
        setUpCompletionBarGraph(layout);
    }

    public void setUpCategoryOnTime(View layout){
        String[] categoryList = SQLfunctionHelper.getCategoryList(getContext());
        if(categoryList.length<5){
            categoriesOnTime = categoryList;
        }
        else{
            categoriesOnTime = new String[5];
            for(int i =0; i<5; i++){
                categoriesOnTime[i] = categoryList[i];
            }
        }
        setUpOnTimeBarGraph(layout);
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
        BarDataSet completedTasks = new BarDataSet(complete, "Completed Tasks");
        completedTasks.setColor(Color.parseColor("#689F38"));
        completedTasks.setValueTextColor(Color.WHITE);
        BarDataSet incompleteTasks = new BarDataSet(incomplete, "Incomplete Tasks");
        incompleteTasks.setColor(Color.parseColor("#D32F2F"));
        incompleteTasks.setValueTextColor(Color.WHITE);
        float groupSpace = .06f;
        float barSpace = .02f;
        float barWidth = .45f;
        final String[] cat = new String[data.size()];
        for(int i =0; i<cat.length; i++){
            cat[i] = completedBarData.get(i).name;
        }
        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
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
        adjustXAxis(chart, formatter);
        XAxis xAxis = chart.getXAxis();
        xAxis.setCenterAxisLabels(true);
        xAxis.setAxisMaximum((float) completedBarData.size());
        setChartLegend(chart);
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
        final LineDataSet completeTasks = new LineDataSet(complete, "Completed Tasks");
        LineDataSet incompleteTasks = new LineDataSet(incomplete, "Incomplete Tasks");
        lineColorsInit(total, completeTasks, incompleteTasks);
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
                String[] dateRep = days[(int) value].split("-");
                return dateRep[1] + "/" + dateRep[2];
            }

            // we don't draw numbers, so no decimal digits needed
        };
        adjustXAxis(chart, formatter);
        chart.setScaleEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getAxisLeft().setGranularity(1f);
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getAxisRight().setEnabled(false);
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int index = (int) e.getX();
                Cursor positive = SQLfunctionHelper.getTaskListLineGraph(completedLineData.get(index).date, true, getContext());
                positive.moveToFirst();
                Cursor negative = SQLfunctionHelper.getTaskListLineGraph(completedLineData.get(index).date, false, getContext());
                negative.moveToFirst();
                Log.d("HighlightDebug", DatabaseUtils.dumpCursorToString(positive));
                Log.d("HighlightDebug", DatabaseUtils.dumpCursorToString(negative));
                //showCompleteLineAlert(getView());
            }

            @Override
            public void onNothingSelected() {

            }
        });
        setChartLegend(chart);
        chart.invalidate();

    }

    public void showCompleteLineAlert(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Selected Date");

        final View layout = getLayoutInflater().inflate(R.layout.linegraph_alertlist_dialog, null);
        builder.setView(layout);

        AlertDialog dialog = builder.create();
        dialog.show();

    }
    public void setUpOnTimeWeekLineGraph(View layout){
        if(StatsFragment.onTimeYear==null){
            Toast.makeText(getActivity(), "You Did Not Select A Date. Try Again", Toast.LENGTH_SHORT).show();
            return;
        }
        String date = TaskCreatorFragment.constructDateStr(onTimeYear, onTimeMonth, onTimeDay);
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
        LineDataSet onTimeTasks = new LineDataSet(onTime, "On-time Tasks");
        LineDataSet lateTasks = new LineDataSet(late, "Late Tasks");
        lineColorsInit(total, onTimeTasks, lateTasks);
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
        adjustXAxis(chart, formatter);
        chart.getDescription().setEnabled(false);
        chart.getAxisLeft().setGranularity(1f);
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getAxisRight().setEnabled(false);
        chart.setScaleEnabled(false);
        setChartLegend(chart);
        chart.invalidate();

    }

    public void setUpToast(){
        Toasty.Config.getInstance()
                .setErrorColor(Color.parseColor("#B71C1C"))
                .setSuccessColor(Color.parseColor("#1B5E20"))
                .setTextColor(Color.WHITE)
                .apply();
    }

    public void showCompleteTaskCatSelection(){
        new MaterialDialog.Builder(getContext())
                .title("Specify Up To Five Categories")
                .items(SQLfunctionHelper.getCategoryList(getContext()))
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        if (text.length > 5) {
                            Toasty.error(getContext(), "Choose 5 or Less Categories!", Toast.LENGTH_LONG, true).show();
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
                            Toasty.error(getContext(), "Choose 5 or Less Categories!", Toast.LENGTH_LONG, true).show();
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

    }


}
