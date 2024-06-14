package com.example.poupaai.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.poupaai.dao.ExpenseDAO;
import com.example.poupaai.dao.FriendRequestDAO;
import com.example.poupaai.dao.MonthDAO;
import com.example.poupaai.dao.NotificationDAO;
import com.example.poupaai.dao.UserDAO;
import com.example.poupaai.entities.Expense;
import com.example.poupaai.entities.FriendRequest;
import com.example.poupaai.entities.Month;
import com.example.poupaai.entities.Notification;
import com.example.poupaai.entities.User;

@Database(entities = {User.class, Month.class, Expense.class, FriendRequest.class, Notification.class}, version = 5)
public abstract class LocalDatabase extends RoomDatabase {
    private static LocalDatabase INSTANCE;

    public static LocalDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            LocalDatabase.class, "ControleDeDespesas")
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

    // Migration from version 2 to version 3
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Create FriendRequest table
            database.execSQL("CREATE TABLE IF NOT EXISTS `friend_request` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`senderId` INTEGER NOT NULL, " +
                    "`receiverId` INTEGER NOT NULL, " +
                    "`status` TEXT, " +
                    "FOREIGN KEY(`senderId`) REFERENCES `users`(`uid`) ON DELETE CASCADE, " +
                    "FOREIGN KEY(`receiverId`) REFERENCES `users`(`uid`) ON DELETE CASCADE)");
        }
    };

    // Migration from version 3 to version 4
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Create Notification table
            database.execSQL("CREATE TABLE IF NOT EXISTS `notification` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`friendRequestId` INTEGER NOT NULL, " +
                    "`message` TEXT, " +
                    "`isRead` INTEGER NOT NULL DEFAULT 0, " +
                    "FOREIGN KEY(`friendRequestId`) REFERENCES `friend_request`(`id`) ON DELETE CASCADE)");
        }
    };

    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Execute the SQL statement to add the new column
            database.execSQL("ALTER TABLE users ADD COLUMN profile_image_path TEXT");
        }
    };

    public abstract UserDAO userModel();
    public abstract MonthDAO monthModel();
    public abstract ExpenseDAO expenseModel();
    public abstract FriendRequestDAO friendRequestDAO();
    public abstract NotificationDAO notificationDAO();
}
