package com.leadestate.backend.dto;

public class TopSalesResponse {

    private String name;
    private String initials;
    private int followUp;
    private int closing;
    private String color;

    public TopSalesResponse() {}

    public TopSalesResponse(String name, String initials, int followUp, int closing, String color) {
        this.name = name;
        this.initials = initials;
        this.followUp = followUp;
        this.closing = closing;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getInitials() {
        return initials;
    }

    public int getFollowUp() {
        return followUp;
    }

    public int getClosing() {
        return closing;
    }

    public String getColor() {
        return color;
    }
}