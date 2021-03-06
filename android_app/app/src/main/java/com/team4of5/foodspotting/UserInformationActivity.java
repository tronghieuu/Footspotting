package com.team4of5.foodspotting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.team4of5.foodspotting.object.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInformationActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnBack, mBtnChangeUsername, mBtnChangePhone, mBtnChangeAddress, mBtnUpdatePassword
            , mBtnChangePassword, mBtnChangePayment, mBtnchangeName, mBtnChangePhoneNum, mBtnChangeAddress_diff, mBtnVerify;
    private TextView mTvUsername, mTvPhone, mTvAddress, mTvEmailInfo;
    private Dialog changePasswordDialog, changeNameDialog, changePhoneDialog, changeAddressDialog, loadingDialog, verifyDialog;
    private EditText mEdtCurrentPassword, mEdtNewPassword, mEdtConfirmPassword, mEdtChangeStreet, mEdtChangeName, mEdtChangePhone, mEdtVerify;
    private String province, district, street, name, phone;
    private ImageView profileImage;
    private static int PICK_IMAGE_REQUEST = 23;
    private Uri filePath;
    private Spinner  mEdtChangeProvince,

            mEdtChangeDistrict;
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

    ArrayAdapter<String> adapter1 = null;
    private static int PICK_IMAGE_REQUEST1 = 11;
    private Uri filePath1;
    private ImageView mBackground;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;

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
                setAddress();
                adapter1.notifyDataSetChanged();
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
        changeAddressDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                mEdtChangeStreet.setText(User.getCurrentUser().getStreet());
                mEdtChangeProvince.setSelection(getIndex(mEdtChangeProvince,User.getCurrentUser().getProvince()));
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

        verifyDialog = new Dialog(this);
        verifyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        verifyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        verifyDialog.setContentView(R.layout.item_verify);
        mEdtVerify = verifyDialog.findViewById(R.id.edtVerify);
        mBtnVerify = verifyDialog.findViewById(R.id.btnVerify);
        mBtnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = mEdtVerify.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    mEdtChangeName.setError("Enter valid code");
                    mEdtVerify.requestFocus();
                    return;
                }

                //verifying the code entered manually
                verifyVerificationCode(code);
            }
        });
        changePhoneDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mEdtVerify.setText("");
            }
        });

        // add listener
        mBtnBack.setOnClickListener(this);
        mBtnChangeUsername.setOnClickListener(this);
        mBtnChangePhone.setOnClickListener(this);
        mBtnChangeAddress.setOnClickListener(this);
        mBtnChangePassword.setOnClickListener(this);
        mBtnChangePayment.setOnClickListener(this);
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                //Getting the code sent by SMS
                String code = phoneAuthCredential.getSmsCode();

                //sometime the code is not detected automatically
                //in this case the code will be null
                //so user has to manually enter the code
                if (code != null) {
                    mEdtVerify.setText(code);
                    //verifying the code
                    verifyVerificationCode(code);
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                verifyDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Gửi mã xác thực thất bại!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                //storing the verification id that is sent to the user
                mVerificationId = s;
            }
        };

        // data
        if (User.getCurrentUser().getBackground()!=null)
        new DownloadImageFromInternet(mBackground)
                .execute(User.getCurrentUser().getBackground());
        if (User.getCurrentUser().getImage()!=null)
        new DownloadImageFromInternet(profileImage)
                .execute(User.getCurrentUser().getImage());
        mTvUsername.setText(User.getCurrentUser().getName());
        mTvAddress.setText(User.getCurrentUser().getProvince());
        mTvPhone.setText(User.getCurrentUser().getPhone());
        mTvEmailInfo.setText(User.getCurrentUser().getEmail());
    }
    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return 0;
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
        if(mEdtChangeProvince.getSelectedItem().toString().equals("Đà Nẵng"))
            adapter1=new ArrayAdapter<String>
                    (
                            this,
                            android.R.layout.simple_spinner_item,
                            arr2
                    );
        if(mEdtChangeProvince.getSelectedItem().toString().equals("Hồ Chí Minh"))
            adapter1=new ArrayAdapter<String>
                    (
                            this,
                            android.R.layout.simple_spinner_item,
                            arr3
                    );
        adapter1.setDropDownViewResource
                (android.R.layout.simple_list_item_single_choice);
        mEdtChangeDistrict.setAdapter(adapter1);
        mEdtChangeDistrict.setSelection(getIndex(mEdtChangeDistrict,User.getCurrentUser().getDistrict()));
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
        mEdtChangePhone.setText("");
        changePhoneDialog.dismiss();
        FirebaseFirestore.getInstance().collection("user")
                .whereEqualTo("phone", phone)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()) {
                    verifyDialog.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            "+84"+phone,
                            60,
                            TimeUnit.SECONDS,
                            TaskExecutors.MAIN_THREAD,
                            callbacks
                    );
                } else {
                    Toast.makeText(getApplicationContext(), "Số điện thoại này đã có người sử dụng!", Toast.LENGTH_SHORT).show();
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

    private void verifyVerificationCode(String code) {
        verifyDialog.dismiss();
        loadingDialog.show();
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(UserInformationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            FirebaseFirestore.getInstance().collection("user")
                                    .document(User.getCurrentUser().getId())
                                    .update("phone", phone).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mTvPhone.setText(phone);
                                    User.getCurrentUser().setPhone(phone);
                                    loadingDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {

                            //verification unsuccessful.. display an error message
                            loadingDialog.dismiss();
                            String message = "Somthing is wrong, we will fix it soon...";

                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
