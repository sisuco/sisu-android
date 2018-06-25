package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import co.sisu.mobile.models.AsyncProfileImageJsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Jeff on 6/20/2018.
 */

public class AsyncLeaderboardImage extends AsyncTask<String, String, String> {
    private AsyncServerEventListener callback;
    private String profile;

    public AsyncLeaderboardImage(AsyncServerEventListener cb, String profile) {
        callback = cb;
        this.profile = profile;
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
                .url("https://api.sisu.co/api/v1/image/" + profile)
                .get()
                .addHeader("Authorization", strings[0])
                .addHeader("Client-Timestamp", strings[1])
                .addHeader("Transaction-Id", strings[2])
                .build();
        try {
            response = client.newCall(request).execute();
//            Log.e("PROFILE PIC", response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(response != null) {
            if(response.code() == 200) {
                AsyncProfileImageJsonObject profileObject = new AsyncProfileImageJsonObject();
                    profileObject.setData(String.valueOf(response.body().charStream()));
                Log.e("Profile", profile);
                    profileObject.setFilename(profile);
                callback.onEventCompleted(profileObject, "Leaderboard Image");
            }
            else {
                callback.onEventFailed(null, "Leaderboard Image");
            }
        }
        else {
            callback.onEventFailed(null, "Leaderboard Image");
        }

        response.body().close();
        return null;
    }
}

