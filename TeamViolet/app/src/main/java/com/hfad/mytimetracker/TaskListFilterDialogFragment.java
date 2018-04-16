package com.hfad.mytimetracker;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import javax.annotation.Nullable;

/**
 * Created by Stephen on 4/14/2018.
 */

public class TaskListFilterDialogFragment  extends DialogFragment {

    private static final String TAG = "TaskFilterDialog";

    public interface onInputListener {
        void sendInput(TaskFilter filter);
    }
    public onInputListener filterOnInputListener;

    private boolean completed;
    private boolean notCompleted;
    private boolean onTime;
    private boolean notOnTime;

    private TextView actionApply;
    private TextView actionCancel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filtertask_dialog, container, false);
        actionCancel = view.findViewById(R.id.tasks_list_dialog_cancel);
        actionApply = view.findViewById(R.id.tasks_list_dialog_apply);

        actionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        actionApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskFilter input;
                //filterOnInputListener.sendInput(input);
                getDialog().dismiss();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            filterOnInputListener = (onInputListener) getActivity();
        } catch (ClassCastException ex) {
            Log.e(TAG, "onAttach: ClassCastException: " + ex.getMessage());
        }
    }
}
