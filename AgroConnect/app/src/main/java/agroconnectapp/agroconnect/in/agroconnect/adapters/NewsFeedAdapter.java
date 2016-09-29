package agroconnectapp.agroconnect.in.agroconnect.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.activities.FirstNewsFeedActivity;
import agroconnectapp.agroconnect.in.agroconnect.activities.FeedThreeActivity;
import agroconnectapp.agroconnect.in.agroconnect.activities.SecondNewsFeedActivity;
import agroconnectapp.agroconnect.in.agroconnect.activities.ThirdNewsFeedActivity;
import agroconnectapp.agroconnect.in.agroconnect.activities.VideoActivity;
import agroconnectapp.agroconnect.in.agroconnect.components.AgroServerCommunication;
import agroconnectapp.agroconnect.in.agroconnect.databases.PrefDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.entities.NewsFeedEntity;
import agroconnectapp.agroconnect.in.agroconnect.utility.KeyIDS;
import agroconnectapp.agroconnect.in.agroconnect.utility.Utils;

/**
 * Created by sumanta on 23/6/16.
 */
public class NewsFeedAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    private Context context;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private ArrayList<NewsFeedEntity> entityList;
    private int visibleThreshold = 2;  // The minimum amount of items to have below your current scroll position before loading more.
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    private ProgressDialog dialog;
    private void showDialog(String message) {
        if (dialog == null) {
            dialog = new ProgressDialog(context);
        }
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.show();
    }
    private void hideDialog() {
        if (dialog!=null && dialog.isShowing())
            dialog.dismiss();
    }

    public NewsFeedAdapter(Context context, ArrayList<NewsFeedEntity> propertyList, RecyclerView recyclerView) {
        this.context = context;
        this.entityList = propertyList;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached call onLoadMore()
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }


    }

    @Override
    public int getItemViewType(int position) {
        return entityList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.news_feed_layout, parent, false);
            viewHolder = new NewsHolder(view);
            ((NewsHolder) viewHolder).likeBtn.setOnClickListener(this);
            ((NewsHolder) viewHolder).commentBtn.setOnClickListener(this);
            ((NewsHolder) viewHolder).bodyWrapperLayout.setOnClickListener(this);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.progress_item, parent, false);
            viewHolder = new ProgressViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NewsHolder) {
            NewsFeedEntity entity = entityList.get(position);
            if (entity != null) {
                ((NewsHolder) holder).bodyWrapperLayout.setTag(position);
                ((NewsHolder) holder).likeBtn.setTag(position);
                ((NewsHolder) holder).commentBtn.setTag(position);
                ((NewsHolder) holder).nameTV.setText(TextUtils.isEmpty(entity.getName())? "" : entity.getName());
                ((NewsHolder) holder).cityTV.setText(TextUtils.isEmpty(entity.getCity())? "" : entity.getCity());
                ((NewsHolder) holder).titleTV.setText(TextUtils.isEmpty(entity.getTitle())? "" : entity.getTitle());
                ((NewsHolder) holder).dateTV.setText(TextUtils.isEmpty(entity.getAddedAt()) ? "" : Utils.getDateDifference(entity.getAddedAt()));
                if(entity.getLikeCount() > 1) {
                    ((NewsHolder) holder).likeBtn.setText(entity.getLikeCount()+" Likes");
                } else {
                    ((NewsHolder) holder).likeBtn.setText(entity.getLikeCount()+" Like");
                }
                if(entity.getCommentCount() > 1) {
                    ((NewsHolder) holder).commentBtn.setText(entity.getCommentCount()+" Discussions");
                } else {
                    ((NewsHolder) holder).commentBtn.setText(entity.getCommentCount()+" Discussion");
                }
                if(!TextUtils.isEmpty(entity.getImageUrl()))
                Picasso.with(context)
                        .load(entity.getImageUrl())
                        .fit()
                        .placeholder(R.mipmap.icon_launcher)
                        .error(R.mipmap.icon_launcher)
                        .into(((NewsHolder) holder).imageView);
            }
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded() {
        loading = false;
    }


    @Override
    public int getItemCount() {
        return entityList.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public void onClick(View v) {
        int position = Integer.parseInt(v.getTag().toString());
        final NewsFeedEntity entity = entityList.get(position);
        switch (v.getId()) {
            case R.id.commentBtn: {
                if (1 == entity.getFeedType()) {
                    //article
                    Intent intent = new Intent(context, FirstNewsFeedActivity.class);
                    intent.putExtra("feedId", entity.getId());
                    context.startActivity(intent);
                } else if (2 == entity.getFeedType()) {
                    //video
                    Intent intent = new Intent(context, SecondNewsFeedActivity.class);
                    intent.putExtra("title", entity.getTitle());
                    intent.putExtra("imageURL", entity.getImageUrl());
                    intent.putExtra("videoID", entity.getSourceUrl());
                    intent.putExtra("feedId", entity.getId());
                    context.startActivity(intent);
                } else if (3 == entity.getFeedType()) {
                    //advisor
                    Intent intent = new Intent(context, ThirdNewsFeedActivity.class);
                    intent.putExtra("feedId", entity.getId());
                    context.startActivity(intent);
                }
                break;
            }
            case R.id.likeBtn: {
                likePost(entity);
                break;
            }
            case R.id.bodyWrapperLayout: {
                if (1 == entity.getFeedType()) {
                    //article
                    Intent intent = new Intent(context, FirstNewsFeedActivity.class);
                    intent.putExtra("feedId", entity.getId());
                    context.startActivity(intent);
                } else if (2 == entity.getFeedType()) {
                    //video
                    Intent intent = new Intent(context, SecondNewsFeedActivity.class);
                    intent.putExtra("title", entity.getTitle());
                    intent.putExtra("imageURL", entity.getImageUrl());
                    intent.putExtra("videoID", entity.getSourceUrl());
                    intent.putExtra("feedId", entity.getId());
                    context.startActivity(intent);
                } else if (3 == entity.getFeedType() || 4 == entity.getFeedType()) {
                    //advisor
                    Intent intent = new Intent(context, ThirdNewsFeedActivity.class);
                    intent.putExtra("feedId", entity.getId());
                    context.startActivity(intent);
                }
                break;
            }
        }
    }

    private void likePost(final NewsFeedEntity entity) {
        String url = "https://mandiex.com/farmville/api/feed/like/"+entity.getId();
        showDialog("Loading");
        AgroServerCommunication.INSTANCE.getServerDataByUrl(context, url, "", null, new AgroServerCommunication.OkuTaskListener() {
            @Override
            public void onPostExecute(JSONObject resultObject) {
                hideDialog();
                if(resultObject!=null) {
                    int likeCount = resultObject.optInt("LikeCount");
//                    likeCount++;
                    entity.setLikeCount(likeCount);
                    NewsFeedAdapter.this.notifyDataSetChanged();
                }
            }

            @Override
            public void onError() {
                hideDialog();
            }

            @Override
            public void noNetwork() {
                hideDialog();
            }
        });
    }

    public static class NewsHolder extends RecyclerView.ViewHolder {
        TextView nameTV, titleTV, dateTV,cityTV;
        ImageView imageView;
        LinearLayout bodyWrapperLayout;
        Button likeBtn, commentBtn;

        public NewsHolder(View view) {
            super(view);
            nameTV = (TextView) view.findViewById(R.id.nameTV);
            titleTV = (TextView) view.findViewById(R.id.titleTV);
            dateTV = (TextView) view.findViewById(R.id.dateTV);
            cityTV = (TextView) view.findViewById(R.id.cityTV);
            imageView = (ImageView) view.findViewById(R.id.imageView);
            likeBtn = (Button) view.findViewById(R.id.likeBtn);
            commentBtn = (Button) view.findViewById(R.id.commentBtn);
            bodyWrapperLayout = (LinearLayout) view.findViewById(R.id.bodyWrapperLayout);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}