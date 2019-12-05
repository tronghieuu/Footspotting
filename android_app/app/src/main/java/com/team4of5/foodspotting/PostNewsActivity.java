package com.team4of5.foodspotting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.team4of5.foodspotting.notification.News;
import com.team4of5.foodspotting.object.Food;
import com.team4of5.foodspotting.object.Restaurant;
import com.team4of5.foodspotting.object.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class PostNewsActivity extends AppCompatActivity {
    private Button mBtnBackDangtin;
    private EditText mEdtTitle, mEdtContent;
    private ImageView mImageNews;
    private FloatingActionButton mBtnAddNews;
    private static int PICK_IMAGE_REQUEST =11;
    private Dialog loadingDialog;
    private Uri filePath;
    private String id_res;
    private String tenquan, address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_news);
        getSupportActionBar().hide();
        id_res = getIntent().getStringExtra("res_id");
        mBtnBackDangtin = findViewById(R.id.btnBackDangtin);
        mEdtTitle = findViewById(R.id.edit_title);
        mEdtContent = findViewById(R.id.edit_content);
        mImageNews = findViewById(R.id.image_news);
        mBtnAddNews = findViewById(R.id.add_news);

        mBtnBackDangtin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mBtnAddNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNews();
            }
        });
        mImageNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentImage = new Intent();
                intentImage.setType("image/*");
                intentImage.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intentImage, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        // Dialog
        loadingDialog = new Dialog(this);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setContentView(R.layout.item_loading);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            //loadingDialog.show();
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                mImageNews.setImageBitmap(bitmap);

            }
            catch (IOException e)
            {
                Toast.makeText(this,"fail",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
    public void addNews(){
        if(filePath != null) {
            final String title = mEdtTitle.getText().toString();
            if(title.length() == 0) {
                Toast.makeText(this, "Chưa nhâp tiêu đề", Toast.LENGTH_SHORT).show();
                return;
            }
            final String content = mEdtContent.getText().toString();
            if(content.length() == 0) {
                Toast.makeText(this, "Chưa nhâp nội dung", Toast.LENGTH_SHORT).show();
                return;
            }
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("restaurants")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        for (DocumentSnapshot doccument: task.getResult())
                            if (doccument.getId().equals(id_res)){
                                tenquan = doccument.getString("name");
                                address = doccument.getString("street") +","+ doccument.getString("district") +"," + doccument.getString("province");
                                loadingDialog.show();
                                Map<String, Object> map = new HashMap<>();
                                map.put("image", "");
                                map.put("content", content);
                                map.put("title", title);
                                map.put("tenquan", tenquan);
                                map.put("address", address);

                                FirebaseFirestore db1 = FirebaseFirestore.getInstance();
                                db1.collection("restaurants").document(id_res).collection("news")
                                        .add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(final DocumentReference documentReference) {
                                        final String id = documentReference.getId();
                                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                                        final StorageReference ref = storageReference.child("notificationImage/"+ documentReference.getId());
                                        ref.putFile(filePath)
                                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                final String imageLink = uri.toString();
                                                                Map<String, Object> data = new HashMap<>();
                                                                data.put("image", imageLink);
                                                                FirebaseFirestore.getInstance().collection("restaurants")
                                                                        .document(id_res)
                                                                        .collection("news")
                                                                        .document(id).update(data)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                loadingDialog.dismiss();
                                                                                Toast.makeText(getApplicationContext(),"Completed News",Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                            }
                                                        });
                                                    }
                                                });
                                    }
                                });
                            }
                    }
                }
                    });
            }else {
            Toast.makeText(getApplicationContext(), "Chưa chọn ảnh", Toast.LENGTH_SHORT).show();
        }
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
