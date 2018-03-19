package com.hfad.mytimetracker;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import net.cachapa.expandablelayout.ExpandableLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment implements View.OnClickListener{

    private ExpandableLayout expandableLayout0;
    private ExpandableLayout expandableLayout1;

    public CalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigation);
//        bottomNavigationView.getMenu().getItem(1).setChecked(true);

        //use the cachapa library to make the list view of tasks
        View layout = inflater.inflate(R.layout.fragment_calendar, container, false);

        expandableLayout0 = layout.findViewById(R.id.expandable_layout_0);
        expandableLayout1 = layout.findViewById(R.id.expandable_layout_1);

        expandableLayout0.setOnExpansionUpdateListener(new ExpandableLayout.OnExpansionUpdateListener() {
            @Override
            public void onExpansionUpdate(float expansionFraction, int state) {
               // Log.d("ExpandableLayout0", "State: " + state);
            }
        });

        expandableLayout1.setOnExpansionUpdateListener(new ExpandableLayout.OnExpansionUpdateListener() {
            @Override
            public void onExpansionUpdate(float expansionFraction, int state) {
               // Log.d("ExpandableLayout1", "State: " + state);
            }
        });

        layout.findViewById(R.id.expand_button).setOnClickListener(this);


        return layout;
    }

    @Override
    public void onClick(View view) {
        if (expandableLayout0.isExpanded()) {
            expandableLayout0.collapse();
        } else if (expandableLayout1.isExpanded()) {
            expandableLayout1.collapse();
        } else {
            expandableLayout0.expand();
            expandableLayout1.expand();
        }
    }
    ;


}
