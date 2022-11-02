package com.example.reminderapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


/**
 * Created by eawedat on 5/29/2018.
 */


//CRUD - create read update delete

public class SQL extends SQLiteOpenHelper {

    public static final String DB_NAME = "Reminders1.db";

    public SQL(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table mytable (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, item TEXT,longitude VARCHAR(10),latitude VARCHAR(10))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS mytable");
        onCreate(db);
    }

    public boolean insertData(String name, String item, Double longitude, Double latitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("name", name);
        contentValues.put("item", item);
        contentValues.put("longitude", longitude);
        contentValues.put("latitude", latitude);

        long result = db.insert("mytable", null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }

    public ArrayList getAllRecords() {
        ArrayList arrayList = new ArrayList();
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Reminder> reminders = new ArrayList<>();


        Cursor res = db.rawQuery("select * from mytable", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            String id = res.getString(0); //String t1 = res.getString(res.getColumnIndex("id"));
            String name = res.getString(1);
            String item = res.getString(2);
            Double longt = res.getDouble(3);
            Double latit = res.getDouble(4);

            //     arrayList.add(id + "    " + name + "   " + email);

            reminders.add(new Reminder(id, name, item, longt, latit));
            res.moveToNext();
        }
        return reminders;
    }

    public void updateData(String id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);


        db.update("mytable", contentValues, "id= ?", new String[]{id});

    }

    public boolean delete(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete("mytable", "id = ?", new String[]{id});

        if (result > 0) {
            return true;
        } else {
            return false;
        }

    }
}
