package com.leadestate.model;

import java.util.HashMap;
import java.util.Map;

public class Report {

    private String title;
    private String summary;
    private Map<String, Object> data;
    
    public Report() {
        this.data = new HashMap<>();
    }

    public Report(String title, String summary) {
        this.title = title;
        this.summary = summary;
        this.data = new HashMap<>();
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return this.summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Map<String, Object> getData() {
        return this.data;
    }

    public void setData(Map<String, Object> data) {
        this.data = (data != null) ? data : new HashMap<>();
    }

    public void tambahData(String key, Object value) {
        this.data.put(key, value);
    }

    @Override
    public String toString() {
        return "Report{"
                + "title='" + this.title + "'"
                + ", summary='" + this.summary + "'"
                + ", data=" + this.data
                + "}";
    }
}
