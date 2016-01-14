package com.cg.watbalance.transaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cg.watbalance.R;

import java.util.ArrayList;

public class TransactionListAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private ArrayList<Transaction> transList;
    private Context context;

    public TransactionListAdapter(Context newContext, ArrayList<Transaction> newTransList) {
        context = newContext;
        transList = newTransList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return transList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.transaction_list_item, null);
        holder.Title = (TextView) rowView.findViewById(R.id.tranTitle);
        holder.Date = (TextView) rowView.findViewById(R.id.tranDate);
        holder.Amount = (TextView) rowView.findViewById(R.id.tranAmt);

        holder.Title.setText(transList.get(position).getPlace());
        holder.Date.setText(transList.get(position).getDateString());
        holder.Amount.setText(transList.get(position).getAmountString());
        return rowView;
    }

    public class Holder {
        TextView Title, Date, Amount;
    }
}
