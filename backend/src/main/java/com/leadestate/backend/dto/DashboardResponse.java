package com.leadestate.backend.dto;

import java.util.List;

public class DashboardResponse {

    private long totalLeads;
    private long totalFollowUps;

    private long pendingFollowUps;
    private long doneFollowUps;
    private long cancelledFollowUps;

    private long pendingReminders;
    private long doneReminders;

    private long closedLeads;
    private long activeLeads;
    private long todayFollowups;
    private long pendingLeads;
    private long monthlyClosing;

    private List<ChartResponse> chartData;
    private List<TopSalesResponse> topSales;
    private List<ReminderResponse> reminders;

    public DashboardResponse() {}

    // Constructor Lengkap
    public DashboardResponse(long totalLeads, long totalFollowUps,
                            long pendingFollowUps, long doneFollowUps, long cancelledFollowUps,
                            long pendingReminders, long doneReminders,
                            long closedLeads, long activeLeads,
                            List<ChartResponse> chartData,
                            List<TopSalesResponse> topSales,
                            List<ReminderResponse> reminders) {

        this.totalLeads = totalLeads;
        this.totalFollowUps = totalFollowUps;
        this.pendingFollowUps = pendingFollowUps;
        this.doneFollowUps = doneFollowUps;
        this.cancelledFollowUps = cancelledFollowUps;
        this.pendingReminders = pendingReminders;
        this.doneReminders = doneReminders;
        this.closedLeads = closedLeads;
        this.activeLeads = activeLeads;
        this.chartData = chartData;
        this.topSales = topSales;
        this.reminders = reminders;
    }

    // --- GETTER ---
    public long getTotalLeads() { return totalLeads; }
    public long getTotalFollowUps() { return totalFollowUps; }
    public long getPendingFollowUps() { return pendingFollowUps; }
    public long getDoneFollowUps() { return doneFollowUps; }
    public long getCancelledFollowUps() { return cancelledFollowUps; }
    public long getPendingReminders() { return pendingReminders; }
    public long getDoneReminders() { return doneReminders; }
    public long getClosedLeads() { return closedLeads; }
    public long getActiveLeads() { return activeLeads; }
    public List<ChartResponse> getChartData() { return chartData; }
    public List<TopSalesResponse> getTopSales() { return topSales; }
    public List<ReminderResponse> getReminders() { return reminders; }
    public long getTodayFollowups() { return todayFollowups;}
    public long getPendingLeads() { return pendingLeads;}
    public long getMonthlyClosing() { return monthlyClosing;}

    // --- SETTER ---
    public void setTotalLeads(long totalLeads) { this.totalLeads = totalLeads; }
    public void setTotalFollowUps(long totalFollowUps) { this.totalFollowUps = totalFollowUps; }
    public void setPendingFollowUps(long pendingFollowUps) { this.pendingFollowUps = pendingFollowUps; }
    public void setDoneFollowUps(long doneFollowUps) { this.doneFollowUps = doneFollowUps; }
    public void setCancelledFollowUps(long cancelledFollowUps) { this.cancelledFollowUps = cancelledFollowUps; }
    public void setPendingReminders(long pendingReminders) { this.pendingReminders = pendingReminders; }
    public void setDoneReminders(long doneReminders) { this.doneReminders = doneReminders; }
    public void setClosedLeads(long closedLeads) { this.closedLeads = closedLeads; }
    public void setActiveLeads(long activeLeads) { this.activeLeads = activeLeads; }
    public void setChartData(List<ChartResponse> chartData) { this.chartData = chartData; }
    public void setTopSales(List<TopSalesResponse> topSales) { this.topSales = topSales; }
    public void setReminders(List<ReminderResponse> reminders) { this.reminders = reminders; }
    public void setTodayFollowups(long todayFollowups) { this.todayFollowups = todayFollowups; }
    public void setPendingLeads(long pendingLeads) { this.pendingLeads = pendingLeads; }
    public void setMonthlyClosing(long monthlyClosing) { this.monthlyClosing = monthlyClosing; }
}