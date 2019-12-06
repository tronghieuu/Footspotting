package com.team4of5.foodspotting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.team4of5.foodspotting.object.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ResInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEdtName, mEdtType, mEdtAddress, mEdtPhone;
    private Spinner mEdtProvince,
                mEdtDistrict;
    ArrayAdapter<String> adapter1 = null;
    String arr[]={
            "Hà Nội",
            "TT Huế",
            "Đà Nẵng"};
    String arr1[]={
            "Quận 1",
            "Quận 2",
            "Quận 3"};
    String arr2[]={
            "Phú Vang",
            "Phú Thượng"};
    String arr3[]={
            "Hải Châu",
            "Liên Chiểu",
            "Hòa Khánh"};
    private Button mBtnBack, mBtnUpdate, mBtnPickPhoto, mBtnOpenning, mBtnClosing;
    private String id_res;
    private ImageView mImageView;
    private Uri filePath;
    private static int PICK_IMAGE_REQUEST = 2341;
    private Dialog dialog;
    private int mHour, mMinute;

    public void setAddress()
    {
        if(mEdtProvince.getSelectedItem().toString().equals("Hà Nội"))
            adapter1=new ArrayAdapter<String>
                    (
                            this,
                            android.R.layout.simple_spinner_item,
                            arr1
                    );
        if(mEdtProvince.getSelectedItem().toString().equals("TT Huế"))
            adapter1=new ArrayAdapter<String>
                    (
                            this,
                            android.R.layout.simple_spinner_item,
                            arr2
                    );
        if(mEdtProvince.getSelectedItem().toString().equals("Đà Nẵng"))
            adapter1=new ArrayAdapter<String>
                    (
                            this,
                            android.R.layout.simple_spinner_item,
                            arr3
                    );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_info);
        getSupportActionBar().hide();
        Window window = this.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.color_owner));
        mEdtName = findViewById(R.id.edtOwnerResName);
        mEdtType = findViewById(R.id.edtOwnerResType);
        mEdtProvince = findViewById(R.id.edtOwnerResProvince);
        mEdtDistrict = findViewById(R.id.edtOwnerResDistrict);

        ///////////
        ArrayAdapter<String> adapter=new ArrayAdapter<String>
                (
                        this,
                        android.R.layout.simple_spinner_item,
                        arr
                );
        adapter.setDropDownViewResource
                (android.R.layout.simple_list_item_single_choice);
        mEdtProvince.setAdapter(adapter);
        mEdtProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setAddress();adapter1.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });


        adapter1.setDropDownViewResource
                (android.R.layout.simple_list_item_single_choice);
        mEdtDistrict.setAdapter(adapter1);
        ///////////
        mEdtAddress = findViewById(R.id.edtOwnerResAddress);
        mEdtPhone = findViewById(R.id.edtOwnerResPhone);
        mBtnOpenning = findViewById(R.id.btn_ResOpen);
        mBtnClosing = findViewById(R.id.btn_ResClose);
        mBtnBack = findViewById(R.id.btnBackOwnerRes);
        mBtnUpdate = findViewById(R.id.btnUpdateOwnerRes);
        mBtnPickPhoto = findViewById(R.id.btnChonAnh);
        mImageView = findViewById(R.id.imageViewPreview);
        mBtnBack.setOnClickListener(this);
        mBtnUpdate.setOnClickListener(this);
        mBtnPickPhoto.setOnClickListener(this);
        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_loading);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });
        loadCurrentData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBackOwnerRes:
                Intent intent = new Intent();
                intent.putExtra("check", "not ok");
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
            case R.id.btnUpdateOwnerRes:
                User.getCurrentUser().setOwnerUpdate(true);
                uploadImage();
                break;
            case R.id.btnChonAnh:
                pickPhoto();
        }
    }

    public void update() {
        String name, type, province, district, address, phone, openingTime, closingTime;
        name = mEdtName.getText().toString();
        type = mEdtType.getText().toString();
        province = mEdtProvince.getSelectedItem().toString();
        district = mEdtDistrict.getSelectedItem().toString();
        address = mEdtAddress.getText().toString();
        phone = mEdtPhone.getText().toString();
        openingTime = mBtnOpenning.getText().toString();
        closingTime = mBtnClosing.getText().toString();

        Map<String, Object> data = new HashMap<>();
        if(name.length() != 0) data.put("name", name);
        if(type.length() != 0) data.put("type", type);
        if(province.length() != 0) data.put("province", province);
        if(district.length() != 0) data.put("district", district);
        if(address.length() != 0) data.put("street", address);
        if(phone.length() != 0) data.put("phone", phone);
        if(openingTime.length() != 0) data.put("opening_time", openingTime);
        if(closingTime.length() != 0) data.put("closing_time", closingTime);

        FirebaseFirestore.getInstance().collection("restaurants").document(id_res)
                .update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent = new Intent();
                intent.putExtra("check", "ok");
                setResult(Activity.RESULT_OK, intent);
                dialog.dismiss();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Failed to update!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadCurrentData() {
        dialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("restaurants")
                .whereEqualTo("user_id", User.getCurrentUser().getId())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                    mEdtName.setHint(doc.getString("name"));
                    mEdtType.setHint(doc.getString("type"));

                    mEdtAddress.setHint(doc.getString("street"));
                    mEdtPhone.setHint(doc.getString("phone"));
                    mBtnOpenning.setHint(doc.getString("opening_time"));
                    mBtnClosing.setHint(doc.getString("closing_time"));
                    new DownloadImageFromInternet(mImageView)
                            .execute(doc.getString("image"));
                    id_res = doc.getId();
                }
            }
        });
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
                mImageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void onClickTimePickerButtonOpen(View view) {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        mBtnOpenning.setText(hourOfDay+":"+minute);
                    }
                },mHour,mMinute,false);
        timePickerDialog.show();
    }

    public void onClickTimePickerButtonClose(View view) {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        mBtnClosing.setText(hourOfDay+":"+minute);
                    }
                },mHour,mMinute,false);
        timePickerDialog.show();
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
            dialog.dismiss();
            imageView.setImageBitmap(result);
        }
    }

    public void uploadImage(){
        dialog.show();
        if(filePath != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final StorageReference ref = storageReference.child("restaurantImage/"+ id_res);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("image", uri.toString());
                                    FirebaseFirestore.getInstance().collection("restaurants")
                                            .document(id_res).update(data)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    update();
                                                }
                                            });
                                }
                            });
                        }
                    });
        } else update();
    }
}
