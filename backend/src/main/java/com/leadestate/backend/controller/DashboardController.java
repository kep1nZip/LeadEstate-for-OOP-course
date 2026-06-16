package com.leadestate.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.leadestate.backend.dto.ChartResponse;
import com.leadestate.backend.service.DashboardService;
import com.leadestate.backend.service.AuthService;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private AuthService authService;

    /**
     * Dashboard Summary
     * Admin only
     */
    @GetMapping
    public ResponseEntity<?> getDashboard(
            @RequestHeader("Role") String role,
            @RequestHeader("User-Id") Integer userId
    ) {

        authService.checkSalesOrAdmin(role);

        return ResponseEntity.ok(
                dashboardService.getDashboardData(role, userId)
        );
    }

    /**
     * Leads by Status Chart
     * Admin only
     */
    @GetMapping("/leads-by-status")
    public List<ChartResponse> getLeadsByStatus(
            @RequestHeader("Role") String role
    ) {

        authService.checkAdmin(role);

        return dashboardService.getLeadsByStatus();
    }

    /**
     * Sales Performance Chart
     * Admin only
     */
    @GetMapping("/sales-performance")
    public List<ChartResponse> getSalesPerformance(
            @RequestHeader("Role") String role
    ) {

        authService.checkAdmin(role);

        return dashboardService.getSalesPerformance();
    }
}