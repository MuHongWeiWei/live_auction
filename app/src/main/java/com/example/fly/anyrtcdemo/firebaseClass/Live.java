package com.example.fly.anyrtcdemo.firebaseClass;

public class Live {
    String viewer;
    String price;
    String type;

    public Live() {
    }

    public Live(String viewer, String price) {
        this.viewer = viewer;
        this.price = price;
    }

    public Live(String viewer, String price, String type) {
        this.viewer = viewer;
        this.price = price;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getViewer() {
        return viewer;
    }

    public void setViewer(String viewer) {
        this.viewer = viewer;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
