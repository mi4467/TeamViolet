package com.hfad.mytimetracker;


import android.arch.persistence.room.Database;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment  {
    private int currentId;
    SQLiteDatabase readableDatabase;
    SQLiteDatabase writableDatabase;
    Button button;
    View yolo;
    String currentDate;
    public CalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.fragment_calendar, container, false);
        NestedScrollView C = layout.findViewById(R.id.nested_scroll_view);
        C.smoothScrollTo(0,0);
        CalendarView view = layout.findViewById(R.id.simpleCalendarView);
        view.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                TimeTrackerDataBaseHelper dataBaseHelper = TimeTrackerDataBaseHelper.getInstance(getActivity());
                currentDate = TaskCreatorFragment.constructDateStr(i, i1, i2);
                Cursor stats = readableDatabase.rawQuery("SELECT * FROM TASK_STATS", null);
                Log.d("CursorDebug", DatabaseUtils.dumpCursorToString(stats));
                Cursor data = readableDatabase.query("TASK_INFORMATION", new String[] {"_ID", "TASK_NAME", "DUE_DATE", "START_TIME", "END_TIME"}, "DUE_DATE = ?", new String[]{ currentDate}, null, null, null);
                Log.d("CursorDebug", DatabaseUtils.dumpCursorToString(data));
                RecyclerView recyclerView = layout.findViewById(R.id.recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(new CalendarFragment.SimpleAdapter(recyclerView, data, stats));
            }
        });
        Calendar today = Calendar.getInstance();
        Integer cday = today.get(Calendar.DAY_OF_MONTH);
        Integer cmonth = today.get(Calendar.MONTH);
        Integer cyear = today.get(Calendar.YEAR);
        RecyclerView recyclerView = layout.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        TimeTrackerDataBaseHelper dataBaseHelper = TimeTrackerDataBaseHelper.getInstance(getActivity());
        readableDatabase = dataBaseHelper.getReadableDatabase();
        writableDatabase = dataBaseHelper.getWritableDatabase();
        String date = TaskCreatorFragment.constructDateStr(cyear, cmonth, cday);
        currentDate = date;
        Cursor stats = readableDatabase.rawQuery("SELECT * FROM TASK_STATS", null);
        Log.d("CursorDebug", date);
        Cursor data = readableDatabase.query("TASK_INFORMATION", new String[] {"_ID", "TASK_NAME", "DUE_DATE", "START_TIME", "END_TIME"}, "DUE_DATE = ?", new String[]{ date}, null, null, null);
        Log.d("CursorDebug", DatabaseUtils.dumpCursorToString(data));
        int resId = R.anim.layout_animation_fall_down;
        recyclerView.setAdapter(new SimpleAdapter(recyclerView, data, stats));       //pass in a cursor, then use this cursor to bind in data
        return layout;
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


    private void deleteTask(int id){
//        Cursor temp = readableDatabase.rawQuery("SELECT * FROM TASK_STATS WHERE TASK_ID = " + id, null);
//        temp.moveToFirst();
//        boolean completed = temp.getInt(4)==1;
//        boolean matters = !(temp.getInt(7) == temp.getInt(8));
//        boolean ontime = temp.getInt(8)==1;
//        Log.d("DeleteDebug", temp.getColumnCount()+"");
//        Log.d("DeleteDebug", completed+" " + matters + " " + ontime);
//        for(int i =9; i<temp.getColumnCount(); i++){
//            Log.d("DeleteDebug", "Column " + i + " is: " + temp.getColumnName(i));
//            Log.d("DeleteDebug", temp.getPosition() + " out of " + temp.getColumnCount());
//            Log.d("DeleteDebug", DatabaseUtils.dumpCursorToString(temp));
//            if(temp.getInt(i)==1){
//                if(completed){
//                    writableDatabase.execSQL("UPDATE TASK_CATEGORY_INFO " +
//                            "SET COMPLETED = COMPLETED - 1 " +
//                            "WHERE CATEGORY_NAME = \"" +
//                            temp.getColumnName(i) + "\"");
//                }
//                else {
//                    writableDatabase.execSQL("UPDATE TASK_CATEGORY_INFO " +
//                            "SET NOT_COMPLETED = NOT_COMPLETED - 1 " +
//                            "WHERE CATEGORY_NAME = \"" +
//                            temp.getColumnName(i) + "\"");
//                }
//                if(matters){
//                    if(ontime){
//                        writableDatabase.execSQL("UPDATE TASK_CATEGORY_INFO " +
//                                "SET ON_TIME = ON_TIME - 1 " +
//                                "WHERE CATEGORY_NAME = \"" +
//                                temp.getColumnName(i) + "\"");
//                    }
//                    else{
//                        writableDatabase.execSQL("UPDATE TASK_CATEGORY_INFO " +
//                                "SET NOT_ON_TIME = NOT_ON_TIME - 1 " +
//                                "WHERE CATEGORY_NAME = \"" +
//                                temp.getColumnName(i) + "\"");
//                    }
//                }
//            }
//        }
//        writableDatabase.delete("TASK_STATS", "TASK_ID = ?", new String[]{id+""});
//        writableDatabase.delete("TASK_INFORMATION", "_ID = ?", new String[]{id+""});
        SQLfunctionHelper.deleteTask(id, readableDatabase, writableDatabase);
        Cursor stats = readableDatabase.rawQuery("SELECT * FROM TASK_STATS", null);
        Cursor data = readableDatabase.query("TASK_INFORMATION", new String[] {"_ID", "TASK_NAME", "DUE_DATE", "START_TIME", "END_TIME"}, "DUE_DATE = ?", new String[]{ currentDate}, null, null, null);
        RecyclerView recyclerView = getView().findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new CalendarFragment.SimpleAdapter(recyclerView, data, stats));
    }

    private void startTaskActivity(int id){
        Intent intent = new Intent(getContext(), TaskActivity.class);
        intent.putExtra("TASK_ID", id);
        startActivity(intent);
    }


    private  class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.ViewHolder> {

        private static final int UNSELECTED = -1;
        private RecyclerView recyclerView;
        private Cursor data;
        private Cursor stats;
        private int selectedItem = UNSELECTED;

        public SimpleAdapter(RecyclerView recyclerView, Cursor data, Cursor stats) {
            this.recyclerView = recyclerView;
            this.data = data;
            data.moveToFirst();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
                return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind();
        }

        @Override
        public int getItemCount() {
            return data.getCount();
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
                expandButton.setText(data.getString(1));
                currentId = data.getInt(0);
                data.moveToNext();
                expandButton.setSelected(isSelected);
                expandableLayout.setExpanded(isSelected, false);
                ((Button)expandableLayout.findViewById(R.id.task_activity)).setTag(R.id.Button_ID, currentId);
                ((Button)expandableLayout.findViewById(R.id.task_complete_button)).setTag(R.id.Button_ID, currentId);
                ((Button)expandableLayout.findViewById(R.id.task_delete_button)).setTag(R.id.Button_ID, currentId);
            }

            @Override
            public void onClick(View view) {
                ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(selectedItem);
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
