package com.leadestate.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Notifikasi extends Reminder {

    private int notifId;
    private String message;
    private Date sentAt;
    private boolean isRead;

    private int userId;
    private static List<Notifikasi> daftarNotifikasi = new ArrayList<>();
    private static int nextNotifId = 1;

    // ===== Constructor =====
    public Notifikasi() {
        super();
        this.isRead = false;
    }

    public Notifikasi(int id, int followupId, Date reminderDate, String status,
                      int notifId, String message, Date sentAt,
                      boolean isRead, int userId) {
        super(id, followupId, reminderDate, status);
        this.notifId  = notifId;
        this.message  = message;
        this.sentAt   = sentAt;
        this.isRead   = isRead;
        this.userId   = userId;
    }

    public Notifikasi(int followupId, String message, int userId) {
        super(followupId, new Date());
        this.message = message;
        this.userId  = userId;
        this.isRead  = false;
    }

    // ===== Getter & Setter =====
    public int getNotifId() {
        return this.notifId;
    }

    public void setNotifId(int notifId) {
        this.notifId = notifId;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getSentAt() {
        return this.sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public boolean isRead() {
        return this.isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // ===== Method sesuai class diagram =====
    
    public void send(int userId, String message) {
        Notifikasi notifBaru = new Notifikasi();
        notifBaru.setNotifId(nextNotifId);
        notifBaru.setUserId(userId);
        notifBaru.setMessage(message);
        notifBaru.setSentAt(new Date());
        notifBaru.setIsRead(false);

        daftarNotifikasi.add(notifBaru);
        nextNotifId++;

        System.out.println("[Notifikasi #" + notifBaru.getNotifId()
                + " → User #" + userId + "] " + message);
    }

    public void markAsRead(int notifId) {
        for (Notifikasi n : daftarNotifikasi) {
            if (n.getNotifId() == notifId) {
                n.setIsRead(true);
                System.out.println("[Notifikasi #" + notifId
                        + "] Ditandai sudah dibaca.");
                return;
            }
        }
        System.out.println("[Notifikasi] Notifikasi dengan id "
                + notifId + " tidak ditemukan.");
    }

    public void resend(int userId) {
        String pesanUlang = (this.message != null) ? this.message
                : "(Notifikasi tanpa pesan)";
        send(userId, "[KIRIM ULANG] " + pesanUlang);
    }

    public void cancelNotification(int notifId) {
        boolean dihapus = daftarNotifikasi
                .removeIf(n -> n.getNotifId() == notifId);

        if (dihapus) {
            System.out.println("[Notifikasi #" + notifId
                    + "] Notifikasi berhasil dibatalkan.");
        } else {
            System.out.println("[Notifikasi] Notifikasi dengan id "
                    + notifId + " tidak ditemukan.");
        }
    }

    @Override
    public String getStatus() {
        String statusReminder = super.getStatus();
        String statusBaca     = this.isRead ? "Sudah Dibaca" : "Belum Dibaca";
        return statusReminder + " | " + statusBaca;
    }

    public void scheduleNotif(Date date) {
        this.sentAt = date;
        System.out.println("[Notifikasi #" + this.notifId
                + "] Dijadwalkan untuk dikirim pada: " + date);
    }

    public int getUnreadCount() {
        int jumlah = 0;
        for (Notifikasi n : daftarNotifikasi) {
            if (n.getUserId() == this.userId && !n.isRead()) {
                jumlah++;
            }
        }
        return jumlah;
    }

    // ===== Akses ke daftar notifikasi (in-memory) =====
    public static List<Notifikasi> getDaftarNotifikasi() {
        return daftarNotifikasi;
    }

    @Override
    public String toString() {
        return "Notifikasi{"
                + "notifId=" + this.notifId
                + ", userId=" + this.userId
                + ", message='" + this.message + "'"
                + ", sentAt=" + this.sentAt
                + ", isRead=" + this.isRead
                + ", followupId=" + this.getFollowupId()
                + ", reminderDate=" + this.getReminderDate()
                + ", status='" + super.getStatus() + "'"
                + "}";
    }
}
