package com.cg.watbalance.data;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cg.watbalance.R;

import java.util.ArrayList;

public class MenuListAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    private ArrayList<OutletData.Menu.Meal.Food> foodList;
    private Context myContext;

    public MenuListAdapter(Context context, ArrayList<OutletData.Menu.Meal.Food> newFoodList) {
        myContext = context;
        foodList = newFoodList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return foodList.size();
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
        View rowView;
        rowView = inflater.inflate(R.layout.menu_list_item, null);

        TextView title = (TextView) rowView.findViewById(R.id.foodTitle);
        title.setText(foodList.get(position).getName());

        LinearLayout foodItem = (LinearLayout) rowView.findViewById(R.id.foodItem);
        foodItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ID = foodList.get(position).getID();
                if (ID != -1) {
                    Intent urlLaunch = new Intent(Intent.ACTION_VIEW, Uri.parse("https://uwaterloo.ca/food-services/menu/product/" + ID));
                    myContext.startActivity(urlLaunch);
                }
            }
        });

        return rowView;
    }

}
