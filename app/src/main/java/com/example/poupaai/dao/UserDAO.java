package com.example.poupaai.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.poupaai.entities.User;

import java.util.List;

@Dao
public interface UserDAO {
    @Query("SELECT * FROM users WHERE uid IN (SELECT receiverId FROM friend_requests WHERE senderId = :userId AND status = 'accepted' UNION SELECT senderId FROM friend_requests WHERE receiverId = :userId AND status = 'accepted')")
    List<User> getAllFriends(int userId);
    @Query("SELECT * FROM users WHERE uid IN (:userIds)")
    List<User> loadAllByIds(int[] userIds);
    @Query("SELECT * FROM users WHERE uid = :id LIMIT 1")
    User findByUid(int id);
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User findByEmail(String email);
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(User user);
    @Update
    void update(User user);
    @Delete
    void delete(User user);
}
