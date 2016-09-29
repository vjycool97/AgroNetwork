package agroconnectapp.agroconnect.in.agroconnect.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import com.android.volley.Request;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.adapters.NewsFeedAdapter;
import agroconnectapp.agroconnect.in.agroconnect.components.ServerCommunication;
import agroconnectapp.agroconnect.in.agroconnect.databases.CommodityDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.databases.PrefDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.entities.NewsFeedEntity;
import agroconnectapp.agroconnect.in.agroconnect.utility.KeypadUtil;

/**
 * Created by sumanta on 27/6/16.
 */
public class NewsFeedSearchActivity extends AgroActivity {

    private int pageid = 1;
    private RecyclerView recyclerView;
    private ArrayList<NewsFeedEntity> entityList = new ArrayList<>();
    private NewsFeedAdapter adapter;
    private String match = "";
    private TextView emptyTV;
    private AutoCompleteTextView searchET;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed_search);
        emptyTV = (TextView) findViewById(R.id.emptyTV);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new NewsFeedAdapter(this, entityList, recyclerView);
        recyclerView.setAdapter(adapter);

        adapter.setOnLoadMoreListener(new NewsFeedAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                entityList.add(null); //add null , so the adapter will check view_type and show progress bar at bottom
                adapter.notifyItemInserted(entityList.size() - 1);
                getDataFromServer("3", match);
            }
        });
        searchET = (AutoCompleteTextView) findViewById(R.id.searchET);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.tag));
        searchET.setAdapter(arrayAdapter);
        searchET.setThreshold(1);
        searchET.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                KeypadUtil.hideKeypad(NewsFeedSearchActivity.this, searchET);
                String input = adapterView.getAdapter().getItem(i).toString().trim();
                if(!TextUtils.isEmpty(input) && input.length()>2 && !match.equals(input)) {
                    match = input;
                    pageid = 1;
                    entityList.clear();
                    adapter.notifyDataSetChanged();
                    emptyTV.setVisibility(View.VISIBLE);
                    emptyTV.setText(getString(R.string.loading));
                    getDataFromServer("3", match);
                }
            }
        });
        searchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    KeypadUtil.hideKeypad(NewsFeedSearchActivity.this, searchET);
                    String input = searchET.getText().toString().trim();
                    if(!TextUtils.isEmpty(input) && input.length()>2 && !match.equals(input)) {
                        match = input;
                        pageid = 1;
                        entityList.clear();
                        adapter.notifyDataSetChanged();
                        emptyTV.setVisibility(View.VISIBLE);
                        emptyTV.setText(getString(R.string.loading));
                        getDataFromServer("3", match);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void getDataFromServer(final String feedResultType, String match) {
        ArrayMap<String, String> paramMap = new ArrayMap<>();
        paramMap.put("pageid", String.valueOf(pageid));
        paramMap.put("feedresulttype", feedResultType);
        paramMap.put("match", match);
        ServerCommunication.INSTANCE.getServerData(NewsFeedSearchActivity.this, true, true, Request.Method.GET, NEWS_FEED_URI, NEWS_FEED_URI, paramMap, new ServerCommunication.OkuTaskListener() {
            @Override
            public void onPostExecute(String result) {
                if (!TextUtils.isEmpty(result)) {
                    if (entityList.size() > 0) {
                        entityList.remove(entityList.size() - 1); //remove progress item
                        adapter.notifyItemRemoved(entityList.size());
                    }
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        ArrayList<NewsFeedEntity> tempList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            NewsFeedEntity entity = new NewsFeedEntity();
                            entity.setId(jsonObject.getInt("Id"));
                            entity.setFeedType(jsonObject.getInt("FeedType"));
                            entity.setName(jsonObject.getString("Name"));
                            entity.setSourceUrl(jsonObject.getString("SourceUrl"));
                            entity.setTitle(jsonObject.getString("Title"));
                            entity.setImageUrl(jsonObject.getString("ImageUrl"));
                            entity.setAddedAt(jsonObject.getString("AddedDate"));
                            tempList.add(entity);
                        }
                        if (!tempList.isEmpty()) {
                            emptyTV.setVisibility(View.GONE);
                            entityList.addAll(tempList);
                            adapter.notifyDataSetChanged();
                            pageid++;
                        } else {
                            if(pageid==1) {
                                emptyTV.setVisibility(View.VISIBLE);
                                emptyTV.setText(getString(R.string.no_record_found));
                            }
                        }
                        adapter.setLoaded();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError() {
                if (entityList.contains(null)) {
                    entityList.remove(null); //remove progress item
                    adapter.notifyDataSetChanged();
                }
                emptyTV.setVisibility(View.VISIBLE);
                emptyTV.setText(getString(R.string.no_record_found));
            }

            @Override
            public void noNetwork() {
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        KeypadUtil.hideKeypad(NewsFeedSearchActivity.this);
    }
}
