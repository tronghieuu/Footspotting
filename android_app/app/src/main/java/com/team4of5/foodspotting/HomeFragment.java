package com.team4of5.foodspotting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.team4of5.foodspotting.home.SlidingImage_Adapter;
import com.team4of5.foodspotting.object.ImageModel;
import com.team4of5.foodspotting.object.Restaurant;
import com.team4of5.foodspotting.object.User;
import com.team4of5.foodspotting.restaurant.NearRestaurantReccyclerViewAdapter;
import com.team4of5.foodspotting.restaurant.Restaurent;
import com.team4of5.foodspotting.search.SearchRecycleAdapter;
import com.team4of5.foodspotting.search.food;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {
    @Nullable
    private static ViewPager mPager;
    private static CirclePageIndicator indicator;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private ArrayList<ImageModel> imageModelArrayList;
    private RecyclerView mRecylerView,mRecylerView2;
    private NearRestaurantReccyclerViewAdapter mAdapter,mAdapter2;
    private List<Restaurant> mRestaurents,mRestaurents2;
    private List<food> foods;
    private SearchView mEdtSearch;
    private SearchRecycleAdapter searchAdapter;
    private FirebaseFirestore db;
    private Spinner sp_city;

    private int[] myImageList = new int[]{R.drawable.slide1, R.drawable.slide2,
            R.drawable.slide3};
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);

        mEdtSearch = view.findViewById(R.id.searchHome);
        imageModelArrayList = new ArrayList<>();
        imageModelArrayList = populateList();
        mPager = view.findViewById(R.id.pagerAd);
        mPager.setAdapter(new SlidingImage_Adapter(getContext(),imageModelArrayList));
        indicator = view.findViewById(R.id.indicator);
        init();

        //RECYLERVIEW
        db = FirebaseFirestore.getInstance();
        mRestaurents = new ArrayList<>();
        mRecylerView = view.findViewById(R.id.recycleViewHome);
        mRecylerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new NearRestaurantReccyclerViewAdapter(getActivity() , mRestaurents, mRecylerView, "All");
        mRecylerView.setAdapter(mAdapter);

        // set RecyclerView on item click listner
        mAdapter.setOnItemListener(new NearRestaurantReccyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Restaurant item) {
                Intent intent = new Intent(getActivity(), Restaurent.class);
                intent.putExtra("id_restaurent", item.getId());
                startActivity(intent);
            }
        });

        //Search
        foods=new ArrayList<>();
        mRestaurents2 = new ArrayList<>();
        sp_city=view.findViewById(R.id.sp_city);
        mEdtSearch=view.findViewById(R.id.searchHome);
        mRecylerView2=view.findViewById(R.id.searchRecycle);
        mRecylerView2.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
       // getAllFoodData();
        mAdapter2=new NearRestaurantReccyclerViewAdapter(getActivity(),mRestaurents2,mRecylerView2,"Search");
       // searchAdapter=new SearchRecycleAdapter(getActivity(),foods,mRecylerView2);
        mRecylerView2.setAdapter(mAdapter2);

        mEdtSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(mEdtSearch.getQuery()==null || mEdtSearch.getQuery().length()<1) mRecylerView2.setVisibility(View.GONE);
                else {
                    search();
                    mRecylerView2.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
        mAdapter2.setOnItemListener(new NearRestaurantReccyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Restaurant item) {
                Intent intent = new Intent(getActivity(), Restaurent.class);
                intent.putExtra("id_restaurent", item.getId());
                startActivity(intent);
            }
        });
        sp_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                queryRestaurantFromProvince();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if (User.getCurrentUser().getProvince()!=null) {
            sp_city.setSelection(getIndex(sp_city, User.getCurrentUser().getProvince()));
        }
        return view;
    }

    private ArrayList<ImageModel> populateList(){
        ArrayList<ImageModel> list = new ArrayList<>();
        for(int i = 0; i < 3; i++){
            ImageModel imageModel = new ImageModel();
            imageModel.setImage_drawable(myImageList[i]);
            list.add(imageModel);
        }
        return list;
    }
    private void init() {
        indicator.setViewPager(mPager);
        final float density = getResources().getDisplayMetrics().density;

        //Set circle indicator radius
        indicator.setRadius(5 * density);
        NUM_PAGES =imageModelArrayList.size();

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }
            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {
            }
            @Override
            public void onPageScrollStateChanged(int pos) {
            }
        });
    }

    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return 0;
    }


    public void queryRestaurantFromProvince(){
            mRestaurents.clear();
            mAdapter.notifyDataSetChanged();
            db.collection("restaurants")
                    .whereEqualTo("province",sp_city.getSelectedItem().toString()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(!queryDocumentSnapshots.isEmpty()){
                        for(DocumentSnapshot doc : queryDocumentSnapshots){
                            Restaurant res = new Restaurant();
                            res.setId(doc.getId());
                            res.setDistrict(doc.getString("district"));
                            res.setImage(doc.getString("image"));
                            res.setName(doc.getString("name"));
                            res.setOpening_time(doc.getString("opening_time"));
                            res.setPhone(doc.getString("phone"));
                            res.setProvince(doc.getString("province"));
                            res.setRate(Float.parseFloat(doc.getString("rate")));
                            res.setStreet(doc.getString("street"));
                            res.setType(doc.getString("type"));
                            res.setUser_id(doc.getString("user_id"));
                            mRestaurents.add(res);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
    }
    public void search()
    {
        mRestaurents2.clear();
        for (final Restaurant r:
             mRestaurents) {
            if(r.getName().contains(mEdtSearch.getQuery().toString())&& r.getProvince().equals(sp_city.getSelectedItem().toString())){
                mRestaurents2.add(r);
            }

        }
        mAdapter2.notifyDataSetChanged();
    }
public void getAllFoodData()
{
    for ( Restaurant res:mRestaurents) {
        final Restaurant r= res;
        db.collection("restaurants").document(r.getId()).collection("food")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        food res = new food();
                        res.setId(doc.getId());
                        res.setImage(doc.getString("image"));
                        res.setInfo(doc.getString("info"));
                        res.setName(doc.getString("name"));
                        res.setPrice(doc.getString("price"));
                        res.setRes(r.getName());
                        res.setResID(r.getId());

                        foods.add(res);
                        searchAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
}
}