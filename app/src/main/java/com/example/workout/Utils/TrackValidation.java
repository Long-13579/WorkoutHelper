package com.example.workout.Utils;

import com.example.workout.Domain.BodyTrack;

public class TrackValidation {
    public static String Validate(BodyTrack track) {
        if (track.Weight < 0 || track.Height < 0) {
            return "Invalid input";
        }
        return null;
    }
}
