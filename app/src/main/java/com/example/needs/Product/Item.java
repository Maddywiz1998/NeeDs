package com.example.needs.Product;

import java.io.Serializable;

public class Item implements Serializable {
    public Item() {
    }

    private String name,desc,price,image;
    private Boolean isService;

    public String getName() {
        return name;
    }

    public Boolean getService() {
        return isService;
    }

    public void setService(Boolean service) {
        isService = service;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public String getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

    public Item(String name, String desc, String price, String image, Boolean isService) {
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.image = image;
        this.isService = isService;
    }
}
