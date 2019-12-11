package com.team4of5.foodspotting.object;

import java.util.Calendar;
import java.util.Date;

public class UserHistory {

    private String id, restaurant_id, restaurant_name, restaurant_image, restaurant_address, food_image, food_name, user_id, user_address, shipper_id;
    private int food_price, food_amount, status;
    private long timestamp;

    public UserHistory(String id, String restaurant_id, String restaurant_name, String restaurant_image, String restaurant_address,
                       String food_image, String food_name, int food_price, int food_amount, int status, long timestamp,
                       String user_id, String user_address, String shipper_id){
        this.id = id;
        this.restaurant_id = restaurant_id;
        this.restaurant_name = restaurant_name;
        this.restaurant_image = restaurant_image;
        this.restaurant_address = restaurant_address;
        this.food_image = food_image;
        this.food_name = food_name;
        this.food_price = food_price;
        this.food_amount = food_amount;
        this.status = status;
        this.timestamp = timestamp;
        this.user_id = user_id;
        this.user_address = user_address;
        this.shipper_id = shipper_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public void setRestaurant_name(String restaurant_name) {
        this.restaurant_name = restaurant_name;
    }

    public String getRestaurant_image() {
        return restaurant_image;
    }

    public void setRestaurant_image(String restaurant_image) {
        this.restaurant_image = restaurant_image;
    }

    public String getRestaurant_address() {
        return restaurant_address;
    }

    public void setRestaurant_address(String restaurant_address) {
        this.restaurant_address = restaurant_address;
    }

    public String getFood_image() {
        return food_image;
    }

    public void setFood_image(String food_image) {
        this.food_image = food_image;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getFood_amount() {
        return food_amount;
    }

    public void setFood_amount(int food_amount) {
        this.food_amount = food_amount;
    }

    public int getFood_price() {
        return food_price;
    }

    public void setFood_price(int food_price) {
        this.food_price = food_price;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTotalPrice(){
        return "đ"+(food_price*food_amount);
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_address() {
        return user_address;
    }

    public void setUser_address(String user_address) {
        this.user_address = user_address;
    }

    public String getShipper_id() {
        return shipper_id;
    }

    public void setShipper_id(String shipper_id) {
        this.shipper_id = shipper_id;
    }

    public String getDateTime() {
        Date date = new Date(timestamp);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MONTH)+"/"+c.get(Calendar.DAY_OF_MONTH)
                +"/"+c.get(Calendar.YEAR)+" "+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE);
    }
}
