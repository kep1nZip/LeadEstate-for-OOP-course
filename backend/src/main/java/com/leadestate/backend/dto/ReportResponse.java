package com.leadestate.backend.dto;

public class ReportResponse {

    private long totalLeads;
    private long totalFollowUps;
    private long closedLeads;
    private long activeLeads;
    private double estimatedRevenue;

    // tetap ada agar tidak breaking
    public ReportResponse(long totalLeads, long totalFollowUps, long closedLeads, long activeLeads) {
        this.totalLeads       = totalLeads;
        this.totalFollowUps   = totalFollowUps;
        this.closedLeads      = closedLeads;
        this.activeLeads      = activeLeads;
        this.estimatedRevenue = 0;
    }

    // Constructor baru dengan revenue
    public ReportResponse(long totalLeads, long totalFollowUps, long closedLeads, long activeLeads, double estimatedRevenue) {
        this.totalLeads       = totalLeads;
        this.totalFollowUps   = totalFollowUps;
        this.closedLeads      = closedLeads;
        this.activeLeads      = activeLeads;
        this.estimatedRevenue = estimatedRevenue;
    }

    public long getTotalLeads()         { return totalLeads; }
    public long getTotalFollowUps()     { return totalFollowUps; }
    public long getClosedLeads()        { return closedLeads; }
    public long getActiveLeads()        { return activeLeads; }
    public double getEstimatedRevenue() { return estimatedRevenue; }
}