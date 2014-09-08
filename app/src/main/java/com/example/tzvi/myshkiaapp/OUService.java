package com.example.tzvi.myshkiaapp;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by tzvi on 8/31/14.
 */
public class OUService extends IntentService {
    String shkia = "";
    Long milliTime;

    public OUService() {
        super("OUService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if(intent.getStringExtra(Intent.EXTRA_TEXT).equals(getString(R.string.alarm))){
            fetchData();
            Log.v("alarmManager","started up" );
            String message ="It is 15 minutes bfore shkia dont forget to daven mincha";
            publishResult(message);
        } else{
        String text = intent.getStringExtra(Intent.EXTRA_TEXT);
        Log.v("onHandle",text);
        fetchData();
        String message = "shkia is at";
        publishResult(message);
        parseTime();
        broadcastIntent();
        }
    }

    protected void parseTime(){
        SimpleDateFormat formatt = new SimpleDateFormat("HH:mm:ss");
        try {
            Date shkiaDate = formatt.parse(shkia);
             milliTime = shkiaDate.getTime();
            } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    protected void broadcastIntent(){
        Context context = getApplicationContext();
        long currentTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        long timeStartDay = calendar.getTime().getTime();
        long secondsOfDay = currentTime - timeStartDay;
        long alarmTime = milliTime-secondsOfDay;
        Intent i = new Intent(context,OUService.class);
        i.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.alarm));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, i, 0);
        // Schedule an alarm to start up the service every so often
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Long milliShkia = timeStartDay + alarmTime;
        Date shkiaDate = new Date(milliShkia);
        Log.v("shkia formatted" ,shkiaDate.toString());
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, 0, 15 * 1000/*AlarmManager.INTERVAL_FIFTEEN_MINUTES*/, pendingIntent);
    }

    protected void fetchData(){
        Date date = new Date();
        SimpleDateFormat sdf= new SimpleDateFormat("M/dd/yyyy");
        String fd = sdf.format(date);
        String result = "";
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
    }

    private void publishResult(String message){
    Log.v("publishResult",message);
        Notification.Builder nb = new Notification.Builder(this);
        nb.setSmallIcon(R.drawable.ic_launcher);
        nb.setContentTitle(message);
        nb.setContentText(shkia);
        Notification notification = nb.build();
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.notify(0, notification);
    }
}