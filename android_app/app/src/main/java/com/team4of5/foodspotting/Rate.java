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
import android.view.ViewGroup;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.primitives.UnsignedInts;
import com.google.common.primitives.UnsignedLongs;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ConstraintLayout constrainLayout1;
    private ConstraintLayout constrainLayout2;
    private ConstraintLayout constrainLayout3;
    private ConstraintLayout constrainLayout4;
    private ConstraintLayout constrainLayout5;
    private int widthView;
    private Button mBtnOpenPostReview;
    private RecyclerView mRv_review;
    private String id_restaurent;
    private String tam_username;
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
        constrainLayout1 = findViewById(R.id.constrain_layout_1);
        constrainLayout2 = findViewById(R.id.constrain_layout_2);
        constrainLayout3 = findViewById(R.id.constrain_layout_3);
        constrainLayout4 = findViewById(R.id.constrain_layout_4);
        constrainLayout5 = findViewById(R.id.constrain_layout_5);
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
                String useId = "";
                useId = User.getCurrentUser().getId();
                if (useId!="") {
                    db.collection("rating").whereEqualTo("user_id", useId).get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                                    openDialogReviewRe(doc.getId(),doc.getString("comment"), doc.getString("rate"));

                                    Toast.makeText(getApplicationContext(), "You have already reviewed!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    openDialogReviewNew();
                                }
                            });
                }
                else  Toast.makeText(getApplicationContext(), "Login to review!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void openDialogReviewNew() {

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
                    Map<String, Object> map = new HashMap<>();
                    map.put("comment", etReview);
                    map.put("rate", rate);
                    map.put("res_id", id_restaurent);
                    map.put("time", FieldValue.serverTimestamp());
                    map.put("user_id", User.getCurrentUser().getId());
                    map.put("user_name", User.getCurrentUser().getName());
                    db.collection("rating").add(map);
                    Toast.makeText(getApplicationContext(), "Done review", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }
    private void openDialogReviewRe(final String uId, final String uCom, final String uRate) {

        etReview = dialog.findViewById(R.id.et_review);
        rate = dialog.findViewById(R.id.rate_star);
        btnSend = dialog.findViewById(R.id.btn_send_review);
        etReview.setText(uCom);
        rate.setRating(Float.parseFloat(uRate));
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (TextUtils.isEmpty(etReview.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Required field!", Toast.LENGTH_SHORT).show();
                } else {
                    if (uCom.contentEquals(etReview.getText()) | uRate.contentEquals(String.valueOf(rate.getRating()))) {
                        db.collection("rating")
                                .document(uId)
                                .update("comment", etReview.getText());
                        db.collection("rating")
                                .document(uId)
                                .update("rate", String.valueOf(rate.getRating()));
                        db.collection("rating")
                                .document(uId)
                                .update("time", FieldValue.serverTimestamp());
                    }
                    Toast.makeText(getApplicationContext(), "Done review", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    private void setOverallReview(){
        mTextRatingNumber.setText(String.valueOf(mRates.size()));
        int r1=0, r2=0, r3=0, r4=0, r5=0, total=0;
        float average;
        for (Rating rating:mRates)
        {
            total=total+rating.getRate();
            if (rating.getRate()==1) r1++;
            else if (rating.getRate()==2) r2++;
            else if (rating.getRate()==3) r3++;
            else if (rating.getRate()==4) r4++;
            else if (rating.getRate()==5) r5++;
        }
        average = (float)total/mRates.size();
        mTextTotalRating.setText(String.valueOf(average));
        mRatingShopOverall.setRating(average);
        db.collection("restaurants")
                .document(id_restaurent)
                .update("rate", String.format("%.1f", average));

        widthView = constrainLayout1.getWidth();

        float sum1 = (r1 / (float)mRates.size());
        int rating1 = (int) (sum1 * widthView);
        ConstraintLayout.LayoutParams layoutParams1 = new ConstraintLayout.LayoutParams(rating1, ConstraintLayout.LayoutParams.MATCH_PARENT);
        mLl_percentage_1.setLayoutParams(layoutParams1);

        float sum2 = (r2 / (float)mRates.size());
        int rating2= (int) (sum2 * widthView);
        ConstraintLayout.LayoutParams layoutParams2 = new ConstraintLayout.LayoutParams(rating2, ConstraintLayout.LayoutParams.MATCH_PARENT);
        mLl_percentage_2.setLayoutParams(layoutParams2);

        float sum3 = (r3 / (float)mRates.size());
        int rating3 = (int) (sum3 * widthView);
        ConstraintLayout.LayoutParams layoutParams3 = new ConstraintLayout.LayoutParams(rating3, ConstraintLayout.LayoutParams.MATCH_PARENT);
        mLl_percentage_3.setLayoutParams(layoutParams3);

        float sum4 = (r4 / (float)mRates.size());
        int rating4 = (int) (sum4 * widthView);
        ConstraintLayout.LayoutParams layoutParams4 = new ConstraintLayout.LayoutParams(rating4, ConstraintLayout.LayoutParams.MATCH_PARENT);
        mLl_percentage_4.setLayoutParams(layoutParams4);

        float sum5 = (r5 / (float)mRates.size());
        int rating5 = (int) (sum5 * widthView);
        ConstraintLayout.LayoutParams layoutParams5 = new ConstraintLayout.LayoutParams(rating5, ConstraintLayout.LayoutParams.MATCH_PARENT);
        mLl_percentage_5.setLayoutParams(layoutParams5);
        mAdapter.notifyDataSetChanged();
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
                        //rating.setId(doc.getId());
                        rating.setComment(doc.getString("comment"));
                        rating.setRate(Integer.parseInt(doc.getString("rate")));
                        rating.setTime(doc.getTimestamp("time").toDate().toString());
                        rating.setUser_name(doc.getString("user_name"));
                        //rating.setUser_id(doc.getString("user_id"));
                        //rating.setRes_id(doc.getString("res_id"));
                        mRates.add(rating);
                        mAdapter.notifyDataSetChanged();
                    }
                    setOverallReview();
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
                mTextShopName.setText(doc.getString("name"));
            }
        });
    }

}
