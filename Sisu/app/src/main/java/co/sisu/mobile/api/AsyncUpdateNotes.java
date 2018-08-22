package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by bradygroharing on 4/25/18.
 */

public class AsyncUpdateNotes extends AsyncTask<String, String, String> {

    private AsyncServerEventListener callback;
    private String noteId;
    private String url;
    private String note;
    private String noteType;

    public AsyncUpdateNotes(AsyncServerEventListener cb, String url, String noteId, String note, String noteType) {
        callback = cb;
        this.noteId = noteId;
        this.note = note;
        this.url = url;
        this.noteType = noteType;
    }

    @Override
    protected String doInBackground(String... strings) {

        try {
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            String jsonInString = "{\"log_type_id\":\"" + noteType + "\", \"note\":\"" + note + "\"}";
            Log.e("POST NOTES", jsonInString);
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonInString);

            Request request = new Request.Builder()
                    .url(url + "api/v1/client/logs/" + noteId)
                    .put(body)
                    .addHeader("Authorization", strings[0])
                    .addHeader("Client-Timestamp", strings[1])
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Transaction-Id", strings[2])
                    .build();

            try {
                response = client.newCall(request).execute();
//                Log.e("UPDATE NOTES", response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null) {
                if (response.code() == 200) {
                    callback.onEventCompleted(null, "Update Notes");
                } else {
                    callback.onEventFailed(null, "Update Notes");
                }
            } else {
                callback.onEventFailed(null, "Update Notes");
            }

            response.body().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}