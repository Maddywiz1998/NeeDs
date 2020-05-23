package com.example.needs.Product;

public class SubCatagory {
    private String name,desc,image;
    private String []price;

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getImage() {
        return image;
    }

    public String[] getPrice() {
        return price;
    }

    public SubCatagory(String name, String desc, String image, String[] price) {
        this.name = name;
        this.desc = desc;
        this.image = image;
        this.price = price;
    }
}
