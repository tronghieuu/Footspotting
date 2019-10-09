package com.team4of5.foodspotting;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

public class DatabaseHelper {

    private FirebaseFirestore db;
    private boolean isSuccess;
    private Users user;

    public DatabaseHelper(){
        db = FirebaseFirestore.getInstance();
        isSuccess = false;
    }

    public boolean addUser(Users user){
        CollectionReference dbUsers = db.collection("users");
        dbUsers.add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        isSuccess = true;
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                isSuccess = false;
            }
        });
        return isSuccess;
    }

    public Users getUser(String email){
        db.collection("users")
                .whereEqualTo("email", email)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                //user = queryDocumentSnapshots.toObjects(Users.class).get(0);
                String username="", email="", password="";
                int type = 2;
                for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                    username = document.get("username").toString();
                    email = document.get("email").toString();
                    password = document.get("password").toString();
                    type = Integer.parseInt(document.get("type").toString());
                }
                user = new Users(username, password, email, type);

                //user = queryDocumentSnapshots.getDocuments().get(0).toObject(Users.class);
            }
        });
        return user;
    }

    public boolean loginSuccess(String email, String password){
        db.collection("users")
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty() || queryDocumentSnapshots.size() != 0){
                    isSuccess = true;
                }
                else isSuccess = false;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                isSuccess = false;
            }
        });
        return isSuccess;
    }
}
