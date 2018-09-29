package com.ericyuegu.whaddyahavin;

public class Meal {

    private String mealName, timestamp, tags, photoUrl, description;

    public Meal() { }

    public Meal(String mealName, String timestamp, String tags, String photoUrl, String description) {
        this.mealName = mealName;
        this.timestamp = timestamp;
        this.tags = tags;
        this.photoUrl = photoUrl;
        this.description = description;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String name) {
        this.mealName = name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getPhotoUrl() { return photoUrl; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return getMealName() + "; " + getTimestamp() + "; " + getTags() + "; " + getPhotoUrl() + "; " + getDescription();
    }
}

