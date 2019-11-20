package com.team4of5.foodspotting;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.InputStream;
import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<UserHistory> mUserHistories;
    private Context mContext;
    private Activity mActivity;
    private OnItemClickListener mListener;
    private BtnHistoryListener btnHistoryListener;

    // for load more
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener onLoadMoreListener;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private boolean isLoading;
    private int visibleThreshold = 6;
    private int lastVisibleItem, totalItemCount;

    public interface BtnHistoryListener{
        void onDeleteButtonClick(View v, int position);
        void onVisitButtonClick(View v, int position);
    }

    public interface OnItemClickListener {
        void onItemClick(UserHistory item);
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void add(int position, UserHistory item) {
        mUserHistories.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(UserHistory item) {
        int position = mUserHistories.indexOf(item);
        mUserHistories.remove(position);
        notifyItemRemoved(position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public OrderHistoryAdapter(Context context, List<UserHistory> userHistories, RecyclerView recyclerView,
                          BtnHistoryListener btnHistoryListener) {
        this.btnHistoryListener = btnHistoryListener;
        mContext = context;
        mActivity = (Activity)context;
        mUserHistories = userHistories;

        // load more
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.history_item, parent, false);
            return new ViewHolderRow(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_loading, parent, false);
            return new ViewHolderLoading(view);
        }
        return null;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        if (holder instanceof ViewHolderRow) {
            UserHistory userHistory = mUserHistories.get(position);

            ViewHolderRow userViewHolder = (ViewHolderRow) holder;

            userViewHolder.tvResName.setText(userHistory.getRestaurant_name());
            userViewHolder.tvResAddress.setText(userHistory.getRestaurant_address());
            userViewHolder.tvFoodName.setText(userHistory.getFood_name());
            userViewHolder.tvOrderAmount.setText("x"+userHistory.getFood_amount());
            userViewHolder.tvFoodPrice.setText("đ"+userHistory.getFood_price());
            userViewHolder.tvTotalPrice.setText(userHistory.getTotalPrice());

            if(userHistory.getStatus() == 0){
                userViewHolder.tvStatus.setText("Từ chối");
            } else if(userHistory.getStatus() == 5){
                userViewHolder.tvStatus.setText("Đã giao");
            }

            new OrderHistoryAdapter.DownloadImageFromInternet(userViewHolder.imageViewFood)
                    .execute(userHistory.getFood_image());

            new OrderHistoryAdapter.DownloadImageFromInternet(userViewHolder.imageViewRes)
                    .execute(userHistory.getRestaurant_image());

            userViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnHistoryListener.onDeleteButtonClick(view , position);
                }
            });

            userViewHolder.btnVisit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnHistoryListener.onVisitButtonClick(view , position);
                }
            });

            // binding item click listner
            userViewHolder.bind(mUserHistories.get(position), mListener);
        } else if (holder instanceof ViewHolderLoading) {
            ViewHolderLoading loadingViewHolder = (ViewHolderLoading) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mUserHistories == null ? 0 : mUserHistories.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    public void setOnItemListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return mUserHistories.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setLoaded() {
        isLoading = false;
    }

    private class ViewHolderLoading extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ViewHolderLoading(View view) {
            super(view);
            progressBar = view.findViewById(R.id.progressBar);
        }
    }

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {

        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView){
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolderRow extends RecyclerView.ViewHolder {
        public TextView tvResName, tvStatus, tvFoodName, tvResAddress, tvOrderAmount, tvFoodPrice, tvTotalPrice;
        public ImageView imageViewRes, imageViewFood;
        public Button btnDelete, btnVisit;

        public ViewHolderRow(View v) {
            super(v);
            tvResName = v.findViewById(R.id.tvResNameHistory);
            tvStatus = v.findViewById(R.id.tvStatusHistory);
            tvFoodName = v.findViewById(R.id.tvFoodNameHistory);
            tvResAddress = v.findViewById(R.id.tvResAddressHistory);
            tvOrderAmount = v.findViewById(R.id.tvAmountOrderHistory);
            tvFoodPrice = v.findViewById(R.id.tvFoodPriceHistory);
            tvTotalPrice = v.findViewById(R.id.tvTotalPriceHistory);
            imageViewRes = v.findViewById(R.id.imageViewResHistory);
            imageViewFood = v.findViewById(R.id.imageViewFoodOrderHistory);
            btnDelete = v.findViewById(R.id.btnDelete);
            btnVisit = v.findViewById(R.id.btnVisit);
        }

        public void bind(final UserHistory item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

}
