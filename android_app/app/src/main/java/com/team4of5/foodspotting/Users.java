package com.team4of5.foodspotting;

public class Users {

    private String username, password, email, phone, street, district, province;
    private int type;

    public Users(String username, String password, String email, int type){
        this.username = username;
        this.password = password;
        this.email = email;
        this.type = type;
        phone = "";
        street = "";
        district = "";
        province = "";
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

    public String getUsername(){
        return username;
    }

    public String getEmail(){
        return email;
    }

    public int getType(){
        return type;
    }

    public void updateAddress(String street, String district, String province){
        this.street = street;
        this.district = district;
        this.province = province;
    }
}
