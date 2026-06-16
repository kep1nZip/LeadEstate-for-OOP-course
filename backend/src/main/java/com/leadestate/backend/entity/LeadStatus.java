package com.leadestate.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "lead_status")
@Data
public class LeadStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "status_name")
    private String statusName;
}