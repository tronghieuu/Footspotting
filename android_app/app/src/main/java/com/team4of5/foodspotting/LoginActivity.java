package com.team4of5.foodspotting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnSignIn;
    private ImageView mBtnBack;
    private DatabaseHelper db;
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
        db = new DatabaseHelper();
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
            db.getUserWhenLogin(email, password);
            if(CurrentUser.CurrentUser().isLogin()){
                Intent intent = new Intent();
                intent.putExtra("isLogin", "ok");
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
            else {
                Toast.makeText(this, "Đăng nhập không thành công", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
