package com.team4of5.foodspotting.list;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.team4of5.foodspotting.R;
import com.team4of5.foodspotting.object.User;
import com.team4of5.foodspotting.object.UserHistory;
import com.team4of5.foodspotting.restaurant.Restaurent;

import java.util.ArrayList;
import java.util.List;

public class ListFragmentHistory extends Fragment {

    private LinearLayout linearLayoutLogin;
    private RelativeLayout relativeLayoutLogout;
    private OrderHistoryAdapter mOrderHistoryAdapter;
    private List<UserHistory> mUserHistories;
    private RecyclerView mRecyclerView;
    private Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.list_fragment_history, container, false);
        linearLayoutLogin = view.findViewById(R.id.history_login);
        relativeLayoutLogout = view.findViewById(R.id.history_no_login);

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            login();
            mUserHistories = new ArrayList<>();
            mRecyclerView = view.findViewById(R.id.recycleViewHistory);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            mOrderHistoryAdapter = new OrderHistoryAdapter(getActivity(), mUserHistories, mRecyclerView, new OrderHistoryAdapter.BtnHistoryListener() {
                @Override
                public void onDeleteButtonClick(View v, final int position) {
                    dialog.show();
                    FirebaseFirestore.getInstance().collection("user")
                            .document(User.getCurrentUser().getId())
                            .collection("history").document(mUserHistories.get(position).getId())
                            .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mUserHistories.remove(position);
                            mOrderHistoryAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });
                }

                @Override
                public void onVisitButtonClick(View v, int position) {
                    Intent intent = new Intent(getActivity(), Restaurent.class);
                    intent.putExtra("id_restaurent", mUserHistories.get(position).getRestaurant_id());
                    startActivity(intent);
                }
            });
            mOrderHistoryAdapter.setOnItemListener(new OrderHistoryAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(UserHistory item) {

                }
            });
            mRecyclerView.setAdapter(mOrderHistoryAdapter);

            dialog = new Dialog(getActivity());
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.item_loading);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {

                }
            });

            getHistory();
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

    public void getHistory(){
        FirebaseFirestore.getInstance().collection("user")
                .document(User.getCurrentUser().getId())
                .collection("history").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot doc : queryDocumentSnapshots){
                    mUserHistories.add(new UserHistory(doc.getId(),
                            doc.getString("restaurant_id"),
                            doc.getString("restaurant_name"),
                            doc.getString("restaurant_image"),
                            doc.getString("restaurant_address"),
                            doc.getString("food_image"),
                            doc.getString("food_name"),
                            Integer.parseInt(doc.getString("food_price")),
                            Integer.parseInt(doc.getString("food_amount")),
                            Integer.parseInt(doc.getString("status")),
                            doc.getLong("timestamp")));
                    mOrderHistoryAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
