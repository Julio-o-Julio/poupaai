package com.example.poupaai.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "notification",
        foreignKeys = @ForeignKey(entity = FriendRequest.class,
                parentColumns = "id",
                childColumns = "friendRequestId",
                onDelete = ForeignKey.CASCADE)
)
public class Notification implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "friendRequestId")
    private int friendRequestId;
    @ColumnInfo(name = "message")
    private String message;
    @ColumnInfo(name = "isRead")
    private boolean isRead;

    public Notification() {}

    protected Notification(Parcel in) {
        id = in.readInt();
        friendRequestId = in.readInt();
        message = in.readString();
        isRead = in.readByte() != 0;
    }

    public static final Creator<Notification> CREATOR = new Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getFriendRequestId() {
        return friendRequestId;
    }
    public void setFriendRequestId(int friendRequestId) {
        this.friendRequestId = friendRequestId;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public boolean isRead() {
        return isRead;
    }
    public void setRead(boolean read) {
        isRead = read;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(friendRequestId);
        dest.writeString(message);
        dest.writeByte((byte) (isRead ? 1 : 0));
    }
}
