package com.team4of5.foodspotting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class EmailLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private GoogleSignInClient mGoogleSignInClient;
    private static int GG_SIGN_IN = 0;
    private Button mBtnBack, mBtnSignIn, mBtnSignUp, mbtnGoogleLogin;
    private EditText mEdtEmail, mEdtPassword;
    private FirebaseAuth mAuth;
    private static int RQ_SIGN_UP = 111;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
        mBtnBack = findViewById(R.id.btnBack);
        mBtnSignIn = findViewById(R.id.btnSignIn);

        // google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mbtnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        mbtnGoogleLogin.setOnClickListener(this);

        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_loading);

        mBtnSignUp = findViewById(R.id.btnSignUp);
        mEdtEmail = findViewById(R.id.edtEmail);
        mEdtPassword = findViewById(R.id.edtPassword);
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
                break;
            case R.id.btnGoogleLogin:
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, GG_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RQ_SIGN_UP){
            if(FirebaseAuth.getInstance().getCurrentUser() != null){
                setResult(Activity.RESULT_CANCELED, new Intent());
                finish();
            }
        } else if(requestCode == GG_SIGN_IN){
            dialog.show();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
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

        dialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(EmailLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            if(password.length() < 6) {
                                Toast.makeText(EmailLoginActivity.this, "Password ngan qua", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
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
                                            user.setId(doc.getId());
                                            user.setName(doc.getString("name"));
                                            user.setImage(doc.getString("image"));
                                            user.setType(Integer.parseInt(doc.getString("type")));
                                            user.setStreet(doc.getString("street"));
                                            user.setDistrict(doc.getString("district"));
                                            user.setProvince(doc.getString("province"));
                                            user.setPhone(doc.getString("phone"));
                                        }
                                        dialog.dismiss();
                                        setResult(Activity.RESULT_CANCELED, new Intent());
                                        finish();
                                    }
                                });
                            }
                        } else {
                            dialog.dismiss();
                            Toast.makeText(EmailLoginActivity.this, "Email hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            final GoogleSignInAccount account = task.getResult(ApiException.class);

            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    final User user = User.getCurrentUser();
                    user.reset();

                    // basic info
                    user.setName(account.getDisplayName());
                    try{
                        user.setImage(account.getPhotoUrl().toString());
                    } catch(Exception e){
                        user.setImage("");
                    }

                    final FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("user")
                            .whereEqualTo("email", account.getEmail())
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots.size() == 0){
                                Map<String, Object> data = new HashMap<>();
                                data.put("email", account.getEmail());
                                data.put("name", account.getDisplayName());
                                data.put("street", "");
                                data.put("district", "");
                                data.put("province", "");
                                data.put("type", "1");
                                data.put("phone", "");
                                if(account.getPhotoUrl() != null){
                                    data.put("image", account.getPhotoUrl().toString());
                                } else data.put("image", "");
                                db.collection("user")
                                        .add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        user.setId(documentReference.getId());
                                        dialog.dismiss();
                                        setResult(Activity.RESULT_CANCELED, new Intent());
                                        finish();
                                    }
                                });
                            } else {
                                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                                user.setName(doc.getString("name"));
                                user.setPhone(doc.getString("phone"));
                                user.setStreet(doc.getString("street"));
                                user.setDistrict(doc.getString("district"));
                                user.setProvince(doc.getString("province"));
                                user.setType(Integer.parseInt(doc.getString("type")));
                                user.setId(doc.getId());
                                user.setName(account.getDisplayName());
                                dialog.dismiss();
                                setResult(Activity.RESULT_CANCELED, new Intent());
                                finish();
                            }

                        }
                    });
                }
            });

            //setResult(Activity.RESULT_CANCELED, new Intent());
            //finish();
        } catch(ApiException e) {
            Toast.makeText(this, "not ok", Toast.LENGTH_SHORT).show();
        }

    }
}
