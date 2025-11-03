package com.example.workout.Domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Workout implements Serializable {
    private int planId;
    private int sessionId;
    private int exerciseId;
    private String planName;
    private Date date;
    private Exercise exercise;
    private ArrayList<Set> sets;

    public Workout(int planId, int sessionId, int exerciseId, String planName, Date date, Exercise exercise, ArrayList<Set> sets) {
        this.planId = planId;
        this.sessionId = sessionId;
        this.exerciseId = exerciseId;
        this.planName = planName;
        this.date = date;
        this.exercise = exercise;
        this.sets = sets;
    }

    public Workout(Workout workout) {
        this.planId = workout.getPlanId();
        this.sessionId = workout.getSessionId();
        this.exerciseId = workout.getExerciseId();
        this.planName = workout.getPlanName();
        this.date = workout.getDate();
        this.exercise = workout.getExercise();
        this.sets = workout.getSets();
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
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

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public ArrayList<Set> getSets() {
        return sets;
    }

    public void setSets(ArrayList<Set> sets) {
        this.sets = sets;
    }

    public int getNumberOfSet() {
        int count = 0;

        for (Set set : sets) {
            if (!set.isFinish()) count++;
        }

        return count;
    }

}
