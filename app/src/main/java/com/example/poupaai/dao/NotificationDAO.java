package com.example.poupaai.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.poupaai.entities.Notification;

import java.util.List;

@Dao
public interface NotificationDAO {
    @Insert
    void insert(Notification notification);
    @Query("SELECT * FROM notification WHERE friendRequestId IN (SELECT id FROM friend_requests WHERE receiverId = :userId) AND isRead = 0")
    List<Notification> getUnreadNotifications(int userId);
    @Update
    void update(Notification notification);
}
