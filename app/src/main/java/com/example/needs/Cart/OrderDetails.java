package com.example.needs.Cart;


import java.io.Serializable;
import java.util.ArrayList;

public class OrderDetails implements Serializable {
  private ArrayList<CartDetails> items;
    private String status;
    private String payment_id;
    private String pick_up_status;
    private String delivery_status;
    private String delivery_address;
    private String amount;
    private String time;
    private String uid;

    public void setPayment_id(String payment_id) {
        this.payment_id = payment_id;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public OrderDetails() {
    }

    public void setDelivery_status(String delivery_status) {
        this.delivery_status = delivery_status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<CartDetails> getItems() {
        return items;
    }

    public String getStatus() {
        return status;
    }

    public String getPayment_id() {
        return payment_id;
    }

    public String getDelivery_status() {
        return delivery_status;
    }

    public String getDelivery_address() {
        return delivery_address;
    }

    public String getAmount() {
        return amount;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getUid() {
        return uid;
    }

    public void setPick_up_status(String pick_up_status) {
        this.pick_up_status = pick_up_status;
    }

    public String getPick_up_status() {
        return pick_up_status;
    }


    private String date;

    public OrderDetails(ArrayList<CartDetails> items, String status, String payment_id, String pick_up_status, String delivery_status, String delivery_address, String amount, String time, String uid, String date) {
        this.items = items;
        this.status = status;
        this.payment_id = payment_id;
        this.pick_up_status = pick_up_status;
        this.delivery_status = delivery_status;
        this.delivery_address = delivery_address;
        this.amount = amount;
        this.time = time;
        this.uid = uid;
        this.date = date;
    }

    public OrderDetails(ArrayList<CartDetails> items, String status, String payment_id, String delivery_status, String delivery_address, String amount, String time, String date) {
        this.items = items;
        this.status = status;
        this.payment_id = payment_id;
        this.delivery_status = delivery_status;
        this.delivery_address = delivery_address;
        this.amount = amount;
        this.time = time;
        this.date = date;
    }
}
