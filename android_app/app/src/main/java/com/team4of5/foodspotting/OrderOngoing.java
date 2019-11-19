package com.team4of5.foodspotting;

public class OrderOngoing {

    private String resImage, resName, resAddress, foodImage, foodName;
    private int foodPrice, foodAmount, status;

    public OrderOngoing(){
        resImage = "";
        resName = "";
        resAddress = "";
        foodImage = "";
        foodName = "";
        foodPrice = 0;
        foodAmount = 0;
        status = 0;
    }

    public OrderOngoing(String resImage, String resName, String resAddress, String foodImage, String foodName,
                        int foodPrice, int foodAmount, int status){
        this.resImage = resImage;
        this. resName = resName;
        this. resAddress = resAddress;
        this. foodImage = foodImage;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodAmount = foodAmount;
        this.status = status;
    }

    public String getResImage() {
        return resImage;
    }

    public void setResImage(String resImage) {
        this.resImage = resImage;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public String getResAddress() {
        return resAddress;
    }

    public void setResAddress(String resAddress) {
        this.resAddress = resAddress;
    }

    public String getFoodImage() {
        return foodImage;
    }

    public void setFoodImage(String foodImage) {
        this.foodImage = foodImage;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getFoodAmount() {
        return foodAmount;
    }

    public void setFoodAmount(int foodAmount) {
        this.foodAmount = foodAmount;
    }

    public int getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(int foodPrice) {
        this.foodPrice = foodPrice;
    }

    public int getTotalPrice(){
        return foodPrice*foodAmount;
    }
}
