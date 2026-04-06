package com.leadestate.backend.entity;

import java.util.Date;

public class FollowUp {
    private int id;
    private int leadId;
    private int salesId;
    private String notes;
    private Date followupDate;
    private String status; // pending, done, cancelled

    // Method tambahan berdasarkan data
    public void markAsDone() {
        this.status = "done";
    }

    public void editNotes(String newNotes) { }
}
