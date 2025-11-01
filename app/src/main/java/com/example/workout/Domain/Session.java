package com.example.workout.Domain;

import java.io.Serializable;
import java.util.Date;

public class Session implements Serializable {
    private int id;
    private int planId;
    private Date date;

    public Session(int id, int planId, Date date) {
        this.id = id;
        this.planId = planId;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

