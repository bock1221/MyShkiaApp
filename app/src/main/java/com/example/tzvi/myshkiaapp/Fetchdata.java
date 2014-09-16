package com.example.tzvi.myshkiaapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by tzvi on 8/31/14.
 */
public class Fetchdata  extends AsyncTask<String,Void,String> {
        Context context;

    public Fetchdata(Context context){this.context =context; }
    @Override
    protected String doInBackground(String... params) {

        SimpleDateFormat sdf= new SimpleDateFormat("MM/dd/yyyy");
        String fd = sdf.format(new Date());
        String result = "";
        String shkia = "";
        try {
            URL url = new URL( "http://db.ou.org/zmanim/getCalendarData.php?" +
                    "mode=day&timezone=America/New_York&dateBegin=" +
                    fd+"&lat=40.590034&lng=-73.940101");
            HttpURLConnection connection = null;
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line = null;
            while ((line = reader.readLine()) != null) {
                result += line;
            }
            JSONObject json = new JSONObject(result);
            Log.v("jsonarray", json.toString());
            try {
                JSONObject item = json.getJSONObject("zmanim");
                shkia = item.getString("sunset");
                Log.v("shkia",shkia);

            } catch (JSONException e1) {
                e1.printStackTrace();
            }
           }
     catch (IOException e) {
        e.printStackTrace();
    } catch (JSONException e) {
        e.printStackTrace();
    }
    return shkia ;

}
    protected void onPostExecute(String result) {
        Log.v("publishResult",result);
        Notification.Builder nb = new Notification.Builder(context);
        nb.setSmallIcon(R.drawable.ic_launcher);
        nb.setContentTitle("This is a test");
        nb.setContentText(result);
        Notification notification = nb.build();
        NotificationManager  nm = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        nm.notify(0, notification);

    }
}
