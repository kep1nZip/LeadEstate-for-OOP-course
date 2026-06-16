/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.leadestate.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Kelas FollowUp merepresentasikan aktivitas tindak lanjut (follow-up) yang
 * dilakukan oleh Sales terhadap suatu Lead dalam sistem LeadEstate.
 *
 * <p>Relasi:</p>
 * <ul>
 *   <li>KOMPOSISI dengan Lead (banyak FollowUp dimiliki satu Lead,
 *       FollowUp tidak ada tanpa Lead — diwakili oleh field leadId).</li>
 *   <li>KOMPOSISI dengan Reminder (satu FollowUp memiliki banyak Reminder,
 *       Reminder tidak ada tanpa FollowUp — disimpan sebagai List&lt;Reminder&gt;
 *       di dalam FollowUp, dan Reminder menyimpan followupId yang merujuk ke
 *       id FollowUp ini).</li>
 *   <li>ASOSIASI dengan User/Sales (FollowUp menyimpan salesId, tidak
 *       menyimpan objek User langsung).</li>
 * </ul>
 *
 * <p>Catatan: Kelas ini adalah POJO murni (Java + JSP — bukan Spring Boot/JPA).
 * Tidak ada anotasi @Entity, @Service, dsb.</p>
 *
 * @author Rafa Ahmad Aulia (103012400169)
 * @version 1.0
 */
public class FollowUp {
 
    // =========================================================================
    // KONSTANTA STATUS
    // =========================================================================
 
    /** Status follow-up masih aktif / belum selesai. */
    public static final String STATUS_PENDING = "Pending";
 
    /**
     * Status follow-up sudah selesai.
     * Nilai ini sengaja konsisten dengan STATUS_DONE di Reminder.java
     * agar keduanya bisa dibandingkan langsung.
     */
    public static final String STATUS_DONE = "Selesai";

    /**
     * Status follow-up dibatalkan (sebelumnya "Cancelled" pada data SQL).
     * Ditambahkan agar selaras dengan data pada tabel `followups`.
     */
    public static final String STATUS_CANCELLED = "Batal";
 
    // =========================================================================
    // ATRIBUT (sesuai class diagram)
    // =========================================================================
 
    /** ID unik follow-up. */
    private int id;
 
    /** ID Lead yang di-follow-up (representasi komposisi: FollowUp milik Lead). */
    private int leadId;
 
    /** ID Sales yang melakukan follow-up (representasi asosiasi ke User/Sales). */
    private int salesId;
 
    /** Catatan hasil interaksi dengan lead. */
    private String notes;
 
    /** Tanggal dan waktu jadwal follow-up. */
    private Date followupDate;
 
    /**
     * Status follow-up saat ini.
     * Nilai yang valid: STATUS_PENDING ("Pending") atau STATUS_DONE ("Selesai").
     * Default: STATUS_PENDING.
     */
    private String status;
 
    /**
     * Daftar Reminder yang terkait dengan FollowUp ini (KOMPOSISI).
     *
     * <p>Pendekatan: FollowUp menyimpan List&lt;Reminder&gt; karena Reminder
     * adalah bagian dari FollowUp (komposisi). Setiap Reminder memiliki field
     * followupId yang nilainya sama dengan id FollowUp ini, sehingga relasi
     * terjaga dari dua sisi.</p>
     *
     * <p>TODO: Saat DAO sudah dibuat, list ini akan diisi via
     * ReminderDAO.findByFollowupId(this.id) — ganti inisialisasi in-memory
     * ini dengan pemanggilan DAO yang sesuai.</p>
     */
    private List<Reminder> daftarReminder;
 
    // =========================================================================
    // KONSTRUKTOR
    // =========================================================================
 
    /**
     * Konstruktor default.
     * Menginisialisasi status ke STATUS_PENDING dan list reminder ke kosong.
     */
    public FollowUp() {
        this.status = STATUS_PENDING;
        this.daftarReminder = new ArrayList<>();
    }
 
    /**
     * Konstruktor lengkap dengan semua parameter utama.
     *
     * @param id          ID unik follow-up
     * @param leadId      ID Lead yang di-follow-up
     * @param salesId     ID Sales yang melakukan follow-up
     * @param notes       Catatan hasil interaksi
     * @param followupDate Tanggal jadwal follow-up
     * @param status      Status follow-up (gunakan konstanta STATUS_PENDING / STATUS_DONE)
     */
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
 
    /**
     * Konstruktor tanpa ID (untuk pembuatan follow-up baru sebelum disimpan ke DB).
     *
     * @param leadId      ID Lead yang di-follow-up
     * @param salesId     ID Sales yang melakukan follow-up
     * @param notes       Catatan awal
     * @param followupDate Tanggal jadwal follow-up
     */
    public FollowUp(int leadId, int salesId, String notes, Date followupDate) {
        this.leadId = leadId;
        this.salesId = salesId;
        this.notes = notes;
        this.followupDate = followupDate;
        this.status = STATUS_PENDING;
        this.daftarReminder = new ArrayList<>();
    }
 
    // =========================================================================
    // METHOD (sesuai class diagram)
    // =========================================================================
 
    /**
     * Mencatat aktivitas interaksi dengan lead ke dalam riwayat follow-up.
     *
     * <p>Method ini menyimpan catatan baru sebagai FollowUp tambahan
     * (atau memperbarui notes pada FollowUp ini) ke dalam daftar in-memory.
     * Format log: "[timestamp] note".</p>
     *
     * <p>TODO: Ganti implementasi in-memory ini dengan pemanggilan DAO
     * (misalnya FollowUpDAO.save()) saat DAO sudah dibuat.</p>
     *
     * @param leadId ID Lead yang aktivitasnya dicatat (harus cocok dengan leadId FollowUp ini)
     * @param note   Isi catatan aktivitas yang ingin dicatat
     */
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
 
        // Tandai catatan dengan timestamp sederhana
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
 
        // TODO: Ganti println di atas dengan FollowUpDAO.save(this)
        //       saat lapisan DAO sudah tersedia.
    }
 
    /**
     * Mengedit/mengganti catatan (notes) pada follow-up ini.
     *
     * <p>TODO: Setelah DAO dibuat, tambahkan pemanggilan FollowUpDAO.update(this)
     * di sini agar perubahan tersimpan ke database.</p>
     *
     * @param newNotes Isi catatan baru yang akan menggantikan catatan lama
     */
    public void editNotes(String newNotes) {
        if (newNotes == null || newNotes.trim().isEmpty()) {
            System.out.println("[FollowUp] Catatan baru tidak boleh kosong.");
            return;
        }
        this.notes = newNotes;
        System.out.println("[FollowUp] Catatan berhasil diperbarui untuk followUpId=" + this.id);
 
        // TODO: Ganti dengan FollowUpDAO.update(this) saat DAO sudah dibuat.
    }
 
    /**
     * Menandai follow-up ini sebagai selesai.
     *
     * <p>Mengubah status menjadi STATUS_DONE ("Selesai"), konsisten dengan
     * konstanta STATUS_DONE di Reminder.java agar kedua kelas sinkron.</p>
     *
     * <p>Method ini juga menandai semua Reminder yang terkait sebagai selesai
     * (karena Reminder merupakan komposisi dari FollowUp).</p>
     *
     * <p>TODO: Setelah DAO dibuat, tambahkan FollowUpDAO.update(this) dan
     * ReminderDAO.updateStatusByFollowupId(this.id, Reminder.STATUS_DONE)
     * di sini agar perubahan tersimpan ke database.</p>
     */
    public void markAsDone() {
        this.status = STATUS_DONE;
 
        // Tandai semua reminder terkait sebagai selesai juga
        for (Reminder reminder : this.daftarReminder) {
            reminder.setStatus(Reminder.STATUS_DONE);
        }
 
        System.out.println("[FollowUp] FollowUp dengan id=" + this.id
                + " telah ditandai selesai.");
 
        // TODO: Ganti dengan FollowUpDAO.update(this) saat DAO sudah dibuat.
    }
 
    // =========================================================================
    // METHOD TAMBAHAN (pendukung relasi & utilitas)
    // =========================================================================
 
    /**
     * Menambahkan Reminder ke dalam daftar reminder follow-up ini.
     *
     * <p>Reminder yang ditambahkan akan otomatis di-set followupId-nya
     * sama dengan id FollowUp ini, agar relasi komposisi konsisten dari dua sisi.</p>
     *
     * @param reminder Objek Reminder yang akan ditambahkan
     */
    public void tambahReminder(Reminder reminder) {
        if (reminder == null) {
            System.out.println("[FollowUp] Reminder tidak boleh null.");
            return;
        }
        reminder.setFollowupId(this.id);
        this.daftarReminder.add(reminder);
        System.out.println("[FollowUp] Reminder berhasil ditambahkan ke followUpId=" + this.id);
    }
 
    /**
     * Menghapus Reminder dari daftar berdasarkan id Reminder.
     *
     * @param reminderId ID Reminder yang akan dihapus
     * @return true jika berhasil dihapus, false jika tidak ditemukan
     */
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
 
    /**
     * Mengecek apakah follow-up ini sudah selesai.
     *
     * @return true jika status adalah STATUS_DONE, false jika belum
     */
    public boolean isSelesai() {
        return STATUS_DONE.equalsIgnoreCase(this.status);
    }
 
    // =========================================================================
    // GETTER & SETTER
    // =========================================================================
 
    /**
     * Mengambil ID follow-up.
     *
     * @return id follow-up
     */
    public int getId() {
        return this.id;
    }
 
    /**
     * Mengatur ID follow-up.
     *
     * @param id ID follow-up
     */
    public void setId(int id) {
        this.id = id;
    }
 
    /**
     * Mengambil ID lead yang di-follow-up.
     *
     * @return leadId
     */
    public int getLeadId() {
        return this.leadId;
    }
 
    /**
     * Mengatur ID lead yang di-follow-up.
     *
     * @param leadId ID lead
     */
    public void setLeadId(int leadId) {
        this.leadId = leadId;
    }
 
    /**
     * Mengambil ID sales yang melakukan follow-up.
     *
     * @return salesId
     */
    public int getSalesId() {
        return this.salesId;
    }
 
    /**
     * Mengatur ID sales yang melakukan follow-up.
     *
     * @param salesId ID sales
     */
    public void setSalesId(int salesId) {
        this.salesId = salesId;
    }
 
    /**
     * Mengambil catatan follow-up.
     *
     * @return notes
     */
    public String getNotes() {
        return this.notes;
    }
 
    /**
     * Mengatur catatan follow-up.
     *
     * @param notes catatan baru
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }
 
    /**
     * Mengambil tanggal jadwal follow-up.
     *
     * @return followupDate
     */
    public Date getFollowupDate() {
        return this.followupDate;
    }
 
    /**
     * Mengatur tanggal jadwal follow-up.
     *
     * @param followupDate tanggal follow-up
     */
    public void setFollowupDate(Date followupDate) {
        this.followupDate = followupDate;
    }
 
    /**
     * Mengambil status follow-up saat ini.
     *
     * @return status (STATUS_PENDING atau STATUS_DONE)
     */
    public String getStatus() {
        return this.status;
    }
 
    /**
     * Mengatur status follow-up.
     * Gunakan konstanta STATUS_PENDING atau STATUS_DONE.
     *
     * @param status nilai status baru
     */
    public void setStatus(String status) {
        this.status = status;
    }
 
    /**
     * Mengambil seluruh daftar Reminder yang terkait dengan FollowUp ini.
     *
     * @return List berisi objek Reminder
     */
    public List<Reminder> getDaftarReminder() {
        return this.daftarReminder;
    }
 
    /**
     * Mengganti seluruh daftar Reminder (misalnya saat memuat data dari DAO).
     *
     * <p>TODO: Method ini akan dipanggil oleh DAO/Service untuk mengisi
     * daftarReminder dari database.</p>
     *
     * @param daftarReminder daftar reminder baru
     */
    public void setDaftarReminder(List<Reminder> daftarReminder) {
        this.daftarReminder = (daftarReminder != null) ? daftarReminder : new ArrayList<>();
    }
 
    // =========================================================================
    // OVERRIDE toString()
    // =========================================================================
 
    /**
     * Representasi teks dari objek FollowUp.
     *
     * @return String berisi informasi follow-up
     */
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