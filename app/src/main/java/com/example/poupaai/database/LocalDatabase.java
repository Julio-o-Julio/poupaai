package com.example.poupaai.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.poupaai.dao.ExpenseDAO;
import com.example.poupaai.dao.MonthDAO;
import com.example.poupaai.dao.UserDAO;
import com.example.poupaai.entities.User;

@Database(entities = {User.class}, version = 1)
public abstract class LocalDatabase extends RoomDatabase {
    private static LocalDatabase INSTANCE;

    public static LocalDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            LocalDatabase.class, "ControleDeDespesas")
                    .addMigrations()
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

    public abstract UserDAO userModel();
    public abstract MonthDAO monthModel();
    public abstract ExpenseDAO expenseModel();
}
