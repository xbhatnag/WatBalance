package com.cg.watbalance.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;


public class WatCardData implements Serializable {

    private ArrayList<OutletData> myOutletList;

    public ArrayList<OutletData> getOutletData() {
        return myOutletList;
    }

    public void setOutletData(String response) {
    }

    public void setBuildingData(String response) {

    }

    public void setMenuData(String response) {
        myOutletList = new ArrayList<>();
        try {
            JSONArray tempOutlets = new JSONObject(response).getJSONObject("data").getJSONArray("outlets");
            for (int i = 0; i < tempOutlets.length(); i++) {
                myOutletList.add(new OutletData(tempOutlets.getJSONObject(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }














}