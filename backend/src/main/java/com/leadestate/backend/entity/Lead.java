package com.leadestate.backend.entity;

import jakarta.persistence.*; 
import lombok.Data;          

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


    @Column(name = "property_id")
    private int propertyId;

    @Column(name = "sales_id")
    private int salesId;

    @Column(name = "status_id")
    private int statusId;

    private String source;

    @Column(name = "created_at", insertable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

    public void changeStatus(int newStatusId) {
        this.statusId = newStatusId;
    }

    public void assignToSales(int newSalesId) {
        this.salesId = newSalesId;
    }
}