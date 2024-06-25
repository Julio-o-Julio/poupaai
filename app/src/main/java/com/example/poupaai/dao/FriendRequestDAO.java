package com.example.poupaai.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.poupaai.entities.FriendRequest;

import java.util.List;

@Dao
public interface FriendRequestDAO {
    @Insert
    void insert(FriendRequest friendRequest);
    @Update
    void update(FriendRequest friendRequest);
    @Delete
    void delete(FriendRequest friendRequest);
    @Query("SELECT * FROM friend_requests WHERE id = :requestId")
    FriendRequest getFindById(int requestId);
    @Query("SELECT * FROM friend_requests WHERE receiverId = :receiverId AND status = 'pending'")
    List<FriendRequest> getPendingFriendRequests(int receiverId);
    @Query("SELECT * FROM friend_requests WHERE senderId = :senderId AND receiverId = :receiverId")
    FriendRequest getRequestSenderIdAndReseiverId(int senderId, int receiverId);
    @Query("SELECT * FROM friend_requests WHERE senderId = :senderId")
    List<FriendRequest> getAllFriendRequestsSender(int senderId);
}
