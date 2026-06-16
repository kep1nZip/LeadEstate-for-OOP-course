/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.leadestate.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FollowUp {
 
    // KONSTANTA STATUS
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_DONE = "Selesai";
    public static final String STATUS_CANCELLED = "Batal";
 
    // ATRIBUT (sesuai class diagram)

    private int id;
    private int leadId;
    private int salesId;
    private String notes;
    private Date followupDate;
    private String status;
    private List<Reminder> daftarReminder;
 
    // KONSTRUKTOR

    public FollowUp() {
        this.status = STATUS_PENDING;
        this.daftarReminder = new ArrayList<>();
    }

    public FollowUp(int id, int leadId, int salesId, String notes,
                    Date followupDate, String status) {
        this.id = id;
        this.leadId = leadId;
        this.salesId = salesId;
        this.notes = notes;
        this.followupDate = followupDate;
        this.status = (status != null && !status.trim().isEmpty())
                      ? status
                      : STATUS_PENDING;
        this.daftarReminder = new ArrayList<>();
    }

    public FollowUp(int leadId, int salesId, String notes, Date followupDate) {
        this.leadId = leadId;
        this.salesId = salesId;
        this.notes = notes;
        this.followupDate = followupDate;
        this.status = STATUS_PENDING;
        this.daftarReminder = new ArrayList<>();
    }
 
    // METHOD (sesuai class diagram)
    public void logActivity(int leadId, String note) {
        if (leadId != this.leadId) {
            System.out.println("[FollowUp] Peringatan: leadId tidak cocok. "
                    + "Expected: " + this.leadId + ", got: " + leadId);
            return;
        }
        if (note == null || note.trim().isEmpty()) {
            System.out.println("[FollowUp] Catatan tidak boleh kosong.");
            return;
        }
 
        // Tandai catatan dengan timestamp
        String timestamp = new Date().toString();
        String logEntry = "[" + timestamp + "] " + note;
 
        // Append catatan ke notes yang sudah ada
        if (this.notes == null || this.notes.trim().isEmpty()) {
            this.notes = logEntry;
        } else {
            this.notes = this.notes + "\n" + logEntry;
        }
 
        System.out.println("[FollowUp] Aktivitas dicatat untuk leadId=" + leadId
                + ": " + note);
    }
 
    public void editNotes(String newNotes) {
        if (newNotes == null || newNotes.trim().isEmpty()) {
            System.out.println("[FollowUp] Catatan baru tidak boleh kosong.");
            return;
        }
        this.notes = newNotes;
        System.out.println("[FollowUp] Catatan berhasil diperbarui untuk followUpId=" + this.id);
    }

    public void markAsDone() {
        this.status = STATUS_DONE;
 
        // Tandai semua reminder terkait sebagai selesai juga
        for (Reminder reminder : this.daftarReminder) {
            reminder.setStatus(Reminder.STATUS_DONE);
        }
 
        System.out.println("[FollowUp] FollowUp dengan id=" + this.id
                + " telah ditandai selesai.");
    }
 
    // METHOD TAMBAHAN (pendukung relasi & utilitas)
 
    public void tambahReminder(Reminder reminder) {
        if (reminder == null) {
            System.out.println("[FollowUp] Reminder tidak boleh null.");
            return;
        }
        reminder.setFollowupId(this.id);
        this.daftarReminder.add(reminder);
        System.out.println("[FollowUp] Reminder berhasil ditambahkan ke followUpId=" + this.id);
    }

    public boolean hapusReminder(int reminderId) {
        for (int i = 0; i < this.daftarReminder.size(); i++) {
            if (this.daftarReminder.get(i).getId() == reminderId) {
                this.daftarReminder.remove(i);
                System.out.println("[FollowUp] Reminder id=" + reminderId + " berhasil dihapus.");
                return true;
            }
        }
        System.out.println("[FollowUp] Reminder id=" + reminderId + " tidak ditemukan.");
        return false;
    }

    public boolean isSelesai() {
        return STATUS_DONE.equalsIgnoreCase(this.status);
    }
 
    // GETTER & SETTER
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLeadId() {
        return this.leadId;
    }

    public void setLeadId(int leadId) {
        this.leadId = leadId;
    }

    public int getSalesId() {
        return this.salesId;
    }

    public void setSalesId(int salesId) {
        this.salesId = salesId;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getFollowupDate() {
        return this.followupDate;
    }

    public void setFollowupDate(Date followupDate) {
        this.followupDate = followupDate;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Reminder> getDaftarReminder() {
        return this.daftarReminder;
    }

    public void setDaftarReminder(List<Reminder> daftarReminder) {
        this.daftarReminder = (daftarReminder != null) ? daftarReminder : new ArrayList<>();
    }
    
    private String leadName;

    public String getLeadName() {
        return leadName;
    }

    public void setLeadName(String leadName) {
        this.leadName = leadName;
    }
 
    // OVERRIDE toString()

    @Override
    public String toString() {
        return "FollowUp{" +
                "id=" + id +
                ", leadId=" + leadId +
                ", salesId=" + salesId +
                ", notes='" + notes + '\'' +
                ", followupDate=" + followupDate +
                ", status='" + status + '\'' +
                ", jumlahReminder=" + (daftarReminder != null ? daftarReminder.size() : 0) +
                '}';
    }
}