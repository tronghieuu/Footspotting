package com.team4of5.foodspotting;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
    private Restaurant res;
    private String id_restaurent;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurent);
        id_restaurent = getIntent().getStringExtra("id_restaurent");
        res = new Restaurant();
        db = FirebaseFirestore.getInstance();
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

        getRes();


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


    }
    public void getRes(){
        db.collection("restaurants")
                .document(id_restaurent)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                mShopName.setText(doc.getString("name"));
                mShopType.setText(doc.getString("type"));
                mOpeningTime.setText(doc.getString("opening_time"));
                phonenum = doc.getString("phone");
                mRatingShopOverall.setText(doc.getString("rate"));
                mRatingShopOverallStar.setRating(Float.parseFloat(doc.getString("rate")));
                String address = doc.getString("street") + " " +
                        doc.getString("district") + " " + doc.getString("province");
                mShopAddress.setText(address);
            }
        });
    }
}
