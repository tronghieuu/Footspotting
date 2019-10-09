package com.team4of5.foodspotting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private Button btnSignIn;
    private DatabaseHelper db;
    private EditText edtEmail, edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnSignIn = findViewById(R.id.btnSignIn);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        db = new DatabaseHelper();
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(db.loginSuccess(edtEmail.getText().toString(), edtPassword.getText().toString())){
                    //String user = db.getUser(edtEmail.getText().toString());
                    Users user = db.getUser(edtEmail.getText().toString());
                    if(user != null){
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("user", user);
                        Intent intent = new Intent();
                        intent.putExtra("user_package", bundle);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    //Toast.makeText(getApplicationContext(), user, Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"fffffffff",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
