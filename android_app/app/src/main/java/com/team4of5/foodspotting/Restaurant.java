package com.team4of5.foodspotting;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Restaurant implements Serializable {
    private String id,name, phone, district, province, street, image, opening_time, type, user_id;
    private float rate;
    private List<Food> foods;
    public Restaurant(String id,String name,String phone,String district,String province,String street,String image,float rate,String opening_time,String type,String user_id){
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.district = district;
        this.province = province;
        this.street = street;
        this.image = image;
        this.rate = rate;
        this.opening_time = opening_time;
        this.type = type;
        this.user_id = user_id;
    }
    public Restaurant(){
        this.id = "";
        this.name = "";
        this.phone = "";
        this.district = "";
        this.province = "";
        this.street = "";
        this.image = "";
        this.rate = 0;
        this.opening_time = "";
        this.type = "";
        this.user_id = "";
        foods = new ArrayList<>();
    }
    public void setId(String id){
        this.id = id;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }
    public void setDistrict(String district){
        this.district = district;
    }
    public void setProvince(String province){
        this.province = province;
    }
    public void setStreet(String street){
        this.street = street;
    }
    public void setImage(String image){
        this.image = image;
    }
    public void setOpening_time(String opening_time){
        this.opening_time = opening_time;
    }
    public void setType(String type){
        this.type = type;
    }
    public void setRate(float rate){
        this.rate = rate;
    }
    public void setUser_id(String user_id){
        this.user_id = user_id;
    }

    public String getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public String getPhone(){
        return phone;
    }
    public String getDistrict(){
        return district;
    }
    public String getProvince(){
        return province;
    }
    public String getStreet(){
        return street;
    }
    public String getImage(){
        return image;
    }
    public String getOpening_time(){
        return opening_time;
    }
    public String getType(){
        return type;
    }
    public String getUser_id(){
        return user_id;
    }
    public String getAddress(){
        return street + " " + district + " " + province;
    }
    public float getRate(){
        return rate;
    }

    public void updateResInfo(String idd){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("restaurants").document(idd)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    id = doc.getId();
                    name = doc.getString("name");
                    district = doc.getString("district");
                    image = doc.getString("image");
                    opening_time = doc.getString("opening_time");
                    phone = doc.getString("phone");
                    province = doc.getString("province");
                    rate = Float.parseFloat(doc.getString("rate"));
                    street = doc.getString("street");
                    type = doc.getString("type");
                    user_id = doc.getString("user_id");
                }
            }
        });
    }

}
