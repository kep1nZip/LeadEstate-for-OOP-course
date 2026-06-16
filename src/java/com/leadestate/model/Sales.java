package com.leadestate.model;

import java.util.ArrayList;
import java.util.List;

public class Sales extends User {

    public static final int ACCESS_LEVEL = 1;
    public static final int ROLE_ID = 2;

    public static final String ROLE_NAME = "Sales";

    // ===== Constructor =====
    public Sales(int id, String name, String email, String password) {
        super(id, name, email, password, ROLE_ID);
    }

    // ===== Method =====
    public void addLead(Lead lead) {
        if (lead == null) {
            System.out.println("[Sales] Lead tidak boleh null.");
            return;
        }
        lead.setSalesId(this.getId());
        System.out.println("[Sales] " + this.getName()
                + " menambahkan lead baru: " + lead.getName());
    }

    public void doFollowUp(int leadId) {
        System.out.println("[Sales] " + this.getName()
                + " melakukan follow-up untuk leadId=" + leadId + ".");
    }

    public void updateLeadStatus(int leadId, int statusId) {
        System.out.println("[Sales] " + this.getName()
                + " memperbarui status leadId=" + leadId
                + " menjadi statusId=" + statusId + ".");
    }

    public List<Lead> viewMyLeads() {
        System.out.println("[Sales] " + this.getName()
                + " menampilkan daftar Lead miliknya.");

        return new ArrayList<>();
    }

    public Report viewMyPerformance() {
        System.out.println("[Sales] " + this.getName()
                + " menampilkan laporan performa pribadi.");

        Report report = new Report(
                "Laporan Performa " + this.getName(),
                "Ringkasan performa penjualan untuk Sales id=" + this.getId() + "."
        );

        return report;
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
        return "Sales{"
                + "id=" + this.getId()
                + ", name='" + this.getName() + "'"
                + ", email='" + this.getEmail() + "'"
                + ", roleId=" + this.getRoleId()
                + "}";
    }
}