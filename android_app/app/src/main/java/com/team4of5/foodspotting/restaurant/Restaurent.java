package com.team4of5.foodspotting.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.team4of5.foodspotting.R;
import com.team4of5.foodspotting.food.FoodAdapter;
import com.team4of5.foodspotting.object.Food;
import com.team4of5.foodspotting.object.User;
import com.team4of5.foodspotting.rating.Rate;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Restaurent extends AppCompatActivity implements View.OnClickListener {
    private TextView mShopType;
    private TextView mOpeningTime;
    private Button mShopContact;
    private TextView mTextShopShowAllReview;
    private TextView mRatingShopOverall;
    private RatingBar mRatingShopOverallStar;
    private TextView mShopAddress;
    private TextView mShowAllReview;
    private RecyclerView mRecylerView;
    private String phonenum;
    private String id_restaurent;
    private FirebaseFirestore db;
    private ImageView mImageView;
    private List<Food> mFoods;
    private FoodAdapter mAdapter;
    private Dialog dialog, mOrderDialog;
    private TextView mTvOrderAmount, mTvOrderPrice;
    private Button mBtnDecrease, mBtnIncrease, mBtnDraft, mBtnOrder;
    private int mAmountOrder, mOrderPrice;
    private Food mFood;
    private TextView mTvResName;
    private Button mBtnBack;
    private String province;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurent);
        getSupportActionBar().hide();

        id_restaurent = getIntent().getStringExtra("id_restaurent");
        db = FirebaseFirestore.getInstance();
        mShopType = findViewById(R.id.textShopType);
        mOpeningTime = findViewById(R.id.textOpeningTime);
        mShopContact = findViewById(R.id.buttonShopContact);
        mTextShopShowAllReview = findViewById(R.id.textShopShowAllReview);
        mRatingShopOverall = findViewById(R.id.textRatingShopOverall);
        mRatingShopOverallStar = findViewById(R.id.ratingShopOverallStar);
        mShopAddress = findViewById(R.id.textShopAddress);
        mShowAllReview = findViewById(R.id.textShopShowAllReview);
        mImageView = findViewById(R.id.imageShop);
        mBtnBack = findViewById(R.id.btnBackResDetail);
        mBtnBack.setOnClickListener(this);
        mTvResName = findViewById(R.id.tvResDetailName);

        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_loading);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });

        mOrderDialog = new Dialog(this);
        mOrderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mOrderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mOrderDialog.setContentView(R.layout.order_dialog_item);
        mTvOrderAmount = mOrderDialog.findViewById(R.id.tvOrderAmount);
        mTvOrderPrice = mOrderDialog.findViewById(R.id.tvOrderPrice);
        mBtnDecrease = mOrderDialog.findViewById(R.id.btnDecrease);
        mBtnDecrease.setOnClickListener(this);
        mBtnIncrease = mOrderDialog.findViewById(R.id.btnIncrease);
        mBtnIncrease.setOnClickListener(this);
        mBtnDraft = mOrderDialog.findViewById(R.id.btnDraft);
        mBtnDraft.setOnClickListener(this);
        mBtnOrder = mOrderDialog.findViewById(R.id.btnOrder);
        mBtnOrder.setOnClickListener(this);
        mOrderDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
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
                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    mFood = item;
                    mOrderDialog.show();
                    mOrderPrice = Integer.parseInt(item.getPrice());
                    mAmountOrder = 1;
                    mTvOrderAmount.setText("1");
                    mTvOrderPrice.setText("đ"+mOrderPrice);
                } else{
                    Toast.makeText(getApplicationContext(), "Bạn cần đăng nhập trước!", Toast.LENGTH_SHORT).show();
                }
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnIncrease:
                mAmountOrder++;
                mTvOrderAmount.setText(mAmountOrder+"");
                mTvOrderPrice.setText("đ"+String.valueOf(mOrderPrice*mAmountOrder));
                break;
            case R.id.btnDecrease:
                if(mAmountOrder>1){
                    mAmountOrder--;
                    mTvOrderAmount.setText(mAmountOrder);
                    mTvOrderPrice.setText("đ"+String.valueOf(mOrderPrice*mAmountOrder));
                }
                break;
            case R.id.btnDraft:
                User.getCurrentUser().setListUpdate(true);
                User.getCurrentUser().setCartUpdate(true);
                User.getCurrentUser().setHistoryUpdate(true);
                User.getCurrentUser().setOrderUpdate(true);
                Toast.makeText(getApplicationContext(), "Coming soon!", Toast.LENGTH_SHORT).show();
                mOrderDialog.dismiss();
                break;
            case R.id.btnOrder:
                mOrderDialog.dismiss();
                if(User.getCurrentUser().getProvince().length() != 0
                    && User.getCurrentUser().getPhone().length() != 0) {
                    if(User.getCurrentUser().getProvince().contentEquals(province)){
                        final long timestamp = new Date().getTime();
                        dialog.show();
                        Map<String, Object> order = new HashMap<>();
                        order.put("timestamp", timestamp);
                        order.put("user_id", User.getCurrentUser().getId());
                        order.put("food_id", mFood.getId());
                        order.put("amount", mAmountOrder+"");
                        order.put("status", "1");
                        order.put("restaurant_id", id_restaurent);
                        order.put("shipper_id", "");
                        order.put("area", User.getCurrentUser().getProvince());
                        FirebaseFirestore.getInstance().collection("order")
                                .add(order).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                User.getCurrentUser().setListUpdate(true);
                                User.getCurrentUser().setCartUpdate(true);
                                User.getCurrentUser().setHistoryUpdate(true);
                                User.getCurrentUser().setOrderUpdate(true);
                                dialog.dismiss();
                            }
                        });
                    } else Toast.makeText(this, "Không thể đặt món ở đây", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(this, "Hãy cập nhật thông tin cá nhân!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnBackResDetail:
                finish();
                break;
        }
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
                province = doc.getString("province");
                mTvResName.setText(doc.getString("name"));
                mShopType.setText(doc.getString("type"));
                mOpeningTime.setText(doc.getString("opening_time")+" - "+doc.getString("closing_time"));
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
