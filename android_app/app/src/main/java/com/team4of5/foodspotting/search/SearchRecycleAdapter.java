package com.team4of5.foodspotting.search;

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

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.team4of5.foodspotting.R;
import com.team4of5.foodspotting.object.Restaurant;
import com.team4of5.foodspotting.restaurant.NearRestaurantReccyclerViewAdapter;

import java.io.InputStream;
import java.util.List;

public class SearchRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Restaurant> mRestaurants;
    private List<food> foods;
    private Context mContext;
    private Activity mActivity;
    private NearRestaurantReccyclerViewAdapter.OnItemClickListener mListener;

    // for load more
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private NearRestaurantReccyclerViewAdapter.OnLoadMoreListener onLoadMoreListener;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private boolean isLoading;
    private int visibleThreshold = 6;
    private int lastVisibleItem, totalItemCount;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.search_item, parent, false);
            return new ViewHolderRow(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_loading, parent, false);
            return new ViewHolderLoading(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NearRestaurantReccyclerViewAdapter.ViewHolderRow) {
            food food = foods.get(position);

            ViewHolderRow userViewHolder = (ViewHolderRow) holder;
            userViewHolder.foodname.setText(food.getName());
            userViewHolder.price.setText(food.getPrice());
            userViewHolder.rate.setText(food.getRes());
            new DownloadImageFromInternet(userViewHolder.imageView)
                    .execute(food.getImage());

            // binding item click listner
            //userViewHolder.bind(mRestaurants.get(position), mListener);
            userViewHolder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        } else if (holder instanceof ViewHolderLoading) {
            ViewHolderLoading loadingViewHolder = (ViewHolderLoading) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    public SearchRecycleAdapter(Context context, List<food> foods, RecyclerView recyclerView)
    {
        mContext = context;
        mActivity = (Activity)context;
        this.foods = foods;
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
    @Override
    public int getItemCount() {
        return foods == null ? 0 : foods.size();
    }

    public class ViewHolderRow extends RecyclerView.ViewHolder {
        public TextView foodname, price,rate;
        public ImageView imageView;
        public CardView container;
        public RatingBar ratingBar;

        public ViewHolderRow(View v) {
            super(v);
            foodname = v.findViewById(R.id.textFoodName);
            price = v.findViewById(R.id.textFoodPrice);
            imageView = v.findViewById(R.id.imageFood);
            rate = v.findViewById(R.id.textRating);
            container=v.findViewById(R.id.searchContainer);
        }

       /* public void bind(final Restaurant item, final NearRestaurantReccyclerViewAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }*/
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
}
