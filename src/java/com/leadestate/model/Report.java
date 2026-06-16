package com.leadestate.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Kelas Report.
 *
 * Merepresentasikan hasil laporan yang ditampilkan kepada Admin
 * (misalnya melalui viewAllReports()) maupun kepada Sales (misalnya
 * melalui viewMyPerformance() pada kelas Sales).
 *
 * Kelas ini dibuat sederhana untuk tahap POJO: berisi judul,
 * ringkasan, dan sebuah peta (key-value) data laporan yang fleksibel
 * sehingga dapat menampung berbagai jenis data laporan (jumlah Lead,
 * closing rate, performa Sales, dsb) tanpa perlu membuat banyak
 * kelas Report yang berbeda-beda.
 *
 * Catatan: Class ini dibuat public dan diletakkan di file terpisah
 * (Report.java) agar dapat diakses dari luar package model, misalnya
 * dari halaman JSP.
 *
 * TODO: Saat Service/DAO tersedia, field data pada kelas ini dapat
 *       diisi dengan hasil query/agregasi sesungguhnya dari
 *       LeadDAO, FollowUpDAO, dsb.
 */
public class Report {

    /** Judul laporan, contoh: "Laporan Performa Sales". */
    private String title;

    /** Ringkasan singkat isi laporan. */
    private String summary;

    /**
     * Data laporan dalam bentuk pasangan key-value, contoh:
     * "totalLead" -> "28", "closingRate" -> "83%".
     * Menggunakan Map agar fleksibel menampung berbagai jenis
     * statistik tanpa perlu banyak field tetap.
     */
    private Map<String, Object> data;

    /**
     * Constructor default.
     * Menginisialisasi data ke Map kosong.
     */
    public Report() {
        this.data = new HashMap<>();
    }

    /**
     * Constructor dengan judul dan ringkasan laporan.
     *
     * @param title   judul laporan.
     * @param summary ringkasan singkat laporan.
     */
    public Report(String title, String summary) {
        this.title = title;
        this.summary = summary;
        this.data = new HashMap<>();
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return this.summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Map<String, Object> getData() {
        return this.data;
    }

    public void setData(Map<String, Object> data) {
        this.data = (data != null) ? data : new HashMap<>();
    }

    /**
     * Menambahkan satu pasangan key-value ke dalam data laporan.
     *
     * @param key   nama statistik, contoh "totalLead".
     * @param value nilai statistik, contoh 28.
     */
    public void tambahData(String key, Object value) {
        this.data.put(key, value);
    }

    @Override
    public String toString() {
        return "Report{"
                + "title='" + this.title + "'"
                + ", summary='" + this.summary + "'"
                + ", data=" + this.data
                + "}";
    }
}
