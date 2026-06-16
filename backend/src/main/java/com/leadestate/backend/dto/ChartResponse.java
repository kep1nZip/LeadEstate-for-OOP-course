package com.leadestate.backend.dto;

public class ChartResponse {

    private String m;
    private int leads;
    private int closing;

    // Constructor 1: Untuk Dashboard Chart
    public ChartResponse(String m, int leads, int closing) {
        this.m = m;
        this.leads = leads;
        this.closing = closing;
    }

    // Constructor 2: Untuk statistik umum (2 Parameter)
    public ChartResponse(String m, long leads) {
        this.m = m;
        this.leads = (int) leads;
        this.closing = 0; // Default closing 0
    }

    // Getter & Setter
    public String getM() { return m; }
    public void setM(String m) { this.m = m; }
    public int getLeads() { return leads; }
    public void setLeads(int leads) { this.leads = leads; }
    public int getClosing() { return closing; }
    public void setClosing(int closing) { this.closing = closing; }
}