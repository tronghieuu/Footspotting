package com.team4of5.foodspotting;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class DatabaseHelper {

    private FirebaseFirestore db;
    private boolean check;
    private Users user;

    public DatabaseHelper(){
        db = FirebaseFirestore.getInstance();
        check = false;
    }

    public boolean addUser(Users user){
        CollectionReference dbUsers = db.collection("users");
        dbUsers.add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        check = true;
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                check = false;
            }
        });
        return check;
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
                    check = true;
                }
                else check = false;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                check = false;
            }
        });
        return check;
    }
}
