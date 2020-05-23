package com.example.needs.Cart;

import com.example.needs.Product.Item;

import java.io.Serializable;

public class CartDetails implements Serializable {
    public CartDetails() {
    }

    private Item item;

    public void setUid(String uid) {
        this.uid = uid;
    }

    private String uid,quantity,total;

    public Item getItem() {
        return item;
    }



    public String getUid() {
        return uid;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getTotal() {
        return total;
    }

    public CartDetails(Item item, String uid, String quantity, String total) {
        this.item = item;
        this.uid = uid;
        this.quantity = quantity;
        this.total = total;
    }
}
