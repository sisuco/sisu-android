package co.sisu.mobile.api;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import co.sisu.mobile.models.AsyncParameterJsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Brady Groharing on 4/29/2018.
 */

public class AsyncTeamParameters extends AsyncTask<String, String, String> {
    private AsyncServerEventListener callback;
    private int teamId;
    private String url;

    public AsyncTeamParameters(AsyncServerEventListener cb, String url, int teamId) {
        callback = cb;
        this.teamId = teamId;
        this.url = url;
    }

    @Override
    protected String doInBackground(String... strings) {

        Response response = null;
        Gson gson = new Gson();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(url + "api/v1/parameter/edit-parameter/1/"+ teamId +"/slack_url")
                .get()
                .addHeader("Authorization", strings[0])
                .addHeader("Client-Timestamp", strings[1])
                .addHeader("Transaction-Id", strings[2])
                .build();

        String responseBody = "";
        try {
            response = client.newCall(request).execute();
            responseBody = response.body().string();
//            Log.e("TEAM PARAMS", responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(response != null) {
            if(response.code() == 200) {
                AsyncParameterJsonObject settings = gson.fromJson(responseBody, AsyncParameterJsonObject.class);
                callback.onEventCompleted(settings, "Team Parameters");
            }
            else {
                callback.onEventFailed(null, "Team Parameters");
            }
        }
        else {
            callback.onEventFailed(null, "Team Parameters");
        }

//        Log.d("ASYNC PING IS", "NULL");
//        response.body().close();
        return null;
    }
}