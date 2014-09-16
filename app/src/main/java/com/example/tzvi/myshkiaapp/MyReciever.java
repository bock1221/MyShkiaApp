package com.example.tzvi.myshkiaapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by tzvi on 9/2/14.
 */
public class MyReciever extends BroadcastReceiver {
    public static final String PROCESS_RESULT_ACTION = "com.example.tzvi.PROCESS_RESULT" ;
    public static final String START_SERVICE_ACTION = "com.example.tzvi.START_SERVICE";
    public static final String START_SERVICE_ALARM_ACTION = "com.example.tzvi.START_SERVICE_ALARM";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("testing", "in onRecieve");
        Date now = new Date();
        SimpleDateFormat sdf= new SimpleDateFormat("MM/dd/yyyy");
        String fd = sdf.format(now);
        if(intent.getAction().equals("REBOOT")) {
            startService(context);
             }
        if(intent.getAction().equals(START_SERVICE_ALARM_ACTION)){
            startService(context);
        }
        }

    protected void startService(Context context){
        Intent i = new Intent(context,OUService.class);
        i.putExtra(Intent.EXTRA_TEXT, "reboot");
        context.startService(i);
    }
    }

