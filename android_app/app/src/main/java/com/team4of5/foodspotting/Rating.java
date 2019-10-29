package com.team4of5.foodspotting;

import java.io.Serializable;

public class Rating implements Serializable {
    private String id, comment, time, res_id, user_id;
    private float rate;
    public Rating(String id, String comment, float rate,String time,String res_id,String user_id){
        this.id = id;
        this.comment = comment;
        this.rate = rate;
        this.time = time;
        this.res_id = res_id;
        this.user_id = user_id;
    }
    public Rating(){
        this.id = "";
        this.comment = "";
        this.rate = 0;
        this.time = "";
        this.res_id = "";
        this.user_id = "";
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
    public void setRes_id(String res_id){
        this.res_id = res_id;
    }
    public void setUser_id(String user_id){
        this.user_id = user_id;
    }

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
    public String getRes_id(){
        return res_id;
    }
    public String getUser_id(){
        return user_id;
    }
}
