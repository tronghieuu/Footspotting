package com.team4of5.foodspotting;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.List;

public class Restaurent extends AppCompatActivity {
    private TextView mShopName;
    private TextView mShopType;
    private TextView mOpeningTime;
    private Button mShopContact;
    private TextView mTextShopShowAllReview;
    private TextView mRatingShopOverall;
    private RatingBar mRatingShopOverallStar;
    private TextView mShopAddress;
    private TextView mShowAllReview;
    private SearchView mSearchFood;
    private RecyclerView mRecylerView;
    private String phonenum;
    private String id_restaurent;
    private FirebaseFirestore db;
    private ImageView mImageView;
    private List<Food> mFoods;
    private FoodAdapter mAdapter;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurent);
        getSupportActionBar().hide();

        id_restaurent = getIntent().getStringExtra("id_restaurent");
        db = FirebaseFirestore.getInstance();
        mShopName = findViewById(R.id.textShopName);
        mShopType = findViewById(R.id.textShopType);
        mOpeningTime = findViewById(R.id.textOpeningTime);
        mShopContact = findViewById(R.id.buttonShopContact);
        mTextShopShowAllReview = findViewById(R.id.textShopShowAllReview);
        mRatingShopOverall = findViewById(R.id.textRatingShopOverall);
        mRatingShopOverallStar = findViewById(R.id.ratingShopOverallStar);
        mShopAddress = findViewById(R.id.textShopAddress);
        mShowAllReview = findViewById(R.id.textShopShowAllReview);
        mSearchFood = findViewById(R.id.searchFood);
        mImageView = findViewById(R.id.imageShop);

        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_loading);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });

        mFoods = new ArrayList<>();
        mRecylerView = findViewById(R.id.recycleViewFoodList);
        mRecylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new FoodAdapter(this, mFoods, mRecylerView);
        mRecylerView.setAdapter(mAdapter);
        getRes();
        queryFood();

        mAdapter.setOnItemListener(new FoodAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Food item) {
                Toast.makeText(getApplicationContext(), "Food detail", Toast.LENGTH_SHORT).show();
            }
        });


        mShopContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" +phonenum));
                startActivity(intent);
            }
        });

        mTextShopShowAllReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Rate.class);
                intent.putExtra("id_restaurent", id_restaurent);
                intent.putExtra("button_show", true);
                startActivity(intent);
            }
        });
    }

    public void queryFood(){
        db.collection("restaurants")
                .document(id_restaurent)
                .collection("food")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    for(DocumentSnapshot doc : queryDocumentSnapshots){
                        Food food = new Food();
                        food.setImage(doc.getString("image"));
                        food.setInfo(doc.getString("info"));
                        food.setName(doc.getString("name"));
                        food.setPrice(doc.getString("price"));
                        food.setId(doc.getId());
                        mFoods.add(food);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    public void getRes(){
        dialog.show();
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
                new Restaurent.DownloadImageFromInternet(mImageView)
                        .execute(doc.getString("image"));
            }
        });
    }

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {

        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView){
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
            dialog.dismiss();
        }
    }
}
