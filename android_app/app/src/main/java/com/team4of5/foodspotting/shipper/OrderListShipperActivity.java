package com.team4of5.foodspotting.shipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderListShipperActivity extends AppCompatActivity {

    private List<Order> mOrders;
    private RecyclerView mRecyclerView;
    private OrderListShipperAdapter mOrderListShipperAdapter;
    private boolean isLoading = false;
    private SwipeRefreshLayout mSwipe;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list_shipper);
        getSupportActionBar().hide();
        Window window = this.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.color_green));
        findViewById(R.id.btnBackOrderShipper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mSwipe = findViewById(R.id.swipeOrderShipper);
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!isLoading) {
                    mOrders.clear();
                    mOrderListShipperAdapter.notifyDataSetChanged();
                    getData();
                } else mSwipe.setRefreshing(false);
            }
        });

        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_loading);

        mOrders = new ArrayList<>();
        mRecyclerView = findViewById(R.id.recyclerViewOrderShipper);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mOrderListShipperAdapter = new OrderListShipperAdapter(this, mOrders, mRecyclerView, new OrderListShipperAdapter.BtnOrderListShipperListener() {
            @Override
            public void onPickShipButtonClick(View v, final int position) {
                dialog.show();
                Map<String, Object> data = new HashMap<>();
                data.put("status", "3");
                data.put("shipper_id", User.getCurrentUser().getId());
                FirebaseFirestore.getInstance().collection("order")
                        .document(mOrders.get(position).getId())
                        .update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();
                        mOrders.remove(position);
                        mOrderListShipperAdapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), "Nhận ship thành công!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        mRecyclerView.setAdapter(mOrderListShipperAdapter);
        mOrderListShipperAdapter.setOnItemListener(new OrderListShipperAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Order item) {

            }
        });
        getData();
    }

    private void getData() {
        isLoading = true;
        FirebaseFirestore.getInstance().collection("order")
                .whereEqualTo("area", User.getCurrentUser().getProvince())
                .whereEqualTo("status", "2")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()) {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        mOrders.add(new Order(doc.getString("restaurant_id"),
                                doc.getString("food_id"),
                                doc.getString("user_id"),
                                Integer.parseInt(doc.getString("amount")),
                                Integer.parseInt(doc.getString("status")),
                                doc.getLong("timestamp"),
                                doc.getId(), doc.getString("shipper_id"),
                                doc.getString("area")));
                        mOrderListShipperAdapter.notifyDataSetChanged();
                    }
                }
                isLoading = false;
                mSwipe.setRefreshing(false);
            }
        });
    }
}
