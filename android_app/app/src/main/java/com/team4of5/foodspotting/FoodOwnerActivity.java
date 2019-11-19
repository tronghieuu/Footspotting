package com.team4of5.foodspotting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodOwnerActivity extends AppCompatActivity implements View.OnClickListener {

    private String id_res;
    private EditText mEdtName, mEdtInfo, mEdtPrice, mEdtModifyName, mEdtModifyPrice, mEdtModifyInfo;
    private ImageView imageView, mImageViewModify;
    private Button mBtnPickPhoto, mBtnAdd, mBtnMenuBack, mBtnPickImageModify, mBtnChange;
    private RecyclerView recyclerView;
    private List<Food> mFoods;
    private FoodAdapterPreview mAdapter;
    private Dialog dialog, loadingDialog;
    private static int PICK_IMAGE_REQUEST = 983, PICK_CHANGE_IMAGE_REQUEST = 888;
    private Uri filePath, subFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_owner);
        getSupportActionBar().hide();
        Window window = this.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.color_owner));
        id_res = getIntent().getStringExtra("res_id");
        mEdtName = findViewById(R.id.edtFoodNameAdd);
        mEdtInfo = findViewById(R.id.edtFoodInfoAdd);
        mEdtPrice = findViewById(R.id.edtFoodPriceAdd);
        imageView = findViewById(R.id.imageViewFood);
        mBtnPickPhoto = findViewById(R.id.btnPickFoodImage);
        mBtnAdd = findViewById(R.id.btnAddFood);
        mBtnMenuBack = findViewById(R.id.btnBackThucDon);
        mBtnPickPhoto.setOnClickListener(this);
        mBtnAdd.setOnClickListener(this);
        mBtnMenuBack.setOnClickListener(this);

        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_modify_food);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                subFilePath = null;
                mEdtModifyInfo.setText("");
                mEdtModifyPrice.setText("");
                mEdtModifyName.setText("");
                mImageViewModify.setImageBitmap(null);
            }
        });

        loadingDialog = new Dialog(this);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setContentView(R.layout.item_loading);

        mFoods = new ArrayList<>();
        recyclerView = findViewById(R.id.rcvFoodAdd);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new FoodAdapterPreview(this, mFoods, recyclerView, new FoodAdapterPreview.FoodButtonListener() {
            @Override
            public void onDeleteButtonClick(View v, final int position) {
                loadingDialog.show();
                FirebaseFirestore.getInstance().collection("food")
                        .document(mFoods.get(position).getId())
                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mFoods.remove(position);
                        mAdapter.notifyDataSetChanged();
                        loadingDialog.dismiss();
                    }
                });
            }

            @Override
            public void onModifyButtonClick(View v, int position) {
                openDialogModify(position);
            }
        });
        recyclerView.setAdapter(mAdapter);

        loadFood();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddFood:
                addFood();
                break;
            case R.id.btnPickFoodImage:
                pickPhoto();
                break;
            case R.id.btnBackThucDon:
                finish();
        }
    }

    public void pickPhoto(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else if(requestCode == PICK_CHANGE_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            subFilePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), subFilePath);
                mImageViewModify.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void addFood(){
        if(filePath != null) {
            final String name = mEdtName.getText().toString();
            if(name.length() == 0) {
                Toast.makeText(this, "Chưa nhâp tên món", Toast.LENGTH_SHORT).show();
                return;
            }
            final String info = mEdtInfo.getText().toString();
            if(info.length() == 0) {
                Toast.makeText(this, "Chưa nhâp thông tin món", Toast.LENGTH_SHORT).show();
                return;
            }
            final String price = mEdtPrice.getText().toString();
            if(price.length() == 0) {
                Toast.makeText(this, "Chưa nhập giá tiền", Toast.LENGTH_SHORT).show();
                return;
            }

            loadingDialog.show();
            Map<String, Object> map = new HashMap<>();
            map.put("image", "");
            map.put("info", info);
            map.put("name", name);
            map.put("price", price);

            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("restaurants").document(id_res).collection("food")
                    .add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(final DocumentReference documentReference) {
                    final String id = documentReference.getId();
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                    final StorageReference ref = storageReference.child("foodImage/"+ documentReference.getId());
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
                                                    .collection("food")
                                                    .document(id).update(data)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            final Food f = new Food(id,imageLink,info,name,price,id_res);
                                                            mFoods.add(f);
                                                            mAdapter.notifyDataSetChanged();
                                                            loadingDialog.dismiss();
                                                        }
                                                    });
                                        }
                                    });
                                }
                            });
                }
            });
        } else {
            Toast.makeText(this, "Chưa chọn ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadFood(){
        FirebaseFirestore.getInstance().collection("restaurants")
                .document(id_res).collection("food")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()) {
                    for(DocumentSnapshot doc : queryDocumentSnapshots) {
                        Food f = new Food();
                        f.setId(doc.getId());
                        f.setName(doc.getString("name"));
                        f.setInfo(doc.getString("info"));
                        f.setPrice(doc.getString("price"));
                        f.setImage(doc.getString("image"));
                        mFoods.add(f);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void openDialogModify(final int position) {

        mEdtModifyInfo = dialog.findViewById(R.id.edtChangeInfoFood);
        mEdtModifyName = dialog.findViewById(R.id.edtChangeNameFood);
        mEdtModifyPrice = dialog.findViewById(R.id.edtChangePriceFood);
        mBtnPickImageModify = dialog.findViewById(R.id.btnPickImageChangeFood);
        mImageViewModify = dialog.findViewById(R.id.imageViewChangeFood);
        mBtnChange = dialog.findViewById(R.id.btnChangeFood);

        final Food f = mFoods.get(position);
        mEdtModifyInfo.setHint("Thông tin: "+f.getInfo());
        mEdtModifyPrice.setHint("Giá: "+f.getPrice());
        mEdtModifyName.setHint("Tên: "+f.getName());
        mBtnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (subFilePath != null) {
                    loadingDialog.show();
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                    final StorageReference ref = storageReference.child("foodImage/" + mFoods.get(position).getId());
                    ref.putFile(subFilePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String linkImage = uri.toString();
                                    f.setImage(linkImage);
                                    Map<String, Object> data = new HashMap<>();
                                    if (mEdtModifyName.getText().toString().length() != 0) {
                                        data.put("name", mEdtModifyName.getText().toString());
                                        f.setName(mEdtModifyName.getText().toString());
                                    }
                                    if (mEdtModifyPrice.getText().toString().length() != 0) {
                                        data.put("price", mEdtModifyPrice.getText().toString());
                                        f.setPrice(mEdtModifyPrice.getText().toString());
                                    }
                                    if(linkImage.length() != 0){
                                        data.put("image", linkImage);
                                    }
                                    FirebaseFirestore.getInstance().collection("restaurants")
                                            .document(id_res)
                                            .collection("food")
                                            .document(mFoods.get(position).getId())
                                            .update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mAdapter.notifyDataSetChanged();
                                            dialog.dismiss();
                                            loadingDialog.dismiss();
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
                else {
                    loadingDialog.show();
                    Map<String, Object> data = new HashMap<>();
                    if (mEdtModifyName.getText().toString().length() != 0) {
                        data.put("name", mEdtModifyName.getText().toString());
                        f.setName(mEdtModifyName.getText().toString());
                    }
                    if (mEdtModifyPrice.getText().toString().length() != 0) {
                        data.put("price", mEdtModifyPrice.getText().toString());
                        f.setPrice(mEdtModifyPrice.getText().toString());
                    }
                    FirebaseFirestore.getInstance().collection("restaurants")
                            .document(id_res)
                            .collection("food")
                            .document(mFoods.get(position).getId())
                            .update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                            loadingDialog.dismiss();
                        }
                    });
                }
            }
        });
        mBtnPickImageModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_CHANGE_IMAGE_REQUEST);
            }
        });
        dialog.show();
    }
}
