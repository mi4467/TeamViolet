package com.hfad.mytimetracker;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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


public class TasksListFragment extends Fragment implements TaskListFilterDialogFragment.onInputListener {

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
        Button enter = layout.findViewById(R.id.tasks_list_filter_button);

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TaskListFilterDialogFragment dialog = new TaskListFilterDialogFragment();
                FragmentManager fm = getActivity().getFragmentManager();
                dialog.show(fm, "TaskFilterDialog");

                //if(taskCategoryNames==null){
                    //Toast.makeText(getActivity(), "Choose Categories", Toast.LENGTH_LONG);
                    //return;
                //}
                //Log.d("ButtonDebug", "We entered");
                //Cursor next = constructCommand();
                //resetFilters();
                //RecyclerView recyclerView = layout.findViewById(R.id.recycler_view_task_list_viewer);
                //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                //recyclerView.setAdapter(new TasksListFragment.SimpleAdapter(recyclerView,next));
            }
        });

        return layout;
    }

    @Override
    public void sendInput(TaskFilter input) {

    }

    private void resetFilters(){
        taskCategoryNames = null;
        completed = false;
        notOnTime = false;
        onTime = false;
        notOnTime = false;
    }

    private Cursor constructCommand(){
        if(completed==false && notCompleted==false){
            completed = true;
            notCompleted = true;
        }
        if(onTime == false && notOnTime == false){
            onTime = true;
            notCompleted = true;
        }
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
        if(onTime){
            cmd.append("AND ( ON_TIME = 1 ");
        }
        else{
            cmd.append("AND ( ON_TIME = 0 ");
        }
        if(notOnTime){
            cmd.append(" OR NOT_ON_TIME = 1 )");
        }
        else{
            cmd.append("OR NOT_ON_TIME = 0 )");
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
                .show();
    }

    public String[] getCategoryList(){
//        TimeTrackerDataBaseHelper db = TimeTrackerDataBaseHelper.getInstance(getContext());
//        SQLiteDatabase read = db.getWritableDatabase();
//        Cursor categories = read.query("TASK_CATEGORY_INFO", new String[] {"CATEGORY_NAME"}, null, null, null, null, null, null);
//        String[] result = new String[categories.getCount()];
//        categories.moveToFirst();
//        for(int i =0; i<categories.getCount(); i++){
//            String name = categories.getString(0);
//            result[i] = name;
//            categories.moveToNext();
//        }
//        return result;
        return SQLfunctionHelper.getCategoryList(getContext());
    }

    private void startTaskActivity(int id){
        Intent intent = new Intent(getContext(), TaskActivity.class);
        intent.putExtra("TASK_ID", id);
        startActivity(intent);
    }

    private void deleteTask(int id){
        SQLfunctionHelper.deleteTask(id, readableDatabase, writableDatabase);
        Cursor stats = readableDatabase.rawQuery(sqlCommand, null);
        //Cursor data = readableDatabase.query("TASK_INFORMATION", new String[] {"_ID", "TASK_NAME", "DUE_DATE", "START_TIME", "END_TIME"}, "DUE_DATE = ?", new String[]{ currentDate}, null, null, null);
        Cursor data = readableDatabase.rawQuery("SELECT * FROM TASK_INFORMATION", null);
        RecyclerView recyclerView = getView().findViewById(R.id.recycler_view_task_list_viewer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new TasksListFragment.SimpleAdapter(recyclerView,stats));
    }

    private void markComplete(int id){
//        //ADD a check for the general category
//        ContentValues completeValue = new ContentValues();
//        completeValue.put("COMPLETED", 1);
//        completeValue.put("NOT_COMPLETED", 0);
//        //writableDatabase = (new TimeTrackerDataBaseHelper(getActivity())).getWritableDatabase();
//        Cursor temp = readableDatabase.rawQuery("SELECT * FROM TASK_STATS WHERE TASK_ID = " + id, null);
//        temp.moveToFirst();
//        //we have to check to see if it's on time or not, mark those values in TASK_STATS
//        Log.d("MarkCompleteDebug", "Is it on time: " + verifyOnTime(id));
//        if(verifyOnTime(id)){
//            completeValue.put("ON_TIME", 1);
//            completeValue.put("NOT_ON_TIME", 0);
//            writableDatabase.update("TASK_STATS", completeValue, "TASK_ID = ?", new String[] {id + ""});
//            for(int i =9; i<temp.getColumnCount(); i++){
//                //UPDATE THE ONTIME AND COMPLETED FOR THAT CATEGORY
//                Log.d("MarkCompleteDebug", "Column " + i + " is: " + temp.getColumnName(i));
//                if(temp.getInt(i)==1){
//                    Log.d("MarkCompleteDebug", "We are entering data for: " + temp.getColumnName(i));
//                    writableDatabase.execSQL("UPDATE TASK_CATEGORY_INFO " +
//                            "SET COMPLETED = COMPLETED + 1 " +
//                            "WHERE CATEGORY_NAME = \"" +
//                            temp.getColumnName(i) + "\"");      //Mark it complete
//
//                    writableDatabase.execSQL("UPDATE TASK_CATEGORY_INFO " +
//                            "SET NOT_COMPLETED = NOT_COMPLETED - 1 " +
//                            "WHERE CATEGORY_NAME = \"" +
//                            temp.getColumnName(i) + "\"");
//
//                    writableDatabase.execSQL("UPDATE TASK_CATEGORY_INFO " +
//                            "SET ON_TIME = ON_TIME + 1 " +
//                            "WHERE CATEGORY_NAME = \"" +
//                            temp.getColumnName(i) + "\"");
//                }
//            }
//        }
//        else{
//            completeValue.put("ON_TIME", 0);
//            completeValue.put("NOT_ON_TIME", 1);
//            writableDatabase.update("TASK_STATS", completeValue, "TASK_ID = ?", new String[] {id + ""});
//            for(int i =9; i<temp.getColumnCount(); i++){
//                //UPDATE THE NOT_ONTIME AND COMPLETED FOR THAT CATEGORY
//                Log.d("MarkCompleteDebug", "Column " + i + " is: " + temp.getColumnName(i));
//                if(temp.getInt(i)==1){
//                    Log.d("MarkCompleteDebug", "We are entering data for: " + temp.getColumnName(i));
//                    writableDatabase.execSQL("UPDATE TASK_CATEGORY_INFO " +
//                            "SET COMPLETED = COMPLETED + 1 " +
//                            "WHERE CATEGORY_NAME = \"" +
//                            temp.getColumnName(i) + "\"");      //Mark it complete
//
//                    writableDatabase.execSQL("UPDATE TASK_CATEGORY_INFO " +
//                            "SET NOT_COMPLETED = NOT_COMPLETED - 1 " +
//                            "WHERE CATEGORY_NAME = \"" +
//                            temp.getColumnName(i) + "\"");
//
//                    writableDatabase.execSQL("UPDATE TASK_CATEGORY_INFO " +
//                            "SET NOT_ON_TIME = NOT_ON_TIME + 1 " +
//                            "WHERE CATEGORY_NAME = \"" +
//                            temp.getColumnName(i) + "\"");
//                }
//            }
//        }
        verifyOnTime(id);

        SQLfunctionHelper.markComplete(id, readableDatabase, writableDatabase, verifyOnTime(id));
        //Then iterate through category information and update the values for the categories the task belongs to

    }

    private boolean verifyOnTime(int id){
        Cursor check = readableDatabase.rawQuery("SELECT * FROM TASK_INFORMATION WHERE _ID = " +id, null);
        check.moveToFirst();
        String[] dueDate = check.getString(2).split("-");
        int dueYear = Integer.parseInt(dueDate[0]);
        int dueDay = Integer.parseInt(dueDate[2]);
        int dueMonth = Integer.parseInt(dueDate[1]);
        String[] dueTime = check.getString(4).split("-");
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
