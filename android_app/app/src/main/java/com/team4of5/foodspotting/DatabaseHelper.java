package com.team4of5.foodspotting;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class DatabaseHelper {

    private FirebaseFirestore db;

    public DatabaseHelper(){
        db = FirebaseFirestore.getInstance();
    }

    public void getUserWhenLogin(String email, String password){
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
                    CurrentUser.CurrentUser().setId(query.getId());
                    CurrentUser.CurrentUser().setLogin(true);
                }
            }
        });}
}