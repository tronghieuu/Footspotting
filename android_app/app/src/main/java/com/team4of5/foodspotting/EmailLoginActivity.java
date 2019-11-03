package com.team4of5.foodspotting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class EmailLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnBack, mBtnSignIn, mBtnSignUp;
    private EditText mEdtEmail, mEdtPassword;
    private ProgressBar mProgressBar;
    private FirebaseAuth mAuth;
    private static int RQ_SIGN_UP = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
        mBtnBack = findViewById(R.id.btnBack);
        mBtnSignIn = findViewById(R.id.btnSignIn);
        mBtnSignUp = findViewById(R.id.btnSignUp);
        mEdtEmail = findViewById(R.id.edtEmail);
        mEdtPassword = findViewById(R.id.edtPassword);
        mProgressBar = findViewById(R.id.progressBarEmail);
        mBtnBack.setOnClickListener(this);
        mBtnSignIn.setOnClickListener(this);
        mBtnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnSignIn:
                signIn();
                break;
            case R.id.btnSignUp:
                startActivityForResult(new Intent(EmailLoginActivity.this, SignUpActivity.class), RQ_SIGN_UP);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RQ_SIGN_UP){
            if(User.getCurrentUser().getAccountType() != 3){
                setResult(Activity.RESULT_CANCELED, new Intent());
                finish();
            }
        }
    }

    public void signIn(){
        final String email = mEdtEmail.getText().toString();
        final String password = mEdtPassword.getText().toString();

        if(TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        mProgressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(EmailLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            if(password.length() < 6) {
                                Toast.makeText(EmailLoginActivity.this, "Password ngan qua", Toast.LENGTH_SHORT).show();
                            } else {
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("user")
                                        .whereEqualTo("email", email)
                                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        User user = User.getCurrentUser();
                                        if(!queryDocumentSnapshots.isEmpty()){
                                            DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                                            user.setAccountType(2);
                                            user.setId(doc.getId());
                                            user.setName(doc.getString("name"));
                                            user.setImage(doc.getString("image"));
                                            user.setType(Integer.parseInt(doc.getString("type")));
                                            user.setStreet(doc.getString("street"));
                                            user.setDistrict(doc.getString("district"));
                                            user.setProvince(doc.getString("province"));
                                            user.setPhone(doc.getString("phone"));
                                        }
                                        mProgressBar.setVisibility(View.GONE);
                                        setResult(Activity.RESULT_CANCELED, new Intent());
                                        finish();
                                    }
                                });
                            }
                        } else {
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(EmailLoginActivity.this, "Email hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
