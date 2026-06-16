package com.leadestate.backend.service;

import com.leadestate.backend.entity.Property;
import com.leadestate.backend.repository.PropertyRepository;
import com.leadestate.backend.repository.UserRepository;

import com.leadestate.backend.dto.LeadRequest;
import com.leadestate.backend.dto.ReportResponse;
import com.leadestate.backend.entity.Lead;
import com.leadestate.backend.entity.LeadStatus;
import com.leadestate.backend.entity.User;
import com.leadestate.backend.repository.LeadRepository;
import com.leadestate.backend.repository.LeadStatusRepository;
import com.leadestate.backend.repository.FollowUpRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeadService {

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private FollowUpRepository followUpRepository;

    @Autowired
    private LeadStatusRepository leadStatusRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Lead> getLeadsByUser(User user) {
        if (user.isAdmin()) {
            return leadRepository.findAll();
        } else {
            return leadRepository.findBySales_Id(user.getId());
        }
    }

    // =========================
    // FR5 - Tambah Lead
    // =========================
    public Lead createLead(LeadRequest request) {
        Lead lead = new Lead();

        lead.setName(
            request.getName()
        );

        lead.setPhone(
            request.getPhone()
        );

        lead.setEmail(
            request.getEmail()
        );

        Property property =
            propertyRepository
                .findById(
                    request.getPropertyId()
                )
                .orElseThrow(
                    () -> new RuntimeException(
                        "Property tidak ditemukan"
                    )
                );

        User sales =
            userRepository
                .findById(
                    request.getSalesId()
                )
                .orElseThrow(
                    () -> new RuntimeException(
                        "Sales tidak ditemukan"
                    )
                );

        lead.setProperty(property);

        lead.setSales(sales);

        LeadStatus status =
            leadStatusRepository
            .findById(
                request.getStatusId()
            )
            .orElseThrow(
                () -> new RuntimeException(
                    "Status tidak ditemukan"
                )
            );

        lead.setStatus(status);

        lead.setSource(
            request.getSource()
        );

        Lead savedLead =
            leadRepository.save(
                lead
            );

        return savedLead;
    }

    // =========================
    // FR6 - Get All Leads
    // =========================
    public List<Lead> getAllLeads() {
        return leadRepository.findAll();
    }

    // =========================
    // FR7 - Update Status Lead
    // =========================
    public Lead updateStatus(
        int id,
        int statusId
    ) {

        Lead lead =
                leadRepository
                .findById(id)
                .orElseThrow(
                        () ->
                        new RuntimeException(
                                "Lead tidak ditemukan"
                        )
                );

        LeadStatus status =
                leadStatusRepository
                .findById(
                        statusId
                )
                .orElseThrow(
                        () ->
                        new RuntimeException(
                                "Status tidak ditemukan"
                        )
                );

        lead.changeStatus(
                status
        );

        Lead saved =
                leadRepository.save(
                        lead
                );

        return saved;
    }

    // =========================
    // FR - Edit Lead
    // =========================
    public Lead updateLead(int id, LeadRequest request) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead tidak ditemukan"));

        lead.setName(request.getName());
        lead.setPhone(request.getPhone());
        lead.setEmail(request.getEmail());
        Property property =
            propertyRepository
                .findById(
                    request.getPropertyId()
                )
                .orElseThrow(
                    () -> new RuntimeException(
                        "Property tidak ditemukan"
                    )
                );

        User sales =
            userRepository
                .findById(
                    request.getSalesId()
                )
                .orElseThrow(
                    () -> new RuntimeException(
                        "Sales tidak ditemukan"
                    )
                );

        lead.setProperty(property);

        lead.setSales(sales);
        lead.setSource(request.getSource());

        // update status
        LeadStatus status = leadStatusRepository.findById(request.getStatusId())
                .orElseThrow(() -> new RuntimeException("Status tidak ditemukan"));

        lead.setStatus(status);

        Lead saved =
                leadRepository.save(
                        lead
                );

        return saved;
    }

    // =========================
    // REPORT - Statistik Lead
    // =========================
    public ReportResponse getLeadStats() {
        long totalLeads     = leadRepository.count();
        long totalFollowUps = followUpRepository.count();
        long closedLeads    = leadRepository.countByStatus_Id(5); // status_id = 5 = Closed
        long activeLeads    = totalLeads - closedLeads;
        
        double estimatedRevenue = leadRepository.sumEstimatedRevenue();

        return new ReportResponse(
                totalLeads,
                totalFollowUps,
                closedLeads,
                activeLeads,
                estimatedRevenue 
        );
    }
}