package com.team4of5.foodspotting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CurrentUser {

    private static CurrentUser instance = null;
    private Users currentUser;

    private CurrentUser(){
        currentUser = null;
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

    public boolean init(String dir) {
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
                    return login(file);
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    private boolean login(File file) {
        try{
            BufferedReader b = new BufferedReader(new FileReader(file));
            String email = b.readLine();
            String password = b.readLine();
            return true;
        } catch (IOException e){
            //
            return false;
        }
    }
}
