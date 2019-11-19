package com.team4of5.foodspotting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ShipperAppActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mBtnBackShipper;
    private RelativeLayout mBtnDanhSachOrderShipper, mBtnDanhSachOrderAccepted, mBtnDanhSachOrderCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper_app);
        getSupportActionBar().hide();
        Window window = this.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.color_green));
        mBtnBackShipper = findViewById(R.id.btnBackShipper);
        mBtnDanhSachOrderShipper = findViewById(R.id.btnDanhSachOrderShipper);
        mBtnDanhSachOrderCompleted = findViewById(R.id.btnDanhSachOrderCompleted);
        mBtnBackShipper.setOnClickListener(this);
        mBtnDanhSachOrderShipper.setOnClickListener(this);
        mBtnDanhSachOrderAccepted.setOnClickListener(this);
        mBtnDanhSachOrderCompleted.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnDanhSachOrderShipper:
                break;
            case R.id.btnDanhSachOrderAccepted:
                break;
            case R.id.btnDanhSachOrderCompleted:
                break;
            case R.id.btnBackShipper:
                finish();
        }
    }
}
