package com.leadestate.backend.controller;

import com.leadestate.backend.dto.ReportResponse;
import com.leadestate.backend.service.LeadService;
import com.leadestate.backend.service.ReportService;
import com.leadestate.backend.service.AuthService; // Import AuthService

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private LeadService leadService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private AuthService authService;

    // validasi Admin
    @GetMapping
    public ReportResponse getReport(
            @RequestHeader("Role") String role
    ) {
        authService.checkAdmin(role);
        return leadService.getLeadStats();
    }

    @GetMapping("/followup-status")
    public Object getFollowUpStatusReport(
            @RequestHeader("Role") String role
    ) {
        authService.checkAdmin(role);
        return reportService.getFollowUpStatusReport();
    }

    @GetMapping("/lead-status")
    public Object getLeadStatusReport(
            @RequestHeader("Role") String role
    ) {
        authService.checkAdmin(role);
        return reportService.getLeadStatusReport();
    }

    // Lead per Month
    @GetMapping("/lead-per-month")
    public Object getLeadPerMonthReport(
            @RequestHeader("Role") String role
    ) {
        authService.checkAdmin(role);
        return reportService.getLeadPerMonthReport();
    }

    // Performance per Sales
    @GetMapping("/sales-performance")
    public Object getSalesPerformanceReport(
            @RequestHeader("Role") String role
    ) {
        authService.checkAdmin(role);
        return reportService.getSalesPerformanceReport();
    }

    // Sumber Lead
    @GetMapping("/lead-source")
    public Object getLeadSourceReport(
            @RequestHeader("Role") String role
    ) {
        authService.checkAdmin(role);
        return reportService.getLeadSourceReport();
    }

    // FilterLeads
    @GetMapping("/leads")
    public Object filterLeads(
            @RequestHeader("Role") String role,
            @RequestParam(required = false) Integer salesId,
            @RequestParam(required = false) Integer propertyId,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end
    ) {
        authService.checkAdmin(role);

        LocalDateTime startDate = start != null ? LocalDateTime.parse(start + "T00:00:00") : null;
        LocalDateTime endDate = end != null ? LocalDateTime.parse(end + "T23:59:59") : null;

        return reportService.filterLeads(salesId, propertyId, startDate, endDate);
    }
}