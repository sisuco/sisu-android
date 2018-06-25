package co.sisu.mobile.fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.adapters.LeaderboardListExpandableAdapter;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.models.AsyncLeaderboardJsonObject;
import co.sisu.mobile.models.AsyncProfileImageJsonObject;
import co.sisu.mobile.models.LeaderboardItemsObject;
import co.sisu.mobile.models.LeaderboardObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class LeaderboardFragment extends Fragment implements AsyncServerEventListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener, com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener {

    private LeaderboardListExpandableAdapter listAdapter;
    private ExpandableListView expListView;
    private List<LeaderboardObject> listDataHeader;
    private HashMap<LeaderboardObject, List<LeaderboardItemsObject>> listDataChild;
    private ProgressBar loader;
    private Calendar calendar = Calendar.getInstance();
    private ParentActivity parentActivity;
    private DataController dataController;
    private ApiManager apiManager;
    private Switch leaderboardToggle;
    private int selectedYear = 0;
    private int selectedMonth = 0;
    private int selectedDay = 0;
    private int[] teamColors = {R.color.colorCorporateGrey, R.color.colorAlmostBlack};
    private int colorCounter = 0;

    public LeaderboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_leaderboard, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        parentActivity = (ParentActivity) getActivity();
        dataController = parentActivity.getDataController();
        apiManager = parentActivity.getApiManager();
        loader = parentActivity.findViewById(R.id.parentLoader);
        expListView = view.findViewById(R.id.teamExpandable);
        expListView.setGroupIndicator(null);
        initToggle();
        loader.setVisibility(View.VISIBLE);

        getLeaderboard(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
        initializeCalendarHandler();
    }

    private void initLeaderBoardImages(String profile) {
        if(profile != null) {
            apiManager.sendAsyncLeaderboardImage(this, dataController.getAgent().getAgent_id(), profile);
        }
    }

    private void initToggle() {
        leaderboardToggle = getView().findViewById(R.id.leaderboardToggle);
        leaderboardToggle.setOnCheckedChangeListener(this);
    }

    private void initializeCalendarHandler() {

//        datePicker.date(this);
        final ImageView calendarLauncher = getView().findViewById(R.id.leaderboard_calender_date_picker);
        final TextView dateDisplay = getView().findViewById(R.id.leaderboard_date);

        selectedYear = Calendar.getInstance().get(Calendar.YEAR);
        selectedMonth = Calendar.getInstance().get(Calendar.MONTH);
        selectedDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        updateDisplayDate(selectedYear, selectedMonth, selectedDay);

        calendarLauncher.setOnClickListener(this);
        dateDisplay.setOnClickListener(this);
    }

    private void updateDisplayDate(int year, int month, int day) {
        selectedYear = year;
        selectedMonth = month;
//        selectedDay = day;

        Date d;
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");

        if(leaderboardToggle.isChecked()) {
            //Year Selected
            sdf = new SimpleDateFormat("yyyy");
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        month += 1;
        String formatDate = year + "/" + month + "/" + day;

        try {
            d = formatter.parse(formatDate);
            Calendar updatedTime = Calendar.getInstance();
            updatedTime.setTime(d);

            TextView dateDisplay = getView().findViewById(R.id.leaderboard_date);
            dateDisplay.setText(sdf.format(updatedTime.getTime()));
        } catch (ParseException e) {
            parentActivity.showToast("Error parsing selected date");
            e.printStackTrace();
        }
    }

    private void getLeaderboard(int year, int month) {
        if(parentActivity.getSelectedTeamId() == -1) {
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loader.setVisibility(View.GONE);
                }
            });
        }
        else {
            loader.setVisibility(View.VISIBLE);
            listAdapter = null;
            expListView.setAdapter(listAdapter);

            String formattedYear = String.valueOf(year);
            String formattedMonth =  "";
            String formattedTeamId = String.valueOf(parentActivity.getSelectedTeamId());
            if(month != 0) {
                formattedMonth = String.valueOf(month);
            }
            if(leaderboardToggle.isChecked()) {
                //Year selected
                apiManager.sendAsyncLeaderboardYear(this, dataController.getAgent().getAgent_id(), formattedTeamId, formattedYear);
            }
            else {
                apiManager.sendAsyncLeaderboardYearAndMonth(this, dataController.getAgent().getAgent_id(), formattedTeamId, formattedYear, formattedMonth);
            }
        }

    }

    public void teamSwap() {
        listAdapter = null;
        expListView.setAdapter(listAdapter);
        getLeaderboard(selectedYear, selectedMonth);
    }

    private List<String> initSpinnerArray() {
        List<String> spinnerArray = new ArrayList<>();
//        spinnerArray.add("Today");
//        spinnerArray.add("Last Week");
//        spinnerArray.add("This Week");

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM");

        String thisMonth = sdf.format(calendar.getTime());

        calendar.add(Calendar.MONTH, -1);
        String lastMonth = sdf.format(calendar.getTime());
        spinnerArray.add(lastMonth);
        spinnerArray.add(thisMonth);

        calendar = Calendar.getInstance();
        sdf = new SimpleDateFormat("yyyy");
        String thisYear = sdf.format(calendar.getTime());

        calendar.add(Calendar.YEAR, -1);
        String lastYear = sdf.format(calendar.getTime());
        spinnerArray.add(lastYear);
        spinnerArray.add(thisYear);
//        spinnerArray.add("All Records");

        return spinnerArray;
    }

    private void prepareListData(LeaderboardObject[] leaderBoardSections) {
        listDataHeader = new ArrayList<LeaderboardObject>();
        listDataChild = new HashMap<LeaderboardObject, List<LeaderboardItemsObject>>();
        colorCounter = 0;

        for(int i = 0; i < leaderBoardSections.length; i++) {
            leaderBoardSections[i].setColor(teamColors[colorCounter]);
            listDataHeader.add(leaderBoardSections[i]);
            List<LeaderboardItemsObject> leaderboardItems = new ArrayList<>();
            for(int j = 0; j < leaderBoardSections[i].getLeaderboardItemsObject().length; j++) {
                if(!leaderBoardSections[i].getLeaderboardItemsObject()[j].getValue().equals("0")) {
                    leaderboardItems.add(leaderBoardSections[i].getLeaderboardItemsObject()[j]);
                    if(leaderBoardSections[i].getLeaderboardItemsObject()[j].getProfile() != null) {
                        if(parentActivity.getImage(leaderBoardSections[i].getLeaderboardItemsObject()[j].getProfile()) == null) {
                            initLeaderBoardImages(leaderBoardSections[i].getLeaderboardItemsObject()[j].getProfile());
                        }
                    }
                }
            }
            listDataChild.put(listDataHeader.get(i), leaderboardItems);
            if(colorCounter == teamColors.length - 1) {
                colorCounter = 0;
            }
            else {
                colorCounter++;
            }
        }
        //i THINK here is where we need to check the cache/run the async call for each profile
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listAdapter = new LeaderboardListExpandableAdapter(getContext(), listDataHeader, listDataChild, parentActivity);
                expListView.setAdapter(listAdapter);
            }
        });
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        if(asyncReturnType.equals("Leaderboard Image")) {
            final AsyncProfileImageJsonObject profileObject = (AsyncProfileImageJsonObject) returnObject;
            //cache image data
            byte[] decodeValue = Base64.decode(profileObject.getData(), Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(decodeValue,0,decodeValue.length);
            parentActivity.saveImage(bmp, profileObject.getFilename().toString());
            //set image data to leaderboard object item
        } else {
            AsyncLeaderboardJsonObject leaderboardJsonObject = (AsyncLeaderboardJsonObject) returnObject;
            LeaderboardObject[] leaderBoardSections = leaderboardJsonObject.getLeaderboardObject();
            prepareListData(leaderBoardSections);
        }
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loader.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onEventFailed(Object o, String s) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leaderboard_calender_date_picker:
            case R.id.leaderboard_date:
                new SpinnerDatePickerDialogBuilder()
                        .context(getContext())
                        .callback(this)
                        .spinnerTheme(android.R.style.Theme_Holo_Dialog)
                        .showTitle(false)
                        .defaultDate(selectedYear, selectedMonth, selectedDay)
                        .showDaySpinner(false)
                        .minDate(1990, 0, 1)
                        .build()
                        .show();
                break;
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        updateDisplayDate(selectedYear, selectedMonth, selectedDay);
        getLeaderboard(selectedYear, selectedMonth + 1);
    }

    @Override
    public void onDateSet(com.tsongkha.spinnerdatepicker.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if(year != selectedYear || monthOfYear != selectedMonth || dayOfMonth != selectedDay) {
            if(leaderboardToggle.isChecked() && monthOfYear != selectedMonth) {
                //TODO: Should this just toggle for them and search it? They obviously want to do that in this situation
                parentActivity.showToast("You're in year search mode. Swap to month search to change month selection.");
            }
            else {
                updateDisplayDate(year, monthOfYear, dayOfMonth);
                getLeaderboard(selectedYear, selectedMonth + 1);
            }
        }
        else {
            parentActivity.showToast("You have selected the same time period");
        }
    }
}
