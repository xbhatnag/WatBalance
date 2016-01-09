package com.cg.watbalance.data;

import android.graphics.Color;
import android.text.format.DateUtils;

import org.apache.commons.lang3.text.WordUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;


public class WatCardData implements Serializable {
    private String FirstName;
    private Float MP, FD, Other, Total, dailyBalance, todaySpent;
    private int daysToTermEnd;
    private DateTime Date;
    private ArrayList<Transaction> myTransHistory;

    public void getBalanceData(Document myDoc) {

        Elements myTDTags = myDoc.getElementsByTag("TD");
        Element myNameTag = myDoc.getElementById("oneweb_account_name");
        String TempFirstName = myNameTag.text().split(",")[1];
        if (TempFirstName == null || TempFirstName.length() == 0) {
            FirstName = TempFirstName;
        } else {
            FirstName = WordUtils.capitalizeFully(TempFirstName.substring(0, TempFirstName.length() - 1));
        }
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

    public ArrayList<Transaction> getTransHistory() {
        return myTransHistory;
    }

    public void setTransHistory(Document myDoc) {
        Elements Tags = myDoc
                .normalise()
                .body()
                .getElementById("oneweb_financial_history_table")
                .getElementsByTag("tr");
        Tags.remove(0);
        Tags.remove(0);

        myTransHistory = new ArrayList<>();
        int TagSize = Tags.size();

        for (int i = 0; i < TagSize; i++) {
            Transaction TempTrans = new Transaction(Tags.first());
            Tags.remove(0);
            myTransHistory.add(TempTrans);
        }
    }

    public boolean complete() {
        return (FirstName != null) && (!FirstName.equals("")) && (MP != null) && (FD != null) && (Other != null) && (Total != null) && (Date != null) && (myTransHistory != null);
    }

    public List<PointValue> makeDayPointValues() {
        List<PointValue> myPointList = new ArrayList<>();

        Transaction firstTrans = myTransHistory.get(0);
        DateTime lastDate = firstTrans.getDate();
        PointValue myPoint = new PointValue(lastDate.getDayOfMonth(), -firstTrans.getAmount());

        for (int i = 1; i < myTransHistory.size(); i++) {
            Transaction currentTrans = myTransHistory.get(i);
            if (!lastDate.withTimeAtStartOfDay().isEqual(currentTrans.getDate().withTimeAtStartOfDay())) {
                myPoint.setLabel(NumberFormat.getCurrencyInstance(Locale.CANADA).format(myPoint.getY()));
                myPointList.add(myPoint);
                lastDate = currentTrans.getDate();
                myPoint = new PointValue(lastDate.getDayOfMonth(), -currentTrans.getAmount());
            } else {
                myPoint.set(myPoint.getX(), myPoint.getY() + -currentTrans.getAmount());
            }
        }
        myPoint.setLabel(NumberFormat.getCurrencyInstance(Locale.CANADA).format(myPoint.getY()));
        myPointList.add(myPoint);
        return myPointList;
    }

    public void setDailyBalance() {
        Float totalAmt = Float.parseFloat("0");
        for (int i = 0; i < myTransHistory.size(); i++) {
            if (myTransHistory.get(i).getDate().withTimeAtStartOfDay().equals(DateTime.now().withTimeAtStartOfDay())) {
                totalAmt += myTransHistory.get(i).getAmount();
            }
        }
        DateTime endOfTerm = new DateTime(2016, 4, 23, 0, 0);
        DateTime today = DateTime.now().withTime(0, 0, 0, 0);
        daysToTermEnd = Days.daysBetween(today, endOfTerm).getDays();
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

    public Axis makeXAxis(List<PointValue> myPoints) {
        Axis myXAxis = new Axis();
        myXAxis.setName("Day of Month");
        List<AxisValue> myXVals = new ArrayList<>();

        for (int i = 0; i < myPoints.size(); i++) {
            AxisValue tempAxisVal = new AxisValue(myPoints.get(i).getX());
            myXVals.add(tempAxisVal);
        }

        myXAxis.setValues(myXVals);

        return myXAxis;
    }

    public LineChartData makeTransChartData() {
        //In most cased you can call data model methods in builder-pattern-like manner.
        List<PointValue> myPoints = makeDayPointValues();

        Line line = new Line(myPoints).setColor(Color.parseColor("#F44336")).setCubic(false).setHasLabelsOnlyForSelected(true).setStrokeWidth(1);

        line.setPointRadius(3);
        List<Line> lines = new ArrayList<>();
        lines.add(line);

        LineChartData data = new LineChartData();

        data.setAxisXBottom(makeXAxis(myPoints));
        data.setLines(lines);
        return data;
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

    public String getFirstName() {
        return FirstName + "'s WatCard";
    }

    public String getDateString() {
        String txt = DateUtils.getRelativeTimeSpanString(Date.getMillis()).toString();
        if (txt.equals("0 minutes ago")) {
            return "Now";
        } else {
            return txt;
        }
    }

    public String getDailyLeftString() {
        return NumberFormat.getCurrencyInstance(Locale.CANADA).format(dailyBalance + todaySpent);
    }
}