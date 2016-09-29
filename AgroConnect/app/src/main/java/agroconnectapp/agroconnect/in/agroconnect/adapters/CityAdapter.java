package agroconnectapp.agroconnect.in.agroconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.entities.CityEntity;

/**
 * Created by sumanta on 9/6/16.
 */
public class CityAdapter extends BaseAdapter {

    private Context context;
    private List<CityEntity> cityEntityList;
    public CityAdapter(Context context, List<CityEntity> cityEntityList) {
        this.context = context;
        this.cityEntityList = cityEntityList;
    }
    @Override
    public int getCount() {
        return cityEntityList.size();
    }

    @Override
    public CityEntity getItem(int position) {
        return cityEntityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.city_com_row, null);
            holder.cityComTV = (TextView) convertView.findViewById(R.id.cityComTV);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        CityEntity entity = getItem(position);
        if(entity != null) {
            holder.cityComTV.setText(entity.getLocalName());
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView cityComTV;
    }
}
