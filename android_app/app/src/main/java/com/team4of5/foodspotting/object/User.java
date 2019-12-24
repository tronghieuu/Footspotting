package com.team4of5.foodspotting.object;

public class User {

    private static User instance = null;
    private int type, orderTab; // 1 normal or 2 shipper or 3 owner
    private String street;
    private String district;
    private String province;
    private String name;
    private String phone;
    private String id;
    private String image;

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    private String background;
    private boolean ownerUpdate, listUpdate, orderUpdate, historyUpdate, cartUpdate, NotiUpdate;

    private User(){
        id = "";
        type = 1;
        street = "";
        district = "";
        province = "";
        name = "";
        phone = "";
        image = "";
        background ="";
        ownerUpdate = false;
        listUpdate = false;
        NotiUpdate = false;
        orderTab = 0;
    }

    public static User getCurrentUser(){
        if(instance == null){
            instance = new User();
        }
        return instance;
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
        type = 1;
        street = "";
        district = "";
        province = "";
        name = "";
        phone = "";
        image = "";
        background = "";
    }

    public boolean getOwnerUpdate(){
        return ownerUpdate;
    }

    public void setOwnerUpdate(boolean ownerUpdate){
        this.ownerUpdate = ownerUpdate;
    }

    public boolean isListUpdate() {
        return listUpdate;
    }

    public void setListUpdate(boolean listUpdate) {
        this.listUpdate = listUpdate;
    }

    public int getOrderTab() {
        return orderTab;
    }

    public void setOrderTab(int orderTab) {
        this.orderTab = orderTab;
    }

    public boolean isOrderUpdate() {
        return orderUpdate;
    }

    public void setOrderUpdate(boolean orderUpdate) {
        this.orderUpdate = orderUpdate;
    }

    public boolean isHistoryUpdate() {
        return historyUpdate;
    }

    public void setHistoryUpdate(boolean historyUpdate) {
        this.historyUpdate = historyUpdate;
    }

    public boolean isCartUpdate() {
        return cartUpdate;
    }

    public void setCartUpdate(boolean cartUpdate) {
        this.cartUpdate = cartUpdate;
    }

    public boolean isNotiUpdate() {
        return NotiUpdate;
    }

    public void setNotiUpdate(boolean notiUpdate) {
        NotiUpdate = notiUpdate;
    }
}
