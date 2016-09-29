package agroconnectapp.agroconnect.in.agroconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.entities.CommodityEntity;

/**
 * Created by sumanta on 9/6/16.
 */
public class CommodityAdapter extends BaseAdapter {

    private Context context;
    private List<CommodityEntity> commodityEntityList;

    public CommodityAdapter(Context context, List<CommodityEntity> commodityEntityList) {
        this.context = context;
        this.commodityEntityList = commodityEntityList;
    }
    @Override
    public int getCount() {
        return commodityEntityList.size();
    }

    @Override
    public CommodityEntity getItem(int position) {
        return commodityEntityList.get(position);
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
        CommodityEntity entity = getItem(position);
        if(entity != null) {
            holder.cityComTV.setText(entity.getLocalName());
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView cityComTV;
    }
}