package com.team4of5.foodspotting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

public class PersonFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener{

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person, container, false);
        return view;
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
                mToast.setText("navLogOut");
                mToast.show();
                break;
        }
        return true;
    }
}
