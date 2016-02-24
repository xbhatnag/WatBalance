package com.cg.watbalance.data.transaction;

import android.graphics.Color;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
import lecho.lib.hellocharts.model.PointValue;

public class TransactionData implements Serializable {
    private ArrayList<Transaction> myTransList;
    private boolean set = false;

    public ArrayList<Transaction> getTransList() {
        return myTransList;
    }

    public void setTransList(Document myDoc) {
        Elements Tags = myDoc
                .normalise()
                .body()
                .getElementById("oneweb_financial_history_table")
                .getElementsByTag("tr");
        Tags.remove(0);
        Tags.remove(0);

        myTransList = new ArrayList<>();
        int TransCount = Tags.size();

        for (int i = 0; i < TransCount; i++) {
            Transaction newTrans = new Transaction(Tags.first());
            Tags.remove(0);
            myTransList.add(newTrans);
        }

        set = true;
    }

    public List<PointValue> makeDayPointValues() {
        List<PointValue> myPointList = new ArrayList<>();

        Transaction firstTrans = myTransList.get(0);
        DateTime lastDate = firstTrans.getDate();
        PointValue myPoint = new PointValue(lastDate.getDayOfMonth(), -firstTrans.getAmount());

        for (int i = 1; i < myTransList.size(); i++) {
            Transaction currentTrans = myTransList.get(i);
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

    public LineChartData makeTransChartData() {
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

    public boolean isSet() {
        return set;
    }

    public class Transaction implements Serializable {
        private String title;
        private String type;
        private DateTime date;
        private float amount;

        public Transaction(Element myElement) {
            String dateTime = myElement.getElementById("oneweb_financial_history_td_date").text() + " " + myElement.getElementById("oneweb_financial_history_td_time").text();
            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.CANADA);
            DateTimeFormatter myDateFormat = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");
            try {
                date = DateTime.parse(dateTime, myDateFormat);
                amount = numberFormat.parse(myElement.getElementById("oneweb_financial_history_td_amount").text()).floatValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
            title = myElement.getElementById("oneweb_financial_history_td_terminal").text();
            if (title.contains("WAT-FS")) {
                type = "Meal Plan";
            } else {
                type = "Flex Dollars";
            }
        }

        public String getPlace() {
            return title.substring(7);
        }

        public String getAmountString() {
            return NumberFormat.getCurrencyInstance(Locale.CANADA).format(amount);
        }

        public String getTimeString() {
            DateTimeFormatter myFormat = DateTimeFormat.forPattern("dd MMM 'at' h:mm aa");
            return myFormat.print(date);
        }

        public float getAmount() {
            return amount;
        }

        public DateTime getDate() {
            return date;
        }

        public String getType() {
            return type;
        }
    }

}
