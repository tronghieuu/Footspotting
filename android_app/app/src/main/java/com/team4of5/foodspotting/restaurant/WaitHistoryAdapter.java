package com.team4of5.foodspotting.restaurant;

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

public class WaitHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<UserHistory> mOrders;
    private Context mContext;
    private Activity mActivity;
    private OnItemClickListener mListener;
    // for load more
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener onLoadMoreListener;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private boolean isLoading;
    private int visibleThreshold = 6;
    private int lastVisibleItem, totalItemCount;



    public interface OnItemClickListener {
        void onItemClick(Order item);
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void add(int position, UserHistory item) {
        mOrders.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Order item) {
        int position = mOrders.indexOf(item);
        mOrders.remove(position);
        notifyItemRemoved(position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public WaitHistoryAdapter(Context context, List<UserHistory> orders, RecyclerView recyclerView) {
        mContext = context;
        mActivity = (Activity)context;
        mOrders = orders;

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
            View view = LayoutInflater.from(mActivity).inflate(R.layout.fragment_order_history_item, parent, false);
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
            final UserHistory order = mOrders.get(position);

            final ViewHolderRow userViewHolder = (ViewHolderRow) holder;

            userViewHolder.tvUserAddress.setText(order.getUser_address());

            userViewHolder.tvFoodName.setText(order.getFood_name());
            userViewHolder.tvFoodPrice.setText("đ"+order.getFood_price());
            userViewHolder.tvOrderAmount.setText("x"+order.getFood_amount());
            userViewHolder.tvTotalPrice.setText("đ"+order.getFood_price()*order.getFood_amount());
                        new DownloadImageFromInternet(userViewHolder.imageViewFood)
                                .execute(order.getFood_image());

            userViewHolder.tvDateTime.setText("  "+order.getDateTime());

        } else if (holder instanceof ViewHolderLoading) {
            ViewHolderLoading loadingViewHolder = (ViewHolderLoading) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mOrders == null ? 0 : mOrders.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    public void setOnItemListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return mOrders.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
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
        public TextView tvFoodName, tvUserAddress, tvOrderAmount, tvFoodPrice, tvTotalPrice, tvDateTime;
        public ImageView imageViewFood;

        public ViewHolderRow(View v) {
            super(v);
            tvFoodName = v.findViewById(R.id.tvFoodName);
            tvUserAddress = v.findViewById(R.id.tvUserAddress);
            tvOrderAmount = v.findViewById(R.id.tvAmountOrder);
            tvFoodPrice = v.findViewById(R.id.tvFoodPrice);
            tvTotalPrice = v.findViewById(R.id.tvTotalPrice);
            imageViewFood = v.findViewById(R.id.imageViewFoodOrder);
            tvDateTime = v.findViewById(R.id.tvDateTime);
        }

        public void bind(final Order item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

}