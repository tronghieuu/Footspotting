package com.team4of5.foodspotting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.team4of5.foodspotting.object.User;
import com.team4of5.foodspotting.rating.Rate;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class OwnerAppActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout mBtnThongTinQuan, mBtnThucDon, mBtnDanhSachOrder, mBtnDongQuan,
            mBtnDanhGiaCuaKhachHang;
    private Button mBtnBack;
    private ImageView mImageView;
    private static int RQ_UPDATE = 234;
    private String id_res;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_app);
        getSupportActionBar().hide();
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.color_owner));
        mBtnThongTinQuan = findViewById(R.id.btnThongTinQuan);
        mBtnThucDon = findViewById(R.id.btnThucDon);
        mBtnDanhSachOrder = findViewById(R.id.btnDanhSachOrder);
        mBtnDanhGiaCuaKhachHang = findViewById(R.id.btnDanhGiaCuaKhanhHang);
        mBtnBack = findViewById(R.id.btnBackOwner);
        mBtnDongQuan = findViewById(R.id.btnDongQuan);
        mImageView = findViewById(R.id.imageOwnerRes);
        setImage();

        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_loading);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });

        mBtnThongTinQuan.setOnClickListener(this);
        mBtnThucDon.setOnClickListener(this);
        mBtnDanhSachOrder.setOnClickListener(this);
        mBtnDanhGiaCuaKhachHang.setOnClickListener(this);
        mBtnDongQuan.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btnThongTinQuan:
                startActivityForResult(new Intent(OwnerAppActivity.this, ResInfoActivity.class), RQ_UPDATE);
                break;
            case R.id.btnThucDon:
                intent = new Intent(OwnerAppActivity.this, FoodOwnerActivity.class);
                intent.putExtra("res_id", id_res);
                startActivity(intent);
                break;
            case R.id.btnDanhSachOrder:
                Intent intent1 = new Intent(getApplicationContext(), OrderManagerActivity.class);
                intent1.putExtra("res_id", id_res);
                startActivity(intent1);
                break;
            case R.id.btnDanhGiaCuaKhanhHang:
                intent = new Intent(OwnerAppActivity.this, Rate.class);
                intent.putExtra("id_restaurent", id_res);
                intent.putExtra("button_show", false);
                startActivity(intent);
                break;
            case R.id.btnDongQuan:
                dialog.show();
                FirebaseFirestore.getInstance().collection("order")
                        .whereEqualTo("restaurant_id", id_res).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();
                                for(DocumentSnapshot doc : queryDocumentSnapshots){
                                    DocumentReference documentReference = FirebaseFirestore.getInstance()
                                            .collection("order").document(doc.getId());
                                    writeBatch.delete(documentReference);
                                }
                                writeBatch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                                        StorageReference ref = storageReference.child("restaurantImage/"+ id_res);
                                        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                FirebaseFirestore.getInstance().collection("restaurants")
                                                        .document(id_res).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Map<String, Object> data = new HashMap<>();
                                                        data.put("type", "1");
                                                        FirebaseFirestore.getInstance().collection("user")
                                                                .document(User.getCurrentUser().getId())
                                                                .update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                User.getCurrentUser().setOwnerUpdate(true);
                                                                User.getCurrentUser().setType(1);
                                                                dialog.dismiss();
                                                                finish();
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });
                break;
            case R.id.btnBackOwner:
                finish();
        }
    }

    public void setImage(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("restaurants")
                .whereEqualTo("user_id", User.getCurrentUser().getId())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                    id_res = doc.getId();
                    new DownloadImageFromInternet(mImageView)
                            .execute(doc.getString("image"));
                }
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
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RQ_UPDATE){
            if(resultCode == RESULT_OK){
                if(data.getStringExtra("check").contentEquals("ok")) {
                    this.recreate();
                }
            }
        }
    }
}
