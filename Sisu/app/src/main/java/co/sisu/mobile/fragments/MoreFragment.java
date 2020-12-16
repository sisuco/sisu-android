package co.sisu.mobile.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.MainActivity;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.adapters.MoreListAdapter;
import co.sisu.mobile.controllers.ActionBarManager;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.MorePageContainer;
import co.sisu.mobile.system.SaveSharedPreference;

/**
 * Created by Brady Groharing on 2/28/2018.
 */

public class MoreFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView mListView;
    private DataController dataController;
    private ParentActivity parentActivity;
    private NavigationManager navigationManager;
    private ColorSchemeManager colorSchemeManager;
    private ActionBarManager actionBarManager;
    private String m_Text;

    public MoreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View toReturn = inflater.inflate(R.layout.activity_more, container, false);
        return toReturn;

    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        parentActivity = (ParentActivity) getActivity();
        navigationManager = parentActivity.getNavigationManager();
        dataController = parentActivity.getDataController();
        colorSchemeManager = parentActivity.getColorSchemeManager();
        actionBarManager = parentActivity.getActionBarManager();
        initializeListView();
        setColorScheme();
    }

    private void setColorScheme() {
        ConstraintLayout layout = getView().findViewById(R.id.moreListParentLayout);
        layout.setBackgroundColor(colorSchemeManager.getAppBackground());
    }

    private void initializeListView() {

        mListView = getView().findViewById(R.id.record_list_view);
        mListView.setDivider(null);
        mListView.setDividerHeight(15);

        final List<MorePageContainer> morePageContainerList = dataController.getMorePageContainer(parentActivity.isRecruiting(), parentActivity.getCurrentTeam().getRole().equals("ADMIN"));

        MoreListAdapter adapter = new MoreListAdapter(getContext(), morePageContainerList, colorSchemeManager);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MorePageContainer value = (MorePageContainer) parent.getItemAtPosition(position);
        if(!parentActivity.isTeamSwapOccurring()) {
            switch(value.getTitle()) {
//                case "Teams":
//                    break;
//                case "Clients":
//                case "Recruits":
//                    navigationManager.stackReplaceFragment(ClientListFragment.class);
//                    break;
                case "My Profile":
                    navigationManager.stackReplaceFragment(MyProfileFragment.class);
                    actionBarManager.setToSaveBar("My Profile");
                    break;
                case "Goal Setup":
                    if(parentActivity.isRecruiting()) {
                        navigationManager.stackReplaceFragment(RecruitingGoalSetupFragment.class);
                    }
                    else {
                        navigationManager.stackReplaceFragment(GoalSetupFragment.class);
                    }
                    actionBarManager.setToSaveBar("Goal Setup");
                    break;
                case "Activity Settings":
                    navigationManager.stackReplaceFragment(ActivitySettingsFragment.class);
                    // TODO: Need to create the list edit bar
                    actionBarManager.setToEditBar("Record Settings");
                    break;
                case "Settings":
                    navigationManager.stackReplaceFragment(SettingsFragment.class);
                    actionBarManager.setToTitleBar("Settings", false);
                    break;
                case "Feedback":
                    navigationManager.stackReplaceFragment(FeedbackFragment.class);
                    actionBarManager.setToTitleBar("Feedback", false);
                    break;
                case "Slack":
                    navigationManager.stackReplaceFragment(SlackMessageFragment.class);
                    actionBarManager.setToTitleBar("Slack", false);
                    break;
                case "Message Center":
                    parentActivity.setNoteOrMessage("Message");
                    navigationManager.stackReplaceFragment(ClientNoteFragment.class);
                    // TODO: Need to create the add bar
                    actionBarManager.setToAddBar("Message Center");
                    break;
                case "Sisu Login":
                    popAgentIdDialog("Enter Agent ID", "agentId");
                    break;
                case "Logout":
                    logout();
                    SaveSharedPreference.setUserName(getContext(), "");
                    break;
            }
        }

    }

    private void popAgentIdDialog(String message, String text) {
        AlertDialog.Builder builder;
        final EditText input = new EditText(parentActivity);
        builder = new AlertDialog.Builder(parentActivity, R.style.lightDialog);
        input.setTextColor(Color.BLACK);

        builder.setTitle(message);
        final String label = text;
// Set up the input
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                if(!m_Text.equals("")) {
                    AgentModel currentAgent = dataController.getAgent();
                    currentAgent.setAgent_id(m_Text);
                    dataController.setAgent(currentAgent);
                }
                else {
                    parentActivity.showToast("Please enter some text in the note field.");
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void logout() {
        Intent intent = new Intent(parentActivity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        parentActivity.finish();
    }

    public void teamSwap() {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initializeListView();
            }
        });
    }
}
