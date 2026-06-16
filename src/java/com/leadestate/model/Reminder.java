package com.leadestate.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Reminder {

    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_OVERDUE = "Overdue";
    public static final String STATUS_DONE = "Selesai";
    public static final String STATUS_CANCELLED = "Dibatalkan";


    private int id;
    private int followupId;
    private Date reminderDate;
    private String status;
    private static List<Reminder> daftarReminder = new ArrayList<>();
    private static int nextId = 1;

    // ===== Constructor =====
    public Reminder() {
        this.status = STATUS_PENDING;
    }

    public Reminder(int id, int followupId, Date reminderDate, String status) {
        this.id = id;
        this.followupId = followupId;
        this.reminderDate = reminderDate;
        this.status = status;
    }

    public Reminder(int followupId, Date reminderDate) {
        this.followupId = followupId;
        this.reminderDate = reminderDate;
        this.status = STATUS_PENDING;
    }

    // ===== Getter & Setter =====
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFollowupId() {
        return this.followupId;
    }

    public void setFollowupId(int followupId) {
        this.followupId = followupId;
    }

    public Date getReminderDate() {
        return this.reminderDate;
    }

    public void setReminderDate(Date reminderDate) {
        this.reminderDate = reminderDate;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // ===== Method =====

    public boolean isOverdue() {
        if (this.reminderDate == null) {
            return false;
        }
        boolean sudahLewatWaktu = this.reminderDate.before(new Date());
        boolean belumSelesai = !STATUS_DONE.equalsIgnoreCase(this.status);
        return sudahLewatWaktu && belumSelesai;
    }

    public Reminder[] checkSchedule() {
        List<Reminder> daftarOverdue = new ArrayList<>();

        for (Reminder r : daftarReminder) {
            if (r.isOverdue()) {
                r.setStatus(STATUS_OVERDUE);
                daftarOverdue.add(r);
            }
        }

        return daftarOverdue.toArray(new Reminder[0]);
    }

    public void generateReminder(int leadId) {
        Reminder reminderBaru = new Reminder();
        reminderBaru.setId(nextId);
        reminderBaru.setFollowupId(leadId);
        reminderBaru.setReminderDate(new Date());
        reminderBaru.setStatus(STATUS_PENDING);

        daftarReminder.add(reminderBaru);
        nextId++;
    }

    public void sendNotification(String message) {
        System.out.println("[Notifikasi Reminder #" + this.id + "] " + message);
    }

    // ===== Akses ke daftar reminder (in-memory) =====

    public static List<Reminder> getDaftarReminder() {
        return daftarReminder;
    }

    @Override
    public String toString() {
        return "Reminder{"
                + "id=" + this.id
                + ", followupId=" + this.followupId
                + ", reminderDate=" + this.reminderDate
                + ", status='" + this.status + "'"
                + "}";
    }
}