package com.cg.watbalance.data;

import org.jsoup.nodes.Element;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Transaction implements Serializable {
    private String terminal;
    private Date date;
    private Float amount;

    public Transaction(Element myElement) {
        String dateTime = myElement.getElementById("oneweb_financial_history_td_date").text() + " " + myElement.getElementById("oneweb_financial_history_td_time").text();
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.CANADA);
        try {
            date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.CANADA).parse(dateTime);
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
        return new SimpleDateFormat("dd MMM 'at' h:mm aa").format(date);
    }

    public Float getAmount() {
        return amount;
    }

    public Date getDate() {
        return date;
    }
}
