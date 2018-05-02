package com.hfad.mytimetracker;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import net.cachapa.expandablelayout.ExpandableLayout;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;

import de.mateware.snacky.Snacky;
import es.dmoral.toasty.Toasty;


public class TasksListFragment extends Fragment {

    private Integer currentId;
    private SQLiteDatabase readableDatabase;
    private SQLiteDatabase writableDatabase;
    private static CharSequence[] taskCategoryNames=null;

    private boolean completed;
    private boolean notCompleted;
    private boolean onTime;
    private boolean notOnTime;
    private String sqlCommand;


    public TasksListFragment() {
        // Required empty public constructor
    }

    @SuppressLint("RestrictedAPI")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.fragment_tasks_list, container, false);
        RecyclerView recyclerView = layout.findViewById(R.id.recycler_view_task_list_viewer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        TimeTrackerDataBaseHelper dataBaseHelper = TimeTrackerDataBaseHelper.getInstance(getActivity());
        readableDatabase = dataBaseHelper.getReadableDatabase();
        writableDatabase = dataBaseHelper.getWritableDatabase();
        Cursor stats = readableDatabase.rawQuery("SELECT * FROM TASK_STATS", null);
        sqlCommand = "SELECT * FROM TASK_STATS";
        Log.d("CursorDebug", DatabaseUtils.dumpCursorToString(stats));
        Cursor data = readableDatabase.rawQuery("SELECT * FROM TASK_INFORMATION", null);
        Log.d("CursorDebug", DatabaseUtils.dumpCursorToString(data));
        int resId = R.anim.layout_animation_fall_down;
        recyclerView.setAdapter(new TasksListFragment.SimpleAdapter(recyclerView,stats));

        //button listener setup

        Button pickCat = layout.findViewById(R.id.tasks_list_category_filter_button);
        Button pickCompStatus = layout.findViewById(R.id.tasks_list_completed_status_filter_button);
        Button pickOnTimeStatus = layout.findViewById(R.id.tasks_list_on_time_status_filter_button);
        Button enter = layout.findViewById(R.id.tasks_list_filter_button);

        pickCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ButtonDebug", "We picked a category");
                showCategorySelectionDialog();
            }
        });

        pickCompStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ButtonDebug", "We picked a completeion");
                showCompletionSelectionDialog();
            }
        });

        pickOnTimeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ButtonDebug", "We picked a ontime");
                showOnTimeSelectionDialog();
            }
        });
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ButtonDebug", "We entered to submit a task");
                if(taskCategoryNames==null){
                    Log.d("ButtonDebug", "We are returning");
                    Toasty.error(getContext(), "Choose Categories!", Toast.LENGTH_LONG, true).show();
                    return;
                }
                Cursor next = constructCommand();
                resetFilters();
                RecyclerView recyclerView = layout.findViewById(R.id.recycler_view_task_list_viewer);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(new TasksListFragment.SimpleAdapter(recyclerView,next));
                resetFilters();
            }
        });
        resetFilters();
        setUpToast();
        return layout;
    }

    @Override
    public void onResume(){
        super.onResume();
        RecyclerView recyclerView = getView().findViewById(R.id.recycler_view_task_list_viewer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        TimeTrackerDataBaseHelper dataBaseHelper = TimeTrackerDataBaseHelper.getInstance(getActivity());
        readableDatabase = dataBaseHelper.getReadableDatabase();
        writableDatabase = dataBaseHelper.getWritableDatabase();
        Cursor stats = readableDatabase.rawQuery("SELECT * FROM TASK_STATS", null);
        sqlCommand = "SELECT * FROM TASK_STATS";
        Log.d("CursorDebug", DatabaseUtils.dumpCursorToString(stats));
        Cursor data = readableDatabase.rawQuery("SELECT * FROM TASK_INFORMATION", null);
        Log.d("CursorDebug", DatabaseUtils.dumpCursorToString(data));
        int resId = R.anim.layout_animation_fall_down;
        recyclerView.setAdapter(new TasksListFragment.SimpleAdapter(recyclerView,stats));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && getView()!=null) {
            // Do your stuff here
            this.onResume();
        }
    }

    private void resetFilters(){
        taskCategoryNames = null;
        completed = false;
        notOnTime = false;
        onTime = false;
        notOnTime = false;
    }

    private Cursor constructCommand(){
        Log.d("TaskFilterDebug", "Before - Complete: " + completed + " Incomplete: " + notCompleted + " On-Time: " + onTime + " Late: " + notOnTime);
        Log.d("TaskFilterDebug", "We entered constructCommand");
        if(completed==false && notCompleted==false){
            completed = true;
            notCompleted = true;
        }
        if(onTime == false && notOnTime == false){
            onTime = true;
            notOnTime = true;
        }
        Log.d("TaskFilterDebug", "After - Complete: " + completed + " Incomplete: " + notCompleted + " On-Time: " + onTime + " Late: " + notOnTime);
        StringBuilder cmd = new StringBuilder();
        cmd.append("SELECT * FROM TASK_STATS WHERE ( ");
        for(int i = 0; i<taskCategoryNames.length-1; i++){
            cmd.append(taskCategoryNames[i].toString() + " = 1 OR");
        }
        cmd.append(" " + taskCategoryNames[taskCategoryNames.length-1] + "=1 ) ");
        if(completed){
            cmd.append("AND ( COMPLETED = 1 ");
        }
        else{
            cmd.append("AND ( COMPLETED = 0 ");
        }
        if(notCompleted){
            cmd.append(" OR NOT_COMPLETED = 1 ) ");
        }
        else{
            cmd.append("OR NOT_COMPLETED = 0 ) ");
        }
        if(!(onTime && notOnTime)) {            //since both can have zeroes at the same time
            if (onTime) {
                cmd.append("AND ( ON_TIME = 1 ");
            } else {
                cmd.append("AND ( ON_TIME = 0 ");
            }
            if (notOnTime) {
                cmd.append(" OR NOT_ON_TIME = 1 )");
            } else {
                cmd.append("OR NOT_ON_TIME = 0 )");
            }
        }
        sqlCommand = new String(cmd);
        Cursor result = readableDatabase.rawQuery(sqlCommand, null);
        Log.d("FilterDebug", sqlCommand);
        return result;
    }

    public void showCategorySelectionDialog(){
        new MaterialDialog.Builder(getContext())
                .title("Specify Categories")
                .items(getCategoryList())
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        TasksListFragment.taskCategoryNames =  text;
                        Log.d("MaterialDebug", Arrays.toString(TasksListFragment.taskCategoryNames));
                        return true;
                    }
                })
                .positiveText("confirm")
                .negativeText("cancel")
                .backgroundColor(Color.parseColor("#263238"))
                .show();
    }

    public void showCompletionSelectionDialog(){
        new MaterialDialog.Builder(getContext())
                .title(R.string.completion_filter)
                .items(R.array.completion_list)
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        HashSet<String> choices = new HashSet<>();
                        for(int i =0; i<text.length; i++){
                            choices.add(text[i].toString());
                        }
                        if(choices.contains("Completed")){
                            completed = true;
                        }
                        else{
                            completed = false;
                        }
                        if(choices.contains("Not Completed")){
                            notCompleted = true;
                        }
                        else{
                            notCompleted = false;
                        }
                        Log.d("MaterialDebug", completed + ""+ notCompleted);
                        return true;
                    }
                })
                .positiveText("confirm")
                .negativeText("cancel")
                .backgroundColor(Color.parseColor("#263238"))
                .show();
    }

    public void showOnTimeSelectionDialog(){
        new MaterialDialog.Builder(getContext())
                .title(R.string.on_time_filter)
                .items(R.array.on_time_list)
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        HashSet<String> choices = new HashSet<>();
                        for(int i =0; i<text.length; i++){
                            choices.add(text[i].toString());
                        }
                        if(choices.contains("On Time")){
                            onTime = true;
                        }
                        else{
                            onTime = false;
                        }
                        if(choices.contains("Not On Time")){
                            notOnTime = true;
                        }
                        else{
                            notOnTime = false;
                        }
                        Log.d("MaterialDebug", onTime + ""+ notOnTime);
                        return true;
                    }
                })
                .positiveText("confirm")
                .negativeText("cancel")
                .backgroundColor(Color.parseColor("#263238"))
                .show();
    }

    public String[] getCategoryList(){
        return SQLfunctionHelper.getCategoryList(getContext());
    }

    private void startTaskActivity(int id){
        Intent intent = new Intent(getContext(), TaskActivity.class);
        intent.putExtra("TASK_ID", id);
        startActivity(intent);
    }

    private void deleteTask(int id){
        SQLfunctionHelper.deleteTask(id, getContext());
        Cursor stats = readableDatabase.rawQuery(sqlCommand, null);
        //Cursor data = readableDatabase.query("TASK_INFORMATION", new String[] {"_ID", "TASK_NAME", "DUE_DATE", "START_TIME", "END_TIME"}, "DUE_DATE = ?", new String[]{ currentDate}, null, null, null);
        Cursor data = readableDatabase.rawQuery("SELECT * FROM TASK_INFORMATION", null);
        RecyclerView recyclerView = getView().findViewById(R.id.recycler_view_task_list_viewer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new TasksListFragment.SimpleAdapter(recyclerView,stats));
    }

    private void markComplete(int id){
        boolean result = SQLfunctionHelper.markComplete(id, TimeTrackerDataBaseHelper.getInstance(getContext()), verifyOnTime(id));
        //Then iterate through category information and update the values for the categories the task belongs to
        if(result){
            Toasty.success(getContext(), "Marked Complete!", Toast.LENGTH_LONG, true).show();
        }
        else{
            Toasty.error(getContext(), "Already Marked Complete!", Toast.LENGTH_LONG, true).show();
        }
    }

    public void setUpToast(){
        Toasty.Config.getInstance()
                .setErrorColor(Color.parseColor("#B71C1C"))
                .setSuccessColor(Color.parseColor("#1B5E20"))
                .setTextColor(Color.WHITE)
                .apply();
    }

    private boolean verifyOnTime(int id){
        Cursor check = readableDatabase.rawQuery("SELECT * FROM TASK_INFORMATION WHERE _ID = " +id, null);
        check.moveToFirst();
        String[] dueDate = check.getString(2).split("-");
        int dueYear = Integer.parseInt(dueDate[0]);
        int dueDay = Integer.parseInt(dueDate[2]);
        int dueMonth = Integer.parseInt(dueDate[1]);
        String[] dueTime = check.getString(5).split("-");
        int dueHour = Integer.parseInt(dueTime[0]);
        int dueMin = Integer.parseInt(dueTime[1]);
        Log.d("MarkCompleteDebug", Arrays.toString(dueDate));
        Log.d("MarkCompleteDebug", Arrays.toString(dueTime));
        Calendar today = Calendar.getInstance();
        int current_hour = today.get(Calendar.HOUR);
        int current_minute = today.get(Calendar.MINUTE);
        int cday = today.get(Calendar.DAY_OF_MONTH);
        int cmonth = today.get(Calendar.MONTH)+1;
        int cyear = today.get(Calendar.YEAR);
        Log.d("MarkCompleteDebug" , "Year: " + cyear + " Month: " + cmonth + " Day: " + cday);
        Log.d("MarkCompleteDebug" , " DUE DATE IS: Year: " + dueYear + " Month: " + dueMonth + " Day: " + dueDay);
        Log.d("MarkCompleteDebug", ((dueYear==cyear))  +"");
        Log.d("MarkCompleteDebug", ((dueMonth==cmonth))  +"");
        Log.d("MarkCompleteDebug", ((dueDay<cday))  +"");


        Log.d("MarkCompleteDebug", ((dueYear==cyear && dueMonth==cmonth && dueDay<cday))  +"");
        if(dueYear<cyear ||  (dueYear==cyear && dueMonth<cmonth) || (dueYear==cyear && dueMonth==cmonth && dueDay<cday) ){
            return false;
        }
        else{
            if(dueDay>cday){
                return true;
            }
            if(current_hour<dueHour || (current_hour==dueHour && current_minute<dueMin)){
                //Toast.makeText(getActivity(), "This Time is Invalid! Make End Time After Start Time", Toast.LENGTH_SHORT).show();
                return true;
            }
            else{
                return false;
            }
        }
    }




    private  class SimpleAdapter extends RecyclerView.Adapter<TasksListFragment.SimpleAdapter.ViewHolder> {

        private static final int UNSELECTED = -1;
        private RecyclerView recyclerView;
        private Cursor data;
        private Cursor stats;
        private int selectedItem = UNSELECTED;

        public SimpleAdapter(RecyclerView recyclerView, Cursor stats) {
            this.recyclerView = recyclerView;
            this.stats = stats;
            this.stats.moveToFirst();
        }

        @Override
        public TasksListFragment.SimpleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
            return new TasksListFragment.SimpleAdapter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(TasksListFragment.SimpleAdapter.ViewHolder holder, int position) {
            holder.bind();
        }

        @Override
        public int getItemCount() {
            return stats.getCount();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ExpandableLayout.OnExpansionUpdateListener {

            private ExpandableLayout expandableLayout;
            private TextView expandButton;

            public ViewHolder(View itemView){
                super(itemView);
                expandableLayout = itemView.findViewById(R.id.expandable_layout);
                expandableLayout.setInterpolator(new OvershootInterpolator());
                expandableLayout.setOnExpansionUpdateListener(this);
                expandButton = itemView.findViewById(R.id.expand_button);
                expandButton.setOnClickListener(this);
            }

            public void bind() {
                int position = getAdapterPosition();
                boolean isSelected = position == selectedItem;
                expandButton.setText(stats.getString(1));
                currentId = stats.getInt(2);
                stats.moveToNext();
                expandButton.setSelected(isSelected);
                expandableLayout.setExpanded(isSelected, false);
                ((Button)expandableLayout.findViewById(R.id.task_activity)).setTag(R.id.Button_ID, currentId);
                ((Button)expandableLayout.findViewById(R.id.task_complete_button)).setTag(R.id.Button_ID, currentId);
                ((Button)expandableLayout.findViewById(R.id.task_delete_button)).setTag(R.id.Button_ID, currentId);
            }

            @Override
            public void onClick(View view) {
                TasksListFragment.SimpleAdapter.ViewHolder holder = (TasksListFragment.SimpleAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(selectedItem);
                if (holder != null) {
                    holder.expandButton.setSelected(false);
                    holder.expandableLayout.collapse();
                }
                int position = getAdapterPosition();
                if (position == selectedItem) {
                    selectedItem = UNSELECTED;
                }
                else {
                    expandButton.setSelected(true);
                    expandableLayout.expand();
                    ((Button)expandableLayout.findViewById(R.id.task_activity)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startTaskActivity((Integer)view.getTag(R.id.Button_ID));
                        }
                    });
                    ((Button)expandableLayout.findViewById(R.id.task_delete_button)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteTask((Integer)view.getTag(R.id.Button_ID));
                        }
                    });
                    ((Button)expandableLayout.findViewById(R.id.task_complete_button)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            markComplete((Integer)view.getTag(R.id.Button_ID));
                        }
                    });
                    selectedItem = position;
                }
            }

            @Override
            public void onExpansionUpdate(float expansionFraction, int state) {
                Log.d("ExpandableLayout", "State: " + state);
                if (state == ExpandableLayout.State.EXPANDING) {
                    recyclerView.smoothScrollToPosition(getAdapterPosition());
                }
            }
        }
    }
}
