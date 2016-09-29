package agroconnectapp.agroconnect.in.agroconnect.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;
import agroconnectapp.agroconnect.in.agroconnect.utility.DatabaseHandler;
import agroconnectapp.agroconnect.in.agroconnect.model.FeedData;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.adapters.FeedAdapter;

public class ShortlistFragment extends Fragment{

    private Activity mContext;
    private DatabaseHandler db;
    private List<FeedData> feedList;
    private RecyclerView feedListView;
    private ProgressBar progressBar;
    private FeedAdapter feedAdapter;

    public ShortlistFragment() {
        // Required empty public constructor
    }

    public static ShortlistFragment newInstance() {
        ShortlistFragment fragment = new ShortlistFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        db = new DatabaseHandler(mContext,null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);
        feedListView = (RecyclerView) rootView.findViewById(R.id.feed_list_view);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        feedListView.setLayoutManager(layoutManager);
        progressBar.setVisibility(View.VISIBLE);
        new FetchData().execute();

        return rootView;
    }

    public void refresh() {
        new FetchData().execute();
    }

    private class FetchData extends AsyncTask<Void,Void,Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            feedList = db.getAllItems();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result) {
                progressBar.setVisibility(View.GONE);
                feedAdapter = new FeedAdapter(mContext,feedList, Constants.TYPE_SHORTLIST,db);
                feedListView.setAdapter(feedAdapter);
            }
        }
    }
}
