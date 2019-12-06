package com.team4of5.foodspotting.restaurant;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.team4of5.foodspotting.R;
import com.team4of5.foodspotting.list.OngoingAdapter;
import com.team4of5.foodspotting.object.Order;
import com.team4of5.foodspotting.object.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WaitConfirmFragment extends Fragment {
    private LinearLayout linearLayoutLogin;
    private RelativeLayout relativeLayoutLogout;
    private WaitConfirmAdapter waitConfirmAdapter;
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
            waitConfirmAdapter = new WaitConfirmAdapter(getActivity(), mOrders, mRecyclerView, new WaitConfirmAdapter.BtnOngoingListener(){
                @Override
                public void onCancelButtonClick(View v, final int position) {
                        dialog.show();
                        Map<String, Object> data = new HashMap<>();
                        data.put("status", "0");
                        FirebaseFirestore.getInstance().collection("order")
                            .document(mOrders.get(position).getId()).update(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mOrders.remove(position);
                                    waitConfirmAdapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            });
                }

                @Override
                public void onContactButtonClick(View v, int position) {
                    dialog.show();
                    FirebaseFirestore.getInstance().collection("user")
                            .document(mOrders.get(position).getRestaurant_id())
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" +documentSnapshot.getString("phone")));
                            dialog.dismiss();
                            startActivity(intent);
                        }
                    });
                }

                @Override
                public void onConfirmButtonClick(View v, final int position) {
                    dialog.show();
                    Map<String, Object> data = new HashMap<>();
                    data.put("status", "2");
                    FirebaseFirestore.getInstance().collection("order")
                            .document(mOrders.get(position).getId()).update(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mOrders.remove(position);
                                    waitConfirmAdapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            });
                }
            });
            waitConfirmAdapter.setOnItemListener(new WaitConfirmAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Order item) {

                }
            });
            mRecyclerView.setAdapter(waitConfirmAdapter);

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
        FirebaseFirestore.getInstance().collection("order")
                .whereEqualTo("restaurant_id", res_id)
                .whereEqualTo("status","1")
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
                            doc.getId(), doc.getString("shipper_id"), doc.getString("area")));
                    Toast.makeText(getContext(), "Done ", Toast.LENGTH_SHORT).show();

                    waitConfirmAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
