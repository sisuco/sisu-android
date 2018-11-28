package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import co.sisu.mobile.models.AsyncUpdateActivitiesJsonObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by bradygroharing on 4/17/18.
 */

public class AsyncUpdateActivities extends AsyncTask<String, String, String> {

    private AsyncServerEventListener callback;
    private String agentId;
    private AsyncUpdateActivitiesJsonObject updateActivitiesModels;
    private String url;
    private int marketId;

    public AsyncUpdateActivities(AsyncServerEventListener cb, String url, String agentId, AsyncUpdateActivitiesJsonObject updateActivitiesModels, int marketId) {
        callback = cb;
        this.agentId = agentId;
        this.updateActivitiesModels = updateActivitiesModels;
        this.url = url;
        this.marketId = marketId;
    }

    @Override
    protected String doInBackground(String... strings) {

        try {
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            Gson gson = new Gson();
            String jsonInString = gson.toJson(updateActivitiesModels);
            Log.e("POST ACTIVITY", jsonInString);

            MediaType mediaType = MediaType.parse("application/json");
//            startDate = "2017-02-01";
//            endDate = "2018-10-05";
            RequestBody body = RequestBody.create(mediaType, jsonInString);

            Request request = new Request.Builder()
                    .url(url + "api/v1/agent/activity/" + agentId + "/" + marketId)
                    .put(body)
                    .addHeader("Authorization", strings[0])
                    .addHeader("Client-Timestamp", strings[1])
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", strings[2])
                    .build();

            try {
                response = client.newCall(request).execute();
//                Log.e("UPDATE ACTIVITIES", response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null) {
                if (response.code() == 200) {
                    callback.onEventCompleted(null, "Update Activities");
                } else {
                    callback.onEventFailed(null, "Update Activities");
                }
            } else {
                callback.onEventFailed(null, "Update Activities");
            }

            response.body().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
