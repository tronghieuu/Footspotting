package com.team4of5.foodspotting.object;

import java.io.Serializable;

public class Rating implements Serializable {
    private String id, comment, time, user_id, user_name;
    private float rate;
    public Rating(String id, String comment, float rate,String time,String res_id,String user_id){
        this.id = id;
        this.comment = comment;
        this.rate = rate;
        this.time = time;
        this.user_id = user_id;
    }
    public Rating(){
        this.id = "";
        this.comment = "";
        this.rate = 0;
        this.time = "";
        this.user_id = "";
        this.user_name = "";
    }
    public void setId(String id){
        this.id = id;
    }
    public void setComment(String comment){
        this.comment = comment;
    }
    public void setRate(float rate){
        this.rate = rate;
    }
    public void setTime(String time){
        this.time = time;
    }
    public void setUser_id(String user_id){ this.user_id = user_id; }
    public void setUser_name(String user_name){ this.user_name = user_name; }

    public String getId(){
        return id;
    }
    public String getComment(){
        return comment;
    }
    public float getRate(){
        return rate;
    }
    public String getTime(){
        return time;
    }
    public String getUser_id(){
        return user_id;
    }
    public String getUser_name(){
        return user_name;
    }
}
