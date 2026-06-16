package com.leadestate.backend.service;

import com.leadestate.backend.dto.FollowUpRequest;
import com.leadestate.backend.entity.FollowUp;

import com.leadestate.backend.repository.FollowUpRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FollowUpService {

    @Autowired
    private FollowUpRepository followUpRepository;


    public List<FollowUp> getByLeadId(Long leadId) {
        return followUpRepository.findByLeadIdOrderByCreatedAtDesc(leadId);
    }

    // FR 8 - Create / Update FollowUp (Upsert State)
    public FollowUp createFollowUp(FollowUpRequest request) {

        // ── 1. VALIDASI WAJIB INDEPENDEN ─────────────────────────────────────
        if (request.getFollowupDate() == null) {
            throw new RuntimeException("Tanggal follow up wajib diisi");
        }

        if (request.getSalesId() == null) {
            throw new RuntimeException("Sales wajib diisi");
        }
        
        if (request.getLeadId() == null) {
            throw new RuntimeException("Lead wajib diisi");
        }

        // ── 2. CEK & UPDATE JIKA ADA FOLLOW-UP AKTIF ─────────────────────────
        List<FollowUp> existing =
            followUpRepository.findByLeadIdOrderByCreatedAtDesc(request.getLeadId());

        FollowUp saved;

        if (!existing.isEmpty()) {
            FollowUp last = existing.get(0);

            if (!"done".equalsIgnoreCase(last.getStatus())) {
                last.setNotes(request.getNotes());
                last.setFollowupDate(request.getFollowupDate());
                
                String status = request.getStatus();
                if ("done".equalsIgnoreCase(status)) {
                    last.setStatus("done");
                } else {
                    last.setStatus("pending");
                }

                saved = followUpRepository.save(last);
                
                return saved;
            }
        }

        // ── 3. JIKA TIDAK ADA YANG AKTIF → BUAT BARU ─────────────────────────
        FollowUp followUp = new FollowUp();
        followUp.setLeadId(request.getLeadId());
        followUp.setSalesId(request.getSalesId());
        followUp.setNotes(request.getNotes());
        followUp.setFollowupDate(request.getFollowupDate());
        
        if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            followUp.setStatus(request.getStatus());
        } else {
            followUp.setStatus("pending");
        }

        saved = followUpRepository.save(followUp);

        return saved;
    }
}