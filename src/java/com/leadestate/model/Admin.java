package com.leadestate.model;

import java.util.ArrayList;
import java.util.List;


public class Admin extends User {

    // ===== Konstanta level akses =====

    public static final int ACCESS_LEVEL = 3;

    public static final int ROLE_ID = 1;

    /** Nama role untuk Admin, sesuai dengan penamaan pada class diagram. */
    public static final String ROLE_NAME = "Admin";

    private static List<String> logArsip = new ArrayList<>();

    // ===== Constructor =====

    public Admin(int id, String name, String email, String password) {
        super(id, name, email, password, ROLE_ID);
    }

    // ===== Method sesuai class diagram =====

    public void manageUsers() {
        System.out.println("[Admin] " + this.getName()
                + " membuka halaman manajemen User.");
    }


    public void manageProperties() {
        System.out.println("[Admin] " + this.getName()
                + " membuka halaman manajemen Property.");
    }

    public Report viewAllReports() {
        System.out.println("[Admin] " + this.getName()
                + " menampilkan seluruh laporan sistem.");

        Report report = new Report(
                "Laporan Keseluruhan Sistem",
                "Ringkasan laporan untuk seluruh Lead, Sales, dan closing rate."
        );

        return report;
    }

    public void assignLeadToSales(int leadId, int salesId) {
        System.out.println("[Admin] Lead id=" + leadId
                + " ditugaskan ke Sales id=" + salesId + ".");
    }

    public void archiveData(String entity, int id) {
        String catatan = "[Arsip] Entity='" + entity + "', id=" + id
                + ", oleh Admin='" + this.getName() + "'";
        logArsip.add(catatan);
        System.out.println("[Admin] " + catatan);
    }

    public static List<String> getLogArsip() {
        return logArsip;
    }

    // ===== Implementasi method abstract dari User =====

    @Override
    public String getRoleName() {
        return ROLE_NAME;
    }

    @Override
    public int getAccessLevel() {
        return ACCESS_LEVEL;
    }

    // ===== Override toString() =====

    @Override
    public String toString() {
        return "Admin{"
                + "id=" + this.getId()
                + ", name='" + this.getName() + "'"
                + ", email='" + this.getEmail() + "'"
                + ", roleId=" + this.getRoleId()
                + "}";
    }
}