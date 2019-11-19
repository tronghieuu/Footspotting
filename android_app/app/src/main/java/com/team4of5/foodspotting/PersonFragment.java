package com.team4of5.foodspotting;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.InputStream;

public class PersonFragment extends Fragment implements View.OnClickListener {

    private CardView mCvLogin, mCvNoLogin;
    private TextView mTvUserName;
    private FirebaseAuth mAuth;
    private static int RQ_LOGIN = 10;
    private static int RQ_INFO = 324;
    private FirebaseFirestore mDb;
    private ImageView profileImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person, container, false);

        // firestore
        mDb = FirebaseFirestore.getInstance();

        // email
        mAuth = FirebaseAuth.getInstance();

        mCvLogin = view.findViewById(R.id.cardViewLogin);
        mCvNoLogin = view.findViewById(R.id.cardViewNoLogin);
        mTvUserName = view.findViewById(R.id.tvUserName);
        profileImage = view.findViewById(R.id.profile_image);
        mCvLogin.setOnClickListener(this);
        mCvNoLogin.setOnClickListener(this);

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            loginState();
        } else logoutState();

        // button
        Button btnPayHistory = view.findViewById(R.id.btnPayHistory);
        btnPayHistory.setOnClickListener(this);
        Button btnShipperApp = view.findViewById(R.id.btnShipperApp);
        btnShipperApp.setOnClickListener(this);
        Button btnOwnerApp = view.findViewById(R.id.btnOwnerApp);
        btnOwnerApp.setOnClickListener(this);
        Button btnAdvice = view.findViewById(R.id.btnAdvice);
        btnAdvice.setOnClickListener(this);
        Button btnAppSetting = view.findViewById(R.id.btnAppSetting);
        btnAppSetting.setOnClickListener(this);
        Button btnUserPolicy = view.findViewById(R.id.btnUserPolicy);
        btnUserPolicy.setOnClickListener(this);
        Button btnPersonalSetting = view.findViewById(R.id.btnPersonalSetting);
        btnPersonalSetting.setOnClickListener(this);
        Button btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cardViewLogin:
                startActivityForResult(new Intent(getActivity(), UserInformationActivity.class), RQ_INFO);
                break;
            case R.id.cardViewNoLogin:
                Intent intent = new Intent(getActivity(), EmailLoginActivity.class);
                startActivityForResult(intent, RQ_LOGIN);
                break;
            case R.id.btnPayHistory:
                Toast.makeText(getActivity(), "Lịch sử mua hàng", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnShipperApp:
                Toast.makeText(getActivity(), "Ứng dụng cho Shipper", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnOwnerApp:
                ownerApp();
                break;
            case R.id.btnUserPolicy:
                Toast.makeText(getActivity(), "Chính sách người dùng", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnAdvice:
                Toast.makeText(getActivity(), "Góp ý", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnAppSetting:
                Toast.makeText(getActivity(), "Cài đặt ứng dụng", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnPersonalSetting:
                Toast.makeText(getActivity(), "Cài đặt cá nhân", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnLogout:
                logout();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RQ_LOGIN){
            if(resultCode == Activity.RESULT_CANCELED){
                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    Toast.makeText(getActivity(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    reloadFragment();
                }
            }
        } else if(requestCode == RQ_INFO){
            if(resultCode == Activity.RESULT_CANCELED){
                reloadFragment();
            }
        }
    }

    private void logoutState(){
        mCvLogin.setVisibility(View.INVISIBLE);
        mCvNoLogin.setVisibility(View.VISIBLE);
    }

    private void loginState() {
        mCvLogin.setVisibility(View.VISIBLE);
        mCvNoLogin.setVisibility(View.INVISIBLE);
        mTvUserName.setText(User.getCurrentUser().getName());
        if(User.getCurrentUser().getImage() != null) {
            new DownloadImageFromInternet(profileImage)
                    .execute(User.getCurrentUser().getImage());
        }
    }


    private void logout(){
        mAuth.signOut();
        reloadFragment();
    }

    private void reloadFragment(){
        Fragment frg = null;
        frg = getFragmentManager().findFragmentByTag("Person");
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
    }

    public void ownerApp(){
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            Toast.makeText(getActivity(), "Hãy đăng nhập trước!", Toast.LENGTH_SHORT).show();
        }
        else if(User.getCurrentUser().getType() == 2){
            Toast.makeText(getActivity(), "Shipper không thể sử dụng chức năng này", Toast.LENGTH_SHORT).show();
        }
        else if(User.getCurrentUser().getType() == 1) {
            startActivity(new Intent(getActivity(), RegisterOwnerActivity.class));
        }
        else if(User.getCurrentUser().getType() == 3) {
            startActivity(new Intent(getActivity(), OwnerAppActivity.class));
        }
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
