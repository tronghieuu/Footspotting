package com.team4of5.foodspotting;

import java.io.Serializable;

public class Food implements Serializable {
    private String id, image, info, name, res_id;
    private String price;
    public Food(String id, String image, String info, String name, String price, String res_id){
        this.id = id;
        this.image = image;
        this.info = info;
        this.name = name;
        this.price = price;
        this.res_id = res_id;
    }
    public Food(){
        this.id = "";
        this.image = "";
        this.info = "";
        this.name = "";
        this.price = "";
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
    public void setPrice(String price){
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
    public String getPrice(){
        return price;
    }
}
