package com.cg.watbalance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cg.watbalance.data.ConnectionDetails;
import com.cg.watbalance.data.WatCardData;
import com.cg.watbalance.notification.NotificationAlarm;
import com.cg.watbalance.preferences.FileManager;
import com.cg.watbalance.preferences.Preferences;
import com.cg.watbalance.transaction.TransactionListActivity;
import com.cg.watbalance.transaction.TransactionListAdapter;

import org.jsoup.Jsoup;

import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;

public class mainScreen extends AppCompatActivity {
    //Variables
    FloatingActionButton myFAB;
    ImageView mySettingsIcon;
    Snackbar mySnackBar;
    SharedPreferences myPreferences;
    WatCardView myCardView;
    Connection myConn;
    ConnectionDetails myConnDet;
    NotificationAlarm myNotifAlarm;
    TextView openFullTranList;
    SharedPreferences.OnSharedPreferenceChangeListener onPrefChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balancescreen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Variable Declaration
        myCardView = new WatCardView();
        myPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        myConnDet = new ConnectionDetails(myPreferences.getString("IDNum", "00000000"), myPreferences.getString("pinNum", "0000"));
        myConn = new Connection(myConnDet);
        myFAB = (FloatingActionButton) findViewById(R.id.fab);
        mySettingsIcon = (ImageView) findViewById(R.id.settingsIcon);
        openFullTranList = (TextView) findViewById(R.id.openFullTranList);
        myNotifAlarm = new NotificationAlarm(getApplicationContext());
        onPrefChange = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                myConn.getData();
                NotificationServiceInitialize();
            }
        };

        // Check New User
        if (myPreferences.getString("IDNum", "00000000").equals("00000000")) {
            Intent myIntent = new Intent(getApplicationContext(), login.class);
            startActivity(myIntent);
        } else {
            // Reads Last Data Written to File
            FileManager myFM = new FileManager(getApplicationContext());
            myFM.openFileInput("lastData");
            WatCardData myData = myFM.readData();
            myFM.closeFileInput();
            myCardView.updateNameView(myData);
            myCardView.updateBalanceView(myData);
            myCardView.updateTransView(myData);
            myCardView.updateDailyBalanceView(myData);
        }

        // Refresh Button Action
        myFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myConn.getData();
            }
        });

        // Edit Card Action
        mySettingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), Preferences.class);
                startActivity(myIntent);
            }
        });

        openFullTranList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), TransactionListActivity.class);
                startActivity(myIntent);
            }
        });

        //Preference Change Refresh
        myPreferences.registerOnSharedPreferenceChangeListener(onPrefChange);

        //Initialize Alarm Notification
        NotificationServiceInitialize();
    }

    public void NotificationServiceInitialize() {
        if (myPreferences.getBoolean("dailyNotification", true)) {
            String[] stringTime = myPreferences.getString("notificationTime", "07:00").split(":");
            myNotifAlarm.setTime(Integer.parseInt(stringTime[0]), Integer.parseInt(stringTime[1]));
            myNotifAlarm.startRepeatingAlarm();
        } else {
            myNotifAlarm.cancelAlarm();
        }
    }

    public class WatCardView {
        private TextView name, idText, total, mp, fd, other, date, dailyBalance, todaySpent, dailyLeft;
        private PieChartView pieChart;
        private ListView tranListView;
        private LineChartView transChart;

        public WatCardView() {
            name = (TextView) findViewById(R.id.Name);
            idText = (TextView) findViewById(R.id.IDText);

            total = (TextView) findViewById(R.id.Total);
            mp = (TextView) findViewById(R.id.mealPlanData);
            fd = (TextView) findViewById(R.id.flexDollarsData);
            other = (TextView) findViewById(R.id.otherData);
            date = (TextView) findViewById(R.id.date);

            dailyBalance = (TextView) findViewById(R.id.dayBalance);
            todaySpent = (TextView) findViewById(R.id.todaySpent);
            dailyLeft = (TextView) findViewById(R.id.dailyLeft);

            pieChart = (PieChartView) findViewById(R.id.pieChart);
            transChart = (LineChartView) findViewById(R.id.transChart);

            tranListView = (ListView) findViewById(R.id.listView);
        }

        public void updateNameView(WatCardData myData) {
            if (myData != null) {
                name.setText(myData.getFirstName());
                idText.setText(myConnDet.getIDString());
            }
        }

        public void updateBalanceView(WatCardData myData) {
            if (myData != null) {
                total.setText(myData.getTotalString());
                mp.setText(myData.getMPString());
                fd.setText(myData.getFDString());
                other.setText(myData.getOtherString());
                date.setText(myData.getDateString());

                pieChart.setInteractive(false);
                pieChart.setPieChartData(myData.makePieChartData());
            }
        }

        public void updateDailyBalanceView(WatCardData myData) {
            if (myData != null) {
                dailyBalance.setText(myData.getDailyBalanceString());
                todaySpent.setText(myData.getTodaySpentString());
                dailyLeft.setText(myData.getDailyLeftString());
            }
        }

        public void updateTransView(WatCardData myData) {
            if (myData != null) {
                tranListView.setAdapter(new TransactionListAdapter(getApplicationContext(), myData.getTransHistory()));
                if (myData.getTransHistory().size() <= 3) {
                    View transLine = findViewById(R.id.transLine);
                    openFullTranList.setVisibility(View.GONE);
                    transLine.setVisibility(View.GONE);
                }

                if (myData.getTransHistory().size() != 0) {
                    transChart.setLineChartData(myData.makeTransChartData());
                    transChart.setValueSelectionEnabled(true);
                    transChart.setZoomEnabled(false);
                } else {
                    transChart.setLineChartData(null);
                }
            }
        }

    }

    public class Connection {
        ConnectionDetails myConnDetails;
        RequestQueue queue;
        WatCardData myData;

        public Connection(ConnectionDetails newConnDetails) {
            myConnDetails = newConnDetails;
            queue = Volley.newRequestQueue(getApplicationContext());
            myData = new WatCardData();
        }

        public void getData() {
            Log.d("CONNECTION", "ESTABLISH");
            // Request a string response from the provided URL.
            mySnackBar = Snackbar.make(findViewById(R.id.FullWindow), "Refreshing...", Snackbar.LENGTH_INDEFINITE);
            mySnackBar.show();

            // Add the request to the RequestQueue.
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
                                myCardView.updateNameView(myData);
                                myCardView.updateBalanceView(myData);

                                if (myData.complete()) {
                                    myData.setDailyBalance();
                                    myCardView.updateDailyBalanceView(myData);

                                    FileManager myFM = new FileManager(getApplicationContext());
                                    myFM.openFileOutput("lastData");
                                    myFM.writeData(myData);
                                    myFM.closeFileOutput();
                                }
                            } else {
                                mySnackBar.dismiss();
                                mySnackBar = Snackbar.make(findViewById(R.id.FullWindow), "Incorrect Login Information", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("CHANGE", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent myIntent = new Intent(getApplicationContext(), login.class);
                                                startActivity(myIntent);
                                            }
                                        });
                                mySnackBar.show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mySnackBar.dismiss();
                    mySnackBar = Snackbar.make(findViewById(R.id.FullWindow), "Connection Error", Snackbar.LENGTH_SHORT);
                    mySnackBar.show();
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
                                myCardView.updateTransView(myData);
                                mySnackBar.dismiss();

                                if (myData.complete()) {
                                    myData.setDailyBalance();
                                    myCardView.updateDailyBalanceView(myData);

                                    FileManager myFM = new FileManager(getApplicationContext());
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


