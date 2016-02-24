package com.cg.watbalance.preferences;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cg.watbalance.data.BalanceData;
import com.cg.watbalance.data.transaction.TransactionData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public abstract class Connection {
    ConnectionDetails myConnDetails;
    RequestQueue queue;
    Context myContext;

    BalanceData myBalData;
    TransactionData myTransData;

    public Connection(ConnectionDetails newConnDetails, Context context) {
        myContext = context;
        myConnDetails = newConnDetails;
        queue = Volley.newRequestQueue(context);
    }

    public void getData() {
        Log.d("CONNECTION", "ESTABLISHED");

        beforeConnect();

        // Add the request to the RequestQueue.
        queue.add(createBalanceRequest());
        queue.add(createTransHistoryRequest());
    }

    private StringRequest createBalanceRequest() {
        return new StringRequest(Request.Method.GET, myConnDetails.getBalanceURL(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.contains("The Account or PIN code is incorrect!")) {
                            Document myDoc = Jsoup.parse(response);
                            onResponseReceive(myDoc);

                            myBalData = new BalanceData();
                            myBalData.setBalanceData(myDoc);
                            onDataReceive();
                        } else {
                            onIncorrectLogin();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onConnectionError();
            }
        });
    }


    private StringRequest createTransHistoryRequest() {
        return new StringRequest(Request.Method.GET, myConnDetails.getTransactionURL(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.contains("The Account or PIN code is incorrect!")) {
                            myTransData = new TransactionData();
                            myTransData.setTransList(Jsoup.parse(response));
                            onDataReceive();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }

    private StringRequest createMenuRequest() {
        return new StringRequest(Request.Method.GET, myConnDetails.getFoodURL(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }

    private StringRequest createOutletRequest() {
        return new StringRequest(Request.Method.GET, myConnDetails.getFoodURL(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }

    private StringRequest createBuildingRequest() {
        return new StringRequest(Request.Method.GET, myConnDetails.getBuildingURL(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }

    private void onDataReceive() {
        if (myBalData != null && myTransData != null) {
            myBalData.setDailyBalance(myTransData);

            FileManager myFM = new FileManager(myContext);
            myFM.openFileOutput("myBalData");
            myFM.writeData(myBalData);
            myFM.closeFileOutput();

            myFM.openFileOutput("myTransData");
            myFM.writeData(myTransData);
            myFM.closeFileOutput();

            onComplete();
        }
    }

    public abstract void onComplete();

    public abstract void onResponseReceive(Document myDoc);

    public abstract void beforeConnect();

    public abstract void onConnectionError();

    public abstract void onIncorrectLogin();
}

