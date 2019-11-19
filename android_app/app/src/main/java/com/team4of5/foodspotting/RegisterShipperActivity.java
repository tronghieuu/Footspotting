package com.team4of5.foodspotting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class RegisterShipperActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnBack, mBtnCreate;
    private Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_shipper);
        getSupportActionBar().hide();
        mBtnBack = findViewById(R.id.btnBackShipperDangKy);
        mBtnCreate = findViewById(R.id.btnShipperDangKy);
        Window window = this.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.color_green));
        mBtnBack.setOnClickListener(this);
        mBtnCreate.setOnClickListener(this);
        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_loading);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBackShipperDangKy:
                finish();
                break;
            case R.id.btnShipperDangKy:
                Intent intent = new Intent(getApplicationContext(), ShipperAppActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
