package com.example.reminderapp;


public class Reminder {

    private String freeText;
    private String Id;
    private String item;
    private double longtitue;
    private double latitue;
    //need to add location,item getters and setters + constructor


    public Reminder(String Id, String freeText, String item, double lon, double lat) {
        this.Id = Id;
        this.freeText = freeText;
        this.item = item;
        this.longtitue = lon;
        this.latitue = lat;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getFreeText() {
        return freeText;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public double getLongtitue() {
        return longtitue;
    }

    public void setLongtitue(double longtitue) {
        this.longtitue = longtitue;
    }

    public double getLatitue() {
        return latitue;
    }

    public void setLatitue(double latitue) {
        this.latitue = latitue;
    }

    public void setFreeText(String freeText) {
        this.freeText = freeText;
    }

}
