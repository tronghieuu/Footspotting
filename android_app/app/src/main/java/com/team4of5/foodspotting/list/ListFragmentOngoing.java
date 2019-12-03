package com.team4of5.foodspotting.list;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.team4of5.foodspotting.R;
import com.team4of5.foodspotting.object.Order;
import com.team4of5.foodspotting.object.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListFragmentOngoing extends Fragment {
    private LinearLayout linearLayoutLogin;
    private RelativeLayout relativeLayoutLogout;
    private OngoingAdapter mOngoingAdapter;
    private List<Order> mOrders;
    private RecyclerView mRecyclerView;
    private Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment_ongoing, container, false);
        linearLayoutLogin = view.findViewById(R.id.ongoing_login);
        relativeLayoutLogout = view.findViewById(R.id.ongoing_no_login);

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            login();
            mOrders = new ArrayList<>();
            mRecyclerView = view.findViewById(R.id.recycleViewOngoing);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            mOngoingAdapter = new OngoingAdapter(getActivity(), mOrders, mRecyclerView, new OngoingAdapter.BtnOngoingListener() {
                @Override
                public void onDetailButtonClick(View v, final int position) {
                    if(mOrders.get(position).getStatus() > 1) {
                        Toast.makeText(getActivity(), "Không thể hủy đơn này!", Toast.LENGTH_SHORT).show();
                    } else{
                        dialog.show();
                        FirebaseFirestore.getInstance().collection("order")
                                .document(mOrders.get(position).getId())
                                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mOrders.remove(position);
                                mOngoingAdapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });
                    }
                }

                @Override
                public void onContactButtonClick(View v, int position) {
                    dialog.show();
                    FirebaseFirestore.getInstance().collection("restaurants")
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
                    FirebaseFirestore.getInstance().collection("restaurants")
                            .document(mOrders.get(position).getRestaurant_id())
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                final Map<String, Object> data = new HashMap<>();
                                data.put("status", mOrders.get(position).getStatus()+"");
                                data.put("restaurant_id", documentSnapshot.getId());
                                data.put("restaurant_name", documentSnapshot.getString("name"));
                                data.put("restaurant_image", documentSnapshot.getString("image"));
                                data.put("food_amount", mOrders.get(position).getOrder_amount()+"");
                                data.put("timestamp", new Date().getTime());
                                data.put("restaurant_address", documentSnapshot.getString("street")+" "+
                                        documentSnapshot.getString("district")+" "+
                                        documentSnapshot.getString("province"));
                                FirebaseFirestore.getInstance().collection("restaurants")
                                        .document(mOrders.get(position).getRestaurant_id()).collection("food")
                                        .document(mOrders.get(position).getFood_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(documentSnapshot.exists()){
                                            data.put("food_name", documentSnapshot.getString("name"));
                                            data.put("food_price", documentSnapshot.getString("price"));
                                            data.put("food_image", documentSnapshot.getString("image"));
                                            FirebaseFirestore.getInstance().collection("user")
                                                    .document(User.getCurrentUser().getId())
                                                    .collection("history").add(data)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            FirebaseFirestore.getInstance().collection("order")
                                                                    .document(mOrders.get(position).getId())
                                                                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    mOrders.remove(position);
                                                                    User.getCurrentUser().setHistoryUpdate(true);
                                                                    mOngoingAdapter.notifyDataSetChanged();
                                                                    dialog.dismiss();
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
            });
            mOngoingAdapter.setOnItemListener(new OngoingAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Order item) {

                }
            });
            mRecyclerView.setAdapter(mOngoingAdapter);

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
        FirebaseFirestore.getInstance().collection("order")
                .whereEqualTo("user_id", User.getCurrentUser().getId())
                .orderBy("timestamp", Query.Direction.DESCENDING)
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
                            doc.getId()));
                    mOngoingAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
