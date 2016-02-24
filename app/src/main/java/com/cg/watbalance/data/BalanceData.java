package com.cg.watbalance.data;

import android.graphics.Color;
import android.text.format.DateUtils;

import com.cg.watbalance.data.transaction.TransactionData;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;

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

    public void setDailyBalance(TransactionData myTransData) {
        float totalAmt = 0;
        ArrayList<TransactionData.Transaction> myTransList = myTransData.getTransList();
        for (int i = 0; i < myTransList.size(); i++) {
            if (myTransList.get(i).getDate().withTimeAtStartOfDay().equals(DateTime.now().withTimeAtStartOfDay())) {
                totalAmt += myTransList.get(i).getAmount();
            }
        }
        DateTime endOfTerm = new DateTime(2016, 4, 23, 0, 0);
        DateTime today = DateTime.now().withTime(0, 0, 0, 0);
        int daysToTermEnd = Days.daysBetween(today, endOfTerm).getDays();
        todaySpent = totalAmt;
        dailyBalance = (Total - todaySpent) / daysToTermEnd;
    }

    public PieChartData makePieChartData() {
        List<SliceValue> mySliceValues = new ArrayList<>();

        SliceValue mpSlice = new SliceValue(MP, Color.parseColor("#F44336"));
        if (MP == 0) {
            mpSlice.setLabel("");
        } else {
            mpSlice.setLabel(NumberFormat.getCurrencyInstance(Locale.CANADA).format(MP));
        }
        mySliceValues.add(mpSlice);

        SliceValue fdSlice = new SliceValue(FD, Color.parseColor("#9C27B0"));
        if (FD == 0) {
            fdSlice.setLabel("");
        } else {
            fdSlice.setLabel(NumberFormat.getCurrencyInstance(Locale.CANADA).format(FD));
        }
        mySliceValues.add(fdSlice);

        SliceValue otherSlice = new SliceValue(Other, Color.parseColor("#FFC107"));
        if (Other == 0) {
            otherSlice.setLabel("");
        } else {
            otherSlice.setLabel(NumberFormat.getCurrencyInstance(Locale.CANADA).format(Other));
        }
        mySliceValues.add(otherSlice);

        PieChartData myPieChartData = new PieChartData(mySliceValues);
        myPieChartData.setHasLabels(true);
        myPieChartData.finish();
        return myPieChartData;
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
