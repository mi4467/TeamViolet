package com.hfad.mytimetracker.taskcreator;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hfad.mytimetracker.R;

public class TaskCreatorBaseFragment extends Fragment {

    private static final String TAG = "TaskCreatorBaseFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_creator_base, container, false);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.replace(R.id.task_creator_base_fragment, new TaskCreatorCategoryFragment());
        transaction.commit();
        return view;
    }

}
