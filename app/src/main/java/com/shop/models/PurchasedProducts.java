package com.shop.models;

public class PurchasedProducts {
    String date, image, pid, pname, quantity, time;
    double price;
    int discount;

    public PurchasedProducts() {
    }

    public PurchasedProducts(String date, int discount, String image, String pid, String pname, double price, String quantity, String time) {
        this.date = date;
        this.discount = discount;
        this.image = image;
        this.pid = pid;
        this.pname = pname;
        this.price = price;
        this.quantity = quantity;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "PurchasedProducts{" +
                "date='" + date + '\'' +
                ", discount='" + discount + '\'' +
                ", image='" + image + '\'' +
                ", pid='" + pid + '\'' +
                ", pname='" + pname + '\'' +
                ", price='" + price + '\'' +
                ", quantity='" + quantity + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
