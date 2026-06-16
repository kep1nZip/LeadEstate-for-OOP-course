package com.leadestate.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leadestate.backend.repository.FollowUpRepository;
import com.leadestate.backend.repository.LeadRepository;
import com.leadestate.backend.dto.ChartResponse;
import com.leadestate.backend.entity.Lead;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReportService {

    @Autowired
    private FollowUpRepository followUpRepository;

    @Autowired
    private LeadRepository leadRepository;

    /**
     * Mengambil laporan jumlah follow-up berdasarkan status
     */
    public List<ChartResponse> getFollowUpStatusReport() {
        List<Object[]> results = followUpRepository.countFollowUpGroupByStatus();
        List<ChartResponse> response = new ArrayList<>();

        for (Object[] row : results) {
            String status = (String) row[0];
            long count = ((Number) row[1]).longValue();
            response.add(new ChartResponse(status, count));
        }

        return response;
    }

    /**
     * Mengambil laporan jumlah lead berdasarkan status
     */ 
    public List<ChartResponse> getLeadStatusReport() {
        List<Object[]> results = leadRepository.countLeadsByStatus();
        List<ChartResponse> response = new ArrayList<>();

        for (Object[] row : results) {
            String statusName = row[0] != null ? (String) row[0] : "Tidak Ada Status";
            long count = ((Number) row[1]).longValue();

            response.add(new ChartResponse(statusName, count));
        }

        return response;
    }

    public List<ChartResponse> getLeadPerMonthReport() {

        List<Object[]> results = leadRepository.getChartData();
        List<ChartResponse> response = new ArrayList<>();

        String[] months = {"Jan","Feb","Mar","Apr","Mei","Jun","Jul","Agu","Sep","Okt","Nov","Des"};

        for (Object[] row : results) {
            int monthIndex = ((Number) row[0]).intValue();
            long leads     = ((Number) row[1]).longValue();
            long closing   = ((Number) row[2]).longValue();

            response.add(new ChartResponse(months[monthIndex - 1], (int) leads, (int) closing));
        }

        return response;
    }

    public List<ChartResponse> getSalesPerformanceReport() {

        List<Object[]> results = leadRepository.getTopSales();
        List<ChartResponse> response = new ArrayList<>();

        for (Object[] row : results) {
            String salesName = row[0] != null ? (String) row[0] : "Unknown";
            long closingCount = ((Number) row[1]).longValue();

            response.add(new ChartResponse(salesName, 0, (int) closingCount));
        }

        return response;
    }

    // Sumber Lead (field source di tabel leads)
    public List<ChartResponse> getLeadSourceReport() {
        List<Object[]> results = leadRepository.countLeadsBySource();
        List<ChartResponse> response = new ArrayList<>();

        for (Object[] row : results) {
            String source = row[0] != null ? (String) row[0] : "Lainnya";
            long count = ((Number) row[1]).longValue();
            response.add(new ChartResponse(source, count));
        }

        return response;
    }

    // FILTER LEAD
    public List<Lead> filterLeads(
        Integer salesId,
        Integer propertyId,
        LocalDateTime start,
        LocalDateTime end
    ) {
        return leadRepository.filterLeads(salesId, propertyId, start, end);
    }
}