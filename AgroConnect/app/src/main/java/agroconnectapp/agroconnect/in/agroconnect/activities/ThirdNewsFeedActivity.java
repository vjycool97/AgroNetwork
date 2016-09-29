package agroconnectapp.agroconnect.in.agroconnect.activities;

import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import com.android.volley.Request;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.adapters.ThirdNewsFeedAdapter;
import agroconnectapp.agroconnect.in.agroconnect.components.ServerCommunication;
import agroconnectapp.agroconnect.in.agroconnect.entities.CommentEntity;
import agroconnectapp.agroconnect.in.agroconnect.utility.Utils;

/**
 * Created by sumanta on 1/8/16.
 */
public class ThirdNewsFeedActivity extends AgroActivity {

    private int feedId;
    private RecyclerView recyclerView;
    private ArrayList<CommentEntity> entityList = new ArrayList<>();
    private ThirdNewsFeedAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        setToolbar();
        feedId = getIntent().getIntExtra("feedId", 0);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        entityList.add(null);
        getDataFromServer();
    }

    private void getDataFromServer() {
        showDialog("Loading detail...");
        ArrayMap<String, String> paramMap = new ArrayMap<>();
        ServerCommunication.INSTANCE.getServerData(ThirdNewsFeedActivity.this, true, true, Request.Method.GET, NEWS_FEED_URI + "/" + feedId, NEWS_FEED_URI, paramMap, new ServerCommunication.OkuTaskListener() {
            @Override
            public void onPostExecute(String result) {
                hideDialog();
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        getCommentFromServer();
                        adapter = new ThirdNewsFeedAdapter(ThirdNewsFeedActivity.this, jsonObject, feedId, entityList);
                        recyclerView.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

    private void getCommentFromServer() {
        String url = "https://mandiex.com/farmville/api/feed/"+feedId+"/Comments";
        ServerCommunication.INSTANCE.getServerDataByGet(ThirdNewsFeedActivity.this, true, url, "", new ServerCommunication.OkuTaskListener() {
            @Override
            public void onPostExecute(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONArray resultArray = new JSONArray(result);
                        for(int i=0; i<resultArray.length(); i++) {
                            JSONObject commentObject = resultArray.getJSONObject(i);
                            CommentEntity entity = new CommentEntity();
                            entity.setComment(commentObject.getString("Comment"));
                            entity.setAgentName(commentObject.getString("AgentName"));
                            entity.setAgentType(commentObject.getInt("AgentType"));
                            entity.setSpecialization(commentObject.getString("Specialisation"));
                            entity.setCity(commentObject.getString("City"));
                            entity.setDiscussion(commentObject.getString("Discussions"));
                            entity.setLastUpdate(commentObject.getString("LastUpdated"));
                            entityList.add(entity);
                            adapter.notifyItemInserted(entityList.size());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError() {
            }

            @Override
            public void noNetwork() {
            }
        });
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("AgroConnect");
        toolbar.setTitleTextColor(getResources().getColor(R.color.color_white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_whatsapp, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }  else if (id == R.id.whatsappId) {
            Utils.takeScreenShotAndShare(ThirdNewsFeedActivity.this, recyclerView);
        }
        return super.onOptionsItemSelected(item);
    }
}