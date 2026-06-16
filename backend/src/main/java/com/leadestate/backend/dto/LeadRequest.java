package com.leadestate.backend.dto;

import lombok.Data;

@Data
public class LeadRequest {

    private String name;
    private String phone;
    private String email;
    private Integer propertyId;
    private Integer salesId;
    private Integer statusId;
    private String source;

}