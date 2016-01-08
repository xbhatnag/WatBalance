package com.cg.watbalance.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;


public class NotificationAlarm {
    AlarmManager alarmMgr;
    PendingIntent alarmPendingIntent;
    Calendar calendar;
    Context context;
    Intent alarmIntent;

    public NotificationAlarm(Context newContext) {
        context = newContext;
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmPendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
    }

    public void setTime(int hour, int minute) {
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
    }

    public void startRepeatingAlarm() {
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmPendingIntent);
        Log.d("SERVICE", "STARTED");
    }

    public void cancelAlarm() {
        PendingIntent cancelIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.cancel(cancelIntent);
        Log.d("SERVICE", "STOPPED");
    }
}
