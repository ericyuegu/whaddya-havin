package com.ericyuegu.whaddyahavin;

import java.util.ArrayList;

public class Meal {

    private String name, timestamp, url;
    private ArrayList<String> tags;

    public Meal(String name, String timestamp, ArrayList<String> tags, String url) {
        this.name = name;
        this.timestamp = timestamp;
        this.tags = tags;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setList(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getUrl() { return url; }

}

