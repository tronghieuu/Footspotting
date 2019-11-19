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

import java.io.InputStream;
import java.util.List;

public class OngoingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<OrderOngoing> mOrderOngoings;
    private Context mContext;
    private Activity mActivity;
    private OnItemClickListener mListener;
    private BtnOngoingListener btnOngoingListener;

    // for load more
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener onLoadMoreListener;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private boolean isLoading;
    private int visibleThreshold = 6;
    private int lastVisibleItem, totalItemCount;

    public interface BtnOngoingListener{
        void onDetailButtonClick(View v, int position);
    }

    public interface OnItemClickListener {
        void onItemClick(OrderOngoing item);
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void add(int position, OrderOngoing item) {
        mOrderOngoings.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(OrderOngoing item) {
        int position = mOrderOngoings.indexOf(item);
        mOrderOngoings.remove(position);
        notifyItemRemoved(position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public OngoingAdapter(Context context, List<OrderOngoing> orderOngoings, RecyclerView recyclerView,
                          BtnOngoingListener btnOngoingListener) {
        this.btnOngoingListener = btnOngoingListener;
        mContext = context;
        mActivity = (Activity)context;
        mOrderOngoings = orderOngoings;

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
            View view = LayoutInflater.from(mActivity).inflate(R.layout.ongoing_item, parent, false);
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
            OrderOngoing orderOngoing = mOrderOngoings.get(position);

            ViewHolderRow userViewHolder = (ViewHolderRow) holder;


            userViewHolder.tvResName.setText(orderOngoing.getResName());
            userViewHolder.tvResAddress.setText(orderOngoing.getResAddress());
            userViewHolder.tvFoodName.setText(orderOngoing.getFoodName());
            userViewHolder.tvFoodPrice.setText(orderOngoing.getFoodPrice());
            userViewHolder.tvOrderAmount.setText(orderOngoing.getFoodAmount());
            userViewHolder.tvTotalPrice.setText(orderOngoing.getTotalPrice());
            userViewHolder.btnDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnOngoingListener.onDetailButtonClick(view, position);
                }
            });
            if(orderOngoing.getStatus() == 1){
                userViewHolder.tvStatus.setText("Chờ xác nhận");
            } else if(orderOngoing.getStatus() == 2){
                userViewHolder.tvStatus.setText("Đang giao");
            }

            new DownloadImageFromInternet(userViewHolder.imageViewFood)
                    .execute(orderOngoing.getFoodImage());

            new DownloadImageFromInternet(userViewHolder.imageViewRes)
                    .execute(orderOngoing.getResImage());

            // binding item click listner
            userViewHolder.bind(mOrderOngoings.get(position), mListener);
        } else if (holder instanceof ViewHolderLoading) {
            ViewHolderLoading loadingViewHolder = (ViewHolderLoading) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mOrderOngoings == null ? 0 : mOrderOngoings.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    public void setOnItemListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return mOrderOngoings.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
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
        public Button btnDetail;

        public ViewHolderRow(View v) {
            super(v);
            tvResName = v.findViewById(R.id.tvResName);
            tvStatus = v.findViewById(R.id.tvStatus);
            tvFoodName = v.findViewById(R.id.tvFoodName);
            tvResAddress = v.findViewById(R.id.tvResAddress);
            tvOrderAmount = v.findViewById(R.id.tvAmountOrder);
            tvFoodPrice = v.findViewById(R.id.tvFoodPrice);
            tvTotalPrice = v.findViewById(R.id.tvTotalPrice);
            imageViewRes = v.findViewById(R.id.imageViewRes);
            imageViewFood = v.findViewById(R.id.imageViewFoodOrder);
            btnDetail = v.findViewById(R.id.btnDetail);
        }

        public void bind(final OrderOngoing item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

}