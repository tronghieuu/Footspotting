package com.team4of5.foodspotting.shipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
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


public class OrderReceivedShipperActivity extends AppCompatActivity {

    private List<Order> mOrders;
    private RecyclerView mRecyclerView;
    private ReceivedAdapter receivedAdapter;
    private boolean isLoading = false;
    private SwipeRefreshLayout mSwipe;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_received_shipper);

        getSupportActionBar().hide();
        Window window = this.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.color_green));
        findViewById(R.id.btnBackOrderReceivedShipper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mSwipe = findViewById(R.id.swipeOrderReceivedShipper);
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!isLoading) {
                    mOrders.clear();
                    receivedAdapter.notifyDataSetChanged();
                    getData();
                } else mSwipe.setRefreshing(false);
            }
        });

        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_loading);

        mOrders = new ArrayList<>();
        mRecyclerView = findViewById(R.id.recyclerViewOrderReceivedShipper);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        receivedAdapter = new ReceivedAdapter(this, mOrders, mRecyclerView, new ReceivedAdapter.BtnOrderReceivedShipperListener() {
            @Override
            public void onReceivedButtonClick(View v, final int position) {
                dialog.show();
                final Map<String, Object> data = new HashMap<>();
                data.put("restaurant_id", mOrders.get(position).getRestaurant_id());
                data.put("user_id", mOrders.get(position).getUser_id());
                data.put("status", "5");
                data.put("timestamp", mOrders.get(position).getTimestamp());
                data.put("amount", mOrders.get(position).getOrder_amount()+"");
                data.put("shipper_id", mOrders.get(position).getShipper_id());
                FirebaseFirestore.getInstance().collection("restaurants")
                        .document(mOrders.get(position).getRestaurant_id())
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            data.put("restaurant_address", documentSnapshot.getString("street")+
                                    ", "+documentSnapshot.getString("district")+", "+documentSnapshot.getString("province"));
                            data.put("restaurant_name", documentSnapshot.getString("name"));
                            data.put("restaurant_image", documentSnapshot.getString("image"));
                            FirebaseFirestore.getInstance().collection("restaurants")
                                    .document(mOrders.get(position).getRestaurant_id())
                                    .collection("food").document(mOrders.get(position).getFood_id())
                                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        data.put("food_name", documentSnapshot.getString("name"));
                                        data.put("food_price", documentSnapshot.getString("price"));
                                        data.put("food_image", documentSnapshot.getString("image"));
                                        FirebaseFirestore.getInstance().collection("user")
                                                .document(mOrders.get(position).getUser_id())
                                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if(documentSnapshot.exists()) {
                                                    data.put("user_address", documentSnapshot.getString("street"+
                                                            ", "+documentSnapshot.getString("district")+", "+
                                                            documentSnapshot.getString("province")));
                                                }
                                                FirebaseFirestore.getInstance().collection("history")
                                                        .add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        FirebaseFirestore.getInstance().collection("order")
                                                                .document(mOrders.get(position).getId()).delete()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        mOrders.remove(position);
                                                                        receivedAdapter.notifyDataSetChanged();
                                                                        dialog.dismiss();
                                                                    }
                                                                });
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void onContactButtonClick(View v, final int position) {

            }
        });
        mRecyclerView.setAdapter(receivedAdapter);
        receivedAdapter.setOnItemListener(new ReceivedAdapter.OnItemClickListener() {
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
                .whereEqualTo("status", "4")
                .whereEqualTo("shipper_id", User.getCurrentUser().getId())
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
                        receivedAdapter.notifyDataSetChanged();
                    }
                }
                isLoading = false;
                mSwipe.setRefreshing(false);
            }
        });
    }
}
