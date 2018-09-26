package com.ericyuegu.whaddyahavin.model;

public class Meal {
    private String title;
    private String desc;
    private String image;
    private String imageUrl;

    public  Meal() {
        //emptry constructor
    }

    public Meal(String title, String desc, String imageUrl) {
        this.title = title;
        this.desc = desc;
        //this.image = image;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
