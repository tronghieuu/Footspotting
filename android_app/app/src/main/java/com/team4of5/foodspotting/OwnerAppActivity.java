package com.team4of5.foodspotting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.InputStream;

public class OwnerAppActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout mBtnThongTinQuan, mBtnThucDon, mBtnDanhSachOrder, mBtnDongQuan,
            mBtnDanhGiaCuaKhachHang;
    private Button mBtnBack;
    private ImageView mImageView;
    private static int RQ_UPDATE = 234, REQUEST_PERMISSION = 293;

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

        mBtnThongTinQuan.setOnClickListener(this);
        mBtnThucDon.setOnClickListener(this);
        mBtnDanhSachOrder.setOnClickListener(this);
        mBtnDanhGiaCuaKhachHang.setOnClickListener(this);
        mBtnDongQuan.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnThongTinQuan:
                startActivityForResult(new Intent(OwnerAppActivity.this, ResInfoActivity.class), RQ_UPDATE);
                break;
            case R.id.btnThucDon:
                break;
            case R.id.btnDanhSachOrder:
                break;
            case R.id.btnDanhGiaCuaKhanhHang:
                break;
            case R.id.btnDongQuan:
                Toast.makeText(this, "Pathetic!", Toast.LENGTH_SHORT).show();
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
