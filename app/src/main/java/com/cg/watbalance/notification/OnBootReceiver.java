package com.cg.watbalance.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (myPreferences.getBoolean("dailyNotification", true)) {
                NotificationAlarm myNotifAlarm = new NotificationAlarm(context);
                String[] stringTime = myPreferences.getString("notificationTime", "07:00").split(":");
                myNotifAlarm.setTime(Integer.parseInt(stringTime[0]), Integer.parseInt(stringTime[1]));
                myNotifAlarm.startRepeatingAlarm();
            }
        }
    }
}
