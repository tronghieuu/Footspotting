package com.team4of5.foodspotting.notification;

import java.io.Serializable;

public class News implements Serializable {
    public News(String tenquan, String image, String title, String content, String address) {
        this.tenquan = tenquan;
        this.image = image;
        this.title = title;
        this.content = content;
        this.address = address;
    }
    public News(){
        this.tenquan = "";
        this.image = "";
        this.title = "";
        this.content = "";
        this.address = "";
    };

    public String getTenquan() {
        return tenquan;
    }

    public void setTenquan(String tenquan) {
        this.tenquan = tenquan;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String tenquan;
    private String image;
    private String title;
    private String content;
    private String address;
}
