package com.team4of5.foodspotting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        if(CurrentUser.CurrentUser().init(this.getFilesDir())){
            CurrentUser.CurrentUser().setLogin(true);
            Toast.makeText(this, CurrentUser.CurrentUser().getA(), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, CurrentUser.CurrentUser().getB(), Toast.LENGTH_SHORT).show();
        }
        else Toast.makeText(this, "false", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, HomeActivity.class));
    }
}
