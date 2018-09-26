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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;

import de.mateware.snacky.Snacky;
import es.dmoral.toasty.Toasty;


public class TasksListFragment extends Fragment {

    private Integer currentId;
    private static CharSequence[] taskCategoryNames=null;

    private boolean completed;
    private boolean notCompleted;
    private boolean onTime;
    private boolean notOnTime;

    private String sqlCommand = "SELECT * FROM TASK_STATS";
    private Cursor stats;


    public TasksListFragment() {
        // Required empty public constructor
    }

    @SuppressLint("RestrictedAPI")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.fragment_tasks_list, container, false);
        setUpListeners(layout);
        setUpStatsCursor();
        setUpRecyclerView(layout);
        resetFilters();
        setUpToast();
        return layout;
    }

    @Override
    public void onResume(){
        super.onResume();
        setUpStatsCursor();
        setUpRecyclerView(getView());
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && getView()!=null) {
            this.onResume();
        }
    }

    private void setUpRecyclerView(View view){
        final View layout = view;
        RecyclerView recyclerView = layout.findViewById(R.id.recycler_view_task_list_viewer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new TasksListFragment.SimpleAdapter(recyclerView,stats));
    }
    private void setUpStatsCursor(){
        stats = SQLfunctionHelper.queryWithString(getContext(), sqlCommand);
    }

    private void setUpListeners(View view){
        final View layout = view;
        Button pickCat = layout.findViewById(R.id.tasks_list_category_filter_button);
        Button pickCompStatus = layout.findViewById(R.id.tasks_list_completed_status_filter_button);
        Button pickOnTimeStatus = layout.findViewById(R.id.tasks_list_on_time_status_filter_button);
        Button enter = layout.findViewById(R.id.tasks_list_filter_button);

        pickCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCategorySelectionDialog();
            }
        });

        pickCompStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCompletionSelectionDialog();
            }
        });

        pickOnTimeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOnTimeSelectionDialog();
            }
        });
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter(layout);
            }
        });
    }

    private void filter(View view){
        View layout = view;
        if(!checkIfValidFilter()){
            return;
        }
        stats = constructCommand();
        resetFilters();
        setUpRecyclerView(layout);
        resetFilters();
    }

    private boolean checkIfValidFilter(){
        if(taskCategoryNames==null){
            Toasty.error(getContext(), "Choose Categories!", Toast.LENGTH_LONG, true).show();
            return false;
        }
        return true;
    }

    private void resetFilters(){
        taskCategoryNames = null;
        completed = false;
        notOnTime = false;
        onTime = false;
        notOnTime = false;
    }

    private void checkAndAdjustParamaterSelection(){
        if(completed==false && notCompleted==false){
            completed = true;
            notCompleted = true;
        }
        if(onTime == false && notOnTime == false){
            onTime = true;
            notOnTime = true;
        }
    }

    private void addCategoryParams(StringBuilder cmd){
        cmd.append("SELECT * FROM TASK_STATS WHERE ( ");
        for(int i = 0; i<taskCategoryNames.length-1; i++){
            cmd.append(taskCategoryNames[i].toString() + " = 1 OR ");
        }
        cmd.append(" " + taskCategoryNames[taskCategoryNames.length-1] + "=1 ) ");
    }

    private void addCompletionParams(StringBuilder cmd){
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
    }

    private void addPunctualityParams(StringBuilder cmd){
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
    }
    private Cursor constructCommand(){
        checkAndAdjustParamaterSelection();
        StringBuilder cmd = new StringBuilder();
        addCategoryParams(cmd);
        addCompletionParams(cmd);
        addPunctualityParams(cmd);
        sqlCommand = new String(cmd);
        return SQLfunctionHelper.queryWithString(getContext(), sqlCommand);
    }

    private void showCategorySelectionDialog(){
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

    private void showCompletionSelectionDialog(){
        new MaterialDialog.Builder(getContext())
                .title(R.string.completion_filter)
                .items(R.array.completion_list)
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        adjustCompletion(text);
                        Log.d("MaterialDebug", completed + ""+ notCompleted);
                        return true;
                    }
                })
                .positiveText("confirm")
                .negativeText("cancel")
                .backgroundColor(Color.parseColor("#263238"))
                .show();
    }

    private void adjustCompletion(CharSequence[] text){
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
    }

    private void adjustPunctual(CharSequence[] text){
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

    }

    private void showOnTimeSelectionDialog(){
        new MaterialDialog.Builder(getContext())
                .title(R.string.on_time_filter)
                .items(R.array.on_time_list)
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        adjustPunctual(text);
                        Log.d("MaterialDebug", onTime + ""+ notOnTime);
                        return true;
                    }
                })
                .positiveText("confirm")
                .negativeText("cancel")
                .backgroundColor(Color.parseColor("#263238"))
                .show();
    }

    private String[] getCategoryList(){
        return SQLfunctionHelper.getCategoryList(getContext());
    }

    private void startTaskActivity(int id){
        Intent intent = new Intent(getContext(), TaskActivity.class);
        intent.putExtra("TASK_ID", id);
        startActivity(intent);
    }

    private void deleteTask(int id){
        SQLfunctionHelper.deleteTask(id, getContext());
        stats = SQLfunctionHelper.queryWithString(getContext(), sqlCommand);
        setUpRecyclerView(getView());
    }

    private void markComplete(int id){
        boolean result = SQLfunctionHelper.markComplete(id, TimeTrackerDataBaseHelper.getInstance(getContext()), verifyOnTime(id));
        if(result){
            Toasty.success(getContext(), "Marked Complete!", Toast.LENGTH_LONG, true).show();
        }
        else{
            Toasty.error(getContext(), "Already Marked Complete!", Toast.LENGTH_LONG, true).show();
        }
    }

    private void setUpToast(){
        Toasty.Config.getInstance()
                .setErrorColor(Color.parseColor("#B71C1C"))
                .setSuccessColor(Color.parseColor("#1B5E20"))
                .setTextColor(Color.WHITE)
                .apply();
    }

    private boolean verifyOnTime(int id){
        Cursor check = SQLfunctionHelper.queryWithString(getContext(), "SELECT * FROM TASK_INFORMATION WHERE _ID = " +id);
        check.moveToFirst();
        String[] dueDate = check.getString(2).split("-");
        int dueYear = Integer.parseInt(dueDate[0]);
        int dueDay = Integer.parseInt(dueDate[2]);
        int dueMonth = Integer.parseInt(dueDate[1]);
        String[] dueTime = check.getString(5).split("-");
        int dueHour = Integer.parseInt(dueTime[0]);
        int dueMin = Integer.parseInt(dueTime[1]);
        Calendar today = Calendar.getInstance();
        int current_hour = today.get(Calendar.HOUR);
        int current_minute = today.get(Calendar.MINUTE);
        int cday = today.get(Calendar.DAY_OF_MONTH);
        int cmonth = today.get(Calendar.MONTH)+1;
        int cyear = today.get(Calendar.YEAR);
        if(dueYear<cyear ||  (dueYear==cyear && dueMonth<cmonth) || (dueYear==cyear && dueMonth==cmonth && dueDay<cday) ){
            return false;
        }
        else{
            if(dueDay>cday){
                return true;
            }
            if(current_hour<dueHour || (current_hour==dueHour && current_minute<dueMin)){
                return true;
            }
            else{
                return false;
            }
        }
    }

    private class SimpleAdapter extends RecyclerView.Adapter<TasksListFragment.SimpleAdapter.ViewHolder> {

        private static final int UNSELECTED = -1;
        private RecyclerView recyclerView;
        private ArrayList<TaskData> data;
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

        public class TaskData {
            int id;
            String name;

            public TaskData(int id, String name){
                this.id = id;
                this.name = name;
            }

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
                setTags();
            }

            private void setTags(){
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
                    setListenersRecycler();
                    selectedItem = position;
                }
            }

            public void setListenersRecycler(){
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
            }

            @Override
            public void onExpansionUpdate(float expansionFraction, int state) {
                if (state == ExpandableLayout.State.EXPANDING) {
                    recyclerView.smoothScrollToPosition(getAdapterPosition());
                }
            }
        }
    }
}