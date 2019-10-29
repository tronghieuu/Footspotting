package com.team4of5.foodspotting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;
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
    private RecyclerView mRecylerView;
    private NearRestaurantReccyclerViewAdapter mAdapter;
    private List<Restaurant> mRestaurents;
    private FirebaseFirestore db;

    private int[] myImageList = new int[]{R.drawable.slide1, R.drawable.slide2,
            R.drawable.slide3};
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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
        mAdapter = new NearRestaurantReccyclerViewAdapter(getActivity() , mRestaurents, mRecylerView);
        mRecylerView.setAdapter(mAdapter);
        queryRestaurant();

        // set RecyclerView on item click listner
        mAdapter.setOnItemListener(new NearRestaurantReccyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Restaurant item) {
                Intent intent = new Intent(getActivity(), Restaurent.class);
                intent.putExtra("id_restaurent", item.getId());
                startActivity(intent);
            }
        });

        //set load more listener for the RecyclerView adapter
        /*mAdapter.setOnLoadMoreListener(new NearRestaurantReccyclerViewAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (mRestaurents.size() <= 100) {
                    mRestaurents.add((null));
                    mAdapter.notifyItemInserted(mRestaurents.size() - 1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRestaurents.remove(mRestaurents.size() - 1);
                            mAdapter.notifyItemRemoved(mRestaurents.size());

                            //Generating more data

                            int index = mRestaurents.size();
                            mAdapter.notifyDataSetChanged();
                            mAdapter.setLoaded();
                        }
                    }, 2000);
                } else {
                    Toast.makeText(getActivity(), "Loading data completed", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
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

    public void queryRestaurant(){
        db.collection("restaurants")
                .limit(6)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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

}