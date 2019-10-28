package com.team4of5.foodspotting;

import java.io.Serializable;

public class Order implements Serializable {
    private String id, food_id, time, user_id;
    private int amount;
    public Order(){
        this.id ="";
        this.amount =0;
        this.food_id ="";
        this.time = "";
        this.user_id ="";
    }
    private Order(String id, int amount, String food_id, String time,String user_id){
        this.id =id;
        this.amount =amount;
        this.food_id = food_id;
        this.time = time;
        this.user_id ="";
    }
    public void setId(String id){
        this.id = id;
    }
    public void setFood_id(String food_id){
        this.food_id = food_id;
    }
    public void setTime(String time){
        this.time = time;
    }
    public void setUser_id(String user_id){
        this.user_id = user_id;
    }
    public void setAmount(int amount){
        this.amount = amount;
    }

    public String getId(){
        return id;
    }
    public String getFood_id(){
        return food_id;
    }
    public String getTime(){
        return time;
    }
    public String getUser_id(){
        return user_id;
    }
    public int getAmount(){
        return getAmount();
    }
}
