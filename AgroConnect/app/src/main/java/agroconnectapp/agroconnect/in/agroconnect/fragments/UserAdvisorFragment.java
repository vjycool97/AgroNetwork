package agroconnectapp.agroconnect.in.agroconnect.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.Request;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.adapters.UserAdvisorAdapter;
import agroconnectapp.agroconnect.in.agroconnect.components.ServerCommunication;
import agroconnectapp.agroconnect.in.agroconnect.entities.AdvisorEntity;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;

/**
 * Created by sumanta on 14/7/16.
 */
public class UserAdvisorFragment extends AgroFragment {

    private RecyclerView recyclerView;
    private int pageid = 1;
    private ArrayList<AdvisorEntity> entityList = new ArrayList<>();
    private UserAdvisorAdapter adapter;
    String advisoryPostResultType = "1";
    int agent_id = -1;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_advisor, container, false);

        advisoryPostResultType = getArguments().getString("advisoryPostResultType");
        if(advisoryPostResultType.equals("3")) {
            agent_id = getArguments().getInt(Constants.agentIdKey);
        }

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new UserAdvisorAdapter(getActivity(), entityList, recyclerView);
        recyclerView.setAdapter(adapter);

        adapter.setOnLoadMoreListener(new UserAdvisorAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                entityList.add(null); //add null , so the adapter will check view_type and show progress bar at bottom
                adapter.notifyItemInserted(entityList.size() - 1);
                getDataFromServer(advisoryPostResultType);
            }
        });
        getDataFromServer(advisoryPostResultType);
        return view;
    }

    private void getDataFromServer(final String advisoryPostResultType) {
        if (pageid == 1 && advisoryPostResultType.equals("1") || advisoryPostResultType.equals("4")) {
            showDialog("Loading data...");
        }
        ArrayMap<String, String> paramMap = new ArrayMap<>();
        paramMap.put("pageId", String.valueOf(pageid));
        paramMap.put("advisoryPostResultType", advisoryPostResultType);
        if(advisoryPostResultType.equals("3")) {
            paramMap.put("postedByAgentid", String.valueOf(agent_id));
        }
        ServerCommunication.INSTANCE.getServerData(getActivity(), true, true, Request.Method.GET, ADVISOR_DATA_URI, ADVISOR_DATA_URI, paramMap, new ServerCommunication.OkuTaskListener() {
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
                        List<AdvisorEntity> dataList = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<AdvisorEntity>>(){}.getType());
                        entityList.addAll(dataList);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    pageid ++;
                    adapter.setLoaded();
                }
            }

            @Override
            public void onError() {
                if (pageid == 1 && advisoryPostResultType.equals("1") || advisoryPostResultType.equals("4")) {
                    hideDialog();
                }
            }

            @Override
            public void noNetwork() {
                if (pageid == 1 && advisoryPostResultType.equals("1") || advisoryPostResultType.equals("4")) {
                    hideDialog();
                }
            }
        });
    }
}
