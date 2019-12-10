package com.team4of5.foodspotting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.team4of5.foodspotting.notification.News;
import com.team4of5.foodspotting.restaurant.Restaurent;

import java.io.InputStream;

public class NewsDetailActivity extends AppCompatActivity {
    private TextView mTenquan, mDiachi, mTieude, mNoidung;
    private ImageView mImage;
    private Button mbtnGotoRes, mBackNewsDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        getSupportActionBar().hide();

        mTenquan = findViewById(R.id.tenquanDetail);
        mDiachi = findViewById(R.id.addressNewsDetail);
        mTieude = findViewById(R.id.titleNewsDetail);
        mNoidung = findViewById(R.id.noidungNewsDetail);
        mImage = findViewById(R.id.imageNewsDetail);
        mbtnGotoRes = findViewById(R.id.btnGotoRes);
        mBackNewsDetail = findViewById(R.id.btnBackNewsDetail);
        setEvent();
    }

    private void setEvent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("package");
        final News news =(News) bundle.getSerializable("news");
        mTenquan.setText(news.getTenquan());
        mDiachi.setText(news.getAddress());
        mTieude.setText(news.getTitle());
        mNoidung.setText(news.getContent());
        //getImage
        if(news.getImage() != null) {
            new DownloadImageFromInternet(mImage)
                    .execute(news.getImage());
        }

        //xu ly btn
        mBackNewsDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mbtnGotoRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Restaurent.class);
                intent.putExtra("id_restaurent",news.getId_res());
                startActivity(intent);
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
}
