package agroconnectapp.agroconnect.in.agroconnect.fragments;

import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import java.util.List;
import agroconnectapp.agroconnect.in.agroconnect.AgroConnect;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.adapters.CityAdapter;
import agroconnectapp.agroconnect.in.agroconnect.adapters.CommodityAdapter;
import agroconnectapp.agroconnect.in.agroconnect.databases.CityDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.databases.CommodityDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.entities.CityEntity;
import agroconnectapp.agroconnect.in.agroconnect.entities.CommodityEntity;
import agroconnectapp.agroconnect.in.agroconnect.events.CityEvent;
import agroconnectapp.agroconnect.in.agroconnect.events.CommodityEvent;

@Keep
public class CityAndCommodityFragment extends AgroFragment implements View.OnClickListener {

    private CityAdapter cityAdapter;
    private CommodityAdapter comAdapter;
    private ListView listView;
    private boolean isCity;
    private List<CityEntity> cityList;
    private List<CommodityEntity> commodityList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city_com, container, false);
        isCity = getArguments().getBoolean("isCity", false);
        listView = (ListView) view.findViewById(R.id.cityComLV);
        listView.setOnItemClickListener(itemClick);
        if(isCity) {
            cityList = CityDataHandler.getAllCityEntityFromDB(getActivity());
            cityAdapter = new CityAdapter(getActivity(), cityList);
            listView.setAdapter(cityAdapter);
        } else {
            commodityList = CommodityDataHandler.getAllCommodityEntityFromDB(getActivity());
            comAdapter = new CommodityAdapter(getActivity(), commodityList);
            listView.setAdapter(comAdapter);
        }

        ((EditText) view.findViewById(R.id.searchET)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(isCity) {
                    cityList.clear();
                    List<CityEntity> tempList = CityDataHandler.getAllMatchingCity(getActivity(), s.toString().trim());
                    cityList.addAll(tempList);
                    cityAdapter.notifyDataSetChanged();
                }
                else {
                    commodityList.clear();
                    List<CommodityEntity> tempList = CommodityDataHandler.getAllMatchingCommodity(getActivity(), s.toString().trim());
                    commodityList.addAll(tempList);
                    comAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        return view;
    }

    private AdapterView.OnItemClickListener itemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(isCity) {
                CityEntity entity = (CityEntity)parent.getItemAtPosition(position);
                if(entity!=null)
                    AgroConnect.agroEventBus.post(new CityEvent(entity));
            } else {
                CommodityEntity entity = (CommodityEntity)parent.getItemAtPosition(position);
                if(entity!=null)
                    AgroConnect.agroEventBus.post(new CommodityEvent(entity));
            }
            getActivity().onBackPressed();
        }
    };

    @Override
    public void onClick(View v) {
    }
}