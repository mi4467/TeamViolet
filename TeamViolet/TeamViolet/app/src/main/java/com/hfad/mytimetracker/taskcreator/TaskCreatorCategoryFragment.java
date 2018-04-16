package com.hfad.mytimetracker.taskcreator;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.hfad.mytimetracker.MainActivity;
import com.hfad.mytimetracker.R;
import com.hfad.mytimetracker.SQLfunctionHelper;
import com.hfad.mytimetracker.TimeTrackerDataBaseHelper;

import java.util.ArrayList;
import java.util.List;

public class TaskCreatorCategoryFragment extends Fragment implements  View.OnClickListener {

    private static Integer color = null;                  //use for first card view
    private static String categoryName = null;            //use for first cardView

    private boolean singleTask = true;

    private Spinner spinnerCategory;

    public TaskCreatorCategoryFragment () {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_task_creator_category, container, false);
        Button pickColor = (Button) layout.findViewById(R.id.color_button);
        Button submitCat = (Button) layout.findViewById(R.id.submit_cat);

        Spinner categorySlector = (Spinner) layout.findViewById(R.id.cat_selector);
        spinnerCategory = categorySlector;

        RadioButton singleTask = (RadioButton) layout.findViewById(R.id.radioButtonSingleTask);
        RadioButton reoccurringTask = (RadioButton) layout.findViewById(R.id.radioButtonReoccurringTask);

        Button nextTaskCreator = (Button) layout.findViewById(R.id.buttonNextTaskCreator);

        pickColor.setOnClickListener(this);
        submitCat.setOnClickListener(this);
        nextTaskCreator.setOnClickListener(this);
        singleTask.setOnClickListener(this);
        reoccurringTask.setOnClickListener(this);

        updateCategoriesInSpinner();
        return layout;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.color_button:
                showColorWheelDialog(view);
                break;
            case R.id.submit_cat:
                enterCatInDB();
                updateCategoriesInSpinner();
                break;
            case R.id.radioButtonSingleTask:
                singleTask = true;
                break;
            case R.id.radioButtonReoccurringTask:
                singleTask = false;
                break;
            case R.id.buttonNextTaskCreator:
                loadTaskSettings();
                break;
        }
    }

    public void showColorWheelDialog(View v) {
        ColorPickerDialogBuilder
                .with(getContext())
                .setTitle("Choose color")
                .initialColor(0xffffffff)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        // toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        TextView yolo = (TextView) getActivity().findViewById(R.id.colorbox);
                        color = selectedColor;
                        yolo.setBackgroundColor(selectedColor);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    private void enterCatInDB() {
        EditText txt = (EditText) getActivity().findViewById(R.id.cat_name);
        String cat = txt.getText().toString();      //paramaters
        TaskCreatorCategoryFragment.categoryName=cat;
        Integer color = this.color;
        if(TaskCreatorCategoryFragment.color==null){
            Log.d("CategorySQL", "No Color");
            Toast.makeText(getActivity(), "This Category Needs a Color!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TaskCreatorCategoryFragment.categoryName.equals("")){
            Log.d("CategorySQL", "this is cat" + cat + "!");
            Toast.makeText(getActivity(), "This Category Needs a Name!", Toast.LENGTH_SHORT).show();
            return;
        }
        TimeTrackerDataBaseHelper categoryHelper = TimeTrackerDataBaseHelper.getInstance(getActivity());
        SQLiteDatabase read = categoryHelper.getReadableDatabase();
        SQLiteDatabase write = categoryHelper.getWritableDatabase();
        if(SQLfunctionHelper.enterCatInDB(getActivity(), read, write, cat, categoryName, color)){
            TaskCreatorCategoryFragment.categoryName = null;
            TaskCreatorCategoryFragment.color = null;
        }
    }

    private void updateCategoriesInSpinner() {
        List<String> spinnerArray = new ArrayList<String>();
        String[] categories = getCategoryList();

        for (int index = 0; index < categories.length; index++) {
            spinnerArray.add(categories[index]);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    public String[] getCategoryList() {
        return SQLfunctionHelper.getCategoryList(getContext());
    }

    private void loadTaskSettings() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (singleTask) {
            ft.replace(R.id.task_creator_base_fragment, new TaskCreatorTaskFragment());
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(null);
            ft.commit();

        } else {
            ft.replace(R.id.task_creator_base_fragment, new TaskCreatorReoccurringTaskFragment());
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(null);
            ft.commit();
        }
    }
}
