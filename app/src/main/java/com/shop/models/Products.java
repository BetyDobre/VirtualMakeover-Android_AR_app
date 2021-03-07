package com.shop.models;

// products class model
public class Products {
    private String pname, description, image, category, pid, date, time;
    private double price;

    public Products() {
    }

    public Products(String pname, String description, String image, String category, String pid, String date, String time, double price) {
        this.pname = pname;
        this.description = description;
        this.image = image;
        this.category = category;
        this.pid = pid;
        this.date = date;
        this.time = time;
        this.price = price;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
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

    @Override
    public String toString() {
        return "Products{" +
                "pname='" + pname + '\'' +
                ", description='" + description + '\'' +
                ", price='" + price + '\'' +
                ", image='" + image + '\'' +
                ", category='" + category + '\'' +
                ", pid='" + pid + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
