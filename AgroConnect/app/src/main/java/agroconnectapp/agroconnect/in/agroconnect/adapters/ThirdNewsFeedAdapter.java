package agroconnectapp.agroconnect.in.agroconnect.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.etiennelawlor.imagegallery.library.activities.FullScreenImageGalleryActivity;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.activities.ThirdNewsFeedActivity;
import agroconnectapp.agroconnect.in.agroconnect.components.AgroServerCommunication;
import agroconnectapp.agroconnect.in.agroconnect.entities.CommentEntity;
import agroconnectapp.agroconnect.in.agroconnect.utility.Utils;

/**
 * Created by sumanta on 1/8/16.
 */
public class ThirdNewsFeedAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    private Context context;
    private int feedId;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int REQUEST_IMAGE_PICK = 2;
    private ArrayList<CommentEntity> commentList;
    private JSONObject jsonObject;
    private ProgressDialog dialog;
    private Dialog imgPickerDialog;
    private Activity activity;
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

    public ThirdNewsFeedAdapter(Context context, JSONObject firstObject, int feedId, ArrayList<CommentEntity> commentList) {
        this.context = context;
        this.jsonObject = firstObject;
        this.feedId = feedId;
        this.commentList = commentList;
        this.activity = (Activity)context;
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
            View view = LayoutInflater.from(context).inflate(R.layout.third_newsfeed_row, parent, false);
            viewHolder = new FirstRow(view);
            ((FirstRow) viewHolder).writeBtn.setOnClickListener(this);
            ((FirstRow) viewHolder).postBtn.setOnClickListener(this);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder instanceof CommentHolder) {
                CommentEntity entity = commentList.get(position);
                if (entity != null) {
                    ((CommentHolder) holder).nameTV.setText(entity.getAgentName());
                    ((CommentHolder) holder).cityTV.setText(entity.getCity());
                    ((CommentHolder) holder).organizationTV.setText(entity.getSpecialization());
                    ((CommentHolder) holder).discussionTV.setText(entity.getDiscussion() + " discussions");
                    ((CommentHolder) holder).commentTV.setText(entity.getComment());
                    ((CommentHolder) holder).labelTV.setText(getLabel(entity.getAgentType()));
                    ((CommentHolder) holder).timestampTV.setText(Utils.getDateDifference(entity.getLastUpdate()));

                    final ArrayList<String> imageList = entity.getImageUrls();

              /*  for(int i = 0; i < imageList.size(); i++) {
                    String url = imageList.get(i);
                    final Activity activity = (Activity) context;
                    ImageView iv = new ImageView(activity);
                    iv.setLayoutParams(new LinearLayout.LayoutParams(activity.getResources().getDimensionPixelSize(R.dimen.d160), activity.getResources().getDimensionPixelSize(R.dimen.d160)));
                    int padding = activity.getResources().getDimensionPixelSize(R.dimen.d10);
                    iv.setPadding(padding,padding,0,padding);
                    iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Picasso.with(activity).load(url).placeholder(R.drawable.loading).into(iv);
                    ((FirstRow) holder).imageContainer.addView(iv);
                    final int pos = i;
                    iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO load gallery here
                            showInGallary(imageList, pos);
                        }
                    });
                }*/

                }
            } else {
                try {

                    ((FirstRow) holder).imgBtn.setOnClickListener(this);
                    String content = jsonObject.getString("Content");
                    ((FirstRow) holder).contentTV.setText(content);
                    Linkify.addLinks(((FirstRow) holder).contentTV, Linkify.ALL);
                    ((FirstRow) holder).contentTV.setMovementMethod(LinkMovementMethod.getInstance());

                    String imageUrls = jsonObject.optString("ImageUrls");
                    if (!TextUtils.isEmpty(imageUrls.trim()) && imageUrls.length() > 5) {
                        ((FirstRow) holder).imageContainer.removeAllViews();
                        String[] imageArray = imageUrls.split(",");
                        final ArrayList<String> imageList = new ArrayList<>();
                        for (String url : imageArray)
                            imageList.add(url);

                        for (int i = 0; i < imageList.size(); i++) {
                            String url = imageList.get(i);
                            final Activity activity = (Activity) context;
                            ImageView iv = new ImageView(activity);
                            iv.setLayoutParams(new LinearLayout.LayoutParams(activity.getResources().getDimensionPixelSize(R.dimen.d160), activity.getResources().getDimensionPixelSize(R.dimen.d160)));
                            int padding = activity.getResources().getDimensionPixelSize(R.dimen.d10);
                            iv.setPadding(padding, padding, 0, padding);
                            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            Picasso.with(activity).load(url).placeholder(R.drawable.loading).into(iv);
                            ((FirstRow) holder).imageContainer.addView(iv);
                            final int pos = i;
                            iv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // TODO load gallery here
                                    showInGallary(imageList, pos);
                                }
                            });
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private String getLabel(int agentType) {
        try{
            String[] array = context.getResources().getStringArray(R.array.agent_types);
            return array[agentType];
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        return context.getResources().getStringArray(R.array.agent_types)[1];
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
            case R.id.imageBtn :{
                initializeAndShowImgPickerDialog();
                break;
            }
            case R.id.camara_btn:
                try {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
                        activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                } catch (Exception e) {
                    Toast.makeText(activity, context.getResources().getString(R.string.unsupported_operation),Toast.LENGTH_LONG).show();
                }
                imgPickerDialog.dismiss();

                break;
            case R.id.gallery_btn:
                try {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activity.startActivityForResult(i, REQUEST_IMAGE_PICK);
                } catch (Exception e) {
                    Toast.makeText(context, context.getResources().getString(R.string.unsupported_operation),Toast.LENGTH_LONG).show();
                }
                imgPickerDialog.dismiss();
                break;
        }
    }

    private void initializeAndShowImgPickerDialog() {
        imgPickerDialog = new Dialog(context);
        imgPickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        imgPickerDialog.setContentView(R.layout.dialog_img_picker);
        Button cameraBtn = (Button) imgPickerDialog.findViewById(R.id.camara_btn);
        Button galleryBtn = (Button) imgPickerDialog.findViewById(R.id.gallery_btn);
        cameraBtn.setOnClickListener(this);
        galleryBtn.setOnClickListener(this);
        imgPickerDialog.show();
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
                ThirdNewsFeedAdapter.this.notifyItemInserted(1);
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
        TextView contentTV;
        EditText commentET;
        Button writeBtn, postBtn,imgBtn;
        LinearLayout commentLinear, imageContainer;
        public FirstRow(View view) {
            super(view);
            contentTV = (TextView) view.findViewById(R.id.contentTV);
            commentET = (EditText) view.findViewById(R.id.commentET);
            writeBtn = (Button) view.findViewById(R.id.writeBtn);
            writeBtn.setTag(this);
            postBtn = (Button) view.findViewById(R.id.postBtn);
            postBtn.setTag(this);
            commentLinear = (LinearLayout) view.findViewById(R.id.commentLinear);
            imageContainer = (LinearLayout) view.findViewById(R.id.imageContainer);
            imgBtn = (Button)view.findViewById(R.id.imageBtn);


        }
    }

    public static class CommentHolder extends RecyclerView.ViewHolder {
        TextView nameTV, cityTV, organizationTV, discussionTV, commentTV, labelTV, timestampTV;
        LinearLayout imageContainer;
        public CommentHolder(View view) {
            super(view);
            nameTV = (TextView) view.findViewById(R.id.nameTV);
            cityTV = (TextView) view.findViewById(R.id.cityTV);
            organizationTV = (TextView) view.findViewById(R.id.organizationTV);
            discussionTV = (TextView) view.findViewById(R.id.discussionTV);
            commentTV = (TextView) view.findViewById(R.id.commentTV);
            labelTV = (TextView) view.findViewById(R.id.labelTV);
            timestampTV = (TextView) view.findViewById(R.id.timestampTV);
            imageContainer = (LinearLayout)view.findViewById(R.id.imageContainer);

        }
    }

    private void showInGallary(ArrayList<String> images, int pos) {
        Intent intent = new Intent(context, FullScreenImageGalleryActivity.class);
        intent.putStringArrayListExtra("images", images);
        intent.putExtra("position", pos);
        context.startActivity(intent);
    }
}