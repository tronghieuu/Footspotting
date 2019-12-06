package com.team4of5.foodspotting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.team4of5.foodspotting.object.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInformationActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnBack, mBtnChangeUsername, mBtnChangePhone, mBtnChangeAddress, mBtnUpdatePassword
            , mBtnChangePassword, mBtnChangePayment, mBtnchangeName, mBtnChangePhoneNum, mBtnChangeAddress_diff;
    private TextView mTvUsername, mTvPhone, mTvAddress, mTvEmailInfo;
    private Dialog changePasswordDialog, changeNameDialog, changePhoneDialog, changeAddressDialog, loadingDialog;
    private EditText mEdtCurrentPassword, mEdtNewPassword, mEdtConfirmPassword, mEdtChangeStreet, mEdtChangeName, mEdtChangePhone;
    private String province, district, street, name, phone;
    private ImageView profileImage;
    private static int PICK_IMAGE_REQUEST = 23;
    private Uri filePath;
    private Spinner  mEdtChangeProvince,
            mEdtChangeDistrict;
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

    ArrayAdapter<String> adapter1 = null;
    private static int PICK_IMAGE_REQUEST1 = 11;
    private Uri filePath1;
    private ImageView mBackground;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);
        getSupportActionBar().hide();
        mBtnBack = findViewById(R.id.btnBackProfile);
        mBtnChangeUsername = findViewById(R.id.btnChangeUsername);
        mBtnChangePhone = findViewById(R.id.btnChangePhone);
        mBtnChangeAddress = findViewById(R.id.btnChangeAddress);
        mBtnChangePassword = findViewById(R.id.btnChangePassword);
        mBtnChangePayment = findViewById(R.id.btnPayment);
        mTvUsername = findViewById(R.id.tvChangeUsername);
        mTvPhone = findViewById(R.id.tvChangePhone);
        mTvAddress = findViewById(R.id.tvChangeAddress);
        mTvEmailInfo = findViewById(R.id.tvEmailIfo);

        mBackground = findViewById(R.id.background);
        mBackground.setOnClickListener(this);

        profileImage = findViewById(R.id.imageViewAvatar);
        profileImage.setOnClickListener(this);

        // Dialog
        loadingDialog = new Dialog(this);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setContentView(R.layout.item_loading);

        changeAddressDialog = new Dialog(this);
        changeAddressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        changeAddressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        changeAddressDialog.setContentView(R.layout.item_change_address);
        mEdtChangeProvince = changeAddressDialog.findViewById(R.id.edtChangeProvince);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>
                (
                        this,
                        android.R.layout.simple_spinner_item,
                        arr
                );
        adapter.setDropDownViewResource
                (android.R.layout.simple_list_item_single_choice);
        mEdtChangeProvince.setAdapter(adapter);

        mEdtChangeProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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


        mEdtChangeDistrict = changeAddressDialog.findViewById(R.id.edtChangeDistrict);
        adapter1.setDropDownViewResource
                (android.R.layout.simple_list_item_single_choice);
        mEdtChangeDistrict.setAdapter(adapter1);

        mEdtChangeStreet = changeAddressDialog.findViewById(R.id.edtChangeStreet);
        mBtnChangeAddress_diff = changeAddressDialog.findViewById(R.id.btnUpdateAddress);
        mBtnChangeAddress_diff.setOnClickListener(this);
        changeAddressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mEdtChangeStreet.setText("");
                mEdtChangeDistrict.setSelection(0);
                mEdtChangeProvince.setSelection(0);
            }
        });

        changeNameDialog = new Dialog(this);
        changeNameDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        changeNameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        changeNameDialog.setContentView(R.layout.item_change_name);
        mEdtChangeName = changeNameDialog.findViewById(R.id.edtChangeUsername);
        mBtnchangeName = changeNameDialog.findViewById(R.id.btnUpdateName);
        mBtnchangeName.setOnClickListener(this);
        changeNameDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mEdtChangeName.setText("");
            }
        });

        changePasswordDialog = new Dialog(this);
        changePasswordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        changePasswordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        changePasswordDialog.setContentView(R.layout.item_change_password);
        mBtnUpdatePassword = changePasswordDialog.findViewById(R.id.btnUpdatePsw);
        mEdtCurrentPassword = changePasswordDialog.findViewById(R.id.edtChangePswCurrentPsw);
        mEdtNewPassword = changePasswordDialog.findViewById(R.id.edtChangePswNewPsw);
        mEdtConfirmPassword = changePasswordDialog.findViewById(R.id.edtChangePswConfirmPsw);
        mBtnUpdatePassword.setOnClickListener(this);
        changePasswordDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mEdtNewPassword.setText("");
                mEdtConfirmPassword.setText("");
                mEdtNewPassword.setText("");
            }
        });

        changePhoneDialog = new Dialog(this);
        changePhoneDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        changePhoneDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        changePhoneDialog.setContentView(R.layout.item_change_phone);
        mEdtChangePhone = changePhoneDialog.findViewById(R.id.edtChangePhone);
        mBtnChangePhoneNum = changePhoneDialog.findViewById(R.id.btnUpdatePhone);
        mBtnChangePhoneNum.setOnClickListener(this);
        changePhoneDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mEdtChangePhone.setText("");
            }
        });

        // add listener
        mBtnBack.setOnClickListener(this);
        mBtnChangeUsername.setOnClickListener(this);
        mBtnChangePhone.setOnClickListener(this);
        mBtnChangeAddress.setOnClickListener(this);
        mBtnChangePassword.setOnClickListener(this);
        mBtnChangePayment.setOnClickListener(this);

        // data
        new DownloadImageFromInternet(mBackground)
                .execute(User.getCurrentUser().getBackground());
        new DownloadImageFromInternet(profileImage)
                .execute(User.getCurrentUser().getImage());
        mTvUsername.setText(User.getCurrentUser().getName());
        mTvAddress.setText(User.getCurrentUser().getProvince());
        mTvPhone.setText(User.getCurrentUser().getPhone());
        FirebaseAuth auth = FirebaseAuth.getInstance();
        mTvEmailInfo.setText(auth.getCurrentUser().getEmail());
    }
    public void setAddress()
    {
        if(mEdtChangeProvince.getSelectedItem().toString().equals("Hà Nội"))
            adapter1=new ArrayAdapter<String>
                    (
                            this,
                            android.R.layout.simple_spinner_item,
                            arr1
                    );
        if(mEdtChangeProvince.getSelectedItem().toString().equals("TT Huế"))
            adapter1=new ArrayAdapter<String>
                    (
                            this,
                            android.R.layout.simple_spinner_item,
                            arr2
                    );
        if(mEdtChangeProvince.getSelectedItem().toString().equals("Đà Nẵng"))
            adapter1=new ArrayAdapter<String>
                    (
                            this,
                            android.R.layout.simple_spinner_item,
                            arr3
                    );
        adapter1.setDropDownViewResource
                (android.R.layout.simple_list_item_single_choice);
        mEdtChangeDistrict.setAdapter(adapter1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.background:
                Intent intentBackground = new Intent();
                intentBackground.setType("image/*");
                intentBackground.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intentBackground, "Select Picture"), PICK_IMAGE_REQUEST1);
                break;
            case R.id.btnBackProfile:
                setResult(Activity.RESULT_CANCELED, new Intent());
                finish();
                break;
            case R.id.btnChangeUsername:
                changeNameDialog.show();
                break;
            case R.id.btnChangeAddress:
                changeAddressDialog.show();
                break;
            case R.id.btnChangePassword:
                Toast.makeText(this, "Không thể thay đổi", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnChangePhone:
                changePhoneDialog.show();
                break;
            case R.id.btnPayment:
                Toast.makeText(this, "Payment", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnUpdateAddress:
                changeAddressDialog.dismiss();
                changeAddress();
                break;
            case R.id.btnUpdateName:
                changeNameDialog.dismiss();
                changeName();
                break;
            case R.id.btnUpdatePhone:
                changePhoneDialog.dismiss();
                changePhone();
                break;
            case R.id.imageViewAvatar:
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
            loadingDialog.show();
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profileImage.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                Toast.makeText(this,"fail",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final StorageReference ref = storageReference.child("userImage/"+ User.getCurrentUser().getId());
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map<String, Object> data = new HashMap<>();
                            data.put("image", uri.toString());
                            User.getCurrentUser().setImage(uri.toString());
                            FirebaseFirestore.getInstance().collection("user")
                                    .document(User.getCurrentUser().getId()).update(data)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            loadingDialog.dismiss();
                                        }
                                    });
                        }
                    });
                }
            });
        }
        else if(requestCode == PICK_IMAGE_REQUEST1 && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            loadingDialog.show();
            filePath1 = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath1);
                mBackground.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                Toast.makeText(this,"fail",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final StorageReference ref = storageReference.child("userImageBackground/"+ User.getCurrentUser().getId());
            ref.putFile(filePath1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map<String, Object> data = new HashMap<>();
                            data.put("background", uri.toString());
                            User.getCurrentUser().setBackground(uri.toString());
                            FirebaseFirestore.getInstance().collection("user")
                                    .document(User.getCurrentUser().getId()).update(data)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            loadingDialog.dismiss();
                                        }
                                    });
                        }
                    });
                }
            });
        }
    }

    public void changePass() {
        String currentPass, newPass, confirmPass;
        currentPass = mEdtCurrentPassword.getText().toString();
        newPass = mEdtNewPassword.getText().toString();
        confirmPass = mEdtConfirmPassword.getText().toString();
    }

    public void changeAddress() {
        province = mEdtChangeProvince.getSelectedItem().toString();
        district = mEdtChangeDistrict.getSelectedItem().toString();
        street = mEdtChangeStreet.getText().toString();
        if(province.length() == 0) {
            Toast.makeText(this, "chưa nhập tình/thành phố", Toast.LENGTH_SHORT).show();
            return;
        }
        if(district.length() == 0) {
            Toast.makeText(this, "chưa nhập quận/huyện", Toast.LENGTH_SHORT).show();
            return;
        }
        if(street.length() == 0) {
            Toast.makeText(this, "chưa nhập địa chỉ", Toast.LENGTH_SHORT).show();
            return;
        }
        loadingDialog.show();

        Map<String, Object> data = new HashMap<>();
        data.put("province", province);
        data.put("district", district);
        data.put("street", street);
        FirebaseFirestore.getInstance().collection("user").document(User.getCurrentUser().getId())
                .update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                User.getCurrentUser().setProvince(province);
                User.getCurrentUser().setDistrict(district);
                User.getCurrentUser().setStreet(street);
                mTvAddress.setText(province);
                loadingDialog.dismiss();
            }
        });
    }

    public void changeName() {
        name = mEdtChangeName.getText().toString();
        if(name.length() == 0) {
            Toast.makeText(this, "chưa nhập tên mới", Toast.LENGTH_SHORT).show();
            return;
        }
        loadingDialog.show();
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        FirebaseFirestore.getInstance().collection("user").document(User.getCurrentUser().getId())
                .update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                User.getCurrentUser().setName(name);
                mTvUsername.setText(name);
                loadingDialog.dismiss();
            }
        });
    }

    public void changePhone(){
        phone = mEdtChangePhone.getText().toString();
        if(phone.length() == 0) {
            Toast.makeText(this, "chưa nhập số điện thoại", Toast.LENGTH_SHORT).show();
            return;
        }
        loadingDialog.show();
        Map<String, Object> data = new HashMap<>();
        data.put("phone", phone);
        FirebaseFirestore.getInstance().collection("user").document(User.getCurrentUser().getId())
                .update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                User.getCurrentUser().setPhone(phone);
                mTvPhone.setText(phone);
                loadingDialog.dismiss();
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
