package com.leadestate.backend.entity;

public class Lead {
    private int id;
    private String name;
    private String phone;
    private String email;
    private int propertyId;
    private int salesId;
    private int statusId;
    private String source; 
    
    public void changeStatus(int newStatusId) { }
    
    public void assignToSales(int newSalesId) { }
}