package com.cg.watbalance.data.transaction;

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
    private ArrayList<TransactionData.Transaction> transList;

    public TransactionListAdapter(Context context, ArrayList<TransactionData.Transaction> newTransList) {
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
        holder.Type = (TextView) rowView.findViewById(R.id.tranType);
        holder.Time = (TextView) rowView.findViewById(R.id.tranTime);
        holder.Amount = (TextView) rowView.findViewById(R.id.tranAmt);

        holder.Title.setText(transList.get(position).getTitle());
        holder.Type.setText(transList.get(position).getTypeString());
        holder.Time.setText(transList.get(position).getTimeString());
        holder.Amount.setText(transList.get(position).getAmountString());
        return rowView;
    }

    public class Holder {
        TextView Title, Type, Time, Amount;
    }
}
