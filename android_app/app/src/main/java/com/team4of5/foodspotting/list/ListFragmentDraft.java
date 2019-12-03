package com.team4of5.foodspotting.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.team4of5.foodspotting.R;

public class ListFragmentDraft extends Fragment {

    private LinearLayout linearLayoutLogin;
    private RelativeLayout relativeLayoutLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment_draft, container, false);
        linearLayoutLogin = view.findViewById(R.id.draft_login);
        relativeLayoutLogout = view.findViewById(R.id.draft_no_login);

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
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