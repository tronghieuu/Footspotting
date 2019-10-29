package com.team4of5.foodspotting;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Restaurent extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurent);

        String id_restaurent = getIntent().getStringExtra("id_restaurent");
        Restaurant res = new Restaurant();
        res.updateResInfo(id_restaurent);

    }
}
