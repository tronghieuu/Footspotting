package com.team4of5.foodspotting;


import android.app.Activity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class CurrentUser {

    private static CurrentUser instance = null;
    private Users currentUser;
    private boolean login;

    private CurrentUser(){
        currentUser = new Users("","", "", "",1);
        login = false;
    }

    public static CurrentUser CurrentUser(){
        if(instance == null){
            instance = new CurrentUser();
        }
        return instance;
    }

    public void setCurrentUser(Users user){
        currentUser = user;
    }

    public Users getCurrentUser(){
        return currentUser;
    }

    public void setLogin(boolean login){
        this.login = login;
    }

    public boolean isLogin(){
        return login;
    }

    public void setId(String id){
        currentUser.setId(id);
    }

    public void setEmail(String email){
        currentUser.setEmail(email);
    }

    public void setUserName(String userName){
        currentUser.setUsername(userName);
    }

    public void setType(int type){
        currentUser.setType(type);
    }

    public void setProvince(String province){
        currentUser.setProvince(province);
    }

    public void setDistrict(String district){
        currentUser.setDistrict(district);
    }

    public void setStreet(String street){
        currentUser.setStreet(street);
    }

    public void setPhone(String phone){
        currentUser.setPhone(phone);
    }

    public void setPassword(String password) {
        currentUser.setPassword(password);
    }

    public boolean init(File dir) {
        try {
            File file = new File(dir, "currentAccount.txt");
            if (!file.exists()) {
                return file.createNewFile()?false:false;
            }
            else {
                if(file.length() == 0) {
                    return false;
                }
                else {
                    BufferedReader b = new BufferedReader(new FileReader(file));
                    currentUser.setEmail(b.readLine());
                    currentUser.setPassword(b.readLine());
                    return true;
                }

            }
        } catch (IOException e) {
            return false;
        }
    }

    public void saveAccount(File file){
        try{
            FileOutputStream stream = new FileOutputStream(file);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(stream));
            bw.write(currentUser.getEmail());
            bw.newLine();
            bw.write(currentUser.getPassword());
            bw.close();
        } catch(IOException e){}
    }
}
