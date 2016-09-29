package agroconnectapp.agroconnect.in.agroconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.List;
import agroconnectapp.agroconnect.in.agroconnect.AdvisoryActivity;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.entities.AdvisorEntity;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;
import agroconnectapp.agroconnect.in.agroconnect.utility.Utils;

/**
 * Created by sumanta on 14/7/16.
 */
public class UserAdvisorAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    private Context context;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private List<AdvisorEntity> entityList;
    private int visibleThreshold = 2;  // The minimum amount of items to have below your current scroll position before loading more.
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    public UserAdvisorAdapter(Context context, List<AdvisorEntity> entityList, RecyclerView recyclerView) {
        this.context = context;
        this.entityList = entityList;
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
            View view = LayoutInflater.from(context).inflate(R.layout.advisory_post_card, parent, false);
            viewHolder = new AdvisorHolder(view);
            ((AdvisorHolder) viewHolder).bodyWrapperLayout.setOnClickListener(this);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.progress_item, parent, false);
            viewHolder = new ProgressViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AdvisorHolder) {
            final AdvisorEntity entity = entityList.get(position);
            if (entity != null) {
                ((AdvisorHolder) holder).bodyWrapperLayout.setTag(position);
                try {
                    ((AdvisorHolder) holder).agentName.setText(entity.getAgentName());
                    ((AdvisorHolder) holder).agentCity.setText(entity.getAgentCity());
                    String commodity = TextUtils.isEmpty(entity.getAgentCommodity()) ? "Not Available" : entity.getAgentCommodity();
                    ((AdvisorHolder) holder).agentCommodity.setText(context.getString(R.string.crop) + ": " + commodity);
                    ((AdvisorHolder) holder).date.setText(Utils.getDateDifference(entity.getLastUpdate()));
                    ((AdvisorHolder) holder).agentPhone.setText(context.getString(R.string.mobile_number) + ": " + entity.getAgentPhoneNumber());
                    ((AdvisorHolder) holder).description.setText(context.getString(R.string.question) + ": " + entity.getDescription());
                    ((AdvisorHolder) holder).repliesCount.setVisibility(View.VISIBLE);
                    ((AdvisorHolder) holder).repliesCount.setText(String.valueOf(entity.getReplies()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    private void setTextNE(TextView textView, String text, String prefix) {
        if(text.isEmpty() || text.equals("null"))
            textView.setVisibility(View.GONE);
        else {
            textView.setText(prefix + ": " + text);
            textView.setVisibility(View.VISIBLE);
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
        final AdvisorEntity entity = entityList.get(position);
        switch (v.getId()) {
            case R.id.bodyWrapperLayout: {
                Intent intent = new Intent(context, AdvisoryActivity.class);
                intent.putExtra(Constants.postId, entity.getId());
                context.startActivity(intent);
                break;
            }
        }
    }

    public static class AdvisorHolder extends RecyclerView.ViewHolder {
        ImageView agentImage;
        TextView agentName, agentCity, agentPhone, agentCommodity, repliesCount, date, description, agentOrganisation,
                agentDepartment, problemAsDiagnosedByAdvisor, productToBeApplied, dosage, applicationTime, additionalAdvice;
        LinearLayout imgContainer, bodyWrapperLayout;

        public AdvisorHolder(View view) {
            super(view);
            agentImage = (ImageView) view.findViewById(R.id.agent_iv);
            agentName = (TextView) view.findViewById(R.id.agent_name_tv);
            agentCity = (TextView) view.findViewById(R.id.city_tv);
            agentPhone = (TextView) view.findViewById(R.id.agent_phone);
            agentCommodity = (TextView) view.findViewById(R.id.commodity_tv);
            description = (TextView) view.findViewById(R.id.description_tv);
            repliesCount = (TextView) view.findViewById(R.id.replies);
            date = (TextView) view.findViewById(R.id.time_tv);
            agentOrganisation = (TextView) view.findViewById(R.id.organization_tv);
            agentDepartment = (TextView) view.findViewById(R.id.department_tv);
            problemAsDiagnosedByAdvisor = (TextView) view.findViewById(R.id.problem_diagnose_tv);
            productToBeApplied = (TextView) view.findViewById(R.id.product_apply_tv);
            dosage = (TextView) view.findViewById(R.id.dosage_tv);
            applicationTime = (TextView) view.findViewById(R.id.application_time_tv);
            additionalAdvice = (TextView) view.findViewById(R.id.additional_advice_tv);
            imgContainer = (LinearLayout) view.findViewById(R.id.img_container);
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
