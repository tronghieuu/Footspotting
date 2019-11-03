package com.team4of5.foodspotting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnEmailLogin, mbtnGoogleLogin;
    private GoogleSignInClient mGoogleSignInClient;
    private static int GG_SIGN_IN = 0;
    private static int EMAIL_SIGN_IN = 1;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        // google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // email
        mAuth = FirebaseAuth.getInstance();

        // button
        mBtnEmailLogin = findViewById(R.id.btnEmailLogin);
        mBtnEmailLogin.setOnClickListener(this);
        mbtnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        mbtnGoogleLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnEmailLogin:
                Intent intent = new Intent(this, EmailLoginActivity.class);
                startActivityForResult(intent, EMAIL_SIGN_IN);
                break;
            case R.id.btnGoogleLogin:
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, GG_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == EMAIL_SIGN_IN){
            if(resultCode == Activity.RESULT_CANCELED){
                Intent intent = new Intent();
                setResult(Activity.RESULT_CANCELED, intent);
                finish();
            }
        } else if(requestCode == GG_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            final GoogleSignInAccount account = task.getResult(ApiException.class);

            // Signed in  successfully, show authenticated UI.
            final User user = User.getCurrentUser();
            user.reset();

            // basic info
            user.setAccountType(1);
            user.setName(account.getDisplayName());
            try{
                user.setImage(account.getPhotoUrl().toString());
            } catch(Exception e){}

            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("user")
                    .whereEqualTo("gmail", account.getEmail())
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots.size() == 0){
                        Map<String, Object> data = new HashMap<>();
                        data.put("gmail", account.getEmail());
                        data.put("street", "");
                        data.put("district", "");
                        data.put("province", "");
                        data.put("type", "1");
                        data.put("phone", "");
                        db.collection("user")
                                .add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                user.setId(documentReference.getId());
                                setResult(Activity.RESULT_CANCELED, new Intent());
                                finish();
                            }
                        });
                    } else {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        user.setPhone(doc.getString("phone"));
                        user.setStreet(doc.getString("street"));
                        user.setDistrict(doc.getString("district"));
                        user.setProvince(doc.getString("province"));
                        user.setType(Integer.parseInt(doc.getString("type")));
                        user.setId(doc.getId());
                        setResult(Activity.RESULT_CANCELED, new Intent());
                        finish();
                    }
                }
            });

            //setResult(Activity.RESULT_CANCELED, new Intent());
            //finish();
        } catch(ApiException e) {
            Toast.makeText(this, "not ok ", Toast.LENGTH_SHORT).show();
        }

    }
}
