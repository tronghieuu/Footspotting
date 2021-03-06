package com.team4of5.foodspotting.shipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.team4of5.foodspotting.R;

public class ShipperAppActivity extends AppCompatActivity implements View.OnClickListener {

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
        findViewById(R.id.btnBackShipper).setOnClickListener(this);
        findViewById(R.id.btnDanhSachOrderShipper).setOnClickListener(this);
        findViewById(R.id.btnHistoryShipper).setOnClickListener(this);
        findViewById(R.id.btnOrderAcceptedShipper).setOnClickListener(this);
        findViewById(R.id.btnOrderReceivedShipper).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnDanhSachOrderShipper:
                startActivity(new Intent(this, OrderListShipperActivity.class));
                break;
            case R.id.btnBackShipper:
                finish();
                break;
            case R.id.btnHistoryShipper:
                startActivity(new Intent(this, HistoryShipperActivity.class));
                break;
            case R.id.btnOrderAcceptedShipper:
                startActivity(new Intent(this, OrderAcceptedShipperActivity.class));
                break;
            case R.id.btnOrderReceivedShipper:
                startActivity(new Intent(this, OrderReceivedShipperActivity.class));
                break;
        }
    }
}
