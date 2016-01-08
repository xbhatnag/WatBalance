package com.cg.watbalance.data;

import org.joda.time.DateTime;

public class ConnectionDetails {
    final String uWaterlooURL = "https://account.watcard.uwaterloo.ca/cgi-bin/OneWeb.exe?";
    private String myIDNum = null;
    private String myPinNum = null;
    private String myBalanceURL;

    public ConnectionDetails(String newIDNum, String newPinNum) {
        myIDNum = newIDNum;
        myPinNum = newPinNum;
        myBalanceURL = uWaterlooURL + "acnt_1=" + myIDNum + "&acnt_2=" + myPinNum + "&FINDATAREP=ON&STATUS=STATUS";
    }

    public String getBalanceURL() {
        return myBalanceURL;
    }

    public String getTransactionURL() {
        DateTime myDate = DateTime.now();
        int month = myDate.getMonthOfYear();
        int lastDayOfMonth = myDate.dayOfMonth().getMaximumValue();
        int year = myDate.getYear();

        return uWaterlooURL + "acnt_1=" + myIDNum + "&acnt_2=" + myPinNum + "&DBDATE=" + month + "%2F1%2F" + year + "&DEDATE=" + month + "%2F" + lastDayOfMonth + "%2F" + year + "&PASS=PASS&STATUS=HIST";
    }

    public String getIDString() {
        return "ID# " + myIDNum;
    }
}