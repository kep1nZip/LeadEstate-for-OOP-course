/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.leadestate.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Kelas LeadStatus merepresentasikan status yang dapat dimiliki oleh sebuah
 * Lead dalam sistem LeadEstate (contoh: "Baru", "Prospect", "Closing", dsb.).
 *
 * <p>Relasi:</p>
 * <ul>
 *   <li>ASOSIASI dengan Lead (satu LeadStatus dapat diacu oleh banyak Lead
 *       melalui field statusId di kelas Lead). LeadStatus tetap ada meskipun
 *       data Lead dihapus — bukan komposisi.</li>
 * </ul>
 *
 * <p>Catatan: Kelas ini adalah POJO murni (Java + JSP — bukan Spring Boot/JPA).
 * Tidak ada anotasi @Entity, @Service, dsb.</p>
 *
 * @author [Nama Anggota Tim - bagian Data Lead]
 * @version 1.0
 */
public class LeadStatus {

    // =========================================================================
    // KONSTANTA NAMA STATUS
    // =========================================================================

    /** Status lead baru masuk. */
    public static final String STATUS_BARU = "Baru";

    /**
     * Status lead sudah dihubungi pertama kali (sebelumnya "Contacted"
     * pada data SQL). Ditambahkan agar selaras dengan tabel `lead_status`.
     */
    public static final String STATUS_DIHUBUNGI = "Dihubungi";

    /** Status lead sedang dalam proses follow-up. */
    public static final String STATUS_PROSPECT = "Prospect";

    /**
     * Status lead sedang dalam tahap negosiasi (sebelumnya "Negotiation"
     * pada data SQL). Ditambahkan agar selaras dengan tabel `lead_status`.
     */
    public static final String STATUS_NEGOSIASI = "Negosiasi";

    /** Status lead sudah melakukan closing / deal berhasil. */
    public static final String STATUS_CLOSING = "Closing";

    /** Status lead batal / tidak jadi (sebelumnya "Closed Lost" pada data SQL). */
    public static final String STATUS_BATAL = "Batal";

    /** Status lead tidak merespons. */
    public static final String STATUS_TIDAK_MERESPONS = "Tidak Merespons";

    // =========================================================================
    // PENYIMPANAN IN-MEMORY (sementara sebelum DAO tersedia)
    // =========================================================================

    /**
     * Daftar riwayat perubahan status yang disimpan sementara di memori.
     *
     * <p>Setiap entri menyimpan informasi: leadId, oldStatus, newStatus,
     * dan timestamp perubahan dalam format String.</p>
     *
     * <p>TODO: Ganti dengan pemanggilan LeadStatusDAO saat DAO sudah dibuat.</p>
     */
    private static List<String> riwayatStatus = new ArrayList<>();

    // =========================================================================
    // ATRIBUT (sesuai class diagram)
    // =========================================================================

    /** ID unik status lead. */
    private int id;

    /** Nama status lead (contoh: "Baru", "Prospect", "Closing", dsb.). */
    private String statusName;

    // =========================================================================
    // KONSTRUKTOR
    // =========================================================================

    /**
     * Konstruktor default.
     * Menginisialisasi statusName ke null (perlu di-set manual atau lewat setter).
     */
    public LeadStatus() {
    }

    /**
     * Konstruktor lengkap dengan semua parameter.
     *
     * @param id         ID unik status
     * @param statusName Nama status lead
     */
    public LeadStatus(int id, String statusName) {
        this.id = id;
        this.statusName = statusName;
    }

    /**
     * Konstruktor tanpa ID (untuk pembuatan status baru sebelum disimpan ke DB).
     *
     * @param statusName Nama status lead
     */
    public LeadStatus(String statusName) {
        this.statusName = statusName;
    }

    // =========================================================================
    // METHOD (sesuai class diagram)
    // =========================================================================

    /**
     * Mengembalikan detail informasi status ini sebagai teks.
     *
     * <p>Berguna untuk ditampilkan di UI atau log, misalnya:
     * "Status #1 - Baru".</p>
     *
     * @return String berisi detail status
     */
    public String getStatusDetail() {
        return "Status #" + this.id + " - " + this.statusName;
    }

    /**
     * Mencatat riwayat perubahan status sebuah Lead ke daftar in-memory.
     *
     * <p>Setiap perubahan status dicatat dengan format:
     * "[timestamp] leadId=X: oldStatus → newStatus".</p>
     *
     * <p>TODO: Ganti implementasi in-memory ini dengan pemanggilan
     * LeadStatusDAO.saveHistory() saat DAO sudah dibuat.</p>
     *
     * @param leadId    ID Lead yang statusnya berubah
     * @param oldStatus ID status lama
     * @param newStatus ID status baru
     */
    public void saveHistory(int leadId, int oldStatus, int newStatus) {
        if (leadId <= 0) {
            System.out.println("[LeadStatus] leadId tidak valid: " + leadId);
            return;
        }
        if (oldStatus == newStatus) {
            System.out.println("[LeadStatus] Status tidak berubah untuk leadId=" + leadId);
            return;
        }

        String timestamp = new java.util.Date().toString();
        String entri = "[" + timestamp + "] leadId=" + leadId
                + ": statusId " + oldStatus + " → " + newStatus;

        riwayatStatus.add(entri);
        System.out.println("[LeadStatus] Riwayat disimpan: " + entri);

        // TODO: Ganti dengan LeadStatusDAO.saveHistory(leadId, oldStatus, newStatus)
        //       saat lapisan DAO sudah tersedia.
    }

    // =========================================================================
    // METHOD TAMBAHAN (utilitas)
    // =========================================================================

    /**
     * Mengambil seluruh riwayat perubahan status yang tersimpan di memori.
     * Berguna untuk pengujian sebelum DAO/Database tersedia.
     *
     * @return List berisi String riwayat status
     */
    public static List<String> getRiwayatStatus() {
        return riwayatStatus;
    }

    // =========================================================================
    // GETTER & SETTER
    // =========================================================================

    /**
     * Mengambil ID status.
     *
     * @return id status
     */
    public int getId() {
        return this.id;
    }

    /**
     * Mengatur ID status.
     *
     * @param id ID status
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Mengambil nama status.
     *
     * @return statusName
     */
    public String getStatusName() {
        return this.statusName;
    }

    /**
     * Mengatur nama status.
     *
     * @param statusName nama status baru
     */
    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    // =========================================================================
    // OVERRIDE toString()
    // =========================================================================

    /**
     * Representasi teks dari objek LeadStatus.
     *
     * @return String berisi informasi status
     */
    @Override
    public String toString() {
        return "LeadStatus{"
                + "id=" + this.id
                + ", statusName='" + this.statusName + "'"
                + "}";
    }
}