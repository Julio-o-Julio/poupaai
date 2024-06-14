package com.example.poupaai.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.poupaai.entities.Expense;

import java.util.List;

@Dao
public interface ExpenseDAO {
    @Query("SELECT * FROM expenses")
    List<Expense> getAll();
    @Query("SELECT * FROM expenses WHERE id IN (:expenseIds)")
    List<Expense> loadAllByIds(int[] expenseIds);
    @Query("SELECT * FROM expenses WHERE id = :id LIMIT 1")
    Expense findById(int id);
    @Query("SELECT monthId FROM expenses WHERE id = :id LIMIT 1")
    int findMonthIdByExpenseId(int id);
    @Query("SELECT * FROM expenses WHERE monthId = :monthId")
    List<Expense> getExpensesByMonthId(int monthId);
    @Query("SELECT * FROM expenses WHERE userId = :userId")
    List<Expense> getExpensesByUserId(int userId);
    @Query("SELECT * FROM expenses WHERE monthId = :monthId AND userId = :userId")
    List<Expense> getExpensesByMonthIdAndUserId(int monthId, int userId);
    @Query("SELECT DISTINCT category FROM expenses WHERE userId = :userId")
    List<String> getAllUniqueCategories(int userId);
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Expense expense);
    @Update
    void update(Expense expense);
    @Delete
    void delete(Expense expense);
}
