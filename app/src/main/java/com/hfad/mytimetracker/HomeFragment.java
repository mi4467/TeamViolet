package com.hfad.mytimetracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


public class HomeFragment extends Fragment {

    public HomeFragment(){

    }

    @SuppressLint("RestrictedAPI")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigation);
//        bottomNavigationView.getMenu().getItem(3).setChecked(true);

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

}
