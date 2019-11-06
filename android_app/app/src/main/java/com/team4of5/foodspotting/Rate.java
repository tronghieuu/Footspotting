package com.team4of5.foodspotting;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Rate extends AppCompatActivity {
    private TextView mTextTotalRating;
    private RatingBar mRatingShopOverall;
    private TextView mTextRatingNumber;
    private TextView mTextShopName;
    private LinearLayout mLl_percentage_5;
    private LinearLayout mLl_percentage_4;
    private LinearLayout mLl_percentage_3;
    private LinearLayout mLl_percentage_2;
    private LinearLayout mLl_percentage_1;
    private Button mBtnOpenPostReview;
    private RecyclerView mRv_review;
    private String id_restaurent;
    private FirebaseFirestore db;
    private List<Rating> mRates;
    private RatingAdapter mAdapter;
    private Dialog dialog;
    private EditText etReview;
    private RatingBar rate;
    private Button btnSend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_allreview);
        getSupportActionBar().hide();
        id_restaurent = getIntent().getStringExtra("id_restaurent");
        db = FirebaseFirestore.getInstance();

        mTextShopName = findViewById(R.id.textShopName);
        mTextTotalRating = findViewById(R.id.textTotalRating);
        mRatingShopOverall = findViewById(R.id.ratingShopOverall);
        mTextRatingNumber = findViewById(R.id.textRatingNumber);
        mLl_percentage_5 = findViewById(R.id.ll_percentage_5);
        mLl_percentage_4 = findViewById(R.id.ll_percentage_4);
        mLl_percentage_3 = findViewById(R.id.ll_percentage_3);
        mLl_percentage_2 = findViewById(R.id.ll_percentage_2);
        mLl_percentage_1 = findViewById(R.id.ll_percentage_1);
        mBtnOpenPostReview = findViewById(R.id.btnOpenPostReview);

        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.shop_post_review);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                etReview.setText("");
                rate.setRating(0);
            }
        });

        mRates = new ArrayList<>();
        mRv_review = findViewById(R.id.rvReviewList);
        mRv_review.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new RatingAdapter(this, mRates, mRv_review);
        mRv_review.setAdapter(mAdapter);

        mTextTotalRating.setText("0.0");
        mRatingShopOverall.setRating(0);
        mTextRatingNumber.setText("0");
        getRes();
        queryRating();

        mBtnOpenPostReview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openDialogReview();
            }
        });
    }
    private void openDialogReview() {

        etReview = dialog.findViewById(R.id.et_review);
        rate = dialog.findViewById(R.id.rate_star);
        btnSend = dialog.findViewById(R.id.btn_send_review);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (TextUtils.isEmpty(etReview.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Required field!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Done review", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }
    public void queryRating(){
        db.collection("rating")
                .whereEqualTo("res_id", id_restaurent)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    for(DocumentSnapshot doc : queryDocumentSnapshots){
                        Rating rating = new Rating();
                        rating.setId(doc.getId());
                        rating.setComment(doc.getString("comment"));
                        rating.setRate(Float.parseFloat(doc.getString("rate")));
                        rating.setTime(doc.getTimestamp("time").toDate().toString());
                        rating.setUser_id(doc.getString("user_id"));
                        rating.setRes_id(doc.getString("res_id"));
                        mRates.add(rating);
                        mAdapter.notifyDataSetChanged();
                    }
                    mTextRatingNumber.setText(String.valueOf(mRates.size()));
                }
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
                mTextTotalRating.setText(doc.getString("rate"));
                mTextShopName.setText(doc.getString("name"));
                mRatingShopOverall.setRating(Float.parseFloat(doc.getString("rate")));
            }
        });
    }

}
