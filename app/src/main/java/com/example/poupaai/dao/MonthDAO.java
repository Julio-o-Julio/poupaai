package com.example.poupaai.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.poupaai.entities.Month;

import java.util.List;

@Dao
public interface MonthDAO {
    @Query("SELECT * FROM months")
    List<Month> getAll();
    @Query("SELECT * FROM months WHERE id IN (:monthIds)")
    List<Month> loadAllByIds(int[] monthIds);
    @Query("SELECT * FROM months WHERE id = :id LIMIT 1")
    Month findById(int id);
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Month city);
    @Update
    void update(Month city);
    @Delete
    void delete(Month city);
}
