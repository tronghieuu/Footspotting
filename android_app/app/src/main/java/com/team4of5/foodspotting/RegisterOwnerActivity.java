package com.team4of5.foodspotting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.team4of5.foodspotting.object.User;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegisterOwnerActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEdtName, mEdtType, mEdtAddress, mEdtPhone;
    private Button mBtnBack, mBtnCreate, mBtnPickPhoto, mBtnResOpenDangKy, mBtnResCloseDangKy;
    private ImageView mImageView;
    private Uri filePath;
    private static int PICK_IMAGE_REQUEST = 341;
    private Dialog dialog;
    private int mHour, mMinute;

    private Spinner mEdtProvince,
            mEdtDistrict;
    ArrayAdapter<String> adapter1 = null;
    String arr[]={
            "Hà Nội",
            "Đà Nẵng",
            "Hồ Chí Minh"};
    String arr1[]={
            "Ba Đình",
            "Bắc Từ Liêm",
            "Cầu Giấy",
            "Đống Đa",
            "Hà Đông"};
    String arr2[]={
            "Hải Châu",
            "Cẩm Lệ",
            "Thanh Khê",
            "Liên Chiểu",
            "Ngũ Hành Sơn",
            "Sơn Trà",
            "Hòa Vang"};
    String arr3[]={
            "Quận 1",
            "Quận 2",
            "Quận 3",
            "Quận 4",
            "Quận 5"};

    public void setAddress()
    {
        if(mEdtProvince.getSelectedItem().toString().equals("Hà Nội"))
            adapter1=new ArrayAdapter<String>
                    (
                            this,
                            android.R.layout.simple_spinner_item,
                            arr1
                    );
        if(mEdtProvince.getSelectedItem().toString().equals("Đà Nẵng"))
            adapter1=new ArrayAdapter<String>
                    (
                            this,
                            android.R.layout.simple_spinner_item,
                            arr2
                    );
        if(mEdtProvince.getSelectedItem().toString().equals("Hồ Chí Minh"))
            adapter1=new ArrayAdapter<String>
                    (
                            this,
                            android.R.layout.simple_spinner_item,
                            arr3
                    );
        adapter1.setDropDownViewResource
                (android.R.layout.simple_list_item_single_choice);
        mEdtDistrict.setAdapter(adapter1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_owner);
        getSupportActionBar().hide();
        Window window = this.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.color_owner));
        mEdtName = findViewById(R.id.edtOwnerResNameDangKy);
        mEdtType = findViewById(R.id.edtOwnerResTypeDangKy);
        mEdtProvince = findViewById(R.id.edtOwnerResProvinceDangKy);
        mEdtDistrict = findViewById(R.id.edtOwnerResDistrictDangKy);

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

        adapter1=new ArrayAdapter<String>
                (
                        this,
                        android.R.layout.simple_spinner_item,
                        arr1
                );
        adapter1.setDropDownViewResource
                (android.R.layout.simple_list_item_single_choice);
        mEdtDistrict.setAdapter(adapter1);

        mEdtAddress = findViewById(R.id.edtOwnerResAddressDangKy);
        mEdtPhone = findViewById(R.id.edtOwnerResPhoneDangKy);
        mBtnResOpenDangKy = findViewById(R.id.btn_ResOpenDangky);
        mBtnResCloseDangKy = findViewById(R.id.btn_ResCloseDangky);
        mBtnBack = findViewById(R.id.btnBackOwnerResDangKy);
        mBtnCreate = findViewById(R.id.btnUpdateOwnerResDangKy);
        mBtnPickPhoto = findViewById(R.id.btnChonAnhDangKy);
        mImageView = findViewById(R.id.imageViewPreviewDangKy);
        mBtnBack.setOnClickListener(this);
        mBtnCreate.setOnClickListener(this);
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBackOwnerResDangKy:
                finish();
                break;
            case R.id.btnUpdateOwnerResDangKy:
                createRestaurant();
                break;
            case R.id.btnChonAnhDangKy:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }
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

    public void createRestaurant(){
        Toast toast = Toast.makeText(this, "toast", Toast.LENGTH_SHORT);
        if(filePath == null) {
            toast.setText("Chưa nhập đủ thông tin!");
            toast.show();
            return;
        }
        final String name = mEdtName.getText().toString();
        if(name.length() == 0){
            toast.setText("Chưa nhập đủ thông tin!");
            toast.show();
            return;
        }
        final String type = mEdtType.getText().toString();
        if(type.length() == 0){
            toast.setText("Chưa nhập đủ thông tin!");
            toast.show();
            return;
        }
        final String province = mEdtProvince.getSelectedItem().toString();
        if(province.length() == 0){
            toast.setText("Chưa nhập đủ thông tin!");
            toast.show();
            return;
        }
        final String district = mEdtDistrict.getSelectedItem().toString();
        if(district.length() == 0){
            toast.setText("Chưa nhập đủ thông tin!");
            toast.show();
            return;
        }
        final String street = mEdtAddress.getText().toString();
        if(street.length() == 0){
            toast.setText("Chưa nhập đủ thông tin!");
            toast.show();
            return;
        }
        final String phone = mEdtPhone.getText().toString();
        if(phone.length() == 0){
            toast.setText("Chưa nhập đủ thông tin!");
            toast.show();
            return;
        }
        final String opening_time = mBtnResOpenDangKy.getText().toString();
        if (opening_time == null){
            toast.setText("Chưa nhập đủ thông tin!");
            toast.show();
            return;
        }
        final String closing_time = mBtnResCloseDangKy.getText().toString();
        if (opening_time == null){
            toast.setText("Chưa nhập đủ thông tin!");
            toast.show();
            return;
        }
        dialog.show();
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("province", province);
        data.put("district", district);
        data.put("street", street);
        data.put("type", type);
        data.put("phone", phone);
        data.put("opening_time", opening_time);
        data.put("closing_time",closing_time);
        data.put("user_id", User.getCurrentUser().getId());
        data.put("rate", "0.0");
        FirebaseFirestore.getInstance().collection("restaurants")
                .add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(final DocumentReference documentReference) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                final StorageReference ref = storageReference.child("restaurantImage/"+ documentReference.getId());
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
                                                .document(documentReference.getId()).update(data)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Map<String, Object> type = new HashMap<>();
                                                        type.put("type", "3");
                                                        User.getCurrentUser().setType(3);
                                                        FirebaseFirestore.getInstance().collection("user")
                                                                .document(User.getCurrentUser().getId())
                                                                .update(type).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(getApplicationContext(), "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                                                User.getCurrentUser().setOwnerUpdate(true);
                                                                Intent intent = new Intent(getApplicationContext(), OwnerAppActivity.class);
                                                                dialog.dismiss();
                                                                startActivity(intent);
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
    }

    public void onClickTimePickerButtonOpen(View view) {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        mBtnResOpenDangKy.setText(hourOfDay+":"+minute);
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
                        mBtnResCloseDangKy.setText(hourOfDay+":"+minute);
                    }
                },mHour,mMinute,false);
        timePickerDialog.show();
    }
}
