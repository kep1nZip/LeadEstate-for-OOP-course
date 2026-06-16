package com.leadestate.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "leads")
@Data
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String phone;
    private String email;

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;

    @ManyToOne
    @JoinColumn(name = "sales_id")
    private User sales;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private LeadStatus status;

    private String source;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public void changeStatus(LeadStatus newStatus) {
        this.status = newStatus;
    }

    public void assignToSales(User newSales) {
        this.sales = newSales;
    }
}