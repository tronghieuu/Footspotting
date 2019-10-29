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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.util.List;

public class NearRestaurantReccyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Restaurant> mRestaurants;
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
        void onItemClick(Restaurant item);
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void add(int position, Restaurant item) {
        mRestaurants.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Restaurant item) {
        int position = mRestaurants.indexOf(item);
        mRestaurants.remove(position);
        notifyItemRemoved(position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public NearRestaurantReccyclerViewAdapter(Context context, List<Restaurant> restaurants, RecyclerView recyclerView) {

        mContext = context;
        mActivity = (Activity)context;
        mRestaurants = restaurants;

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
            View view = LayoutInflater.from(mActivity).inflate(R.layout.fragment_home_item, parent, false);
            return new ViewHolderRow(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_loading, parent, false);
            return new ViewHolderLoading(view);
        }
        return null;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        if (holder instanceof ViewHolderRow) {
            Restaurant restaurant = mRestaurants.get(position);

            ViewHolderRow userViewHolder = (ViewHolderRow) holder;

            userViewHolder.ratingBar.setRating((float)restaurant.getRate());
            userViewHolder.tvRestName.setText(restaurant.getName());
            userViewHolder.tvRestAddress.setText(restaurant.getAddress());
            new DownloadImageFromInternet(userViewHolder.imageView)
                    .execute(restaurant.getId());

            // binding item click listner
            userViewHolder.bind(mRestaurants.get(position), mListener);
        } else if (holder instanceof ViewHolderLoading) {
            ViewHolderLoading loadingViewHolder = (ViewHolderLoading) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mRestaurants == null ? 0 : mRestaurants.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    public void setOnItemListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return mRestaurants.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
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
        public TextView tvRestName, tvRestAddress;
        public ImageView imageView;
        public RatingBar ratingBar;

        public ViewHolderRow(View v) {
            super(v);
            tvRestName = v.findViewById(R.id.tvRestaurantName);
            tvRestAddress = v.findViewById(R.id.tvRestaurantAddress);
            imageView = v.findViewById(R.id.imageShop);
            ratingBar = v.findViewById(R.id.ratingShopOverall);
        }

        public void bind(final Restaurant item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

}

