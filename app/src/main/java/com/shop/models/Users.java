package com.shop.models;

// user class model
public class Users {
    private String email;
    private String name;
    private String password;
    private String image;
    private String address;

    public Users() {
    }

    public Users(String email, String name, String password, String image, String address) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.image = image;
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
