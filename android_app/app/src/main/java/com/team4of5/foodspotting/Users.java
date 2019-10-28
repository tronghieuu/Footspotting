package com.team4of5.foodspotting;

import java.io.Serializable;

public class Users implements Serializable {

    private String username, password, email, phone, street, district, province, id;
    private int type;

    public Users(){
        username = "default";
        password = "1";
        email = "default";
        phone = "default";
        street = "default";
        district = "default";
        province = "default";
        id = "default";
        type = 1;
    }

    public Users(String id, String username, String password, String email, int type){
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.type = type;
        phone = "";
        street = "";
        district = "";
        province = "";
    }

    public Users(String email, String password, String username, int type,
                 String phone, String street, String district, String province, String id) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.type = type;
        this.phone = phone;
        this.street = street;
        this.district = district;
        this.province = province;
        this.id = id;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public boolean setPassword(String currentPassword, String newPassword){
        if(currentPassword == this.password){
            this.password = newPassword;
            return true;
        }
        return false;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setType(int type){
        if(type == 1 || type == 2 || type == 3){
            this.type = type;
        }
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public void setProvince(String province){
        this.province = province;
    }

    public void setStreet(String street){
        this.street = street;
    }

    public void setDistrict(String district){

    }

    public void setId(String id){
        this.id = id;
    }

    public String getUsername(){
        return username;
    }

    public String getEmail(){
        return email;
    }

    public int getType(){
        return type;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return street+" "+district+" "+province;
    }

    public String getStreet() {
        return street;
    }

    public String getDistrict(){
        return district;
    }

    public String getProvince(){
        return province;
    }

    public void updateAddress(String street, String district, String province){
        this.street = street;
        this.district = district;
        this.province = province;
    }

    public void updatePhone(String phone) {
        this.phone = phone;
    }
}
