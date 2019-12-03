package com.team4of5.foodspotting.search;

public class food {
    private String name;
    private String price;
    private String image;
    private String info;
    private String Res;
    private String ResID;
    private String id;

    public String getResID() {
        return ResID;
    }

    public void setResID(String resID) {
        ResID = resID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getRes() {
        return Res;
    }

    public void setRes(String res) {
        Res = res;
    }

    public food(){}
    public food(String name,String price,String image, String info,String res,String id)
    {
        this.name=name;
        this.price=price;
        this.image=image;
        this.info=info;
        this.Res=res;
        this.id=id;
    }
}
