package com.cg.watbalance.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;

public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (myPreferences.getBoolean("dailyNotification", true)) {
                startRepeat(context);
            }
        }
    }

    public void startRepeat(Context context) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent newPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, Service.class), 0);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(),
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, newPendingIntent);
    }

}
