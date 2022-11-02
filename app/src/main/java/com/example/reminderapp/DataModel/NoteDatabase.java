package com.example.reminderapp.DataModel;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
        entities = Note.class,
        version = 1,
        exportSchema = false
)

public abstract class NoteDatabase extends RoomDatabase {
    private static NoteDatabase db=null;

    public abstract NoteDAO noteDAO();
    public static synchronized NoteDatabase getDBInstance(Context context){
        if(db==null){
            db = Room.databaseBuilder(
                    context.getApplicationContext(),
                    NoteDatabase.class,
                    "NoteDataBase"
            ).allowMainThreadQueries().build();
        }
        return db;
    }
}
