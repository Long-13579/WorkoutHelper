package com.example.workout.Domain;


import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class BodyTrack {
    @SerializedName("id")
    public int Id;
    @SerializedName("height")
    public int Height;
    @SerializedName("weight")
    public int Weight;

    @SerializedName("date")
    public Date Date;
    @SerializedName("userId")
    public int UserId;

    @Override
    public String toString() {
        return "Id=" + Id + ", Height=" + Height + ", Weight=" + Weight + ", Date=" + Date + ", UserId=" + UserId;
    }
}