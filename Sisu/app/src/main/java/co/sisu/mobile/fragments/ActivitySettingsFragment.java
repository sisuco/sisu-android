package co.sisu.mobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.adapters.ActivityListAdapter;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.models.AsyncParameterJsonObject;
import co.sisu.mobile.models.AsyncUpdateSettingsJsonObject;
import co.sisu.mobile.models.ParameterObject;
import co.sisu.mobile.models.SelectedActivities;
import co.sisu.mobile.models.UpdateSettingsObject;

/**
 * Created by Jeff on 4/18/2018.
 */

public class ActivitySettingsFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener, AsyncServerEventListener, DragListView.DragListListener {

    private DragListView mListView;
    private ParentActivity parentActivity;
    private NavigationManager  navigationManager;
    private ApiManager apiManager;
    private DataController dataController;
    private List<SelectedActivities> selectedActivities;
    private List<String> currentActivitiesSorting;
    private ProgressBar loader;
    private ArrayList mItemArray = new ArrayList<>();
    private boolean editMode = false;
    private TextView saveButton, editButton;

    public ActivitySettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View toReturn = inflater.inflate(R.layout.activity_activities_settings, container, false);
        return toReturn;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        parentActivity = (ParentActivity) getActivity();
        navigationManager = parentActivity.getNavigationManager();
        dataController = parentActivity.getDataController();
        apiManager = parentActivity.getApiManager();
        loader = view.findViewById(R.id.activitySettingsLoader);
        initializeButtons();
        initializeListView();
        loader.setVisibility(View.VISIBLE);
        apiManager.sendAsyncActivitySettings(this, dataController.getAgent().getAgent_id());
    }

    private void setupFieldsWithData() {
        HashMap<String, SelectedActivities> activitiesSelected = dataController.getActivitiesSelected();
        selectedActivities = new ArrayList<>();

        for ( String key : activitiesSelected.keySet() ) {
            SelectedActivities selectedActivity = activitiesSelected.get(key);

            if(selectedActivity.getName() != null) {
                selectedActivities.add(selectedActivity);
            }
        }
    }

    private void initializeButtons() {
        saveButton = parentActivity.findViewById(R.id.saveButton);
        if(saveButton != null) {
            saveButton.setOnClickListener(this);
        }
        editButton = parentActivity.findViewById(R.id.editButton);
        if(editButton != null) {
            editButton.setOnClickListener(this);
        }
    }

    private void initializeListView() {
        mListView = getView().findViewById(R.id.activity_list_view);
        mListView.setDragListListener(this);
        mListView.setLayoutManager(new LinearLayoutManager(parentActivity));
        mListView.getRecyclerView().setVerticalScrollBarEnabled(true);
    }



    private void fillListViewWithData(HashMap<String, SelectedActivities> selectedActivities) {
        int counter = 0;
        if(getContext() != null) {

            for ( String key : selectedActivities.keySet() ) {
                SelectedActivities value = selectedActivities.get(key);
                mItemArray.add(new Pair<>((long) counter, value));
                counter++;
            }
        }
        ActivityListAdapter activityListAdapter = null;

        if(!editMode) {
            activityListAdapter = new ActivityListAdapter(mItemArray, R.layout.adapter_activity_list, R.id.activity_list_title, false);
            mListView.setDragEnabled(false);
        }
        else {
            activityListAdapter = new ActivityListAdapter(mItemArray, R.layout.adapter_edit_activity_list, R.id.editButton, false);
            mListView.setDragEnabled(true);
        }
        mListView.setAdapter(activityListAdapter, false);
        mListView.setCanDragHorizontally(false);
        mListView.setCustomDragItem(null);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //ActivitiesPageContainer value = (ActivitiesPageContainer) parent.getItemAtPosition(position);

//        switch() {
//            default:
//                break;
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveButton://notify of success update api
                if(editMode) {
                    //This would save the editing of priority
                    editMode = false;
                    saveButton.setText("Save");
                    editButton.setVisibility(View.VISIBLE);
                    fillListViewWithData(dataController.getActivitiesSelected());
                }
                else {
                    saveSettings();
                }
                break;
            case R.id.editButton:
                editMode = !editMode;
                if(editMode) {
                    saveButton.setText("Done");
                    editButton.setVisibility(View.INVISIBLE);
                }
                else {
                    saveButton.setText("Save");
                }
                fillListViewWithData(dataController.getActivitiesSelected());
                break;
        }
    }

    private void updateCurrentSettings() {

    }

    private void saveSettings() {
        apiManager.sendAsyncUpdateActivitySettings(this, dataController.getAgent().getAgent_id(), createUpdateObject(selectedActivities));

    }

    private AsyncUpdateSettingsJsonObject createUpdateObject(List<SelectedActivities> selectedActivities) {
        String valueString = "{";
        int counter = 0;
        for(SelectedActivities activity : selectedActivities) {
            valueString += "\"" + activity.getType() + "\"" + ":" + activity.getValue();
            if(counter < selectedActivities.size() - 1) {
                valueString += ",";
            }
            counter++;
        }
        valueString += "}";
        List<UpdateSettingsObject> list = new ArrayList<>();
        list.add(new UpdateSettingsObject("record_activities", valueString, 7));
        AsyncUpdateSettingsJsonObject asyncUpdateSettingsJsonObject = new AsyncUpdateSettingsJsonObject(2, Integer.valueOf(dataController.getAgent().getAgent_id()), list);
        return asyncUpdateSettingsJsonObject;
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        if(asyncReturnType.equals("Activity Settings")) {
            AsyncParameterJsonObject settingsJson = (AsyncParameterJsonObject) returnObject;
            ParameterObject settings = settingsJson.getParameter();
            dataController.setActivitiesSelected(settings);
            currentActivitiesSorting = setupCurrentSorting(dataController.getActivitiesSelected());
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loader.setVisibility(View.GONE);
                    setupFieldsWithData();
                    fillListViewWithData(dataController.getActivitiesSelected());
                }
            });
        }
        else if(asyncReturnType.equals("Update Settings")) {
            navigationManager.clearStackReplaceFragment(MoreFragment.class);
            parentActivity.showToast("Activity updates saved");

        }
    }

    private List<String> setupCurrentSorting(LinkedHashMap<String, SelectedActivities> activitiesSelected) {
        List<String> currentSorting = new ArrayList<>();
        for ( String key : activitiesSelected.keySet() ) {
            SelectedActivities selectedActivity = activitiesSelected.get(key);

            currentSorting.add(key);
        }
        return currentSorting;
    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {

    }

    @Override
    public void onItemDragStarted(int position) {

    }

    @Override
    public void onItemDragging(int itemPosition, float x, float y) {

    }

    @Override
    public void onItemDragEnded(int fromPosition, int toPosition) {
        Log.e("FROM POSITION", String.valueOf(fromPosition));
        String fromActivity = currentActivitiesSorting.get(fromPosition);
        Log.e("FROM", fromActivity);
        Log.e("TO POSITION", String.valueOf(toPosition));
        String toActivity = currentActivitiesSorting.get(toPosition);
        Log.e("TO", toActivity);
        sortActivities(fromActivity, fromPosition, toPosition);
        if (fromPosition != toPosition) {
            Log.e("SELECTED", String.valueOf(currentActivitiesSorting));
        }
    }

    private void sortActivities(String fromActivity, int fromPosition, int toPosition) {
        List<String> newSorting = new ArrayList<>();
        if(fromPosition > toPosition) {
            for(int i = 0; i < toPosition; i++) {
                if(i != fromPosition) {
                    newSorting.add(currentActivitiesSorting.get(i));
                }
            }
            newSorting.add(fromActivity);
            for(int i = toPosition + 1; i < currentActivitiesSorting.size(); i++) {
                if(i != fromPosition) {
                    newSorting.add(currentActivitiesSorting.get(i));
                }
            }
        }
        else if(toPosition > fromPosition) {

        }
        Log.e("SORTING", String.valueOf(newSorting));
    }
}
