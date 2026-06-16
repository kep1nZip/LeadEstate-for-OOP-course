package com.leadestate.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leadestate.backend.dto.DashboardResponse;
import com.leadestate.backend.dto.ChartResponse;
import com.leadestate.backend.dto.TopSalesResponse;
import com.leadestate.backend.dto.ReminderResponse;
import com.leadestate.backend.repository.LeadRepository;
import com.leadestate.backend.repository.FollowUpRepository;
import com.leadestate.backend.repository.ReminderRepository;

import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter;

@Service
public class DashboardService {

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private FollowUpRepository followUpRepository;

    @Autowired
    private ReminderRepository reminderRepository;

    public DashboardResponse getDashboardData(String role, Integer userId) {
        long totalLeads = leadRepository.count();

        long totalFollowUps = followUpRepository.count();
        long pendingFollowUps = followUpRepository.countByStatus("pending");
        long doneFollowUps = followUpRepository.countByStatus("done");
        long cancelledFollowUps = followUpRepository.countByStatus("cancelled");

        long pendingReminders = pendingFollowUps;
        long doneReminders = doneFollowUps;

        long closedLeads = leadRepository.countByStatus_Id(5); // status 5 = Closed
        long activeLeads = totalLeads - closedLeads;
        
        DashboardResponse response = new DashboardResponse(
            totalLeads,
            totalFollowUps,
            pendingFollowUps,
            doneFollowUps,
            cancelledFollowUps,
            pendingReminders,
            doneReminders,
            closedLeads,
            activeLeads,
            null,
            null,
            null
        );

        // ================= CHART DATA =================
        List<Object[]> chartResults = leadRepository.getChartData();
        List<ChartResponse> chartData = new ArrayList<>();

        String[] months = {
            "", "Jan", "Feb", "Mar", "Apr", "Mei",
            "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des"
        };

        for (Object[] row : chartResults) {
            int month = ((Number) row[0]).intValue();
            long leads = ((Number) row[1]).longValue();
            long closing = ((Number) row[2]).longValue();

            chartData.add(new ChartResponse(
                months[month],
                (int) leads,
                (int) closing
            ));
        }
        response.setChartData(chartData);

        // ================= TOP SALES =================
        List<Object[]> salesResults = leadRepository.getTopSales();
        List<TopSalesResponse> topSales = new ArrayList<>();

        String[] colors = {
            "#f59e0b",
            "#6366f1",
            "#10b981",
            "#ef4444",
            "#8b5cf6"
        };

        int i = 0;
        for (Object[] row : salesResults) {
            String name = (String) row[0];
            long closing = ((Number) row[1]).longValue();
            String initials = (
                name != null && !name.isBlank()
            )
            ? name.substring(0,1).toUpperCase()
            : "?";

            topSales.add(new TopSalesResponse(
                name,
                initials,
                (int) closing * 2,
                (int) closing,
                colors[i % colors.length]
            ));
            i++;
        }
        response.setTopSales(topSales);

        // ================= REMINDERS =================
        List<Object[]> reminderResults;
        if ("admin".equalsIgnoreCase(role)) {
            reminderResults =
                    reminderRepository.getTodayReminders();
        } else {
            reminderResults =
                    reminderRepository.getTodayRemindersBySales(userId);
        }

        List<ReminderResponse> reminders = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        for (Object[] row : reminderResults) {
            String leadName = (String) row[0];
            String propertyName = (String) row[1];
            
            java.time.LocalDateTime followupDate = null;
            if (row[2] != null) {
                if (row[2] instanceof java.sql.Timestamp ts) {
                    followupDate = ts.toLocalDateTime();
                } else {
                    followupDate = (java.time.LocalDateTime) row[2];
                }
            }

            String status = (String) row[3];

            reminders.add(new ReminderResponse(
                leadName,
                (
                    leadName != null &&
                    !leadName.isBlank()
                )
                ? leadName.substring(0,1).toUpperCase()
                : "?",
                "🏠 " + (
                    propertyName != null
                    ? propertyName
                    : "Tanpa Property"
                ),
                "H+1",
                followupDate != null
                    ? followupDate.format(formatter)
                    : "-",
                "done".equalsIgnoreCase(status) ? "today" : "soon",
                "#f59e0b"
            ));
        }
        response.setReminders(reminders);

        // FOLLOW UP HARI INI
        response.setTodayFollowups(pendingReminders);

        // LEAD TERTUNDA
        response.setPendingLeads(pendingReminders);

        // CLOSING BULAN INI
        response.setMonthlyClosing(closedLeads);


        return response;
    }

    /**
     * Mengambil statistik Leads berdasarkan Status
     */
    public List<ChartResponse> getLeadsByStatus() {
        List<Object[]> results = leadRepository.countLeadsByStatus();
        List<ChartResponse> response = new ArrayList<>();

        for (Object[] row : results) {
            String statusName = (String) row[1];
            long total = ((Number) row[2]).longValue(); 

            response.add(new ChartResponse(statusName, total));
        }

        return response;
    }

    public List<ChartResponse> getLeadsPerMonth() {
        List<Object[]> results = leadRepository.countLeadsPerMonth();
        List<ChartResponse> response = new ArrayList<>();

        for (Object[] row : results) {
            int month = ((Number) row[0]).intValue();
            long total = ((Number) row[1]).longValue();

            response.add(new ChartResponse(String.valueOf(month), total));
        }

        return response;
    }

    public List<ChartResponse> getSalesPerformance() {
        List<Object[]> results = leadRepository.countLeadsBySales();
        List<ChartResponse> response = new ArrayList<>();

        for (Object[] row : results) {
            Integer salesId = (Integer) row[0];
            long total = ((Number) row[1]).longValue();

            response.add(new ChartResponse("Sales " + salesId, total));
        }

        return response;
    }
}