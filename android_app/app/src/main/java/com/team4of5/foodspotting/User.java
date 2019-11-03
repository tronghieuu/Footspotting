package com.team4of5.foodspotting;

public class User {

    private static User instance = null;
    private int accountType; // 1 google or 2 email password or 3 guest
    private int type; // 1 normal or 2 shipper or 3 owner
    private String street, district, province, name, phone, id, image;

    private User(){
        id = "";
        accountType = 3;
        type = 1;
        street = "";
        district = "";
        province = "";
        name = "";
        phone = "";
        image = "";
    }

    public static User getCurrentUser(){
        if(instance == null){
            instance = new User();
        }
        return instance;
    }

    public void setAccountType(int type){
        accountType = type;
    }

    public void setType(int type){
        this.type = type;
    }

    public void setStreet(String street){
        this.street = street;
    }

    public void setDistrict(String district){
        this.district = district;
    }

    public void setProvince(String province){
        this.province = province;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public void setId(String id){
        this.id = id;
    }

    public void setImage(String image){
        this.image = image;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getAccountType(){
        return accountType;
    }

    public int getType(){
        return  type;
    }

    public String getName(){
        return name;
    }

    public String getStreet(){
        return street;
    }

    public String getDistrict(){
        return district;
    }

    public String getProvince(){
        return province;
    }

    public String getPhone(){
        return phone;
    }

    public String getId(){
        return id;
    }

    public String getImage(){
        return image;
    }

    public String getAddress(){
        return street+" "+district+" "+province;
    }

    public void reset(){
        id = "";
        accountType = 3;
        type = 1;
        street = "";
        district = "";
        province = "";
        name = "";
        phone = "";
        image = "";
    }
}
