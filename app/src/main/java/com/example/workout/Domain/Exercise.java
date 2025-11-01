package com.example.workout.Domain;

import androidx.annotation.NonNull;

import com.example.workout.Enum.MuscleEnum;

import java.io.Serializable;

public class Exercise implements Serializable {
    private int id;
    private String name;
    private String description;
    private String videoUrl;
    private MuscleEnum targetMuscle1;
    private MuscleEnum targetMuscle2;
    private MuscleEnum targetMuscle3;

    public Exercise(int id, String name, String videoUrl, String description,
                    MuscleEnum targetMuscle1, MuscleEnum targetMuscle2, MuscleEnum targetMuscle3) {
        this.id = id;
        this.name = name;
        this.videoUrl = videoUrl;
        this.description = description;
        this.targetMuscle1 = targetMuscle1;
        this.targetMuscle2 = targetMuscle2;
        this.targetMuscle3 = targetMuscle3;
    }

    public Exercise(int id, String name, String videoUrl, String description,
                    MuscleEnum targetMuscle1, MuscleEnum targetMuscle2) {
        this(id, name, videoUrl, description, targetMuscle1, targetMuscle2, null);
    }

    public Exercise(int id, String name, String videoUrl, String description,
                    MuscleEnum targetMuscle1) {
        this(id, name, videoUrl, description, targetMuscle1, null, null);
    }

    public Exercise(int id, String name, String videoUrl, String description) {
        this(id, name, videoUrl, description, null, null, null);
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return "pic_" + this.id;
    }

    public String getDescription() { return  this.description; }

    public  void setDescription(String description) { this.description = description; }

    public String getVideoUrl() { return  this.videoUrl; }

    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public MuscleEnum getTargetMuscle1() {
        return targetMuscle1;
    }

    public void setTargetMuscle1(MuscleEnum targetMuscle1) {
        this.targetMuscle1 = targetMuscle1;
    }

    public MuscleEnum getTargetMuscle2() {
        return targetMuscle2;
    }

    public void setTargetMuscle2(MuscleEnum targetMuscle2) {
        this.targetMuscle2 = targetMuscle2;
    }

    public MuscleEnum getTargetMuscle3() {
        return targetMuscle3;
    }

    public void setTargetMuscle3(MuscleEnum targetMuscle3) {
        this.targetMuscle3 = targetMuscle3;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}

