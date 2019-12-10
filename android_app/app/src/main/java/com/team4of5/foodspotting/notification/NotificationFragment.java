package com.team4of5.foodspotting.notification;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.team4of5.foodspotting.NewsDetailActivity;
import com.team4of5.foodspotting.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {
    private LinearLayout linearLayoutLogin;
    private LinearLayout relativeLayoutLogout;
    private RecyclerView mRVNotification;
    private List<News> list;
    private NotificationAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        linearLayoutLogin = view.findViewById(R.id.notification_login);
        relativeLayoutLogout = view.findViewById(R.id.notification_no_login);

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            login();
            setProperties(view);
        } else logout();

        return view;
    }

    private void setProperties(View view) {
        mRVNotification = view.findViewById(R.id.rvNews);
        mRVNotification.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        list = new ArrayList<News>();
        adapter =new NotificationAdapter(getContext(),list,mRVNotification);
        mRVNotification.setAdapter(adapter);
        queryNews();
        adapter.setOnItemListener(new NotificationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(News item) {
                Intent intent = new Intent(getActivity(),NewsDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("news", item);
                intent.putExtra("package", bundle);
                startActivity(intent);
            }
        });
    }

    private void queryNews() {
        FirebaseFirestore.getInstance().collection("news")
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (!task.getResult().isEmpty())
                    for (DocumentSnapshot doc : task.getResult()){
                        News news = new News();
                        news.setTenquan(doc.getString("tenquan"));
                        news.setTitle(doc.getString("title"));
                        news.setContent(doc.getString("content"));
                        news.setAddress("Địa chỉ:"+doc.getString("address"));
                        news.setImage(doc.getString("image"));
                        news.setDateCreated(doc.getDate("dateCreated"));
                        news.setId_res(doc.getString("id_res"));
                        list.add(news);
                        adapter.notifyDataSetChanged();
                    }
            }
        });
    }

    public void logout(){
        linearLayoutLogin.setVisibility(View.INVISIBLE);
        relativeLayoutLogout.setVisibility(View.VISIBLE);
    }

    public void login(){
        linearLayoutLogin.setVisibility(View.VISIBLE);
        relativeLayoutLogout.setVisibility(View.INVISIBLE);
    }
}
