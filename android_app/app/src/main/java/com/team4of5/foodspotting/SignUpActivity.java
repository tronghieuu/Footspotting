package com.team4of5.foodspotting;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnSignIn, mBtnSignUp, mBtnBackSignUp;
    private EditText mEdtUsername, mEdtEmail, mEdtPassword, mEdtConfirmPassword;
    private ImageButton mImageBtnTrue1, mImageBtnTrue2, mImageBtnTrue3, mImageBtnTrue4,
            mImageBtnFalse1, mImageBtnFalse2, mImageBtnFalse3, mImageBtnFalse4;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();
        mBtnBackSignUp = findViewById(R.id.btnBackSignUp);
        mBtnSignIn = findViewById(R.id.btnSignInn);
        mBtnSignUp = findViewById(R.id.btnSignUpp);
        mEdtEmail = findViewById(R.id.edtEmail);
        mEdtUsername = findViewById(R.id.edtUserName);
        mEdtPassword = findViewById(R.id.edtPassword);
        mEdtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        mImageBtnTrue1 = findViewById(R.id.imageBtnTrue1);
        mImageBtnTrue2 = findViewById(R.id.imageBtnTrue2);
        mImageBtnTrue3 = findViewById(R.id.imageBtnTrue3);
        mImageBtnTrue4 = findViewById(R.id.imageBtnTrue4);
        mImageBtnFalse1 = findViewById(R.id.imageBtnFalse1);
        mImageBtnFalse2 = findViewById(R.id.imageBtnFalse2);
        mImageBtnFalse3 = findViewById(R.id.imageBtnFalse3);
        mImageBtnFalse4 = findViewById(R.id.imageBtnFalse4);
        mImageBtnTrue1.setVisibility(View.INVISIBLE);
        mImageBtnTrue2.setVisibility(View.INVISIBLE);
        mImageBtnTrue3.setVisibility(View.INVISIBLE);
        mImageBtnTrue4.setVisibility(View.INVISIBLE);
        mImageBtnFalse1.setVisibility(View.INVISIBLE);
        mImageBtnFalse2.setVisibility(View.INVISIBLE);
        mImageBtnFalse3.setVisibility(View.INVISIBLE);
        mImageBtnFalse4.setVisibility(View.INVISIBLE);

        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_loading);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });

        mBtnSignUp.setOnClickListener(this);
        mBtnSignIn.setOnClickListener(this);
        mBtnBackSignUp.setOnClickListener(this);

        mEdtUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String username = mEdtUsername.getText().toString();
                if(username.length() == 0){
                    mImageBtnFalse1.setVisibility(View.INVISIBLE);
                    mImageBtnTrue1.setVisibility(View.INVISIBLE);
                } else{
                    mImageBtnFalse1.setVisibility(View.INVISIBLE);
                    mImageBtnTrue1.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mEdtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                final String email = mEdtEmail.getText().toString();
                if(email.length() == 0){
                    mImageBtnFalse2.setVisibility(View.INVISIBLE);
                    mImageBtnTrue2.setVisibility(View.INVISIBLE);
                } else if(!emailSyntaxCheck(email)){
                    mImageBtnFalse2.setVisibility(View.VISIBLE);
                    mImageBtnTrue2.setVisibility(View.INVISIBLE);
                } else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("user")
                            .whereEqualTo("email", email)
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots.isEmpty()){
                                mImageBtnFalse2.setVisibility(View.INVISIBLE);
                                mImageBtnTrue2.setVisibility(View.VISIBLE);
                            } else {
                                mImageBtnFalse2.setVisibility(View.VISIBLE);
                                mImageBtnTrue2.setVisibility(View.INVISIBLE);
                            }
                            if(email.length() == 0){
                                mImageBtnFalse2.setVisibility(View.INVISIBLE);
                                mImageBtnTrue2.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mEdtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(mEdtPassword.getText().toString().length()<6){
                    mImageBtnFalse3.setVisibility(View.VISIBLE);
                    mImageBtnTrue3.setVisibility(View.INVISIBLE);
                } else {
                    mImageBtnFalse3.setVisibility(View.INVISIBLE);
                    mImageBtnTrue3.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mEdtConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(mEdtPassword.getText().toString()
                        .contentEquals(mEdtConfirmPassword.getText().toString())){
                    mImageBtnFalse4.setVisibility(View.INVISIBLE);
                    mImageBtnTrue4.setVisibility(View.VISIBLE);
                } else {
                    mImageBtnFalse4.setVisibility(View.VISIBLE);
                    mImageBtnTrue4.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnBackSignUp:
            case R.id.btnSignInn:
                setResult(Activity.RESULT_CANCELED, new Intent());
                finish();
                break;
            case R.id.btnSignUpp:
                SignUp();
        }
    }

    public void SignUp(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String email, username, password, confirmPassword;
        email = mEdtEmail.getText().toString();
        username = mEdtUsername.getText().toString();
        password = mEdtPassword.getText().toString();
        confirmPassword = mEdtConfirmPassword.getText().toString();
        if(username.contentEquals("")){
            Toast.makeText(this, "Username không được để trống!", Toast.LENGTH_SHORT).show();
            mImageBtnFalse1.setVisibility(View.VISIBLE);
            return;
        }
        if(email.contentEquals("")){
            Toast.makeText(this, "Email không được để trống!", Toast.LENGTH_SHORT).show();
            mImageBtnFalse2.setVisibility(View.VISIBLE);
            return;
        }
        if(mImageBtnFalse2.isShown()){
            Toast.makeText(this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.length() < 6){
            Toast.makeText(this, "Độ dài của password không được bé hơn 6!", Toast.LENGTH_SHORT).show();
            mImageBtnFalse3.setVisibility(View.VISIBLE);
            return;
        }
        if(!password.contentEquals(confirmPassword)){
            Toast.makeText(this, "Password và confirm password phải trùng nhau!", Toast.LENGTH_SHORT).show();
            mImageBtnFalse4.setVisibility(View.VISIBLE);
            return;
        }
        dialog.show();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        Map<String, Object> data = new HashMap<>();
                        data.put("email", email);
                        data.put("name", username);
                        data.put("type", "1");
                        data.put("street", "");
                        data.put("district", "");
                        data.put("province", "");
                        data.put("phone","");
                        data.put("image", "");
                        db.collection("user")
                                .add(data)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        User.getCurrentUser().setId(documentReference.getId());
                                        User.getCurrentUser().setAccountType(2);
                                        User.getCurrentUser().setType(1);
                                        User.getCurrentUser().setName(username);
                                        dialog.dismiss();
                                        setResult(Activity.RESULT_CANCELED, new Intent());
                                        finish();
                                    }
                                });
                    }
                });
    }

    public boolean emailSyntaxCheck(String email){
        boolean checka = false;
        boolean checkDot = false;
        if(email.charAt(0) == '@' || email.charAt(0) == '.' ||
                email.charAt(email.length()-1) == '.' || email.charAt(email.length()-1) == '@')
            return false;
        for(int i = 1; i < email.length(); i++){
            if(!checka) {
                if(email.charAt(i) == '@'){
                    checka = true;
                    if(email.charAt(i+1) == '.') return false;
                } else if(checkDot) return false;
            } else if(email.charAt(i) == '@') return false;
            if(email.charAt(i) == '.'){
                checkDot = true;
                if(email.charAt(i+1) == '.') return false;
            }
        } if(checka && checkDot) return true;
        else return false;
    }
}
