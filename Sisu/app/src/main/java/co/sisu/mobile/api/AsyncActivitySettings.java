package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import co.sisu.mobile.models.AsyncActivitySettingsJsonObject;
import co.sisu.mobile.models.AsyncSettingsJsonObject;
import co.sisu.mobile.models.JWTObject;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Brady Groharing on 4/29/2018.
 */

public class AsyncActivitySettings extends AsyncTask<Void, Void, Void> {
    private String secretKey = "33SnhbgJaXFp6fYYd1Ru";

    private AsyncServerEventListener callback;
    private String agentId;
//    private JWTObject jwtObject;

    public AsyncActivitySettings(AsyncServerEventListener cb, String agentId, JWTObject jwtObject) {
        callback = cb;
        this.agentId = agentId;
//        this.jwtObject = jwtObject;
    }

    public String getJWT(String transactionID, Calendar time, String timestamp, Calendar expTime) {

        String jwtStr = Jwts.builder()
                .claim("Client-Timestamp", timestamp)
                .setIssuer("sisu-android:8c535552-bf1f-4e46-bd70-ea5cb71fef4d")
                .setIssuedAt(time.getTime())
                .setExpiration(expTime.getTime())
//                .claim("iat", time)
//                .claim("exp", expTime)
                .claim("Transaction-Id", transactionID)
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();

        return jwtStr;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String transactionID = UUID.randomUUID().toString();
        Calendar date = Calendar.getInstance();
        date.add(Calendar.SECOND, -60);
        String timestamp = String.valueOf(date.getTimeInMillis());

        Calendar expDate = Calendar.getInstance();
        expDate.add(Calendar.DATE, 1);

        String jwt = getJWT(transactionID, date, timestamp, expDate);

        Response response = null;
        Gson gson = new Gson();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url("https://api.sisu.co/api/v1/parameter/edit-parameter/2/"+ agentId +"/record_activities")
                .get()
                .addHeader("Authorization", jwt)
                .addHeader("Client-Timestamp", timestamp)
                .addHeader("Transaction-Id", transactionID)
                .build();

        String responseBody = "";
        try {
            response = client.newCall(request).execute();
            responseBody = response.body().string();
            Log.e("SETTINGS", responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(response != null) {
            if(response.code() == 200) {
                AsyncActivitySettingsJsonObject settings = gson.fromJson(responseBody, AsyncActivitySettingsJsonObject.class);
                callback.onEventCompleted(settings, "Activity Settings");
            }
            else {
                callback.onEventFailed(null, "Activity Settings");
            }
        }
        else {
            callback.onEventFailed(null, "Activity Settings");
        }

//        Log.d("ASYNC PING IS", "NULL");
//        response.body().close();
        return null;
    }
}
