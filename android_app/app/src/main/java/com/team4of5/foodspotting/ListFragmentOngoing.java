package com.team4of5.foodspotting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ListFragmentOngoing extends Fragment {
    private LinearLayout linearLayoutLogin;
    private RelativeLayout relativeLayoutLogout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment_ongoing, container, false);
        linearLayoutLogin = view.findViewById(R.id.ongoing_login);
        relativeLayoutLogout = view.findViewById(R.id.ongoing_no_login);

        if(User.getCurrentUser().getAccountType() == 1 || User.getCurrentUser().getAccountType() == 2){
            login();
        } else logout();


        return view;
    }
    public void logout(){
        linearLayoutLogin.setVisibility(View.INVISIBLE);
        relativeLayoutLogout.setVisibility(View.VISIBLE);
    }

    public void login(){
        linearLayoutLogin.setVisibility(View.VISIBLE);
        relativeLayoutLogout.setVisibility(View.INVISIBLE);
    }
}
