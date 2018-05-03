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

import de.mateware.snacky.Snacky;
import es.dmoral.toasty.Toasty;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment  {
    private int currentId;
    Button button;
    String currentDate;
    public CalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.fragment_calendar, container, false);
        initListeners(layout);
        setDate(Calendar.getInstance());
        setUpRecyclerView(layout);
        setUpToast();
        return layout;
    }

    @Override
    public void onResume(){
        super.onResume();
        setUpRecyclerView(getView());
    }

    public void initListeners(View view){
        final View layout = view;
        CalendarView calendarView = layout.findViewById(R.id.simpleCalendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                TimeTrackerDataBaseHelper dataBaseHelper = TimeTrackerDataBaseHelper.getInstance(getActivity());
                setDate(i, i1, i2);
                setUpRecyclerView(layout);
            }
        });
    }
    public void setDate(Calendar today){
        Integer cday = today.get(Calendar.DAY_OF_MONTH);
        Integer cmonth = today.get(Calendar.MONTH);
        Integer cyear = today.get(Calendar.YEAR);
        currentDate = TaskCreatorFragment.constructDateStr(cyear, cmonth, cday);
    }

    public void setDate(Integer year, Integer month, Integer day){
        currentDate = TaskCreatorFragment.constructDateStr(year, month, day);
    }


    public void setUpRecyclerView(View view){
        final View layout = view;
        Cursor data = SQLfunctionHelper.queryWithParams(getContext(), "TASK_INFORMATION", new String[] {"_ID", "TASK_NAME", "DUE_DATE", "START_TIME", "END_TIME"}, "DUE_DATE = ?", new String[]{ currentDate});
        RecyclerView recyclerView = layout.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new SimpleAdapter(recyclerView, data));       //pa
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && getView()!=null) {
            setUpRecyclerView(getView());
        }
    }

    private void setUpToast(){
        Toasty.Config.getInstance()
                .setErrorColor(Color.parseColor("#B71C1C"))
                .setSuccessColor(Color.parseColor("#1B5E20"))
                .setTextColor(Color.WHITE)
                .apply();
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


    private void deleteTask(int id){
        SQLfunctionHelper.deleteTask(id, getContext());
        Cursor data = SQLfunctionHelper.queryWithParams(getContext(), "TASK_INFORMATION", new String[] {"_ID", "TASK_NAME", "DUE_DATE", "START_TIME", "END_TIME"}, "DUE_DATE = ?", new String[]{ currentDate});
        RecyclerView recyclerView = getView().findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new CalendarFragment.SimpleAdapter(recyclerView, data));
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
        private int selectedItem = UNSELECTED;

        public SimpleAdapter(RecyclerView recyclerView, Cursor data) {
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
                setUpTags();
            }

            public void setUpTags(){
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
                    setListeners();
                    selectedItem = position;
                }

            }

            public void setListeners(){
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
                Log.d("ExpandableLayout", "State: " + state);
                if (state == ExpandableLayout.State.EXPANDING) {
                    recyclerView.smoothScrollToPosition(getAdapterPosition());
                }
            }
        }
    }
}
