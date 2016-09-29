package agroconnectapp.agroconnect.in.agroconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.activities.FirstNewsFeedActivity;
import agroconnectapp.agroconnect.in.agroconnect.activities.NewsFeedActivity;
import agroconnectapp.agroconnect.in.agroconnect.activities.ProfileActivity;
import agroconnectapp.agroconnect.in.agroconnect.activities.SecondNewsFeedActivity;
import agroconnectapp.agroconnect.in.agroconnect.activities.ThirdNewsFeedActivity;
import agroconnectapp.agroconnect.in.agroconnect.entities.NotificationEntity;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;
import agroconnectapp.agroconnect.in.agroconnect.utility.Utils;

/**
 * Created by sumanta on 22/7/16.
 */
public class NotificationAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    private Context context;
    private ArrayList<NotificationEntity> entityList;

    public NotificationAdapter(Context context, ArrayList<NotificationEntity> entityList) {
        this.context = context;
        this.entityList = entityList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_row_layout, parent, false);
        RecyclerView.ViewHolder viewHolder = new NotificationHolder(view);
        ((NotificationHolder) viewHolder).bodyWrapperLayout.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final NotificationEntity entity = entityList.get(position);
        if (entity != null) {
            ((NotificationHolder) holder).bodyWrapperLayout.setTag(position);
            if ("1".equals(entity.getId())) {
                ((NotificationHolder) holder).notifIV.setImageResource(R.drawable.default_profile_img);
            } else if ("3".equals(entity.getId())) {
                ((NotificationHolder) holder).notifIV.setImageResource(R.drawable.shop);
            } else if ("4".equals(entity.getId())) {
                ((NotificationHolder) holder).notifIV.setImageResource(R.drawable.book);
            } else {
                ((NotificationHolder) holder).notifIV.setImageResource(R.mipmap.information_icon);
            }
            ((NotificationHolder) holder).timeTV.setText(Utils.showTimeStamp(entity.getTimestamp()));
            ((NotificationHolder) holder).messageTV.setText(entity.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return entityList.size();
    }


    @Override
    public void onClick(View v) {
        int position = Integer.parseInt(v.getTag().toString());
        NotificationEntity entity = entityList.get(position);
        switch (v.getId()) {
            case R.id.bodyWrapperLayout: {
                try {
                    if ("1".equals(entity.getId())) {
                        int agent_id = Integer.parseInt(entity.getAgentId());
                        Intent intent = new Intent(context, ProfileActivity.class);
                        intent.putExtra(Constants.agentIdKey, agent_id);
                        intent.putExtra("id", entity.getId());
                        context.startActivity(intent);
                    } else if ("3".equals(entity.getId())) {
                        Intent intent = new Intent(context, NewsFeedActivity.class);
                        intent.putExtra(Constants.commodityId, entity.getCommodityId());
                        intent.putExtra(Constants.cityId, entity.getCityId());
                        intent.putExtra("id", entity.getId());
                        context.startActivity(intent);
                    } /*else if ("4".equals(entity.getId())) {
                        Intent intent = new Intent(context, AdvisoryActivity.class);
                        intent.putExtra(Constants.postId, Integer.parseInt(entity.getAdvisoryPostId()));
                        context.startActivity(intent);
                    }*/ else if ("5".equals(entity.getId())) {
                        String str = entity.getFeedType();
                        Intent intent;
                        if ("1".equals(str)) {
                             intent = new Intent(context, FirstNewsFeedActivity.class);
                            intent.putExtra("feedId", Integer.parseInt(entity.getFeedId()));
                            context.startActivity(intent);
                        }
                        if ("2".equals(str)) {
                             intent = new Intent(context, SecondNewsFeedActivity.class);
                            intent.putExtra("feedId", Integer.parseInt(entity.getFeedId()));
                            context.startActivity(intent);
                        } else {
                            intent = new Intent(context, ThirdNewsFeedActivity.class);
                            intent.putExtra("feedId", Integer.parseInt(entity.getFeedId()));
                            context.startActivity(intent);
                        }
//                        Intent intent = new Intent(context, NewsFeedActivity.class);
//                        intent.putExtra("feedId", entity.getFeedId());
//                        intent.putExtra("id", entity.getId());
//                        context.startActivity(intent);
                    } else {
                        throw new Exception();
                    }
                } catch (Exception ex) {
                    context.startActivity(new Intent(context, NewsFeedActivity.class));
                }
            }
        }
    }

    public static class NotificationHolder extends RecyclerView.ViewHolder {
        TextView timeTV, messageTV;
        ImageView notifIV;
        LinearLayout bodyWrapperLayout;

        public NotificationHolder(View view) {
            super(view);
            notifIV = (ImageView) view.findViewById(R.id.notifIV);
            timeTV = (TextView) view.findViewById(R.id.timeTV);
            messageTV = (TextView) view.findViewById(R.id.messageTV);
            bodyWrapperLayout = (LinearLayout) view.findViewById(R.id.bodyWrapperLayout);
        }
    }
}