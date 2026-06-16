package com.leadestate.backend.dto;

import java.time.LocalDateTime;

public class FollowUpRequest {

    private Long leadId;
    private Long salesId;
    private String notes;
    private LocalDateTime followupDate;
    private String status;

    // Getters and Setters
    public Long getLeadId() { return leadId; }
    public void setLeadId(Long leadId) { this.leadId = leadId; }

    public Long getSalesId() { return salesId; }
    public void setSalesId(Long salesId) { this.salesId = salesId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getFollowupDate() { return followupDate; }
    public void setFollowupDate(LocalDateTime followupDate) { this.followupDate = followupDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
