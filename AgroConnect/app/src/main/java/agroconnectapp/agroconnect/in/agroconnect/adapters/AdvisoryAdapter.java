package agroconnectapp.agroconnect.in.agroconnect.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.etiennelawlor.imagegallery.library.activities.FullScreenImageGalleryActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import agroconnectapp.agroconnect.in.agroconnect.AdvisoryActivity;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.model.AdvisoryData;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;

/**
 * Created by niteshtarani on 18/04/16.
 */
public class AdvisoryAdapter extends RecyclerView.Adapter<AdvisoryAdapter.CustomViewHolder> {

    private Activity activity;
    private List<AdvisoryData> dataList;
    private int type;

    public AdvisoryAdapter(Activity activity, List<AdvisoryData> dataList,int type) {
        this.activity = activity;
        this.dataList = dataList;
        this.type = type;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == Constants.TYPE_PROGRESS_BAR) {
            View progBar = LayoutInflater.from(activity).inflate(R.layout.footer_progress_bar,null);
            RecyclerView.LayoutParams cardLayoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            progBar.setLayoutParams(cardLayoutParams);
            CustomViewHolder vh = new CustomViewHolder(progBar);
            return vh;
        }
        if(viewType == Constants.TYPE_SEPERATOR) {
            View line = new View(activity);
            line.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
            RecyclerView.LayoutParams cardLayoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,activity.getResources().getDimensionPixelSize(R.dimen.d4));
            cardLayoutParams.topMargin = activity.getResources().getDimensionPixelSize(R.dimen.d16);
            cardLayoutParams.bottomMargin = activity.getResources().getDimensionPixelSize(R.dimen.d4);
            line.setLayoutParams(cardLayoutParams);
            CustomViewHolder vh = new CustomViewHolder(line);
            return vh;
        }
        View card = LayoutInflater.from(activity).inflate(R.layout.advisory_post_card,null);
        RecyclerView.LayoutParams cardLayoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        card.setLayoutParams(cardLayoutParams);
        CustomViewHolder vh = new CustomViewHolder(card);
        vh.card = card;
        vh.agentImage = (ImageView) card.findViewById(R.id.agent_iv);
        vh.agentName = (TextView) card.findViewById(R.id.agent_name_tv);
        vh.agentCity = (TextView) card.findViewById(R.id.city_tv);
        vh.agentPhone = (TextView) card.findViewById(R.id.agent_phone);
        vh.agentCommodity = (TextView) card.findViewById(R.id.commodity_tv);
        vh.description = (TextView) card.findViewById(R.id.description_tv);
        vh.repliesCount = (TextView) card.findViewById(R.id.replies);
        vh.date = (TextView) card.findViewById(R.id.time_tv);
        vh.agentOrganisation = (TextView) card.findViewById(R.id.organization_tv);
        vh.agentDepartment = (TextView) card.findViewById(R.id.department_tv);
        vh.problemAsDiagnosedByAdvisor = (TextView) card.findViewById(R.id.problem_diagnose_tv);
        vh.productToBeApplied = (TextView) card.findViewById(R.id.product_apply_tv);
        vh.dosage = (TextView) card.findViewById(R.id.dosage_tv);
        vh.applicationTime = (TextView) card.findViewById(R.id.application_time_tv);
        vh.additionalAdvice = (TextView) card.findViewById(R.id.additional_advice_tv);
        vh.imgContainer = (LinearLayout) card.findViewById(R.id.img_container);
        vh.expertLinear = (LinearLayout) card.findViewById(R.id.expertLinear);

        return vh;

    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {

        try {
            final AdvisoryData ad = dataList.get(position);

            if (ad.getType() == Constants.TYPE_PROGRESS_BAR || ad.getType() == Constants.TYPE_SEPERATOR)
                return;

            holder.agentName.setText(ad.getAgentName());
            holder.agentCity.setText(ad.getAgentCity());
            holder.agentCommodity.setText(activity.getString(R.string.crop) + ": " + ad.getAgentCommodity());
            holder.date.setText(ad.getLastUpdated());
            holder.agentPhone.setText(activity.getString(R.string.mobile_number) + ": " + ad.getAgentPhone());
            if(type == Constants.TYPE_FEED)
                holder.description.setText(activity.getString(R.string.question) + ": " + ad.getDescription());
            else if(type == Constants.TYPE_DETAIL) {
                if(position == 0) {
                    holder.description.setText(activity.getString(R.string.question) + ": " + ad.getDescription());
                    holder.agentPhone.setVisibility(View.VISIBLE);
                    holder.agentOrganisation.setVisibility(View.GONE);
                    holder.agentDepartment.setVisibility(View.GONE);
                    holder.problemAsDiagnosedByAdvisor.setVisibility(View.GONE);
                    holder.productToBeApplied.setVisibility(View.GONE);
                    holder.dosage.setVisibility(View.GONE);
                    holder.applicationTime.setVisibility(View.GONE);
                    holder.additionalAdvice.setVisibility(View.GONE);
                } else {
                    if(ad.getDescription().contains("1001thx")) {
                        holder.description.setText(activity.getString(R.string.say_thank_you));
                    } else {
                        holder.description.setText(activity.getString(R.string.solution) + ": " + ad.getDescription());
                    }
                    Linkify.addLinks(holder.description, Linkify.ALL);
                    holder.description.setMovementMethod(LinkMovementMethod.getInstance());
                    holder.agentPhone.setVisibility(View.GONE);

                    if(2 == ad.getAgentType()) {
                        holder.expertLinear.setVisibility(View.VISIBLE);
                    } else {
                        holder.expertLinear.setVisibility(View.GONE);
                    }

                    setTextNE(holder.agentOrganisation, ad.getAgentOrganisation(), activity.getString(R.string.organization));
                    setTextNE(holder.agentDepartment, ad.getAgentDepartment(), activity.getString(R.string.department));
                    setTextNE(holder.problemAsDiagnosedByAdvisor, ad.getProblemAsDiagnosedByAdvisor(), activity.getString(R.string.problem_diagnosed_by_advisor));
                    setTextNE(holder.productToBeApplied, ad.getProductToBeApplied(), activity.getString(R.string.product_to_be_applied));
                    setTextNE(holder.dosage, ad.getDosage(), activity.getString(R.string.dosage));
                    setTextNE(holder.applicationTime, ad.getApplicationTime(), activity.getString(R.string.application_time));
                    setTextNE(holder.additionalAdvice, ad.getAdditionalAdvice(), activity.getString(R.string.additional_advice));
                }
            }

            if(type == Constants.TYPE_FEED) {
                holder.repliesCount.setText(ad.getRepliesCount());
                holder.repliesCount.setVisibility(View.VISIBLE);
                holder.card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, AdvisoryActivity.class);
                        intent.putExtra(Constants.postId, ad.getId());
                        activity.startActivity(intent);
                    }
                });
            } else {
                holder.repliesCount.setVisibility(View.GONE);
            }

            JSONArray resources = ad.getResources();
            if(resources != null) {
                holder.imgContainer.removeAllViews();
                final ArrayList<String> images = new ArrayList<>();
                for(int i=0; i<resources.length(); i++) {
                    String imgUrl = Constants.baseImgUrl + resources.getString(i);
                    images.add(imgUrl);
                }

                for(int i = 0; i < images.size(); i++) {
                    String url = images.get(i);
                    ImageView iv = new ImageView(activity);
                    iv.setLayoutParams(new LinearLayout.LayoutParams(activity.getResources().getDimensionPixelSize(R.dimen.d100), activity.getResources().getDimensionPixelSize(R.dimen.d100)));
                    int padding = activity.getResources().getDimensionPixelSize(R.dimen.d10);
                    iv.setPadding(padding,padding,padding,padding);
                    Picasso.with(activity).load(url).placeholder(R.drawable.loading).into(iv);
                    holder.imgContainer.addView(iv);
                    final int pos = i;
                    iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO load gallery here
                            showInGallary(images, pos);
                        }
                    });
                }
            }

        } catch ( Exception e) {
            e.printStackTrace();
            Crashlytics.logException(new Throwable("Error displaying advisory list - " + e.toString()));
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

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position).getType();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView agentImage;
        TextView agentName;
        TextView agentCity;
        TextView agentPhone;
        TextView agentCommodity;
        TextView repliesCount;
        TextView date;
        TextView description;
        TextView agentOrganisation;
        TextView agentDepartment;
        TextView problemAsDiagnosedByAdvisor;
        TextView productToBeApplied;
        TextView dosage;
        TextView applicationTime;
        TextView additionalAdvice;
        LinearLayout imgContainer;
        LinearLayout expertLinear;
        View card;

        public CustomViewHolder(View itemView) {
            super(itemView);
        }
    }

    private void showInGallary(ArrayList<String> images, int pos) {
        Intent intent = new Intent(activity, FullScreenImageGalleryActivity.class);
        intent.putStringArrayListExtra("images", images);
        intent.putExtra("position", pos);
        activity.startActivity(intent);
    }
}
