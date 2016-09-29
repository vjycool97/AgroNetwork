package agroconnectapp.agroconnect.in.agroconnect.fragments;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import agroconnectapp.agroconnect.in.agroconnect.PickerDialog;
import agroconnectapp.agroconnect.in.agroconnect.databases.PrefDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;
import agroconnectapp.agroconnect.in.agroconnect.utility.DatabaseHandler;
import agroconnectapp.agroconnect.in.agroconnect.model.FeedData;
import agroconnectapp.agroconnect.in.agroconnect.utility.HttpRequest;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.utility.Utils;
import agroconnectapp.agroconnect.in.agroconnect.adapters.FeedAdapter;

/**
 * Created by nitin.gupta on 12/6/2015.
 */
public class FeedFragment extends AgroFragment{

    private RelativeLayout mainContainer;
    private List<FeedData> feedListMain;
    private List<FeedData> feedList;
    private RecyclerView feedListView;
    private FeedAdapter feedAdapter;
    private Activity mContext;
    private ProgressBar progressBar;
    private boolean loading = false;
    private RelativeLayout errorLayout;
    private TextView errorText;
    private Button tryAgainBtn;
    private int agentId;
    private DatabaseHandler db;
//    private SharedPreferences sharedPreferences;
    private String queryParam="";

    public FeedFragment() {
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mContext = getActivity();
//        if (getArguments() != null) {
//            agentId = getArguments().getInt(Constants.agentIdKey);
//        }
//        db = new DatabaseHandler(mContext,null);
////        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        try {

            mContext = getActivity();
            if (getArguments() != null) {
                agentId = getArguments().getInt(Constants.agentIdKey);
            }
            db = new DatabaseHandler(mContext, null);

            mainContainer = (RelativeLayout) rootView.findViewById(R.id.root_feed_layout);
            errorLayout = (RelativeLayout) rootView.findViewById(R.id.error_layout);
            errorText = (TextView) rootView.findViewById(R.id.error_tv);
            tryAgainBtn = (Button) rootView.findViewById(R.id.try_again_btn);
            feedListView = (RecyclerView) rootView.findViewById(R.id.feed_list_view);
            progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            feedListView.setLayoutManager(layoutManager);
            feedListView.addOnScrollListener(new EndlessScrollListener(layoutManager));
            feedListMain = new ArrayList<>();
            feedList = new ArrayList<>();
            if (Utils.isNetworkAvailable(mContext)) {
                getFeedData(1, queryParam);
            } else {
                errorText.setText(mContext.getResources().getString(R.string.error_msg_internet));
                errorLayout.setVisibility(View.VISIBLE);
            }

            tryAgainBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utils.isNetworkAvailable(mContext)) {
                        getFeedData(1, queryParam);
                    } else {
                        errorText.setText(mContext.getResources().getString(R.string.error_msg_internet));
                        errorLayout.setVisibility(View.VISIBLE);
                    }
                }
            });
            feedAdapter = new FeedAdapter(mContext, feedListMain, Constants.TYPE_FEED, db);
            feedListView.setAdapter(feedAdapter);

        }catch (Exception e){
            e.printStackTrace();
        }
        return rootView;
    }

    public void getFeedData(int pageId, String query){

        try {

            queryParam = query;

            try {
                if (pageId == 1) {
                    progressBar.setVisibility(View.VISIBLE);
                    feedListMain.clear();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            errorLayout.setVisibility(View.GONE);
            try {
                HttpRequest request = new HttpRequest(true, TOKEN, PrefDataHandler.getInstance().getSharedPref().getString(TOKEN, ""));
                Callback callback = new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                errorText.setText(mContext.getResources().getString(R.string.error_msg_unknown));
                                errorLayout.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (response.isSuccessful()) {
                            try {
                                JSONArray feedArray = new JSONArray(response.body().string());
                                if (feedArray != null) {
                                    parseData(feedArray);
                                }
                                mContext.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Crashlytics.logException(new Throwable("Error parsing feed - " + e.toString()));
                            }
                        } else {
                            mContext.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    errorText.setText(mContext.getResources().getString(R.string.error_msg_unknown));
                                    errorLayout.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                };

                String dataUrl = Constants.feedUrl + "?pageId=" + pageId;
                if (agentId != -1) {
                    dataUrl = dataUrl + "&agentId=" + agentId;
                }
                if (!queryParam.isEmpty())
                    dataUrl = dataUrl + "&match=" + queryParam;

                request.doGetRequest(dataUrl, callback);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseData(JSONArray feedArray){
        try {
            removeProgressBar();

            feedList.clear();
            if (feedArray.length() == 0) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        feedAdapter.notifyDataSetChanged();
                    }
                });
                return;
            }
            for (int count = 0; count < feedArray.length(); count++) {
                JSONObject feedObj = null;

                feedObj = feedArray.optJSONObject(count);
                if (feedObj != null) {
                    FeedData fd = new FeedData();
                    fd.setListingId(feedObj.optInt("ListingId"));
                    fd.setAgentId(feedObj.optInt("AgentId"));
                    fd.setAgentName(feedObj.optString("AgentName"));
                    String[] agentTypeArray = getResources().getStringArray(R.array.agent_types);
                    fd.setAgentType(agentTypeArray[feedObj.optInt("AgentType")]);
                    fd.setLastUpdated(Utils.getDateDifference(feedObj.optString("LastUpdated")));
                    if (feedObj.optString("Quantity") != null && !feedObj.optString("Quantity").equalsIgnoreCase("null") && !feedObj.optString("Quantity").isEmpty())
                        fd.setQuantity(feedObj.optString("Quantity"));
                    else
                        fd.setQuantity(getString(R.string.not_available));
                    if (feedObj.optString("Price") != null && !feedObj.optString("Price").equalsIgnoreCase("null") && !feedObj.optString("Price").isEmpty())
                        fd.setPrice(feedObj.optString("Price"));
                    else
                        fd.setPrice(getString(R.string.not_available));
                    fd.setIsSell(feedObj.optBoolean("IsSell"));
                    fd.setCommodityId(feedObj.optInt("CommodityId"));
                    fd.setCommodityName(feedObj.optString("CommodityName"));
                    fd.setVariety(feedObj.optString("Variety"));
                    if (feedObj.optString("Description") != null && !feedObj.optString("Description").equalsIgnoreCase("null") && !feedObj.optString("Description").isEmpty())
                        fd.setDescription(feedObj.optString("Description"));
                    else
                        fd.setDescription(getString(R.string.not_available));
                    if (feedObj.optString("Location") != null && !feedObj.optString("Location").equalsIgnoreCase("null") && !feedObj.optString("Location").isEmpty())
                        fd.setLocation(feedObj.optString("Location"));
                    else
                        fd.setLocation(getString(R.string.not_available));
                    fd.setType(Constants.TYPE_FEED);

                    feedList.add(fd);
                }
            }
            feedListMain.addAll(feedList);
            loading = false;
            addProgressBar();
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    feedAdapter.notifyDataSetChanged();
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class EndlessScrollListener extends RecyclerView.OnScrollListener {

        private int pastItems, visibleItemCount,totalItemCount;
        private LinearLayoutManager mLayoutManager;
        private int pageId = 2;

        public EndlessScrollListener(LinearLayoutManager mLayoutManager) {
            this.mLayoutManager = mLayoutManager;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            totalItemCount = mLayoutManager.getItemCount();
            visibleItemCount = mLayoutManager.getChildCount();
            pastItems = mLayoutManager.findFirstVisibleItemPosition();
            if(!loading) {
                if (pastItems + visibleItemCount >= totalItemCount) {
                    getFeedData(pageId,queryParam);
                    loading = true;
                    pageId++;

                    if(pageId == 3 && agentId == -1) {
                        Utils.addInfo(mContext, new PickerDialog(mContext), mainContainer);
                    }
                }
            }
        }
    }

    private void addProgressBar(){
        if(feedListMain != null && !feedListMain.isEmpty()) {
            FeedData fd = new FeedData();
            fd.setType(Constants.TYPE_PROGRESS_BAR);
            feedListMain.add(fd);
        }
    }

    private void removeProgressBar(){
        if(feedListMain != null && !feedListMain.isEmpty()) {
            feedListMain.remove(feedListMain.size()-1);
        }
    }

}
