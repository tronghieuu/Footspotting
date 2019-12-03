package com.team4of5.foodspotting.object;

import java.io.Serializable;

public class Food implements Serializable {
    private String id, image, info, name;
    private String price;
    public Food(String id, String image, String info, String name, String price, String res_id){
        this.id = id;
        this.image = image;
        this.info = info;
        this.name = name;
        this.price = price;
    }
    public Food(){
        this.id = "";
        this.image = "";
        this.info = "";
        this.name = "";
        this.price = "";
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
    public String getPrice(){
        return price;
    }
}
