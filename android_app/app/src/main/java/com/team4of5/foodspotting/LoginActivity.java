package com.team4of5.foodspotting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnSignIn;
    private ImageView mBtnBack;
    private FirebaseFirestore db;
    private EditText mEdtEmail, mEdtPassword;
    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mBtnSignIn = findViewById(R.id.btnSignIn);
        mEdtEmail = findViewById(R.id.edtEmail);
        mEdtPassword = findViewById(R.id.edtPassword);
        mBtnBack = findViewById(R.id.btnBack);
        db = FirebaseFirestore.getInstance();
        mBtnSignIn.setOnClickListener(this);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        email = mEdtEmail.getText().toString();
        password = mEdtPassword.getText().toString();
        if(email.contentEquals("") || password.contentEquals(""))
            Toast.makeText(this, "Chưa nhập đủ thông tin", Toast.LENGTH_SHORT).show();
        else {
            tryLogin();
        }
    }

    public void tryLogin(){
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
                    Intent intent = new Intent();
                    intent.putExtra("isLogin", "ok");
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else Toast.makeText(getApplicationContext(), "Đăng nhập không thành công", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
