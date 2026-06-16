package com.leadestate.backend.dto;

public class ReminderResponse {

    private String name;
    private String initials;
    private String prop;
    private String h;
    private String time;
    private String tag;
    private String color;

    public ReminderResponse() {}

    public ReminderResponse(String name, String initials, String prop,
                            String h, String time, String tag, String color) {
        this.name = name;
        this.initials = initials;
        this.prop = prop;
        this.h = h;
        this.time = time;
        this.tag = tag;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getInitials() {
        return initials;
    }

    public String getProp() {
        return prop;
    }

    public String getH() {
        return h;
    }

    public String getTime() {
        return time;
    }

    public String getTag() {
        return tag;
    }

    public String getColor() {
        return color;
    }
}
