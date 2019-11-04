package com.team4of5.foodspotting;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserInformationActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnBack, mBtnChangeUsername, mBtnChangePhone, mBtnChangeAddress
            , mBtnChangePassword, mBtnChangePayment;
    private TextView mTvUsername, mTvPhone, mTvAddress, mTvEmailInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);
        getSupportActionBar().hide();
        mBtnBack = findViewById(R.id.btnBackProfile);
        mBtnChangeUsername = findViewById(R.id.btnChangeUsername);
        mBtnChangePhone = findViewById(R.id.btnChangePhone);
        mBtnChangeAddress = findViewById(R.id.btnChangeAddress);
        mBtnChangePassword = findViewById(R.id.btnChangePassword);
        mBtnChangePayment = findViewById(R.id.btnPayment);
        mTvUsername = findViewById(R.id.tvChangeUsername);
        mTvPhone = findViewById(R.id.tvChangePhone);
        mTvAddress = findViewById(R.id.tvChangeAddress);
        mTvEmailInfo = findViewById(R.id.tvEmailIfo);

        // add listener
        mBtnBack.setOnClickListener(this);
        mBtnChangeUsername.setOnClickListener(this);
        mBtnChangePhone.setOnClickListener(this);
        mBtnChangeAddress.setOnClickListener(this);
        mBtnChangePassword.setOnClickListener(this);
        mBtnChangePayment.setOnClickListener(this);

        // data
        mTvUsername.setText(User.getCurrentUser().getName());
        mTvAddress.setText(User.getCurrentUser().getProvince());
        mTvPhone.setText(User.getCurrentUser().getPhone());
        if(User.getCurrentUser().getAccountType() == 1) {
            GoogleSignInAccount gg = GoogleSignIn.getLastSignedInAccount(this);
            mTvEmailInfo.setText(gg.getEmail());
        } else {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            mTvEmailInfo.setText(auth.getCurrentUser().getEmail());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBackProfile:
                setResult(Activity.RESULT_CANCELED, new Intent());
                finish();
                break;
            case R.id.btnChangeUsername:
                Toast.makeText(this, "Thay đổi username", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnChangeAddress:
                Toast.makeText(this, "Thay đổi địa chỉ", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnChangePassword:
                Toast.makeText(this, "Thay đổi mật khẩu", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnChangePhone:
                Toast.makeText(this, "Thay đổi số điện thoại", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnPayment:
                Toast.makeText(this, "Payment", Toast.LENGTH_SHORT).show();
        }
    }
}
