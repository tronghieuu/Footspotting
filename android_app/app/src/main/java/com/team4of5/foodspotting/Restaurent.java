package com.team4of5.foodspotting;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;

public class Restaurent extends AppCompatActivity {
    private TextView mShopName;
    private TextView mShopType;
    private TextView mOpeningTime;
    private Button mShopContact;
    private TextView mRatingShopOverall;
    private RatingBar mRatingShopOverallStar;
    private TextView mShopAddress;
    private TextView mShowAllReview;
    private SearchView mSearchFood;
    private RecyclerView mViewFoodList;
    private String phonenum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurent);
        String id_restaurent = getIntent().getStringExtra("id_restaurent");
        Restaurant res = new Restaurant();
        res.updateResInfo(id_restaurent);

        mShopName = findViewById(R.id.textShopName);
        mShopType = findViewById(R.id.textShopType);
        mOpeningTime = findViewById(R.id.textOpeningTime);
        mShopContact = findViewById(R.id.buttonShopContact);
        mRatingShopOverall = findViewById(R.id.textRatingShopOverall);
        mRatingShopOverallStar = findViewById(R.id.ratingShopOverallStar);
        mShopAddress = findViewById(R.id.textShopAddress);
        mShowAllReview = findViewById(R.id.textShopShowAllReview);
        mSearchFood = findViewById(R.id.searchFood);
        mViewFoodList = findViewById(R.id.recycleViewFoodList);

        mShopName.setText(res.getName());
        mShopType.setText(res.getType());
        mOpeningTime.setText(res.getOpening_time());
        phonenum = res.getPhone();
        mShopContact.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_CALL, Uri.parse(phonenum));
                if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
                startActivity(i);

            }
        });
        mRatingShopOverall.setText(Float.toString(res.getRate()));
        mRatingShopOverallStar.setRating(res.getRate());
        mShopAddress.setText(res.getAddress());

    }
}
