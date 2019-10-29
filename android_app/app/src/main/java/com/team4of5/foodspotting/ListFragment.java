package com.team4of5.foodspotting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ListFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list,container,false);
        BottomNavigationView bottomNav = view.findViewById(R.id.lis_top_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListner);
        getFragmentManager().beginTransaction().replace(R.id.list_fragment_container, new ListFragmentOngoing()).commit();
        return view;
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListner =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    FragmentManager t = getFragmentManager();
                    String fragTag="";
                    Fragment selectedFragment = null;
                    if(t.findFragmentByTag("Ongoing") != null){
                        //if the other fragment is visible, hide it.
                        t.beginTransaction().hide(t.findFragmentByTag("Ongoing")).commit();
                    }
                    if(t.findFragmentByTag("History") != null){
                        //if the other fragment is visible, hide it.
                        t.beginTransaction().hide(t.findFragmentByTag("History")).commit();
                    }
                    if(t.findFragmentByTag("Draft") != null){
                        //if the other fragment is visible, hide it.
                        t.beginTransaction().hide(t.findFragmentByTag("Draft")).commit();
                    }

                    switch (menuItem.getItemId()){
                        case R.id.nav_ongoing:
                            selectedFragment = new ListFragmentOngoing();
                            fragTag="Ongoing";
                            break;
                        case R.id.nav_history:
                            selectedFragment = new ListFragmentHistory();
                            fragTag="History";
                            break;
                        case R.id.nav_draft:
                            selectedFragment = new ListFragmentDraft();
                            fragTag="Draft";
                            break;
                    }

                    if(getFragmentManager().findFragmentByTag(fragTag)!=null)
                        t.beginTransaction().show(getFragmentManager().findFragmentByTag(fragTag)).commit();
                    else
                        t.beginTransaction().add(R.id.list_fragment_container, selectedFragment,fragTag).addToBackStack(null).commit();
                    return true;
                }
            };

}
