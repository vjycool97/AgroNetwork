package agroconnectapp.agroconnect.in.agroconnect.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.model.Pair;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;

/**
 * Created by niteshtarani on 15/01/16.
 */
public class AutoCompleteTextViewAdapter extends BaseAdapter implements Filterable {

    private SharedPreferences sharedPreferences;
    private Context mContext;
    private int type;
    private List<Pair> resultList = new ArrayList<Pair>();
    private List<Pair> intermediateList = new ArrayList<Pair>();
    private JSONArray allCitiesArray;
    private JSONArray allCommoditiesArray;

    public AutoCompleteTextViewAdapter(Context context, int type) {
        this.mContext = context;
        this.type = type;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        try {
            if(type == Constants.TYPE_COMMODITY)
                allCommoditiesArray = new JSONArray(sharedPreferences.getString(Constants.allCommodities,"[]"));

            if(type == Constants.TYPE_CITY)
                allCitiesArray = new JSONArray(sharedPreferences.getString(Constants.allCities,"[]"));

            if(type == Constants.TYPE_SEARCH) {
                allCommoditiesArray = new JSONArray(sharedPreferences.getString(Constants.allCommodities,"[]"));
                allCitiesArray = new JSONArray(sharedPreferences.getString(Constants.allCities,"[]"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index).getName();
    }

    @Override
    public long getItemId(int position) {
        try {
            return Long.parseLong(resultList.get(position).getId());
        } catch (Exception e) {
            return -1l;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.view_spinner_item, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.text1)).setText(getItem(position));
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<Pair> list = fetchAutocompleteData(constraint.toString());
                    // Assign the data to the FilterResults
                    filterResults.values = list;
                    filterResults.count = list.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    resultList.clear();
                    resultList.addAll((List<Pair>) results.values);
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }

    private List<Pair> fetchAutocompleteData(String query) {

        try {
            intermediateList.clear();
            if(type == Constants.TYPE_COMMODITY) {
                for(int i=0; i<allCommoditiesArray.length(); i++) {
                    JSONObject obj = allCommoditiesArray.getJSONObject(i);
                    String commodityName = obj.optString("Name");
                    String id = obj.optString("CommodityId");
                    if(commodityName.toLowerCase().contains(query.toLowerCase())) {
                        intermediateList.add(new Pair(id,commodityName));
                    }
                }
            }

            if(type == Constants.TYPE_CITY) {
                for(int i=0; i<allCitiesArray.length(); i++) {
                    JSONObject obj = allCitiesArray.getJSONObject(i);
                    String cityName = obj.optString("Name");
                    String id = obj.optString("CityId");
                    if(cityName.toLowerCase().contains(query.toLowerCase())) {
                        intermediateList.add(new Pair(id,cityName));
                    }
                }
            }

            if(type == Constants.TYPE_SEARCH) {

                for(int i=0; i<allCommoditiesArray.length(); i++) {
                    JSONObject obj = allCommoditiesArray.getJSONObject(i);
                    String commodityName = obj.optString("Name");
                    String commodityid = obj.optString("CommodityId");
                    if(commodityName.toLowerCase().contains(query.toLowerCase())) {
                        intermediateList.add(new Pair(commodityid,commodityName));
                    }
                }

                for(int j=0; j<allCitiesArray.length(); j++) {
                    JSONObject obj = allCitiesArray.getJSONObject(j);
                    String cityName = obj.optString("Name");
                    String cityid = obj.optString("CityId");
                    if(cityName.toLowerCase().contains(query.toLowerCase())) {
                        intermediateList.add(new Pair(cityid,cityName));
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return intermediateList;
    }
}