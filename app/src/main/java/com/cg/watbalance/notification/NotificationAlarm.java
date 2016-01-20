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

    public void startRepeatingAlarm() {
        calendar = Calendar.getInstance();
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_HOUR, alarmPendingIntent);
        Log.d("SERVICE", "STARTED");
    }
}
