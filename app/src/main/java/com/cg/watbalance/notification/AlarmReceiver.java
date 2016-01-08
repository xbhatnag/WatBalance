package com.cg.watbalance.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cg.watbalance.R;
import com.cg.watbalance.data.ConnectionDetails;
import com.cg.watbalance.data.WatCardData;
import com.cg.watbalance.mainScreen;
import com.cg.watbalance.preferences.FileManager;

import org.jsoup.Jsoup;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("SERVICE", "TRIGGERED");
        SharedPreferences myLoginPref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        ConnectionDetails myConnDet = new ConnectionDetails(myLoginPref.getString("IDNum", "00000000"), myLoginPref.getString("pinNum", "0000"));
        Connection myConn = new Connection(myConnDet, context);
        myConn.getData();
    }


    public class Connection {
        ConnectionDetails myConnDetails;
        RequestQueue queue;
        Context myContext;
        WatCardData myData;

        public Connection(ConnectionDetails newConnDetails, Context context) {
            myContext = context;
            myConnDetails = newConnDetails;
            queue = Volley.newRequestQueue(myContext);
            myData = new WatCardData();
        }

        public void getData() {
            queue.add(createBalanceRequest());
            queue.add(createTransHistoryRequest());
        }

        public StringRequest createBalanceRequest() {
            return new StringRequest(Request.Method.GET, myConnDetails.getBalanceURL(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (!response.contains("The Account or PIN code is incorrect!")) {
                                myData.getBalanceData(Jsoup.parse(response));
                                NotificationManager notificationManager = (NotificationManager) myContext.getSystemService(Context
                                        .NOTIFICATION_SERVICE);

                                PendingIntent openApp = PendingIntent.getActivity(myContext, 0, new Intent(myContext, mainScreen.class), 0);

                                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(myContext.getApplicationContext())
                                        .setSmallIcon(R.drawable.ic_local_atm_24dp)
                                        .setContentTitle("WatBalance")
                                        .setContentText("You have " + myData.getTotalString() + " in your WatCard")
                                        .setContentIntent(openApp)
                                        .setVisibility(NotificationCompat.VISIBILITY_PRIVATE);

                                notificationManager.notify(1, notificationBuilder.build());

                                if (myData.complete()) {
                                    FileManager myFM = new FileManager(myContext);
                                    myFM.openFileOutput("lastData");
                                    myFM.writeData(myData);
                                    myFM.closeFileOutput();
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
        }

        public StringRequest createTransHistoryRequest() {
            return new StringRequest(Request.Method.GET, myConnDetails.getTransactionURL(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (!response.contains("The Account or PIN code is incorrect!")) {
                                myData.setTransHistory(Jsoup.parse(response));

                                if (myData.complete()) {
                                    FileManager myFM = new FileManager(myContext);
                                    myFM.openFileOutput("lastData");
                                    myFM.writeData(myData);
                                    myFM.closeFileOutput();
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
        }

    }


}
