package com.team4of5.foodspotting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PersonFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener{

    private NavigationView mNavigationView;
    private CardView mCvLogin, mCvNoLogin;
    private TextView mTvUserName;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person, container, false);
        mNavigationView = view.findViewById(R.id.navPersonal);
        mCvLogin = view.findViewById(R.id.cardViewLogin);
        mCvNoLogin = view.findViewById(R.id.cardViewNoLogin);
        mTvUserName = view.findViewById(R.id.tvUserName);

        mCvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Hoạt động", Toast.LENGTH_SHORT).show();
            }
        });

        mCvNoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }
        if(CurrentUser.CurrentUser().isLogin()){
            login();
        } else logout();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                String isLogin = data.getStringExtra("isLogin");
                if(isLogin.contentEquals("ok")) {
                    CurrentUser.CurrentUser().saveAccount(new File(getActivity().getFilesDir(), "currentAccount.txt"));
                    reloadFragment();
                    Toast.makeText(getActivity(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void logout(){
        mCvLogin.setVisibility(View.INVISIBLE);
        mCvNoLogin.setVisibility(View.VISIBLE);
    }

    private void login() {
        mCvLogin.setVisibility(View.VISIBLE);
        mCvNoLogin.setVisibility(View.INVISIBLE);
        mTvUserName.setText(CurrentUser.CurrentUser().getCurrentUser().getUsername());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Toast mToast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
        switch (menuItem.getItemId()) {
            case R.id.navHistory:
                mToast.setText("History");
                mToast.show();
                break;
            case R.id.navForShipper:
                mToast.setText("ForShipper");
                mToast.show();
                break;
            case R.id.navForShopOwner:
                mToast.setText("navForShopOwner");
                mToast.show();
                break;
            case R.id.navInvoice:
                mToast.setText("navInvoice");
                mToast.show();
                break;
            case R.id.navAppSetting:
                mToast.setText("navAppSetting");
                mToast.show();
                break;
            case R.id.navPolicy:
                mToast.setText("navPolicy");
                mToast.show();
                break;
            case R.id.navUserSetting:
                mToast.setText("navUserSetting");
                mToast.show();
                break;
            case R.id.navLogOut:
                Logout();
                reloadFragment();
                break;
        }
        return true;
    }

    private void Logout(){
        try{
            File file = new File(getActivity().getFilesDir(), "currentAccount.txt");
            FileOutputStream writer = new FileOutputStream(file);
            CurrentUser.CurrentUser().setLogin(false);
        } catch(IOException e){}

    }

    private void reloadFragment(){
        Fragment frg = null;
        frg = getFragmentManager().findFragmentByTag("Person");
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
    }
}
