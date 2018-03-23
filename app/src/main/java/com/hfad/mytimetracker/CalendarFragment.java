package com.hfad.mytimetracker;


import android.content.ContentValues;
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

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment  {
    private int currentId;
    private ExpandableLayout expandableLayout0;
    private ExpandableLayout expandableLayout1;
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
                TimeTrackerDataBaseHelper dataBaseHelper = new TimeTrackerDataBaseHelper(getActivity());
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
        TimeTrackerDataBaseHelper dataBaseHelper = new TimeTrackerDataBaseHelper(getActivity());
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
        ContentValues completeValue = new ContentValues();
        completeValue.put("COMPLETED", 1);
        completeValue.put("NOT_COMPLETED", 0);
        writableDatabase = (new TimeTrackerDataBaseHelper(getActivity())).getWritableDatabase();
        writableDatabase.update("TASK_STATS", completeValue, "TASK_ID = ?", new String[] {id + ""});

    }

    private void deleteTask(int id){
        writableDatabase.delete("TASK_STATS", "TASK_ID = ?", new String[]{id+""});
        writableDatabase.delete("TASK_INFORMATION", "_ID = ?", new String[]{id+""});
        Cursor stats = readableDatabase.rawQuery("SELECT * FROM TASK_STATS", null);
        Log.d("CursorDebug", DatabaseUtils.dumpCursorToString(stats));
        Cursor data = readableDatabase.query("TASK_INFORMATION", new String[] {"_ID", "TASK_NAME", "DUE_DATE", "START_TIME", "END_TIME"}, "DUE_DATE = ?", new String[]{ currentDate}, null, null, null);
        Log.d("CursorDebug", DatabaseUtils.dumpCursorToString(data));
        RecyclerView recyclerView = getView().findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new CalendarFragment.SimpleAdapter(recyclerView, data, stats));
    }

    private void startTaskActivity(int id){
        Log.d("TaskActivity", "Should Direct to an activity");
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
