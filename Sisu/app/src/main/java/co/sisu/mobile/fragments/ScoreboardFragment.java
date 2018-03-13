package co.sisu.mobile.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.AddClientActivity;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.models.Metric;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScoreboardFragment extends Fragment implements View.OnClickListener{

    DataController dataController = new DataController();

    public ScoreboardFragment() {
        // Required empty public constructor
    }

    public void teamSwap() {
//        showToast("SCOREBOARD TEAM SWAP");
        createAndAnimateProgressBars(dataController.updateScoreboardTimeline());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_scoreboard, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final List<Metric> metricList = dataController.getMetrics();
        createAndAnimateProgressBars(metricList);
        initializeTimelineSelector();
        initializeButton();
    }

    private void initializeTimelineSelector() {
        Spinner spinner = getView().findViewById(R.id.timelineSelector);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.timeline_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                showToast("Spinner1: position=" + position + " id=" + id);
                createAndAnimateProgressBars(dataController.updateScoreboardTimeline());
                //will need to refresh page with fresh data based on api call here determined by timeline value selected
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //not sure what this does
            }
        });
    }

    private void initializeButton(){

        ImageView addButton = getView().findViewById(R.id.addView);
        addButton.setOnClickListener(this);

    }

    private void createAndAnimateProgressBars(List<Metric> metricList){
        final int ANIMATION_DURATION = 2500; // 2500ms = 2,5s

        Context context = getContext();

        Metric contactsMetric = metricList.get(0);
        CircularProgressBar contactsProgress = getView().findViewById(R.id.contactsProgress);
        contactsProgress.setColor(ContextCompat.getColor(context, contactsMetric.getColor()));
        contactsProgress.setBackgroundColor(ContextCompat.getColor(context, R.color.colorCorporateGrey));
        contactsProgress.setProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        contactsProgress.setBackgroundProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        contactsProgress.setProgressWithAnimation(contactsMetric.getPercentComplete(), ANIMATION_DURATION);
        TextView contactsCurrentNumber = getView().findViewById(R.id.contactsCurrentNumber);
        TextView contactsGoalNumber = getView().findViewById(R.id.contactsGoalNumber);
        contactsCurrentNumber.setText(String.valueOf(contactsMetric.getCurrentNum()));
        contactsGoalNumber.setText(String.valueOf(contactsMetric.getGoalNum()));

        Metric appointmentsMetric = metricList.get(1);
        CircularProgressBar appointmentsProgress = getView().findViewById(R.id.appointmentsProgress);
        appointmentsProgress.setColor(ContextCompat.getColor(context, appointmentsMetric.getColor()));
        appointmentsProgress.setBackgroundColor(ContextCompat.getColor(context, R.color.colorCorporateGrey));
        appointmentsProgress.setProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        appointmentsProgress.setBackgroundProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        appointmentsProgress.setProgressWithAnimation(appointmentsMetric.getPercentComplete(), ANIMATION_DURATION);
        TextView appointmentsCurrentNumber = getView().findViewById(R.id.appointmentsCurrentNumber);
        TextView appointmentsGoalNumber = getView().findViewById(R.id.appointmentsGoalNumber);
        appointmentsCurrentNumber.setText(String.valueOf(appointmentsMetric.getCurrentNum()));
        appointmentsGoalNumber.setText(String.valueOf(appointmentsMetric.getGoalNum()));

        Metric bbSignedMetric = metricList.get(2);
        CircularProgressBar bbSignedProgress = getView().findViewById(R.id.bbSignedProgress);
        bbSignedProgress.setColor(ContextCompat.getColor(context, bbSignedMetric.getColor()));
        bbSignedProgress.setBackgroundColor(ContextCompat.getColor(context, R.color.colorCorporateGrey));
        bbSignedProgress.setProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        bbSignedProgress.setBackgroundProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        bbSignedProgress.setProgressWithAnimation(bbSignedMetric.getPercentComplete(), ANIMATION_DURATION);
        TextView bbSignedCurrentNumber = getView().findViewById(R.id.bbsignedCurrentNumber);
        TextView bbSignedGoalNumber = getView().findViewById(R.id.bbsignedGoalNumber);
        bbSignedCurrentNumber.setText(String.valueOf(bbSignedMetric.getCurrentNum()));
        bbSignedGoalNumber.setText(String.valueOf(bbSignedMetric.getGoalNum()));

        Metric listingsTakenMetric = metricList.get(3);
        CircularProgressBar listingsTakenProgress = getView().findViewById(R.id.listingsTakenProgress);
        listingsTakenProgress.setColor(ContextCompat.getColor(context, listingsTakenMetric.getColor()));
        listingsTakenProgress.setBackgroundColor(ContextCompat.getColor(context, R.color.colorCorporateGrey));
        listingsTakenProgress.setProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        listingsTakenProgress.setBackgroundProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        listingsTakenProgress.setProgressWithAnimation(listingsTakenMetric.getPercentComplete(), ANIMATION_DURATION);
        TextView listingsTakenCurrentNumber = getView().findViewById(R.id.listingsTakenCurrentNumber);
        TextView listingsTakenGoalNumber = getView().findViewById(R.id.listingsTakenGoalNumber);
        listingsTakenCurrentNumber.setText(String.valueOf(listingsTakenMetric.getCurrentNum()));
        listingsTakenGoalNumber.setText(String.valueOf(listingsTakenMetric.getGoalNum()));

        Metric underContractMetric = metricList.get(4);
        CircularProgressBar underContractProgress = getView().findViewById(R.id.underContractProgress);
        underContractProgress.setColor(ContextCompat.getColor(context, underContractMetric.getColor()));
        underContractProgress.setBackgroundColor(ContextCompat.getColor(context, R.color.colorCorporateGrey));
        underContractProgress.setProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        underContractProgress.setBackgroundProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        underContractProgress.setProgressWithAnimation(underContractMetric.getPercentComplete(), ANIMATION_DURATION);
        TextView underContractCurrentNumber = getView().findViewById(R.id.underContractCurrentNumber);
        TextView underContractGoalNumber = getView().findViewById(R.id.underContactGoalNumber);
        underContractCurrentNumber.setText(String.valueOf(underContractMetric.getCurrentNum()));
        underContractGoalNumber.setText(String.valueOf(underContractMetric.getGoalNum()));

        Metric closedMetric = metricList.get(5);
        CircularProgressBar closedProgress = getView().findViewById(R.id.closedProgress);
        closedProgress.setColor(ContextCompat.getColor(context, closedMetric.getColor()));
        closedProgress.setBackgroundColor(ContextCompat.getColor(context, R.color.colorCorporateGrey));
        closedProgress.setProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        closedProgress.setBackgroundProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        closedProgress.setProgressWithAnimation(closedMetric.getPercentComplete(), ANIMATION_DURATION);
        TextView closedCurrentNumber = getView().findViewById(R.id.closedCurrentNumber);
        TextView closedGoalNumber = getView().findViewById(R.id.closedGoalNumber);
        closedCurrentNumber.setText(String.valueOf(closedMetric.getCurrentNum()));
        closedGoalNumber.setText(String.valueOf(closedMetric.getGoalNum()));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addView:
                //do stuff
                //open floating menu
                navigatePage(AddClientActivity.class);
//                showToast("Add Button is clicked");
                break;
            default:
                //do stuff
                break;
        }
    }

    private void navigatePage(Class c){
        Intent intent = new Intent(getContext(), c);
        startActivity(intent);
    }

    private void showToast(CharSequence msg){
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
    }

}
