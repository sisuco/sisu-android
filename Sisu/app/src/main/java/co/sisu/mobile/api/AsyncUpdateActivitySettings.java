package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import co.sisu.mobile.models.AsyncUpdateSettingsJsonObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Brady Groharing on 4/29/2018.
 */

public class AsyncUpdateActivitySettings extends AsyncTask<Void, Void, Void> {

    private AsyncServerEventListener callback;
    AsyncUpdateSettingsJsonObject updateSettingsModel;

    public AsyncUpdateActivitySettings(AsyncServerEventListener cb, AsyncUpdateSettingsJsonObject updateSettingsModel) {
        callback = cb;
        this.updateSettingsModel = updateSettingsModel;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            Gson gson = new Gson();
            String jsonInString = gson.toJson(updateSettingsModel);
            Log.e("POST ACTIVITY SETTINGS", jsonInString);

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonInString);

            Request request = new Request.Builder()
                    .url("http://staging.sisu.co/api/parameter/edit-parameter")
                    .put(body)
                    .addHeader("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJDbGllbnQtVGltZXN0YW1wIjoiMTUyMDk5OTA5NSIsImlzcyI6InNpc3UtaW9zOjk1YmI5ZDkxLWZlMDctNGZhZi1hYzIzLTIxOTFlMGQ1Y2RlNiIsImlhdCI6MTUyMDk5OTA5NS4xMTQ2OTc5LCJleHAiOjE1Mjg3NzUwOTUuMTE1OTEyLCJUcmFuc2FjdGlvbi1JZCI6IkU5NThEQzAyLThGNjEtNEU5Ny05MEI3LUYyNjZEQ0M1OTdFOSJ9.bFQhBCgnsujtl3PndALtAL8rcqFpm3rn5quqoXak0Hg")
                    .addHeader("Client-Timestamp", "1520999095")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", "E958DC02-8F61-4E97-90B7-F266DCC597E9")
                    .build();

            try {
                response = client.newCall(request).execute();
//                Log.e("UPDATE ACTIVITY SETTINGS", response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null) {
                if (response.code() == 200) {
                    callback.onEventCompleted(null, "Update Settings");
                } else {
                    callback.onEventFailed(null, "Server Ping");
                }
            } else {
                callback.onEventFailed(null, "Server Ping");
            }

            response.body().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}