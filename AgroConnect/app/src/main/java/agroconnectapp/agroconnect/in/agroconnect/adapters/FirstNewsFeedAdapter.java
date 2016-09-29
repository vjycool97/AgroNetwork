package agroconnectapp.agroconnect.in.agroconnect.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.components.AgroServerCommunication;
import agroconnectapp.agroconnect.in.agroconnect.entities.CommentEntity;
import agroconnectapp.agroconnect.in.agroconnect.utility.Utils;

/**
 * Created by sumanta on 31/7/16.
 */
public class FirstNewsFeedAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    private Context context;
    private int feedId;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private ArrayList<CommentEntity> commentList;
    private JSONObject firstObject;
    private static ProgressBar progressBar;
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

    public FirstNewsFeedAdapter(Context context, JSONObject firstObject, int feedId, ArrayList<CommentEntity> commentList) {
        this.context = context;
        this.firstObject = firstObject;
        this.feedId = feedId;
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
            View view = LayoutInflater.from(context).inflate(R.layout.first_newsfeed_row, parent, false);
            viewHolder = new FirstRow(view);
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
            try {
                String content = firstObject.getString("Content");
                webView.loadData(content, "text/html; charset=UTF-8", null);
                String sourceURL = firstObject.getString("SourceUrl");
                ((FirstRow) holder).sourceTV.setText(sourceURL);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        String url = "https://mandiex.com/farmville/api/feed/"+feedId+"/Comment";
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
                FirstNewsFeedAdapter.this.notifyItemInserted(1);
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

    static WebView webView;
    public static class FirstRow extends RecyclerView.ViewHolder {
        TextView sourceTV;
        EditText commentET;
        Button writeBtn, postBtn;
        LinearLayout commentLinear;
        public FirstRow(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            webView = (WebView) view.findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    webView.loadUrl("javascript:MyApp.resize(document.body.getBoundingClientRect().height)");
                    super.onPageFinished(view, url);
                    progressBar.setVisibility(View.GONE);
                }
            });
            webView.addJavascriptInterface(this, "MyApp");
            sourceTV = (TextView) view.findViewById(R.id.sourceTV);
            commentET = (EditText) view.findViewById(R.id.commentET);
            writeBtn = (Button) view.findViewById(R.id.writeBtn);
            writeBtn.setTag(this);
            postBtn = (Button) view.findViewById(R.id.postBtn);
            postBtn.setTag(this);
            commentLinear = (LinearLayout) view.findViewById(R.id.commentLinear);
        }
    }

    @JavascriptInterface
    public void resize(final float height) {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.setLayoutParams(new LinearLayout.LayoutParams(context.getResources().getDisplayMetrics().widthPixels,
                        (int) (height * context.getResources().getDisplayMetrics().density)));
            }
        });
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
