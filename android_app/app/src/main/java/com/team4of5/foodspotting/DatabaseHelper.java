package com.team4of5.foodspotting;

import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class DatabaseHelper {

    private FirebaseFirestore db;

    public DatabaseHelper(){
        db = FirebaseFirestore.getInstance();
    }

    public void getAllRestaurant(){
        db.collection("restaurants")
                .limit(6)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    for(DocumentSnapshot doc : queryDocumentSnapshots){
                        Restaurant res = new Restaurant();
                        res.setId(doc.getId());
                        res.setDistrict(doc.getString("district"));
                        res.setImage(doc.getString("image"));
                        res.setName(doc.getString("name"));
                        res.setOpening_time(doc.getString("opening_time"));
                        res.setPhone(doc.getString("phone"));
                        res.setProvince(doc.getString("province"));
                        res.setRate(Float.parseFloat(doc.getString("rate")));
                        res.setStreet(doc.getString("street"));
                        res.setType(doc.getString("type"));
                        res.setUser_id(doc.getString("user_id"));
                        CurrentUser.CurrentUser().addRestaurant(res);
                    }
                }
            }
        });
    }

    public void getUserWhenLogin(String email, final String password){
        db.collection("users")
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    DocumentSnapshot query = queryDocumentSnapshots.getDocuments().get(0);
                    CurrentUser.CurrentUser().setEmail(query.getString("email"));
                    CurrentUser.CurrentUser().setUserName(query.getString("username"));
                    try{
                        CurrentUser.CurrentUser().setType(Integer.parseInt(query.getString("type")));
                    } catch(Exception e){}
                    CurrentUser.CurrentUser().setDistrict(query.getString("district"));
                    CurrentUser.CurrentUser().setProvince(query.getString("province"));
                    CurrentUser.CurrentUser().setPhone(query.getString("phone"));
                    CurrentUser.CurrentUser().setStreet(query.getString("street"));
                    CurrentUser.CurrentUser().setPassword(query.getString("password"));
                    CurrentUser.CurrentUser().setId(query.getId());
                    CurrentUser.CurrentUser().setLogin(true);
                }
            }
        });
    }
}