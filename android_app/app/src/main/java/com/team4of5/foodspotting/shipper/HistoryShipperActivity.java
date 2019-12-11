package com.team4of5.foodspotting.shipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.team4of5.foodspotting.R;
import com.team4of5.foodspotting.object.Order;
import com.team4of5.foodspotting.object.User;
import com.team4of5.foodspotting.object.UserHistory;

import java.util.ArrayList;
import java.util.List;

public class HistoryShipperActivity extends AppCompatActivity {

    private List<UserHistory> mUserHistories;
    private RecyclerView mRecyclerView;
    private HistoryShipperAdapter historyShipperAdapter;
    private boolean isLoading = false;
    private SwipeRefreshLayout mSwipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_shipper);

        getSupportActionBar().hide();
        Window window = this.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.color_green));

        findViewById(R.id.btnBackHistoryShipper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mSwipe = findViewById(R.id.swipeHistoryShipper);
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!isLoading) {
                    mUserHistories.clear();
                    historyShipperAdapter.notifyDataSetChanged();
                    getData();
                } else mSwipe.setRefreshing(false);
            }
        });

        mUserHistories = new ArrayList<>();
        mRecyclerView = findViewById(R.id.recyclerViewHistoryShipper);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        historyShipperAdapter = new HistoryShipperAdapter(this, mUserHistories, mRecyclerView);
        mRecyclerView.setAdapter(historyShipperAdapter);
        historyShipperAdapter.setOnItemListener(new HistoryShipperAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(UserHistory item) {

            }
        });

        getData();
    }

    private void getData() {
        isLoading = true;
        FirebaseFirestore.getInstance().collection("history")
                .whereEqualTo("shipper_id", User.getCurrentUser().getId())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        mUserHistories.add(new UserHistory(doc.getId(),
                                doc.getString("restaurant_id"),
                                doc.getString("restaurant_name"),
                                doc.getString("restaurant_image"),
                                doc.getString("restaurant_address"),
                                doc.getString("food_image"),
                                doc.getString("food_name"),
                                Integer.parseInt(doc.getString("food_price")),
                                Integer.parseInt(doc.getString("amount")),
                                Integer.parseInt(doc.getString("status")),
                                doc.getLong("timestamp"),
                                doc.getString("user_id"),
                                doc.getString("user_address"),
                                doc.getString("shipper_id")));
                        historyShipperAdapter.notifyDataSetChanged();
                    }
                }
                isLoading = false;
                mSwipe.setRefreshing(false);
            }
        });
    }
}
