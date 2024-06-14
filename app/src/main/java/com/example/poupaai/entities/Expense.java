package com.example.poupaai.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "expenses",
        foreignKeys = {
                @ForeignKey(entity = Month.class,
                        parentColumns = "id",
                        childColumns = "monthId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class,
                        parentColumns = "uid",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE)
        }
)
public class Expense implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "value")
    private double value;
    @ColumnInfo(name = "category")
    private String category;
    @ColumnInfo(name = "day")
    private int day;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "status")
    private String status;
    @ColumnInfo(name = "monthId")
    private int monthId;
    @ColumnInfo(name = "userId")
    private int userId;

    public Expense() {}

    // Constructor used by Parcelable
    protected Expense(Parcel in) {
        id = in.readInt();
        value = in.readDouble();
        category = in.readString();
        day = in.readInt();
        description = in.readString();
        status = in.readString();
        monthId = in.readInt();
        userId = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeDouble(value);
        dest.writeString(category);
        dest.writeInt(day);
        dest.writeString(description);
        dest.writeString(status);
        dest.writeInt(monthId);
        dest.writeInt(userId);
    }

    public static final Parcelable.Creator<Expense> CREATOR = new Parcelable.Creator<Expense>() {
        @Override
        public Expense createFromParcel(Parcel in) {
            return new Expense(in);
        }

        @Override
        public Expense[] newArray(int size) {
            return new Expense[size];
        }
    };

    // Getters and Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public double getValue() {
        return value;
    }
    public void setValue(double value) {
        this.value = value;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public int getDay() {
        return day;
    }
    public void setDay(int day) {
        this.day = day;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public int getMonthId() {
        return monthId;
    }
    public void setMonthId(int monthId) {
        this.monthId = monthId;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
}
