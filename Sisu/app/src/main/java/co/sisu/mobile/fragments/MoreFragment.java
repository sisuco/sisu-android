package co.sisu.mobile.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.MainActivity;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.adapters.MoreListAdapter;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.models.MorePageContainer;
import co.sisu.mobile.system.SaveSharedPreference;

/**
 * Created by Brady Groharing on 2/28/2018.
 */

public class MoreFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView mListView;
    DataController dataController;
    ParentActivity parentActivity;
    NavigationManager navigationManager;
    public MoreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dataController = new DataController();
        View toReturn = inflater.inflate(R.layout.activity_more, container, false);
        return toReturn;

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        parentActivity = (ParentActivity) getActivity();
        navigationManager = parentActivity.getNavigationManager();
        initializeListView();
    }

    private void initializeListView() {

        mListView = getView().findViewById(R.id.record_list_view);
        mListView.setDivider(null);
        mListView.setDividerHeight(15);

        final List<MorePageContainer> morePageContainerList = dataController.getMorePageContainer();

        MoreListAdapter adapter = new MoreListAdapter(getContext(), morePageContainerList);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MorePageContainer value = (MorePageContainer) parent.getItemAtPosition(position);

        switch(value.getTitle()) {
            case "Teams":

                break;
            case "Clients":
                navigationManager.stackReplaceFragment(ClientListFragment.class);
//                navigationManager.swapToClientListBar(null);
                break;
            case "My Profile":
                navigationManager.stackReplaceFragment(MyProfileFragment.class);
//                navigationManager.swapToBacktionBar("My Profile", null);
                break;
            case "Goal Setup":
                navigationManager.stackReplaceFragment(GoalSetupFragment.class);
//                navigationManager.swapToBacktionBar("Goal Setup", null);
                break;
            case "Activity Settings":
                navigationManager.stackReplaceFragment(ActivitySettingsFragment.class);
//                navigationManager.swapToBacktionBar("Activity Settings", null);
                break;
            case "Settings":
                navigationManager.stackReplaceFragment(SettingsFragment.class);
//                navigationManager.swapToBacktionBar("Settings", null);
                break;
            case "Feedback":
                navigationManager.stackReplaceFragment(FeedbackFragment.class);
//                navigationManager.swapToTitleBar("Feedback");
                break;
            case "Logout":
                logout();
                SaveSharedPreference.setUserName(getContext(), "");
                break;
        }
    }

    public void logout() {
        Intent intent = new Intent(parentActivity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        parentActivity.finish();
    }
}
