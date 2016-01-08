package com.cg.watbalance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cg.watbalance.data.ConnectionDetails;
import com.cg.watbalance.data.WatCardData;
import com.cg.watbalance.preferences.FileManager;

import org.jsoup.Jsoup;

public class login extends AppCompatActivity {

    EditText IDNum;
    EditText pinNum;
    Connection myConn;
    ConnectionDetails myConnDet;
    Button mySaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Variable Declaration
        IDNum = (EditText) findViewById(R.id.IDNum);
        pinNum = (EditText) findViewById(R.id.pinNum);
        mySaveButton = (Button) findViewById(R.id.button);

        mySaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myConnDet = new ConnectionDetails(IDNum.getText().toString(), pinNum.getText().toString());
                myConn = new Connection(myConnDet);
                myConn.getData();
            }
        });
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

                                if (myData.complete()) {
                                    Log.d("Context", getApplicationContext().getFilesDir().toString());
                                    FileManager myFM = new FileManager(getApplicationContext());
                                    myFM.openFileOutput("lastData");
                                    myFM.writeData(myData);
                                    myFM.closeFileOutput();

                                    Intent myIntent = new Intent(login.this, mainScreen.class);
                                    startActivity(myIntent);
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
                            if (response.contains("The Account or PIN code is incorrect!")) {
                                Toast.makeText(getApplicationContext(), "Incorrect Login Information", Toast.LENGTH_LONG).show();
                            } else {
                                myData.setTransHistory(Jsoup.parse(response));

                                SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString("IDNum", IDNum.getText().toString());
                                editor.putString("pinNum", pinNum.getText().toString());
                                editor.apply();

                                if (myData.complete()) {
                                    Log.d("Context", getApplicationContext().getFilesDir().toString());
                                    FileManager myFM = new FileManager(getApplicationContext());
                                    myFM.openFileOutput("lastData");
                                    myFM.writeData(myData);
                                    myFM.closeFileOutput();

                                    Intent myIntent = new Intent(login.this, mainScreen.class);
                                    startActivity(myIntent);
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


}
