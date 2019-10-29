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

public class ListFragmentHistory extends Fragment {

    private LinearLayout linearLayoutLogin;
    private RelativeLayout relativeLayoutLogout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.list_fragment_history, container, false);
        


        return view;
    }

    public void login(){

    }

    public void logout(){

    }
}
