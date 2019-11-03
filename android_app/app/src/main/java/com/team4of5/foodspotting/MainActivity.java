package com.team4of5.foodspotting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    private GoogleSignInAccount mGoogleSignInAccount;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        // email or password
        mAuth = FirebaseAuth.getInstance();

        // google
        mGoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);

        // check email or google or guest
        if(mAuth.getCurrentUser() != null){
            User.getCurrentUser().setAccountType(2);
            getEmailAccount();
        } else if(mGoogleSignInAccount != null){
            User.getCurrentUser().setAccountType(1);
            User.getCurrentUser().setName(mGoogleSignInAccount.getDisplayName());
            try{
                User.getCurrentUser().setImage(mGoogleSignInAccount.getPhotoUrl().toString());
            } catch(Exception e){}
            getGoogleAccount();
        } else {
            User.getCurrentUser().setAccountType(3);
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
    }

    public void getGoogleAccount(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user")
                .whereEqualTo("gmail", mGoogleSignInAccount.getEmail())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.size() != 0){
                    DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                    User.getCurrentUser().setId(doc.getId());
                    User.getCurrentUser().setType(Integer.parseInt(doc.getString("type")));
                    User.getCurrentUser().setStreet(doc.getString("street"));
                    User.getCurrentUser().setDistrict(doc.getString("district"));
                    User.getCurrentUser().setProvince(doc.getString("province"));
                    User.getCurrentUser().setPhone(doc.getString("phone"));
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }
            }
        });
    }

    public void getEmailAccount(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user")
                .whereEqualTo("email", mAuth.getCurrentUser().getEmail())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.size() != 0){
                    DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                    User.getCurrentUser().setId(doc.getId());
                    User.getCurrentUser().setType(Integer.parseInt(doc.getString("type")));
                    User.getCurrentUser().setStreet(doc.getString("street"));
                    User.getCurrentUser().setName(doc.getString("name"));
                    User.getCurrentUser().setImage(doc.getString("image"));
                    User.getCurrentUser().setDistrict(doc.getString("district"));
                    User.getCurrentUser().setProvince(doc.getString("province"));
                    User.getCurrentUser().setPhone(doc.getString("phone"));
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }
            }
        });
    }
}
