package com.team4of5.foodspotting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        db = FirebaseFirestore.getInstance();
        if(CurrentUser.CurrentUser().init(this.getFilesDir())){
            login();
        } else startActivity(new Intent(getApplicationContext(), HomeActivity.class));;
    }

    public void login(){
        db.collection("users")
                .whereEqualTo("email", CurrentUser.CurrentUser().getCurrentUser().getEmail())
                .whereEqualTo("password", CurrentUser.CurrentUser().getCurrentUser().getPassword())
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
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }
            }
        });
    }
}
