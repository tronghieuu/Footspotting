package com.team4of5.foodspotting.object;

public class Order {
    private String restaurant_id, food_id, user_id, id;
    private int order_amount, status;
    private long timestamp;

    public Order(){
        restaurant_id = "";
        food_id = "";
        user_id = "";
        order_amount = 1;
        status = 1;
        timestamp = 0;
        id = "";
    }

    public Order(String restaurant_id, String food_id, String user_id, int order_amount, int status, long timestamp, String id){
        this.restaurant_id = restaurant_id;
        this.food_id = food_id;
        this.user_id = user_id;
        this.order_amount = order_amount;
        this.status = status;
        this.timestamp = timestamp;
        this.id = id;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getFood_id() {
        return food_id;
    }

    public void setFood_id(String food_id) {
        this.food_id = food_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getOrder_amount() {
        return order_amount;
    }

    public void setOrder_amount(int order_amount) {
        this.order_amount = order_amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

