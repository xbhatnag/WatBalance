package com.cg.watbalance.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;

import com.cg.watbalance.data.transaction.TransactionData;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class BalanceData implements Serializable {
    private float MP = 0;
    private float FD = 0;
    private float Other = 0;
    private float Total = 0;
    private float dailyBalance = 0;
    private float todaySpent = 0;
    private DateTime Date;

    public void setBalanceData(Document myDoc) {
        Elements myTDTags = myDoc.getElementsByTag("TD");
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.CANADA);
        try {
            MP = numberFormat.parse(myTDTags.get(7).text()).floatValue() + numberFormat.parse(myTDTags.get(14).text()).floatValue() + numberFormat.parse(myTDTags.get(21).text()).floatValue() + numberFormat.parse(myTDTags.get(49).text()).floatValue();
            FD = numberFormat.parse(myTDTags.get(42).text()).floatValue() + numberFormat.parse(myTDTags.get(35).text()).floatValue() + numberFormat.parse(myTDTags.get(28).text()).floatValue();
            Other = numberFormat.parse(myTDTags.get(56).text()).floatValue() + numberFormat.parse(myTDTags.get(63).text()).floatValue() + numberFormat.parse(myTDTags.get(70).text()).floatValue() + numberFormat.parse(myTDTags.get(77).text()).floatValue() + numberFormat.parse(myTDTags.get(84).text()).floatValue();
            Total = numberFormat.parse(myTDTags.get(91).text().substring(2)).floatValue();
            Date = DateTime.now();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDailyBalance(TransactionData myTransData, Context context) {
        todaySpent = 0;
        ArrayList<TransactionData.Transaction> myTransList = myTransData.getTransList();

        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int DailyBalConfig = Integer.parseInt(myPreferences.getString("dailyBalanceChoice", "1"));

        DateTime endOfTerm = new DateTime(2016, 4, 23, 0, 0);
        DateTime today = DateTime.now().withTimeAtStartOfDay();
        int daysToTermEnd = Days.daysBetween(today, endOfTerm).getDays();

        switch (DailyBalConfig) {
            case 2: {
                for (int i = 0; i < myTransList.size(); i++) {
                    boolean isToday = myTransList.get(i).getDate().withTimeAtStartOfDay().equals(today);
                    boolean isMealPlan = (myTransList.get(i).getType() == 0);
                    if (isToday && isMealPlan) {
                        todaySpent += myTransList.get(i).getAmount();
                    }
                }
                dailyBalance = (MP - todaySpent) / daysToTermEnd;
                break;
            }
            case 3: {
                for (int i = 0; i < myTransList.size(); i++) {
                    boolean isToday = myTransList.get(i).getDate().withTimeAtStartOfDay().equals(today);
                    boolean isFlexDollar = (myTransList.get(i).getType() == 1);
                    if (isToday && isFlexDollar) {
                        todaySpent += myTransList.get(i).getAmount();
                    }
                }
                dailyBalance = (FD - todaySpent) / daysToTermEnd;
                break;
            }
            default: {
                for (int i = 0; i < myTransList.size(); i++) {
                    boolean isToday = myTransList.get(i).getDate().withTimeAtStartOfDay().equals(today);
                    if (isToday) {
                        todaySpent += myTransList.get(i).getAmount();
                    }
                }
                dailyBalance = (Total - todaySpent) / daysToTermEnd;
                break;
            }
        }

    }

    public String getMPString() {
        return NumberFormat.getCurrencyInstance(Locale.CANADA).format(MP);
    }

    public String getFDString() {
        return NumberFormat.getCurrencyInstance(Locale.CANADA).format(FD);
    }

    public String getOtherString() {
        return NumberFormat.getCurrencyInstance(Locale.CANADA).format(Other);
    }

    public String getTotalString() {
        return NumberFormat.getCurrencyInstance(Locale.CANADA).format(Total);
    }

    public String getDailyBalanceString() {
        return NumberFormat.getCurrencyInstance(Locale.CANADA).format(dailyBalance);
    }

    public String getTodaySpentString() {
        return NumberFormat.getCurrencyInstance(Locale.CANADA).format(todaySpent);
    }

    public String getTodayLeftString() {
        return NumberFormat.getCurrencyInstance(Locale.CANADA).format(dailyBalance + todaySpent);
    }

    public String getDateString() {
        String txt = DateUtils.getRelativeTimeSpanString(Date.getMillis()).toString();
        if (txt.equals("0 minutes ago")) {
            return "Now";
        } else {
            return txt;
        }
    }

}
