package com.leadestate.backend.controller;

import com.leadestate.backend.dto.FollowUpRequest;
import com.leadestate.backend.entity.FollowUp;
import com.leadestate.backend.service.FollowUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/followups")
public class FollowUpController {

    @Autowired
    private FollowUpService followUpService;

    // FR 8 - POST /followups
    @PostMapping
    public ResponseEntity<?> createFollowUp(@RequestBody FollowUpRequest request) {
        try {
            FollowUp savedFollowUp = followUpService.createFollowUp(request);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Follow-up berhasil dibuat");
            response.put("data", savedFollowUp);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Gagal membuat follow-up");
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @GetMapping("/lead/{leadId}")
    public ResponseEntity<?> getByLead(
            @PathVariable Long leadId
    ){
        return ResponseEntity.ok(
            followUpService.getByLeadId(leadId)
        );
    }
}
