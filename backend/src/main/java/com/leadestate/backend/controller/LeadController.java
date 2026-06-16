package com.leadestate.backend.controller;

import com.leadestate.backend.dto.LeadRequest;
import com.leadestate.backend.entity.Lead;
import com.leadestate.backend.entity.User;
import com.leadestate.backend.repository.LeadRepository;
import com.leadestate.backend.repository.UserRepository;
import com.leadestate.backend.service.LeadService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leads")
public class LeadController {

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private LeadService leadService;

    @Autowired
    private UserRepository userRepository;

    // =========================
    // FR5 - Tambah Lead
    // =========================
    @PostMapping
    public Lead createLead(@RequestBody LeadRequest request) {
        return leadService.createLead(request);
    }

    // =========================
    // FR6 - Get All Leads (SERVICE)
    // =========================
    @GetMapping
    public List<Lead> getLeads(@RequestParam int userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        return leadService.getLeadsByUser(user);
    }

    // =========================
    // FR6 versi RAW
    // =========================
    @GetMapping("/raw")
    public List<Lead> getAllLeadsRaw() {
        return leadRepository.findAll();
    }

    // =========================
    // FR7 - Update Status
    // =========================
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable int id,
            @RequestBody Map<String, Integer> body
    ) {
        return ResponseEntity.ok(
            leadService.updateStatus(id, body.get("statusId"))
        );
    }
    // =========================
// FR - Edit Lead
// =========================
@PutMapping("/{id}")
public ResponseEntity<?> updateLead(
        @PathVariable int id,
        @RequestBody LeadRequest request
) {

    return ResponseEntity.ok(
            leadService.updateLead(id, request)
    );
}
    
}