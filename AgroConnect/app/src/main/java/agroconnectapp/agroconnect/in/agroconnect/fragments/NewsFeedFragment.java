package agroconnectapp.agroconnect.in.agroconnect.fragments;

import android.media.MediaCodec;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.Request;
import com.crashlytics.android.Crashlytics;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import agroconnectapp.agroconnect.in.agroconnect.AgroConnect;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.adapters.NewsFeedAdapter;
import agroconnectapp.agroconnect.in.agroconnect.components.ServerCommunication;
import agroconnectapp.agroconnect.in.agroconnect.entities.NewsFeedEntity;
import agroconnectapp.agroconnect.in.agroconnect.events.NewsFeedEvent;

/**
 * Created by sumanta on 26/6/16.
 */
public class NewsFeedFragment extends AgroFragment{

    private RecyclerView recyclerView;
    private int pageid = 1;
    private ArrayList<NewsFeedEntity> entityList = new ArrayList<>();
    private NewsFeedAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    String feedResultType = "1";
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newsfeed, container, false);
        try{
            AgroConnect.agroEventBus.register(this);
        }catch (Exception e){
            e.printStackTrace();
            Crashlytics.log("NewsFeed Fragment exception");
        }

        feedResultType = getArguments().getString("feedResultType");

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
               getDataFromServer(feedResultType);
                onItemsLoadComplete();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_green_dark,
                android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_red_light);



        adapter = new NewsFeedAdapter(getActivity(), entityList, recyclerView);
        recyclerView.setAdapter(adapter);


        adapter.setOnLoadMoreListener(new NewsFeedAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                entityList.add(null); //add null , so the adapter will check view_type and show progress bar at bottom
                adapter.notifyItemInserted(entityList.size() - 1);
                getDataFromServer(feedResultType);
            }
        });
        getDataFromServer(feedResultType);
        return view;
    }

    private void onItemsLoadComplete() {
        swipeContainer.setRefreshing(false);

    }

    @Subscribe
    public void getRefress(NewsFeedEvent feedEvent) {
        /*if("1".equals(feedResultType)) {
            System.out.println("---------------------------------------------------------------");
            entityList.clear();
            adapter.notifyDataSetChanged();
            pageid = 1;
            getDataFromServer(feedResultType);
        }*/
    }



    private void getDataFromServer(final String feedResultType) {
        if (pageid == 1 && feedResultType.equals("1")) {
            showDialog("Loading news feed...");
        }
        ArrayMap<String, String> paramMap = new ArrayMap<>();
        paramMap.put("pageid", String.valueOf(pageid));
        paramMap.put("feedresulttype", feedResultType);
        ServerCommunication.INSTANCE.getServerData(getActivity(), true, true, Request.Method.GET, NEWS_FEED_URI, NEWS_FEED_URI, paramMap, new ServerCommunication.OkuTaskListener() {
            @Override
            public void onPostExecute(String result) {
                if (pageid == 1) {
                    hideDialog();
                }
                if (entityList.size() > 0) {
                    entityList.remove(entityList.size() - 1); //remove progress item
                    adapter.notifyItemRemoved(entityList.size());
                }
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
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
                            entity.setLikeCount(jsonObject.getInt("LikeCount"));
                            entity.setCommentCount(jsonObject.getInt("CommentCount"));
                            if (jsonObject.has("city") && !jsonObject.isNull("city")){
                                entity.setCity(jsonObject.getString("city"));
                            }
                            entityList.add(entity);
                            adapter.notifyItemInserted(entityList.size());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    pageid ++;
                    adapter.setLoaded();
                }
            }

            @Override
            public void onError() {
                if (pageid == 1 && feedResultType.equals("1")) {
                    hideDialog();
                }
            }

            @Override
            public void noNetwork() {
                if (pageid == 1 && feedResultType.equals("1")) {
                    hideDialog();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AgroConnect.agroEventBus.unregister(this);
    }
}
