package com.example.workout.Utils;

import com.example.workout.Domain.Set;

public class SetValidation {
    public static String Validate(Set set) {
        if (set.getReps() < 1 || set.getWeight() < 0 || set.getRestTime() < 1) {
            return "Invalid input";
        }
        return null;
    }
}
