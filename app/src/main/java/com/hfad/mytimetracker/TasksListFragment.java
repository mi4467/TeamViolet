package com.hfad.mytimetracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class TasksListFragment extends Fragment {


    public TasksListFragment() {
        // Required empty public constructor
    }

    @SuppressLint("RestrictedAPI")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigation);
//        bottomNavigationView.getMenu().getItem(2).setChecked(true);

          //Button pickTime = (Button) getView().findViewById(R.id.test_button);
          View layout = inflater.inflate(R.layout.fragment_tasks_list, container, false);
          Button pickTime = (Button) layout.findViewById(R.id.test_button);
          Log.d("Test", pickTime.getText() + " ");
      //  Log.d("time picker test", pickTime.hasOnClickListeners() +" it entered before creating the  listener");
       // pickTime.setOnClickListener(this);
          pickTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view){
                Log.d("fuck this shit", "How do I call buttons in a fragment");
                Toast.makeText(getActivity(), "a", Toast.LENGTH_LONG).show();
            }

        });
        return layout;
    }


}
