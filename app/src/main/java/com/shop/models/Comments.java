package com.shop.models;

// product comments class model
public class Comments {
    private String content, userImg, userEmail, date, time;

    public Comments(String content, String userImg, String userName, String userEmail, String date, String time) {
        this.content = content;
        this.userImg = userImg;
        this.userEmail = userEmail;
        this.date = date;
        this.time = time;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Comments() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
