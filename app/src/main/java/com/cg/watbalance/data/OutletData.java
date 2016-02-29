package com.cg.watbalance.data;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class OutletData implements Serializable {

    private ArrayList<Menu> myMenuList;

    public void setOutletData(String response) {
        myMenuList = new ArrayList<>();
        try {
            JSONArray menuArray = new JSONObject(response).getJSONObject("data").getJSONArray("outlets");
            for (int i = 0; i < menuArray.length(); i++) {
                myMenuList.add(new Menu(menuArray.getJSONObject(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Menu findMenu(int id) {
        for (int i = 0; i < myMenuList.size(); i++) {
            if (myMenuList.get(i).getOutletID() == id) {
                return myMenuList.get(i);
            }
        }
        return null;
    }

    public void setOutletStatus(String response) {
        try {
            JSONArray outletArray = new JSONObject(response).getJSONArray("data");
            for (int i = 0; i < myMenuList.size(); i++) {
                for (int j = 0; j < outletArray.length(); j++) {
                    if (myMenuList.get(i).getOutletID() == outletArray.getJSONObject(j).getInt("outlet_id")) {
                        myMenuList.get(i).setOpen(outletArray.getJSONObject(j).getBoolean("is_open_now"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class Menu implements Serializable {
        private String outletName;
        private int outletID;
        private boolean open;
        private Meal lunch, dinner;

        public Menu(JSONObject myOutlet) {
            try {
                outletName = myOutlet.getString("outlet_name");
                outletID = myOutlet.getInt("outlet_id");
                JSONArray menu = myOutlet.getJSONArray("menu");
                lunch = new Meal();
                dinner = new Meal();
                for (int i = 0; i < menu.length(); i++) {
                    DateTime tempDate = DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(menu.getJSONObject(i).getString("date"));
                    Boolean sameDay = DateTime.now().withTimeAtStartOfDay().equals(tempDate);
                    if (sameDay) {
                        lunch.addFood(menu.getJSONObject(i).getJSONObject("meals").getJSONArray("lunch"));
                        dinner.addFood(menu.getJSONObject(i).getJSONObject("meals").getJSONArray("dinner"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public boolean getOpen() {
            return open;
        }

        public void setOpen(boolean newStatus) {
            open = newStatus;
        }

        public String getOutletName() {
            return outletName;
        }

        public int getOutletID() {
            return outletID;
        }

        public Meal getLunch() {
            return lunch;
        }

        public Meal getDinner() {
            return dinner;
        }

        public class Meal implements Serializable {
            private ArrayList<Food> myFoodList;

            public Meal() {
                myFoodList = new ArrayList<>();
            }

            public void addFood(JSONArray myMeal) {
                myFoodList = new ArrayList<>();
                try {
                    for (int i = 0; i < myMeal.length(); i++) {
                        myFoodList.add(new Food(myMeal.getJSONObject(i)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public ArrayList<Food> getFoodList() {
                return myFoodList;
            }

            public class Food implements Serializable {
                private String Name = "";
                private int ID = -1;

                public Food(JSONObject myFood) {
                    try {
                        Name = myFood.getString("product_name");
                        if (Name.substring(0, 4).equals("R - ")) {
                            Name = Name.substring(4);
                        }
                        ID = myFood.getInt("product_id");
                    } catch (Exception e) {
                    }
                }

                public String getName() {
                    return Name;
                }

                public int getID() {
                    return ID;
                }
            }
        }
    }
}
