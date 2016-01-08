package com.cg.watbalance.data;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.nodes.Element;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Locale;

public class Transaction implements Serializable {
    private String terminal;
    private DateTime date;
    private Float amount;

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
        terminal = myElement.getElementById("oneweb_financial_history_td_terminal").text();
    }

    public String getPlace() {
        return terminal.substring(7);
    }

    public String getAmountString() {
        return NumberFormat.getCurrencyInstance(Locale.CANADA).format(amount);
    }

    public String getDateString() {
        DateTimeFormatter myFormat = DateTimeFormat.forPattern("dd MMM 'at' h:mm aa");
        return myFormat.print(date);
    }

    public Float getAmount() {
        return amount;
    }

    public DateTime getDate() {
        return date;
    }
}
