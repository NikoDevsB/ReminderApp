package com.example.reminderapp.DataModel;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Notes")
public class Note {
    @PrimaryKey(autoGenerate = true)
    int nid;

    @ColumnInfo(name = "Reminder")
    String text;

    @ColumnInfo(name = "Location")
    String location;

    @ColumnInfo(name = "Item")
    String item;

    public Note(int nid, String text, String location, String item) {
        this.nid = nid;
        this.text = text;
        this.location = location;
        this.item = item;
    }

    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
}
