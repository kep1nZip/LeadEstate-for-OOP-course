package com.leadestate.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Kelas Reminder.
 *
 * Merepresentasikan pengingat (reminder) untuk sebuah FollowUp pada
 * sistem LeadEstate.
 *
 * Relasi sesuai class diagram:
 * - FollowUp 1 -- 1..* Reminder (KOMPOSISI)
 *   Artinya satu FollowUp dapat memiliki banyak Reminder, dan sebuah
 *   Reminder tidak akan ada (musnah) tanpa adanya FollowUp tersebut.
 * - Reminder akan diturunkan (extends) oleh kelas Notifikasi.
 */
public class Reminder {

    // ===== Konstanta status reminder =====
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_OVERDUE = "Overdue";
    public static final String STATUS_DONE = "Selesai";

    /**
     * Status reminder yang dibatalkan (sebelumnya tidak aktif/"Inactive").
     * Ditambahkan agar selaras dengan data pada tabel `reminders`.
     */
    public static final String STATUS_CANCELLED = "Dibatalkan";

    // ===== Atribut (sesuai class diagram) =====
    private int id;
    private int followupId;
    private Date reminderDate;
    private String status;

    /*
     * Penyimpanan sementara di memori (in-memory).
     * Digunakan sebagai pengganti sumber data sebelum DAO/Database
     * dibuat. Method checkSchedule() dan generateReminder() akan
     * bekerja terhadap daftar ini.
     */
    private static List<Reminder> daftarReminder = new ArrayList<>();
    private static int nextId = 1;

    // ===== Constructor =====

    /**
     * Constructor default.
     * Status awal di-set ke "Pending".
     */
    public Reminder() {
        this.status = STATUS_PENDING;
    }

    /**
     * Constructor dengan parameter lengkap.
     * Biasanya dipakai saat membentuk objek Reminder dari data
     * yang sudah ada (misal hasil baca dari database).
     */
    public Reminder(int id, int followupId, Date reminderDate, String status) {
        this.id = id;
        this.followupId = followupId;
        this.reminderDate = reminderDate;
        this.status = status;
    }

    /**
     * Constructor tanpa id.
     * Dipakai saat membuat Reminder baru sebelum disimpan,
     * id akan digenerate otomatis (lihat generateReminder()).
     */
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

    // ===== Method sesuai class diagram =====

    /**
     * Mengecek apakah reminder ini sudah melewati batas waktu (overdue).
     * Reminder dianggap overdue apabila reminderDate sudah lewat dari
     * waktu sekarang dan status-nya belum "Selesai".
     *
     * @return true jika reminder sudah overdue, false jika belum.
     */
    public boolean isOverdue() {
        if (this.reminderDate == null) {
            return false;
        }
        boolean sudahLewatWaktu = this.reminderDate.before(new Date());
        boolean belumSelesai = !STATUS_DONE.equalsIgnoreCase(this.status);
        return sudahLewatWaktu && belumSelesai;
    }

    /**
     * Memeriksa seluruh reminder yang tersimpan (daftarReminder).
     * Setiap reminder yang sudah melewati batas waktu akan diubah
     * statusnya menjadi "Overdue", lalu dikembalikan sebagai array.
     *
     * @return array Reminder yang berstatus overdue.
     */
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

    /**
     * Membuat reminder baru yang terkait dengan sebuah lead, lalu
     * menyimpannya ke dalam daftarReminder.
     *
     * Catatan: parameter leadId mengikuti signature pada class diagram.
     * Pada implementasi DAO/Database nanti, leadId ini akan dipakai
     * untuk mencari followupId terkait dari tabel FollowUp.
     *
     * @param leadId id lead yang akan dibuatkan reminder-nya.
     */
    public void generateReminder(int leadId) {
        Reminder reminderBaru = new Reminder();
        reminderBaru.setId(nextId);
        reminderBaru.setFollowupId(leadId);
        reminderBaru.setReminderDate(new Date());
        reminderBaru.setStatus(STATUS_PENDING);

        daftarReminder.add(reminderBaru);
        nextId++;
    }

    /**
     * Mengirimkan notifikasi terkait reminder ini.
     * Implementasi pengiriman notifikasi yang sesungguhnya akan
     * dilakukan secara lebih lengkap oleh kelas Notifikasi
     * (subclass dari Reminder).
     *
     * @param message isi pesan notifikasi yang dikirim.
     */
    public void sendNotification(String message) {
        System.out.println("[Notifikasi Reminder #" + this.id + "] " + message);
    }

    // ===== Akses ke daftar reminder (in-memory) =====

    /**
     * Mengambil seluruh reminder yang tersimpan di memori.
     * Berguna untuk pengujian sebelum DAO/Database tersedia.
     */
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