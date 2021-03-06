package com.team4of5.foodspotting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.team4of5.foodspotting.list.ListFragment;
import com.team4of5.foodspotting.notification.NotificationFragment;
import com.team4of5.foodspotting.object.User;
import com.team4of5.foodspotting.personal.PersonFragment;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        getSupportActionBar().hide();
        bottomNav.setSelectedItemId(R.id.nav_home);
        //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    FragmentManager t = getSupportFragmentManager();
                    String fragTag="";
                    Fragment selectedFragment = null;
                    if(t.findFragmentByTag("Home") != null){
                        //if the other fragment is visible, hide it.
                        t.beginTransaction().hide(t.findFragmentByTag("Home")).commit();
                    }
                    if(t.findFragmentByTag("List") != null){
                        //if the other fragment is visible, hide it.
                        t.beginTransaction().hide(t.findFragmentByTag("List")).commit();
                    }
                    if(t.findFragmentByTag("Noti") != null){
                        //if the other fragment is visible, hide it.
                        t.beginTransaction().hide(t.findFragmentByTag("Noti")).commit();
                    }
                    if(t.findFragmentByTag("Person") != null){
                        //if the other fragment is visible, hide it.
                        t.beginTransaction().hide(t.findFragmentByTag("Person")).commit();
                    }

                    switch (menuItem.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            fragTag="Home";
                            break;
                        case R.id.nav_list:
                            selectedFragment = new ListFragment();
                            fragTag="List";
                            break;
                        case R.id.nav_notifications:
                            selectedFragment = new NotificationFragment();
                            fragTag="Noti";
                            break;
                        case R.id.nav_personal:
                            selectedFragment = new PersonFragment();
                            fragTag="Person";
                            break;
                    }

                    if(getSupportFragmentManager().findFragmentByTag(fragTag)!=null) {
                        t.beginTransaction().show(getSupportFragmentManager().findFragmentByTag(fragTag)).commit();
                        if(fragTag.contentEquals("Home") && User.getCurrentUser().getOwnerUpdate()){
                            User.getCurrentUser().setOwnerUpdate(false);
                            Fragment frg = getSupportFragmentManager().findFragmentByTag(fragTag);
                            FragmentTransaction ft = t.beginTransaction();
                            ft.detach(frg);
                            ft.attach(frg);
                            ft.commit();
                        } else if(fragTag.contentEquals("List") && User.getCurrentUser().isListUpdate()){
                            User.getCurrentUser().setListUpdate(false);
                            Fragment frg = getSupportFragmentManager().findFragmentByTag(fragTag);
                            FragmentTransaction ft = t.beginTransaction();
                            ft.detach(frg);
                            ft.attach(frg);
                            ft.commit();
                        } else if(fragTag.contentEquals("Noti") && User.getCurrentUser().isNotiUpdate()){
                            User.getCurrentUser().setNotiUpdate(false);
                            Fragment frg = getSupportFragmentManager().findFragmentByTag(fragTag);
                            FragmentTransaction ft = t.beginTransaction();
                            ft.detach(frg);
                            ft.attach(frg);
                            ft.commit();
                        }
                    }
                    else
                       t.beginTransaction().add(R.id.fragment_container, selectedFragment,fragTag).addToBackStack(null).commit();
                    return true;
                }
            };
}