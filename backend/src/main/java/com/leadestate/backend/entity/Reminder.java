package com.leadestate.backend.entity;

import java.util.Date;

public class Reminder {
    private int id;
    private int followupId;
    private Date reminderDate;
    private String status; // pending, done

    // Method tambahan berdasarkan data
    public boolean isOverdue() {
        // Cek apakah tanggal sekarang > reminderDate
        return false;
    }
}