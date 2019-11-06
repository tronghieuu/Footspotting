package com.team4of5.foodspotting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
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
    private static int REQUEST_PERMISSION = 2314;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
            return;
        } else {
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
                getGoogleAccount();
            } else {
                User.getCurrentUser().setAccountType(3);
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
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
                    User.getCurrentUser().setImage(doc.getString("image"));
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
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
                    finish();
                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
                    finish();
                }
            } else {
                finish();
            }
        }
    }
}
