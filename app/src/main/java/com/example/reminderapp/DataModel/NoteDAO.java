package com.example.reminderapp.DataModel;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDAO {

    @Query("SELECT * FROM Notes")
    List<Note> getAll();


    @Insert
    void insertAll(Note... notes);

    @Insert
    void insertOne(Note note);

    @Delete
    void delete(Note note);

    @Update
    void updateNote(Note note);


    @Query("SELECT * FROM Notes WHERE nid = 1")
    List<Note> getById();

}
