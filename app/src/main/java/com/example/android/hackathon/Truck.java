package com.example.android.hackathon;

import android.content.res.Resources;

import com.example.android.hackathon.R;

/** Class to store the Truck information. */
public class Truck {
    private String name;
    private String type;
    private String status;
    private String menu;
    private String truckImage;
    private String menuImage;
    private double lat;
    private double lng;
    private int icon;


    public void setName(String val) { name = val; }
    public void setType(String val) { type = val; }
    public void setStatus(String val) { status = val; }
    public void setMenu(String val) { menu = val; }
    public void setTruckImage(String val) { truckImage = val; }
    public void setMenuImage(String val){ menuImage = val;}
    public void setLat(double val) { lat = val; }
    public void setLong(double val) { lng = val; }
    public void setIcon(String val) {
        try {
            switch (val) {
                case "Mexican":
                    icon = R.drawable.taco_truck_marker;
                    break;
                case "American":
                    icon = R.drawable.burger_truck_marker;
                    break;
                case "Desserts":
                    icon = R.drawable.twinkie_truck_marker;
                    break;
                case "Seafood":
                    icon = R.drawable.twinkie_truck_marker;
                    break;
                case "Pizza":
                    icon = R.drawable.pizza_truck_marker;
                    break;
                default:
                    icon = R.drawable.spec_truck_marker;
            }
        } catch (Resources.NotFoundException ex) {
            ex.getMessage();
        }
    }

    public String getName() {return name; }
    public String getType() {return type; }
    public String getStatus() {return status; }
    public String getMenu() {return menu; }
    public String getTruckImage() {return truckImage; }
    public String getMenuImage(){return menuImage;}
    public double getLat() {return lat; }
    public double getLong() {return lng; }
    public int getIcon() { return icon; }

}