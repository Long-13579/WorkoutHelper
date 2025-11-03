package com.example.workout.Domain;

import java.io.Serializable;

public class Set implements Serializable {
    private int id;
    private int sessionId;
    private int exerciseId;
    private int weight;
    private int reps;
    private int restTime;
    private boolean isFinish;

    // Constructor
    public Set(int id, int sessionId, int exerciseId, int weight, int reps, int restTime) {
        this.id = id;
        this.sessionId = sessionId;
        this.exerciseId = exerciseId;
        this.weight = weight;
        this.reps = reps;
        this.restTime = restTime;
        this.isFinish = false;
    }

    public Set(int id, int sessionId, int exerciseId, int weight, int reps, int restTime, boolean isFinish) {
        this.id = id;
        this.sessionId = sessionId;
        this.exerciseId = exerciseId;
        this.weight = weight;
        this.reps = reps;
        this.restTime = restTime;
        this.isFinish = isFinish;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public int getRestTime() {
        return restTime;
    }

    public void setRestTime(int restTime) {
        this.restTime = restTime;
    }

    public boolean isFinish() { return isFinish; }
    public void setIsFinish(boolean isFinish) { this.isFinish = isFinish; }
}


