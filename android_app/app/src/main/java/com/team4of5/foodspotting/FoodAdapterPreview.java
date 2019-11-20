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
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.InputStream;
import java.util.List;

public class FoodAdapterPreview extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Food> mFoods;
    private Context mContext;
    private Activity mActivity;
    private FoodButtonListener mBtnFoodListener;


    // for load more
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private boolean isLoading;
    private int visibleThreshold = 6;
    private int lastVisibleItem, totalItemCount;

    public interface FoodButtonListener {
        void onDeleteButtonClick(View v, int position);
        void onModifyButtonClick(View v, int position);
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void add(int position, Food item) {
        mFoods.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Food item) {
        int position = mFoods.indexOf(item);
        mFoods.remove(position);
        notifyItemRemoved(position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FoodAdapterPreview(Context context, List<Food> foods, RecyclerView recyclerView,
                              FoodButtonListener btnFoodListener) {

        mContext = context;
        mActivity = (Activity)context;
        mFoods = foods;
        mBtnFoodListener = btnFoodListener;

        // load more
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_food_preview, parent, false);
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
            final Food food = mFoods.get(position);

            ViewHolderRow userViewHolder = (ViewHolderRow) holder;

            userViewHolder.tvPrice.setText("đ"+food.getPrice());
            userViewHolder.tvFoodName.setText(food.getName());
            new DownloadImageFromInternet(userViewHolder.imageView)
                    .execute(food.getImage());

            // binding item click listner



            userViewHolder.btnModify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mBtnFoodListener.onModifyButtonClick(view, position);
                }
            });

            userViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mBtnFoodListener.onDeleteButtonClick(view, position);
                }
            });

        } else if (holder instanceof ViewHolderLoading) {
            ViewHolderLoading loadingViewHolder = (ViewHolderLoading) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mFoods == null ? 0 : mFoods.size();
    }


    @Override
    public int getItemViewType(int position) {
        return mFoods.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
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
        public TextView tvFoodName, tvPrice;
        public ImageView imageView;
        public Button btnModify, btnDelete;

        public ViewHolderRow(View v) {
            super(v);
            tvFoodName = v.findViewById(R.id.textFoodNamePreview);
            imageView = v.findViewById(R.id.imageFoodPreview);
            tvPrice = v.findViewById(R.id.textFoodPricePreview);
            btnModify = v.findViewById(R.id.btnFoodModify);
            btnDelete = v.findViewById(R.id.btnFoodDelete);

        }
    }

}
