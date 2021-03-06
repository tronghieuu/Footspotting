package com.team4of5.foodspotting.object;

import com.team4of5.foodspotting.object.Food;
import com.team4of5.foodspotting.object.Rating;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Restaurant implements Serializable {
    private String id,name, phone, district, province, street, image, opening_time, closing_time, type, user_id;
    private float rate;
    private List<Rating> ratingList;
    private List<Food> foods;
    public Restaurant(String id,String name,String phone,String district,String province,String street,String image,float rate,String opening_time,String closing_time,String type,String user_id){
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.district = district;
        this.province = province;
        this.street = street;
        this.image = image;
        this.rate = rate;
        this.opening_time = opening_time;
        this.closing_time = closing_time;
        this.type = type;
        this.user_id = user_id;
    }
    public Restaurant(){
        this.id = "";
        this.name = "";
        this.phone = "";
        this.district = "";
        this.province = "";
        this.street = "";
        this.image = "";
        this.rate = 0;
        this.opening_time = "";
        this.type = "";
        this.user_id = "";
        this.ratingList = new ArrayList<>();
        foods = new ArrayList<>();
    }
    public void setId(String id){
        this.id = id;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }
    public void setDistrict(String district){
        this.district = district;
    }
    public void setProvince(String province){
        this.province = province;
    }
    public void setStreet(String street){
        this.street = street;
    }
    public void setImage(String image){
        this.image = image;
    }
    public void setOpening_time(String opening_time){
        this.opening_time = opening_time;
    }
    public void setType(String type){
        this.type = type;
    }
    public void setRate(float rate){
        this.rate = rate;
    }
    public void setUser_id(String user_id){
        this.user_id = user_id;
    }
    public void setRatingList(List<Rating> ratingList){this.ratingList=ratingList;}

    public String getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public String getPhone(){
        return phone;
    }
    public String getDistrict(){
        return district;
    }
    public String getProvince(){
        return province;
    }
    public String getStreet(){
        return street;
    }
    public String getImage(){
        return image;
    }
    public String getOpening_time(){
        return opening_time;
    }
    public String getType(){
        return type;
    }
    public String getUser_id(){
        return user_id;
    }
    public String getAddress(){
        return street + ", " + district + ", " + province;
    }
    public float getRate(){
        return rate;
    }
    public List<Rating> getRatingList(){return ratingList;}

    public String getClosing_time() {
        return closing_time;
    }

    public void setClosing_time(String closing_time) {
        this.closing_time = closing_time;
    }
}
