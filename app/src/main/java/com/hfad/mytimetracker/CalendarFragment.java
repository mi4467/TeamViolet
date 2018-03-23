package com.hfad.mytimetracker;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CursorAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.Calendar;

import io.github.yavski.fabspeeddial.FabSpeedDial;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment  {
    private int currentId;
    private ExpandableLayout expandableLayout0;
    private ExpandableLayout expandableLayout1;
    SQLiteDatabase database;
    Button button;
    View yolo;
    FabSpeedDial fabSpeedDial;
    public CalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //use the cachapa library to make the list view of tasks
        final View layout = inflater.inflate(R.layout.fragment_calendar, container, false);

        NestedScrollView C = layout.findViewById(R.id.nested_scroll_view);
        C.smoothScrollTo(0,0);
        CalendarView view = layout.findViewById(R.id.simpleCalendarView);

        view.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                TimeTrackerDataBaseHelper dataBaseHelper = new TimeTrackerDataBaseHelper(getActivity());
                SQLiteDatabase database = dataBaseHelper.getReadableDatabase();
                String date = TaskCreatorFragment.constructDateStr(i, i1, i2);
                Cursor stats = database.rawQuery("SELECT * FROM TASK_STATS", null);
                Log.d("CursorDebug", DatabaseUtils.dumpCursorToString(stats));
                Cursor data = database.query("TASK_INFORMATION", new String[] {"_ID", "TASK_NAME", "DUE_DATE", "START_TIME", "END_TIME"}, "DUE_DATE = ?", new String[]{ date}, null, null, null);
                Log.d("CursorDebug", DatabaseUtils.dumpCursorToString(data));
                RecyclerView recyclerView = layout.findViewById(R.id.recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(new CalendarFragment.SimpleAdapter(recyclerView, data, stats));
            }
        });

        fabSpeedDial = (FabSpeedDial) layout.findViewById(R.id.calendar_fab);
        fabSpeedDial.setVisibility(View.GONE);

        Calendar today = Calendar.getInstance();
        Integer cday = today.get(Calendar.DAY_OF_MONTH);
        Integer cmonth = today.get(Calendar.MONTH);
        Integer cyear = today.get(Calendar.YEAR);


        yolo = inflater.inflate(R.layout.recycler_item, container, false);
        button = yolo.findViewById(R.id.task_activity);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTaskActivity();
            }
        });
        Log.d("ButtonCheck", button.getText().toString());
        //button.performClick();




        RecyclerView recyclerView = layout.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        TimeTrackerDataBaseHelper dataBaseHelper = new TimeTrackerDataBaseHelper(getActivity());
         database = dataBaseHelper.getReadableDatabase();
        String date = TaskCreatorFragment.constructDateStr(cyear, cmonth, cday);
        Cursor stats = database.rawQuery("SELECT * FROM TASK_STATS", null);
        Log.d("CursorDebug", date);
        Cursor data = database.query("TASK_INFORMATION", new String[] {"_ID", "TASK_NAME", "DUE_DATE", "START_TIME", "END_TIME"}, "DUE_DATE = ?", new String[]{ date}, null, null, null);
        Log.d("CursorDebug", DatabaseUtils.dumpCursorToString(data));
        int resId = R.anim.layout_animation_fall_down;
        recyclerView.setAdapter(new SimpleAdapter(recyclerView, data, stats));       //pass in a cursor, then use this cursor to bind in data

        return layout;
    }

    private void markComplete(){

    }

    private void deleteTask(){

    }

    private void startTaskActivity(){
        Log.d("TaskActivity", "Should Direct to an activity");
        Intent intent = new Intent(getContext(), TaskActivity.class);
        intent.putExtra("TASK_ID", currentId);
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
           // holder.onClick(new Button(getContext()));
            holder.bind();
            //data.moveToNext();
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
                //This is where you want to connect the data
                //expandButton.setText(position + ". Tap to expand");

                expandButton.setText(data.getString(1));

                currentId = data.getInt(0);
                //expandButton.setTag(0, data.getInt(0));                 //we pass in the _id to the button


                //expandButton.setBackgroundColor(getColor(data.getString(1)));

                data.moveToNext();
                expandButton.setSelected(isSelected);
                expandableLayout.setExpanded(isSelected, false);
            }

            public int getColor(String name){
               Cursor innerj = database.rawQuery("SELECT * FROM TASK_STATS INNER JOIN TASK_INFORMATION ON TASK_STATS.TASK_NAME = TASK_INFORMATION.TASK_NAME", null);
               Log.d("ColorDebug", DatabaseUtils.dumpCursorToString(innerj));
               return Color.DKGRAY;
            }

            @Override
            public void onClick(View view) {
                ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(selectedItem);
                if (holder != null) {
                    holder.expandButton.setSelected(false);
                    holder.expandableLayout.collapse();
                    fabSpeedDial.setVisibility(View.INVISIBLE);
                }
                int position = getAdapterPosition();
                if (position == selectedItem) {
                    selectedItem = UNSELECTED;
                }
                else {
                    expandButton.setSelected(true);
                    expandableLayout.expand();
                    fabSpeedDial.setVisibility(View.VISIBLE);
                    Display mdisp = getActivity().getWindowManager().getDefaultDisplay();
                    Point mdispSize = new Point();
                    mdisp.getSize(mdispSize);
                    int maxX = mdispSize.x;
                    int maxY = mdispSize.y;
//                    fabSpeedDial.setX(maxX);
//                    fabSpeedDial.setY(maxY);
                    selectedItem = position;

                }

            }

            @Override
            public void onExpansionUpdate(float expansionFraction, int state) {
                Log.d("ExpandableLayout", "State: " + state);
                if (state == ExpandableLayout.State.EXPANDING) {
                    recyclerView.smoothScrollToPosition(getAdapterPosition());
//                    Button button = yolo.findViewById(R.id.task_activity);
//                    button.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            startTaskActivity();
//                        }
//                    });
//                    button.callOnClick();
                    Log.d("ButtonCheck", button.getText().toString());
                }
            }
        }
    }
}
