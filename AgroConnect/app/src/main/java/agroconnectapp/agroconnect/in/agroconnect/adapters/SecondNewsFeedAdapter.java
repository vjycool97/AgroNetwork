package agroconnectapp.agroconnect.in.agroconnect.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.activities.VideoActivity;
import agroconnectapp.agroconnect.in.agroconnect.components.AgroServerCommunication;
import agroconnectapp.agroconnect.in.agroconnect.entities.CommentEntity;
import agroconnectapp.agroconnect.in.agroconnect.utility.Utils;

/**
 * Created by sumanta on 1/8/16.
 */
public class SecondNewsFeedAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    private Context context;
    private String title, iamgeURL, videoID;
    private int feedID;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private ArrayList<CommentEntity> commentList;
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

    public SecondNewsFeedAdapter(Context context, String title, String iamgeURL, String videoID, int feedID, ArrayList<CommentEntity> commentList) {
        this.context = context;
        this.title = title;
        this.iamgeURL = iamgeURL;
        this.videoID = videoID;
        this.feedID = feedID;
        this.commentList = commentList;
    }

    @Override
    public int getItemViewType(int position) {
        return commentList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.comment_row, parent, false);
            viewHolder = new CommentHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.second_newsfeed_row, parent, false);
            viewHolder = new FirstRow(view);
            ((FirstRow) viewHolder).imageView.setOnClickListener(this);
            ((FirstRow) viewHolder).writeBtn.setOnClickListener(this);
            ((FirstRow) viewHolder).postBtn.setOnClickListener(this);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CommentHolder) {
            CommentEntity entity = commentList.get(position);
            if (entity != null) {
                ((CommentHolder) holder).nameTV.setText(entity.getAgentName());
                ((CommentHolder) holder).cityTV.setText(entity.getCity());
                ((CommentHolder) holder).organizationTV.setText(entity.getSpecialization());
                ((CommentHolder) holder).discussionTV.setText(entity.getDiscussion()+" discussions");
                ((CommentHolder) holder).commentTV.setText(entity.getComment());
                ((CommentHolder) holder).labelTV.setText(getLabel(entity.getAgentType()));
                ((CommentHolder) holder).timestampTV.setText(Utils.getDateDifference(entity.getLastUpdate()));
            }
        } else {
            ((FirstRow) holder).titleTV.setText(title);
            Picasso.with(context)
                    .load(iamgeURL)
                    .fit()
                    .placeholder(R.mipmap.icon_launcher)
                    .error(R.mipmap.icon_launcher)
                    .into(((FirstRow) holder).imageView);
        }
    }

    private String getLabel(int agentType) {
        String[] array = context.getResources().getStringArray(R.array.agent_types);
        return array[agentType];
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView : {
                Intent intent = new Intent(context, VideoActivity.class);
                intent.putExtra("videoID", videoID);
                context.startActivity(intent);
                break;
            }
            case R.id.writeBtn : {
                FirstRow firstRow = (FirstRow) v.getTag();
                firstRow.writeBtn.setVisibility(View.GONE);
                firstRow.commentLinear.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.postBtn : {
                FirstRow firstRow = (FirstRow) v.getTag();
                String comment = firstRow.commentET.getText().toString().trim();
                if(TextUtils.isEmpty(comment)) {
                    Toast.makeText(context, "Please write comment", Toast.LENGTH_SHORT).show();
                } else {
                    sendComment(comment, firstRow);
                    firstRow.commentET.setText("");
                    firstRow.commentLinear.setVisibility(View.GONE);
                    firstRow.writeBtn.setVisibility(View.VISIBLE);
                }
                break;
            }
        }
    }

    private void sendComment(String comment, final FirstRow firstRow) {
        String url = "https://mandiex.com/farmville/api/feed/"+feedID+"/Comment";
        JSONObject paramObject = new JSONObject();
        try {
            paramObject.put("Comment", comment);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        showDialog("Posting comment");
        AgroServerCommunication.INSTANCE.getServerDataByUrl(context, url, "", paramObject, new AgroServerCommunication.OkuTaskListener() {
            @Override
            public void onPostExecute(JSONObject resultObject) {
                hideDialog();
                CommentEntity entity = new CommentEntity();
                entity.setComment(resultObject.optString("Comment"));
                entity.setAgentName(resultObject.optString("AgentName"));
                entity.setLastUpdate(resultObject.optString("LastUpdated"));
                entity.setAgentType(resultObject.optInt("AgentType"));
                entity.setSpecialization(resultObject.optString("Specialisation"));
                entity.setCity(resultObject.optString("City"));
                entity.setDiscussion(resultObject.optString("Discussions"));
                commentList.add(1, entity);
                SecondNewsFeedAdapter.this.notifyItemInserted(1);
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

    public static class FirstRow extends RecyclerView.ViewHolder {
        TextView titleTV;
        ImageView imageView;
        EditText commentET;
        Button writeBtn, postBtn;
        LinearLayout commentLinear;
        public FirstRow(View view) {
            super(view);
            titleTV = (TextView) view.findViewById(R.id.titleTV);
            imageView = (ImageView) view.findViewById(R.id.imageView);
            commentET = (EditText) view.findViewById(R.id.commentET);
            writeBtn = (Button) view.findViewById(R.id.writeBtn);
            writeBtn.setTag(this);
            postBtn = (Button) view.findViewById(R.id.postBtn);
            postBtn.setTag(this);
            commentLinear = (LinearLayout) view.findViewById(R.id.commentLinear);
        }
    }

    public static class CommentHolder extends RecyclerView.ViewHolder {
        TextView nameTV, cityTV, organizationTV, discussionTV, commentTV, labelTV, timestampTV;
        public CommentHolder(View view) {
            super(view);
            nameTV = (TextView) view.findViewById(R.id.nameTV);
            cityTV = (TextView) view.findViewById(R.id.cityTV);
            organizationTV = (TextView) view.findViewById(R.id.organizationTV);
            discussionTV = (TextView) view.findViewById(R.id.discussionTV);
            commentTV = (TextView) view.findViewById(R.id.commentTV);
            labelTV = (TextView) view.findViewById(R.id.labelTV);
            timestampTV = (TextView) view.findViewById(R.id.timestampTV);
        }
    }
}