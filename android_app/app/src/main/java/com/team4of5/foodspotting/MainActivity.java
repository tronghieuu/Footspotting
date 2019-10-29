package com.team4of5.foodspotting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        if(CurrentUser.CurrentUser().init(this.getFilesDir())){
            CurrentUser.CurrentUser().setLogin(true);
        }
        startActivity(new Intent(MainActivity.this, HomeActivity.class));
    }
}
