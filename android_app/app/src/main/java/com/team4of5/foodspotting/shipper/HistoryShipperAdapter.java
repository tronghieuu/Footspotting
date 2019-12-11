package com.team4of5.foodspotting.shipper;

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
import com.team4of5.foodspotting.R;
import com.team4of5.foodspotting.object.Order;
import com.team4of5.foodspotting.object.UserHistory;

import java.io.InputStream;
import java.util.List;

public class HistoryShipperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<UserHistory> mUserHistories;
    private Context mContext;
    private Activity mActivity;
    private HistoryShipperAdapter.OnItemClickListener mListener;

    // for load more
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private HistoryShipperAdapter.OnLoadMoreListener onLoadMoreListener;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private boolean isLoading;
    private int visibleThreshold = 6;
    private int lastVisibleItem, totalItemCount;

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
    public HistoryShipperAdapter(Context context, List<UserHistory> userHistories, RecyclerView recyclerView) {
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
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_history_shipper, parent, false);
            return new HistoryShipperAdapter.ViewHolderRow(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_loading, parent, false);
            return new HistoryShipperAdapter.ViewHolderLoading(view);
        }
        return null;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        if (holder instanceof HistoryShipperAdapter.ViewHolderRow) {
            final UserHistory userHistory = mUserHistories.get(position);

            final HistoryShipperAdapter.ViewHolderRow userViewHolder = (HistoryShipperAdapter.ViewHolderRow) holder;

            userViewHolder.tvResName.setText(userHistory.getRestaurant_name());
            userViewHolder.tvResAddress.setText(userHistory.getRestaurant_address());
                    new HistoryShipperAdapter.DownloadImageFromInternet(userViewHolder.imageViewRes)
                            .execute(userHistory.getRestaurant_image());
            userViewHolder.tvFoodName.setText(userHistory.getFood_name());
            userViewHolder.tvFoodPrice.setText("đ"+userHistory.getFood_price());
            userViewHolder.tvOrderAmount.setText("x"+userHistory.getFood_amount());
            userViewHolder.tvTotalPrice.setText("đ"+userHistory.getFood_price()*userHistory.getFood_amount());
            new HistoryShipperAdapter.DownloadImageFromInternet(userViewHolder.imageViewFood)
                    .execute(userHistory.getFood_image());
            userViewHolder.tvDestination.setText(userHistory.getUser_address());
            userViewHolder.tvDateTime.setText(userHistory.getDateTime());

            // binding item click listner
            userViewHolder.bind(mUserHistories.get(position), mListener);
        } else if (holder instanceof HistoryShipperAdapter.ViewHolderLoading) {
            HistoryShipperAdapter.ViewHolderLoading loadingViewHolder = (HistoryShipperAdapter.ViewHolderLoading) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mUserHistories == null ? 0 : mUserHistories.size();
    }

    public void setOnLoadMoreListener(HistoryShipperAdapter.OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    public void setOnItemListener(HistoryShipperAdapter.OnItemClickListener listener) {
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
        public TextView tvResName, tvDateTime, tvFoodName, tvResAddress, tvOrderAmount, tvFoodPrice, tvTotalPrice, tvDestination;
        public ImageView imageViewRes, imageViewFood;

        public ViewHolderRow(View v) {
            super(v);
            tvResName = v.findViewById(R.id.tvResNameOrderShipper);
            tvDateTime = v.findViewById(R.id.tvDateTimeOrderShipper);
            tvFoodName = v.findViewById(R.id.tvFoodNameOrderShipper);
            tvResAddress = v.findViewById(R.id.tvResAddressOrderShipper);
            tvOrderAmount = v.findViewById(R.id.tvAmountOrderOrderShipper);
            tvFoodPrice = v.findViewById(R.id.tvFoodPriceOrderShipper);
            tvDestination = v.findViewById(R.id.tvDestinationOrderShipper);
            tvTotalPrice = v.findViewById(R.id.tvTotalPriceOrderShipper);
            imageViewRes = v.findViewById(R.id.imageViewResOrderShipper);
            imageViewFood = v.findViewById(R.id.imageViewFoodOrderShipper);
        }

        public void bind(final UserHistory item, final HistoryShipperAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
