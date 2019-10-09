package com.team4of5.foodspotting;

public class CurrentUser {

    private static CurrentUser instance = null;
    private Users currentUser;
    private CurrentUser(){}
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
}
