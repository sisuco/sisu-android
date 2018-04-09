package co.sisu.mobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import co.sisu.mobile.api.AsyncAuthenticator;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.api.AsyncServerPing;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.AsyncAgentJsonObject;
import co.sisu.mobile.system.SaveSharedPreference;

/**
 * Created by Jeff on 3/7/2018.
 */

public class SplashScreenActivity extends AppCompatActivity implements AsyncServerEventListener {

    int WAIT_AMOUNT = 1000;
    static boolean loaded = false;
    private boolean pingRetry = false;
    private CountDownTimer cdt;
    Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaded = false;

        pingServer();

    }

    private void pingServer() {
        new AsyncServerPing(this).execute();
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        if(asyncReturnType.equals("Server Ping")) {
            if(SaveSharedPreference.getUserName(SplashScreenActivity.this).length() == 0) {
                intent = new Intent(this, MainActivity.class);
                launchActivity();
            }
            else {
                String userName = SaveSharedPreference.getUserName(SplashScreenActivity.this);
                String userPassword = SaveSharedPreference.getUserPassword(SplashScreenActivity.this);
                new AsyncAuthenticator(this, userName, userPassword).execute();
            }
        } else if(asyncReturnType.equals("Authenticator")) {
            AsyncAgentJsonObject agentObject = (AsyncAgentJsonObject) returnObject;
            AgentModel agent = agentObject.getAgent();
            intent = new Intent(this, ParentActivity.class);
            intent.putExtra("Agent", agent);
            launchActivity();
        }



    }

    private void launchActivity() {
        startActivity(intent);
        finish();
    }

    @Override
    public void onEventFailed() {
        Log.d("FAILED", "FAILED");
        if(!pingRetry) {
            pingServer();
        }
    }
}
