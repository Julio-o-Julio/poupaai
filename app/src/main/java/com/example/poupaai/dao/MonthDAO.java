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
    @Query("SELECT * FROM months WHERE id IN (SELECT DISTINCT monthId FROM expenses WHERE userId = :userId)")
    List<Month> getMonthsWithExpenses(int userId);
    @Query("SELECT * FROM months WHERE monthName = :monthName AND year = :year")
    List<Month> findByMonthNameAndYear(String monthName, int year);
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Month month);
    @Update
    void update(Month month);
    @Delete
    void delete(Month month);
}
