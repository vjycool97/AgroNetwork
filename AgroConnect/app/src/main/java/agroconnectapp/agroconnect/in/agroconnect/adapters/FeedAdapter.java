package agroconnectapp.agroconnect.in.agroconnect.adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.List;

import agroconnectapp.agroconnect.in.agroconnect.ListingDetailActivity;
import agroconnectapp.agroconnect.in.agroconnect.MainActivity;
import agroconnectapp.agroconnect.in.agroconnect.ProfileActivity;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.model.FeedData;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;
import agroconnectapp.agroconnect.in.agroconnect.utility.DatabaseHandler;

/**
 * Created by nitin.gupta on 12/6/2015.
 */
public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.CustomViewHolder>{

    private List<FeedData> feedList;
    private Activity mContext;
    private int type;
    private DatabaseHandler db;
    private SharedPreferences sharedPreferences;

    public FeedAdapter(Activity mContext,List<FeedData> feedList,int type,DatabaseHandler db) {

        this.mContext = mContext;
        this.feedList = feedList;
        this.type = type;
        this.db = db;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType == Constants.TYPE_PROGRESS_BAR) {
            View progBar = LayoutInflater.from(mContext).inflate(R.layout.footer_progress_bar,null);
            CustomViewHolder vh = new CustomViewHolder(progBar);
            return vh;
        }

        View card = LayoutInflater.from(mContext).inflate(R.layout.feed_card,null);
        CustomViewHolder vh = new CustomViewHolder(card);
        vh.agentImage = (ImageView) card.findViewById(R.id.agent_iv);
        vh.agentName = (TextView) card.findViewById(R.id.agent_name_tv);
        vh.description = (TextView) card.findViewById(R.id.description_tv);
        vh.location = (TextView) card.findViewById(R.id.location_tv);
        vh.price = (TextView) card.findViewById(R.id.price_tv);
        vh.commodityName = (TextView) card.findViewById(R.id.commodity_name_tv);
        vh.descriptionLayout = (LinearLayout) card.findViewById(R.id.description_layout);
        vh.agentType = (TextView) card.findViewById(R.id.agent_type_tv);
        vh.date = (TextView) card.findViewById(R.id.time_tv);
        vh.buyerSeller = (TextView) card.findViewById(R.id.buyer_seller_tv);
        vh.shortListButton = (TextView) card.findViewById(R.id.shortlist_btn_tv);
        vh.shareButton = (TextView) card.findViewById(R.id.share_btn_tv);
        if(type == Constants.TYPE_SHORTLIST) {
            vh.shortListButton.setText(mContext.getResources().getString(R.string.remove));
            vh.shortListButton.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_delete,0,0,0);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {

        try {
            final FeedData fd = feedList.get(position);

            if (fd.getType() == Constants.TYPE_PROGRESS_BAR)
                return;

            holder.agentName.setText(fd.getAgentName());
            holder.location.setText(mContext.getString(R.string.location) + ": " + fd.getLocation());
            holder.commodityName.setText(mContext.getString(R.string.commodity) + ": " + fd.getCommodityName());
            holder.description.setText(mContext.getString(R.string.description) + ": " + fd.getDescription());
            holder.price.setText(mContext.getString(R.string.price) + ": " + fd.getPrice());
            holder.agentType.setText(fd.getAgentType());
            holder.date.setText(fd.getLastUpdated());
            if (fd.isSell()) {
                holder.buyerSeller.setText(mContext.getResources().getString(R.string.seller));
            } else {
                holder.buyerSeller.setText(mContext.getResources().getString(R.string.buyer));
            }
            holder.descriptionLayout.setOnClickListener(new ClickListener(mContext, fd, type));
            holder.agentImage.setOnClickListener(new ClickListener(mContext, fd, type));
            holder.agentName.setOnClickListener(new ClickListener(mContext, fd, type));
            holder.shortListButton.setOnClickListener(new ClickListener(mContext, fd, type));
            holder.shareButton.setOnClickListener(new ClickListener(mContext, fd, type));

        } catch ( Exception e) {
            e.printStackTrace();
            Crashlytics.logException(new Throwable("Error displaying feed - " + e.toString()));
        }
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return feedList.get(position).getType();
    }

    private class ClickListener implements View.OnClickListener {
        private Activity mContext;
        private FeedData fd;
        private int type;

        public ClickListener(Activity mContext,FeedData fd, int type) {
            this.mContext = mContext;
            this.fd = fd;
            this.type = type;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.agent_iv:
                case R.id.agent_name_tv:
                    Intent profileIntent = new Intent(mContext,ProfileActivity.class);
                    profileIntent.putExtra(Constants.agentIdKey, fd.getAgentId());
                    mContext.startActivity(profileIntent);
                    break;
                case R.id.description_layout:
                    Intent listingActivityIntent = new Intent(mContext, ListingDetailActivity.class);
                    listingActivityIntent.putExtra(Constants.feedParcelKey, fd);
                    mContext.startActivity(listingActivityIntent);
                    break;
                case  R.id.shortlist_btn_tv:

                    if(type == Constants.TYPE_FEED) {

                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                db.addToShortlist(fd);
                            }
                        });
                        if(mContext instanceof MainActivity) {
                            ((MainActivity)mContext).refreshShortlistFragment();
                        }
                    } else if(type == Constants.TYPE_SHORTLIST){

                        feedList.remove(fd);
                        notifyDataSetChanged();
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                db.removeFromShortlist(fd);
                            }
                        });
                    }

                    break;

                case R.id.share_btn_tv:
                    try {
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");

                        String shareBody = sharedPreferences.getString(Constants.name, "");
                        if(!fd.getAgentName().equalsIgnoreCase(sharedPreferences.getString(Constants.name, "")))
                            shareBody = shareBody + ", " + fd.getAgentName();

                        shareBody = shareBody + " and many others use AgroConnect for their buying/selling/sharing requirements. Join now the biggest Agriculture Network to grow your Business.  ";
                        shareBody = shareBody + Constants.playStoreUrl + mContext.getPackageName();
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        mContext.startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Crashlytics.logException(new Throwable("Error in feed share - " + e.toString()));
                    }

                    break;
            }
        }
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView agentImage;
        TextView agentName;
        TextView location;
        TextView commodityName;
        TextView description;
        TextView price;
        LinearLayout descriptionLayout;
        TextView agentType;
        TextView date;
        TextView buyerSeller;
        TextView shortListButton;
        TextView shareButton;

        public CustomViewHolder(View itemView) {
            super(itemView);
            RecyclerView.LayoutParams cardLayoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(cardLayoutParams);
        }
    }
}
