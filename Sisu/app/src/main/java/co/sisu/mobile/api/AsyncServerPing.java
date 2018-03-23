package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Brady Groharing on 3/21/2018.
 */

public class AsyncServerPing extends AsyncTask<Void, Void, Void> {
    private AsyncPingEventListener callback;

    public AsyncServerPing (AsyncPingEventListener cb) {
       callback = cb;
    }

//    @Override
//    protected void onPostExecute(Integer result) {
//        if(callback != null) {
//            callback.onEventCompleted();
//        }
//    }

    @Override
    protected Void doInBackground(Void... voids) {
        Response response = null;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://staging.sisu.co/api/ping")
                .get()
                .addHeader("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJDbGllbnQtVGltZXN0YW1wIjoiMTUyMDk5OTA5NSIsImlzcyI6InNpc3UtaW9zOjk1YmI5ZDkxLWZlMDctNGZhZi1hYzIzLTIxOTFlMGQ1Y2RlNiIsImlhdCI6MTUyMDk5OTA5NS4xMTQ2OTc5LCJleHAiOjE1Mjg3NzUwOTUuMTE1OTEyLCJUcmFuc2FjdGlvbi1JZCI6IkU5NThEQzAyLThGNjEtNEU5Ny05MEI3LUYyNjZEQ0M1OTdFOSJ9.bFQhBCgnsujtl3PndALtAL8rcqFpm3rn5quqoXak0Hg")
                .addHeader("Client-Timestamp", "1520999095")
                .addHeader("Transaction-Id", "E958DC02-8F61-4E97-90B7-F266DCC597E9")
                .build();
        try {
            response = client.newCall(request).execute();
            Log.d("ASYNC", response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(response.code() == 200) {
            callback.onEventCompleted();
        }
        else {
            callback.onEventFailed();
        }
        return null;
    }
}
