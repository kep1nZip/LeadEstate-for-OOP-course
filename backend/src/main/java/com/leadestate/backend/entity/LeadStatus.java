package com.leadestate.backend.entity;

public class LeadStatus {
    private int id;
    private String statusName; // New Lead, Contacted, Follow Up, dll

    public String getStatusDetail() { return this.statusName; }
}