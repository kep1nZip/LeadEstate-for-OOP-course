/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.leadestate.model;

import java.util.ArrayList;
import java.util.List;

public class LeadStatus {

    // KONSTANTA NAMA STATUS

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

    // PENYIMPANAN IN-MEMORY (sementara sebelum DAO tersedia)

    private static List<String> riwayatStatus = new ArrayList<>();

    // ATRIBUT (sesuai class diagram)

    /** ID unik status lead. */
    private int id;

    /** Nama status lead (contoh: "Baru", "Prospect", "Closing", dsb.). */
    private String statusName;

    // KONSTRUKTOR

    public LeadStatus() {
    }

    public LeadStatus(int id, String statusName) {
        this.id = id;
        this.statusName = statusName;
    }

    public LeadStatus(String statusName) {
        this.statusName = statusName;
    }

    // METHOD (sesuai class diagram)

    public String getStatusDetail() {
        return "Status #" + this.id + " - " + this.statusName;
    }

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
    }

    // METHOD TAMBAHAN (utilitas)

    public static List<String> getRiwayatStatus() {
        return riwayatStatus;
    }

    // GETTER & SETTER

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatusName() {
        return this.statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    // OVERRIDE toString()

    @Override
    public String toString() {
        return "LeadStatus{"
                + "id=" + this.id
                + ", statusName='" + this.statusName + "'"
                + "}";
    }
}