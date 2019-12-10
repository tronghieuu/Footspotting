package com.team4of5.foodspotting.restaurant;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.team4of5.foodspotting.R;
import com.team4of5.foodspotting.object.Order;

import java.util.ArrayList;
import java.util.List;


public class WaitHistoryFragment extends Fragment {
    private LinearLayout linearLayoutLogin;
    private RelativeLayout relativeLayoutLogout;
    private WaitHistoryAdapter waitHistoryAdapter;
    private List<Order> mOrders;
    private RecyclerView mRecyclerView;
    private Dialog dialog;
    String res_id;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_wait_confirm, container, false);
        linearLayoutLogin = view.findViewById(R.id.ongoing_login);
        relativeLayoutLogout = view.findViewById(R.id.ongoing_no_login);

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            login();
            mOrders = new ArrayList<>();
            mRecyclerView = view.findViewById(R.id.recycleViewOngoing);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            waitHistoryAdapter = new WaitHistoryAdapter(getActivity(), mOrders, mRecyclerView);
            waitHistoryAdapter.setOnItemListener(new WaitHistoryAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Order item) {

                }
            });
            mRecyclerView.setAdapter(waitHistoryAdapter);

            dialog = new Dialog(getActivity());
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.item_loading);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {

                }
            });

            getListOrder();
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

    public void getListOrder(){
        res_id = getActivity().getIntent().getStringExtra("res_id");
        FirebaseFirestore.getInstance().collection("restaurants")
                .document(res_id)
                .collection("history")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot doc : queryDocumentSnapshots){
                    mOrders.add(new Order(doc.getString("restaurant_id"),
                            doc.getString("food_id"),
                            doc.getString("user_id"),
                            Integer.parseInt(doc.getString("amount")),
                            Integer.parseInt(doc.getString("status")),
                            doc.getLong("timestamp"),
                            doc.getId(),doc.getString("shipper_id"), doc.getString("area")));
                    Toast.makeText(getContext(), "Done ", Toast.LENGTH_SHORT).show();

                    waitHistoryAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}