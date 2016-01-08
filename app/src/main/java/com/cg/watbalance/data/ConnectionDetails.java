package com.cg.watbalance.data;

import java.util.Calendar;
import java.util.Date;

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
        Date myDate = new Date();
        Calendar myCal = Calendar.getInstance();
        myCal.setTime(myDate);
        int month = myCal.get(Calendar.MONTH) + 1;
        int lastDayOfMonth = myCal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int year = myCal.get(Calendar.YEAR);

        return uWaterlooURL + "acnt_1=" + myIDNum + "&acnt_2=" + myPinNum + "&DBDATE=" + month + "%2F1%2F" + year + "&DEDATE=" + month + "%2F" + lastDayOfMonth + "%2F" + year + "&PASS=PASS&STATUS=HIST";
    }

    public String getIDString() {
        return "ID# " + myIDNum;
    }
}