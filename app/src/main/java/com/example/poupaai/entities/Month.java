package com.example.poupaai.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "months")
public class Month implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "monthName")
    private String monthName;

    @ColumnInfo(name = "year")
    private int year;

    public Month() {}

    // Constructor used by Parcelable
    protected Month(Parcel in) {
        id = in.readInt();
        monthName = in.readString();
        year = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(monthName);
        dest.writeInt(year);
    }

    public static final Parcelable.Creator<Month> CREATOR = new Parcelable.Creator<Month>() {
        @Override
        public Month createFromParcel(Parcel in) {
            return new Month(in);
        }

        @Override
        public Month[] newArray(int size) {
            return new Month[size];
        }
    };

    // Getters and Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getMonthName() {
        return monthName;
    }
    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }
    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }
}
