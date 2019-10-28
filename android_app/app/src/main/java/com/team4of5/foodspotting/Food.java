package com.team4of5.foodspotting;

import java.io.Serializable;

public class Food implements Serializable {
    private String id, image, info, name, res_id;
    private int price;
    public Food(String id, String image, String info, String name, int price, String res_id){
        this.id = id;
        this.image = image;
        this.info = info;
        this.name = name;
        this.price = 0;
        this.res_id = res_id;
    }
    public Food(){
        this.id = "";
        this.image = "";
        this.info = "";
        this.name = "";
        this.price = 0;
        this.res_id = "";
    }
    public void setId(String id){
        this.id = id;
    }
    public void setImage(String image){
        this.image = image;
    }
    public void setInfo(String info){
        this.info = info;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setRes_id(String res_id){
        this.res_id = res_id;
    }
    public void setPrice(int price){
        this.price = price;
    }

    public String getId(){
        return id;
    }
    public String getImage(){
        return image;
    }
    public String getInfo(){
        return info;
    }
    public String getName(){
        return name;
    }
    public String getRes_id(){
        return res_id;
    }
    public int getPrice(){
        return price;
    }
}
