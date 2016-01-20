package com.cg.watbalance.data;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Outlet implements Serializable{

    String name;
    int ID;
    Meal lunch, dinner;
    public Outlet(JSONObject myOutlet) {
        try {
            name = myOutlet.getString("outlet_name");
            ID = myOutlet.getInt("outlet_id");
            JSONArray menu = myOutlet.getJSONArray("menu");
            for (int i = 0; i < menu.length(); i++) {
                DateTime tempDate = DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(menu.getJSONObject(i).getString("date"));
                Boolean sameDay = DateTime.now().withTimeAtStartOfDay().equals(tempDate);
                if (sameDay) {
                    lunch = new Meal(menu.getJSONObject(i).getJSONObject("meals").getJSONArray("lunch"));
                    dinner = new Meal(menu.getJSONObject(i).getJSONObject("meals").getJSONArray("dinner"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getName(){return name;}

    public Meal getLunch() {
        return lunch;
    }

    public Meal getDinner() {
        return dinner;
    }

    public class Meal implements Serializable {
        List<Food> myFoodList;

        public Meal(JSONArray myMeal) {
            myFoodList = new ArrayList<>();
            try {
                for (int i = 0; i < myMeal.length(); i++) {
                    myFoodList.add(new Food(myMeal.getJSONObject(i)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public List<Food> getFoodList() {
            return myFoodList;
        }

        public class Food implements Serializable {
            String Name;
            int ID;

            public Food(JSONObject myFood) {
                ID = -1;
                try {
                    Name = myFood.getString("product_name");
                    ID = myFood.getInt("product_id");
                } catch (Exception e) {
                    Log.d("FOOD", "Product ID/Name is null");
                }
            }

            public String getName() {
                if (Name.substring(0, 4).equals("R - ")) {
                    return Name.substring(4);
                }
                return Name;
            }

            public int getID() {
                return ID;
            }
        }

    }
}
