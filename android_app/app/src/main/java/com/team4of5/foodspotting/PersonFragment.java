package com.team4of5.foodspotting;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;

public class PersonFragment extends Fragment implements View.OnClickListener {

    private Users mCurrentUser;
    private TextView tvOwner, tvShipper, tvUserName;
    private LinearLayout llLogin;
    private Button btnLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_person,container,false);
        tvOwner = view.findViewById(R.id.tvOwner);
        tvShipper = view.findViewById(R.id.tvShipper);
        llLogin = view.findViewById(R.id.llLogin);
        tvUserName = view.findViewById(R.id.tvUserName);
        btnLogin = view.findViewById(R.id.btnLogin);
        tvOwner.setVisibility(View.INVISIBLE);
        tvShipper.setVisibility(View.INVISIBLE);
        llLogin.setVisibility(View.INVISIBLE);
        tvUserName.setVisibility(View.INVISIBLE);
        mCurrentUser = CurrentUser.CurrentUser().getCurrentUser();
        if(mCurrentUser != null){
            tvUserName.setText(mCurrentUser.getUsername());
            tvUserName.setVisibility(View.VISIBLE);
            llLogin.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.INVISIBLE);
            if(mCurrentUser.getType() == 2){
                tvOwner.setVisibility(View.VISIBLE);
            }
            else if(mCurrentUser.getType() == 3){
                tvShipper.setVisibility(View.VISIBLE);
            }
        }
        else {
            tvOwner.setVisibility(View.INVISIBLE);
            tvShipper.setVisibility(View.INVISIBLE);
            llLogin.setVisibility(View.INVISIBLE);
            tvUserName.setVisibility(View.INVISIBLE);
        }

        btnLogin.setOnClickListener(this);
        return view;
    }
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivityForResult(intent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode == RESULT_OK){
            if(requestCode == 1){
                Bundle bundle = intent.getBundleExtra("user_package");
                Users user = (Users) bundle.getSerializable("user");
                tvUserName.setText(user.getUsername());
                tvUserName.setVisibility(View.VISIBLE);
                llLogin.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.INVISIBLE);
                if(user.getType() == 2){
                    tvOwner.setVisibility(View.VISIBLE);
                }
                else if(user.getType() == 3){
                    tvShipper.setVisibility(View.VISIBLE);
                }
            }
        }
    }


}
