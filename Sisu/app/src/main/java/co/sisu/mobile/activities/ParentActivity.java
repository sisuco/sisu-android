package co.sisu.mobile.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.FileIO;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.controllers.NotificationReceiver;
import co.sisu.mobile.fragments.ErrorMessageFragment;
import co.sisu.mobile.fragments.LeaderboardFragment;
import co.sisu.mobile.fragments.MoreFragment;
import co.sisu.mobile.fragments.RecordFragment;
import co.sisu.mobile.fragments.ReportFragment;
import co.sisu.mobile.fragments.ScoreboardFragment;
import co.sisu.mobile.models.AgentGoalsObject;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.AsyncGoalsJsonObject;
import co.sisu.mobile.models.AsyncParameterJsonObject;
import co.sisu.mobile.models.AsyncSettingsJsonObject;
import co.sisu.mobile.models.AsyncUpdateActivitiesJsonObject;
import co.sisu.mobile.models.ClientObject;
import co.sisu.mobile.models.Metric;
import co.sisu.mobile.models.NotesObject;
import co.sisu.mobile.models.ParameterObject;
import co.sisu.mobile.models.TeamObject;
import co.sisu.mobile.models.UpdateActivitiesModel;

/**
 * Created by bradygroharing on 2/26/18.
 */

public class ParentActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AsyncServerEventListener {

    private DataController dataController;
    private NavigationManager navigationManager;
    private ApiManager apiManager;
    private ProgressBar parentLoader;
    private String currentSelectedRecordDate = "";
    private boolean clientFinished = false;
    private boolean goalsFinished = false;
    private boolean teamParamFinished = false;
    private boolean settingsFinished = false;
    private String timeline = "month";
    private int timelineSelection = 5;
    private AgentModel agent;
    private ErrorMessageFragment errorFragment;
    private FileIO io;
    private File internalStorageFile;
    private NotesObject selectedNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        dataController = new DataController();
        navigationManager = new NavigationManager(this);
        apiManager = new ApiManager();
        agent = getIntent().getParcelableExtra("Agent");
        dataController.setAgent(agent);
        errorFragment = new ErrorMessageFragment();
        parentLoader = findViewById(R.id.parentLoader);
        io = new FileIO(ParentActivity.this);

        initializeButtons();
        apiManager.sendAsyncTeams(this, agent.getAgent_id());
        apiManager.sendAsyncClients(this, agent.getAgent_id());
//        internalStorageFile = new File(this.getFilesDir(), "images");
//        File f = getDir("images", MODE_PRIVATE);
//        Log.e("f", String.valueOf(f));
//        for(String s : fileList()) {
//            Log.e("FILES", s);
//        }
//        testSMSObserver();
    }

//    private void testSMSObserver() {
//        Log.e("TURNING ON SMS", "THIS IS A TEST");
//        ContentResolver contentResolver = getContentResolver();
//        contentResolver.registerContentObserver(Uri.parse("content://sms"), true, new MySMSObserver(new Handler(), this));
//    }

    private void initializeButtons(){
        ImageView scoreBoardButton = findViewById(R.id.scoreboardView);
        scoreBoardButton.setOnClickListener(this);

        ImageView reportButton = findViewById(R.id.reportView);
        reportButton.setOnClickListener(this);

        ImageView recordButton = findViewById(R.id.recordView);
        recordButton.setOnClickListener(this);

        ImageView leaderBoardButton = findViewById(R.id.leaderBoardView);
        leaderBoardButton.setOnClickListener(this);

        ImageView moreButton = findViewById(R.id.moreView);
        moreButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(dataController.getUpdatedRecords().size() > 0) {
            updateRecordedActivities();
        }

        switch (v.getId()) {
            case R.id.action_bar_home:
                navigationManager.toggleDrawer();
                break;
            case R.id.scoreboardView:
                navigationManager.clearStackReplaceFragment(ScoreboardFragment.class);
                break;
            case R.id.reportView:
                navigationManager.clearStackReplaceFragment(ReportFragment.class);
                break;
            case R.id.recordView:
                navigationManager.clearStackReplaceFragment(RecordFragment.class);
                break;
            case R.id.leaderBoardView:
                navigationManager.clearStackReplaceFragment(LeaderboardFragment.class);
                break;
            case R.id.moreView:
                navigationManager.clearStackReplaceFragment(MoreFragment.class);
                break;
            case R.id.cancelButton:
                navigationManager.clearStackReplaceFragment(ScoreboardFragment.class);
            default:
                break;
        }
    }

    public void updateRecordedActivities() {
        List<Metric> updatedRecords = dataController.getUpdatedRecords();
        List<UpdateActivitiesModel> updateActivitiesModels = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        Date d = c.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        AsyncUpdateActivitiesJsonObject activitiesJsonObject = new AsyncUpdateActivitiesJsonObject();
        for(Metric m : updatedRecords) {
            if(currentSelectedRecordDate.equals("")) {
                updateActivitiesModels.add(new UpdateActivitiesModel(formatter.format(d), m.getType(), m.getCurrentNum(), Integer.valueOf(agent.getAgent_id())));
            }
            else {
                updateActivitiesModels.add(new UpdateActivitiesModel(currentSelectedRecordDate, m.getType(), m.getCurrentNum(), Integer.valueOf(agent.getAgent_id())));
            }
        }
        UpdateActivitiesModel[] array = new UpdateActivitiesModel[updateActivitiesModels.size()];
        updateActivitiesModels.toArray(array);

        activitiesJsonObject.setActivities(array);

        apiManager.sendAsyncUpdateActivities(this, agent.getAgent_id(), activitiesJsonObject);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //This is what goes off when you click a new team.
        TeamObject team = (TeamObject) parent.getItemAtPosition(position);
        navigationManager.updateTeam(team);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment f = fragmentManager.findFragmentById(R.id.your_placeholder);
        navigationManager.updateSelectedTeam(position);
        apiManager.getTeamParams(this, dataController.getAgent().getAgent_id(), team.getId());
        switch (f.getTag()) {
            case "Scoreboard":
                ((ScoreboardFragment) f).teamSwap();
                break;
            case "Record":
                ((RecordFragment) f).teamSwap();
                break;
            case "Report":
                ((ReportFragment) f).teamSwap();
                break;
            case "Leaderboard":
                ((LeaderboardFragment) f).teamSwap();
                break;
        }
        navigationManager.closeDrawer();
    }

    private void navigateToScoreboard() {
        if(clientFinished && goalsFinished && settingsFinished && teamParamFinished) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    navigationManager.clearStackReplaceFragment(ScoreboardFragment.class);
                }
            });
            clientFinished = false;
            goalsFinished = false;
            settingsFinished = false;
            teamParamFinished = false;
        }
    }

    @Override
    public void onBackPressed() {
        navigationManager.onBackPressed();
    }

    public void showToast(final CharSequence msg){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(ParentActivity.this, msg,Toast.LENGTH_SHORT);
                View view = toast.getView();
                TextView text = (TextView) view.findViewById(android.R.id.message);
                text.setTextColor(Color.WHITE);
                text.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorCorporateOrange));
                view.setBackgroundResource(R.color.colorCorporateOrange);
                text.setPadding(20, 8, 20, 8);
                toast.show();
            }
        });
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        if(asyncReturnType.equals("Teams")) {
            dataController.setTeamsObject(ParentActivity.this, returnObject);
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    navigationManager.initializeTeamBar(dataController.getTeamsObject());
                    apiManager.getTeamParams(ParentActivity.this, agent.getAgent_id(), dataController.getTeamsObject().get(0).getId());
                    apiManager.sendAsyncAgentGoals(ParentActivity.this, agent.getAgent_id());
                    apiManager.sendAsyncSettings(ParentActivity.this, agent.getAgent_id());
                }
            });
        }
        else if(asyncReturnType.equals("Goals")) {
            AsyncGoalsJsonObject goals = (AsyncGoalsJsonObject) returnObject;
            AgentGoalsObject[] agentGoalsObject = goals.getGoalsObjects();
            dataController.setAgentGoals(agentGoalsObject);
            goalsFinished = true;
            navigateToScoreboard();
        }
        else if(asyncReturnType.equals("Settings")) {
            AsyncSettingsJsonObject settingsJson = (AsyncSettingsJsonObject) returnObject;
            ParameterObject[] settings = settingsJson.getParameters();
            dataController.setSettings(settings); //sets settings, and fills with default alarm notification if empty/not set yet
            List<ParameterObject> newSettings = dataController.getSettings(); //this is the new settings object list including any defaults generated
            settingsFinished = true;
            int hour = 0;
            int minute = 0;
            for (ParameterObject s : newSettings) {
                Log.e(s.getName(), s.getValue());
                switch (s.getName()) {
                    case "daily_reminder_time":
                        String[] values = s.getValue().split(":");
                        hour = Integer.parseInt(values[0]);
                        minute = Integer.parseInt(values[1]);
                        Log.e("ALARM TIME", hour + " " + minute);
                        break;
                }
            }

            createNotificationAlarm(hour, minute, null); //sets the actual alarm with correct times from user settings
            navigateToScoreboard();
        }
        else if(asyncReturnType.equals("Team Parameters")) {
            AsyncParameterJsonObject settingsJson = (AsyncParameterJsonObject) returnObject;
            if(settingsJson.getStatus_code().equals("-1")) {
                dataController.setSlackInfo(null);
            }
            else {
                ParameterObject params = settingsJson.getParameter();
                dataController.setSlackInfo(params.getValue());
            }
            teamParamFinished = true;
            navigateToScoreboard();
        }
        else if(asyncReturnType.equals("Update Activities")) {
            dataController.clearUpdatedRecords();
        }
        else if(asyncReturnType.equals("Clients")) {
            dataController.setClientListObject(returnObject);
            clientFinished = true;
            navigateToScoreboard();
        }
    }

    public void createNotificationAlarm(int currentSelectedHour, int currentSelectedMinute, PendingIntent pendingIntent) {
        if(pendingIntent == null) {
            Intent myIntent = new Intent(this, NotificationReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this, 1412, myIntent, 0);
        }
        Calendar calendar = Calendar.getInstance();
        int interval = 1000 * 60 * 60 * 24; // One day

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.MINUTE, currentSelectedMinute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, currentSelectedHour);

        Log.e("CALENDAR SET", calendar.getTime().toString());
        Log.e("CALENDAR CURRENT TIME", Calendar.getInstance().getTime().toString());

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, pendingIntent);
    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {

        Log.e("FAILURE", asyncReturnType);
        errorFragment.setMessage(asyncReturnType + " cause this failure.");
        navigationManager.clearStackReplaceFragment(ErrorMessageFragment.class);

    }


    // GETTERS AND SETTERS

    public NotesObject getSelectedNote() {
        return selectedNote;
    }

    public void setSelectedNote(NotesObject selectedNote) {
        this.selectedNote = selectedNote;
    }

    public void setSelectedClient(ClientObject client) {
        navigationManager.setSelectedClient(client);
    }

    public ClientObject getSelectedClient() {
        return navigationManager.getSelectedClient();
    }

    public void updateSelectedRecordDate(String formattedDate) {
        this.currentSelectedRecordDate = formattedDate;
    }

    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
    }

    public int getTimelineSelection() {
        return timelineSelection;
    }

    public void setTimelineSelection(int timelineSelection) {
        this.timelineSelection = timelineSelection;
    }

    public int getSelectedTeamId() {
        int teamId = navigationManager.getSelectedTeamId();
        return teamId;
    }

    public NavigationManager getNavigationManager() {
        return navigationManager;
    }

    public DataController getDataController() {
        return dataController;
    }

    public ApiManager getApiManager() {
        return apiManager;
    }

    public Bitmap getImage(String profile) { return io.getImage(profile, 1024); }//size here should be cache size i think

    public void saveImage(byte[] image, String profile) { io.addImage(profile, image); }

    public boolean imageExists(Context context,  String id) {
        return "".equals(id) || context.getDir(id, Context.MODE_PRIVATE).exists();
    }
}
