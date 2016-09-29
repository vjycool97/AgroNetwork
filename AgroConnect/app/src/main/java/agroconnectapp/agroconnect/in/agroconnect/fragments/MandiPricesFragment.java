package agroconnectapp.agroconnect.in.agroconnect.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import agroconnectapp.agroconnect.in.agroconnect.AgroConnect;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.activities.AgroActivity;
import agroconnectapp.agroconnect.in.agroconnect.activities.FragmentWrapperActivity;
import agroconnectapp.agroconnect.in.agroconnect.activities.NewsFeedActivity;
import agroconnectapp.agroconnect.in.agroconnect.databases.CityDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.databases.CommodityDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.databases.PrefDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.entities.CityEntity;
import agroconnectapp.agroconnect.in.agroconnect.entities.CommodityEntity;
import agroconnectapp.agroconnect.in.agroconnect.events.CityEvent;
import agroconnectapp.agroconnect.in.agroconnect.events.CommodityEvent;
import agroconnectapp.agroconnect.in.agroconnect.fragments.AgroFragment;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.model.City;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.model.Commodity;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;
import agroconnectapp.agroconnect.in.agroconnect.utility.HttpRequest;
import agroconnectapp.agroconnect.in.agroconnect.utility.Utils;
import dmax.dialog.SpotsDialog;

public class MandiPricesFragment extends AgroFragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private TableLayout tableLayout;
    private ProgressBar progressBar;
    private TextView commodityBtn;
    private TextView mandiBtn;
    private TextView dateBtn;
    private Calendar calendar;
    private int year, month, day;
    private DatePickerDialog datePickerDialog;
    private String selectedCommodityName, selectedCommodityId;
    private String selectedCityName, selectedCityId;
  //  private CustomListDialog commodityDialog;
  //  private CustomListDialog cityDialog;
    private String currentDate;
    private SpotsDialog spotDialog = null;
    private NewsFeedActivity newsFeedActivity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        try {
            AgroConnect.agroEventBus.register(this);
        }catch (Exception e){
            e.printStackTrace();
            Crashlytics.log("Mandi Price Fragment");
        }
        newsFeedActivity = (NewsFeedActivity) getActivity();
        View view = inflater.inflate(R.layout.activity_mandi_prices, container, false);
      //  Toolbar toolbar = (Toolbar) view.findViewById(R.id.action_bar);
//        newsFeedActivity.setSupportActionBar(toolbar);


        commodityBtn = (TextView)view.findViewById(R.id.commodity_btn);
        commodityBtn.setOnClickListener(this);
        mandiBtn = (TextView)view.findViewById(R.id.mandi_btn);
        mandiBtn.setOnClickListener(this);
        dateBtn = (TextView)view.findViewById(R.id.date_btn);
        dateBtn.setOnClickListener(this);
        progressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
        tableLayout = (TableLayout)view.findViewById(R.id.table_container);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog( getActivity(),R.style.DateDialogTheme, this, year, month, day);
        currentDate = year + "-" + (month + 1) + "-" + day;
        dateBtn.setText(currentDate);
        spotDialog = new SpotsDialog(getActivity());
  //      commodityDialog = new CustomListDialog(getActivity(), Constants.TYPE_COMMODITY);
    //    cityDialog = new CustomListDialog(getActivity(),Constants.TYPE_CITY);



        if(newsFeedActivity.getIntent().hasExtra(Constants.cityId) && newsFeedActivity.getIntent().hasExtra(Constants.commodityId)) {
            selectedCityId = newsFeedActivity.getIntent().getStringExtra(Constants.cityId);
            selectedCommodityId = newsFeedActivity.getIntent().getStringExtra(Constants.commodityId);
        } else {
            selectedCityId = PrefDataHandler.getInstance().getSharedPref().getString(LAST_CITY_ID, "");
            selectedCommodityId = PrefDataHandler.getInstance().getSharedPref().getString(LAST_COMMODITY_ID, "");
        }
        if (selectedCityId.isEmpty() || selectedCommodityId.isEmpty()) {
            selectedCityId = String.valueOf(PrefDataHandler.getInstance().getSharedPref().getInt(CITYID, 0));
            selectedCommodityId = String.valueOf(PrefDataHandler.getInstance().getSharedPref().getInt(COMMODITYID, 0));
        }
        CityEntity city = CityDataHandler.getCityById(newsFeedActivity, Integer.valueOf(selectedCityId));
        CommodityEntity commodity = CommodityDataHandler.getCommodityById(newsFeedActivity, Integer.valueOf(selectedCommodityId));

        if (city != null && commodity != null) {
            //Fix this
            commodityBtn.setText(commodity.getLocalName());
            mandiBtn.setText(city.getLocalName());
            getMandiPrices(selectedCommodityId, selectedCityId, currentDate);
        }

        return view;
    }






//    private void setToolbar() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle(getString(R.string.mandi_prices));
//        toolbar.setTitleTextColor(getResources().getColor(R.color.color_white));
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//    }

//    @Subscribe
//    public void cityEvent(CityEvent event) {
//        CityEntity cityEntity = event.getEntity();
//        if (cityEntity != null) {
//            mandiBtn.setText(cityEntity.getLocalName());
//            selectedCityId = String.valueOf(cityEntity.getId());
//            PrefDataHandler.getInstance().getEditor().putString(LAST_CITY_ID, selectedCityId).apply();
//            if (!selectedCommodityId.isEmpty() && !selectedCityId.isEmpty())
//                getMandiPrices(selectedCommodityId, selectedCityId, currentDate);
//        }
//    }

//    @Subscribe
//    public void CommodityEvent(CommodityEvent event) {
//        CommodityEntity commodityEntity = event.getEntity();
//        if (commodityEntity != null) {
//            commodityBtn.setText(commodityEntity.getLocalName());
//            selectedCommodityId = String.valueOf(commodityEntity.getId());
//            PrefDataHandler.getInstance().getEditor().putString(LAST_COMMODITY_ID, selectedCommodityId).apply();
//            if (!selectedCommodityId.isEmpty() && !selectedCityId.isEmpty())
//                getMandiPrices(selectedCommodityId, selectedCityId, currentDate);
//        }
//    }

    Call mMamdiPricesGetCall = null;

    private void getMandiPrices(String commodityId, String cityId, String arrivalDate) {
       // spotDialog.show();

        if (!Utils.isNetworkAvailable(newsFeedActivity)) {
            Toast.makeText(newsFeedActivity, R.string.error_msg_internet, Toast.LENGTH_LONG).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        HttpRequest request = new HttpRequest(true, TOKEN, PrefDataHandler.getInstance().getSharedPref().getString(TOKEN, ""));
        Callback callback = new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                spotDialog.dismiss();
                mMamdiPricesGetCall = null;

            }

            @Override
            public void onResponse(Response response) throws IOException {
                spotDialog.dismiss();
                mMamdiPricesGetCall = null;

                if (response.isSuccessful()) {

                    try {
                        final JSONObject responseObj = new JSONObject(response.body().string());
                        newsFeedActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                parseData(responseObj);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Crashlytics.logException(new Throwable("Exception parsing mandi prices response - " + e.toString()));
                    }
                } else {
                    newsFeedActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                    Crashlytics.logException(new Throwable("Unsuccessful response mandi prices - " + response.code()));
                }
            }
        };

        final String dataUrl = Constants.mandiPricesUrl + "commodityid=" + commodityId + "&cityId=" + cityId + "&arrivaldate=" + arrivalDate;
        try {
            mMamdiPricesGetCall = request.doGetRequest(dataUrl, callback);
        } catch (Exception e) {
            Crashlytics.logException(new Throwable("Exception in fetching mandi prices - " + e.toString()));
        }
    }

    private void parseData(JSONObject responseObj) {

        tableLayout.removeAllViews();
        if (responseObj.length() > 0) {
            List<String> keyList = new ArrayList<>();
            Iterator<?> keys = responseObj.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                keyList.add(key);
            }
            Collections.sort(keyList);
            for (String key_i : keyList) {
                String key = key_i;
                JSONArray valueArray = responseObj.optJSONArray(key);

                if (valueArray == null)
                    return;

                TableLayout.LayoutParams titleLp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                titleLp.topMargin = getResources().getDimensionPixelSize(R.dimen.d2);
                TextView title = new TextView(newsFeedActivity);
                title.setText(key);
                title.setTextSize(20);
                title.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                title.setLayoutParams(titleLp);
                tableLayout.addView(title);

                TableRow headRow = new TableRow(newsFeedActivity);
                headRow.setBackgroundColor(getResources().getColor(R.color.gray_background));
                headRow.setPadding(getResources().getDimensionPixelSize(R.dimen.d2), getResources().getDimensionPixelSize(R.dimen.d4), getResources().getDimensionPixelSize(R.dimen.d2), getResources().getDimensionPixelSize(R.dimen.d4));
                TableLayout.LayoutParams tablelayoutLP = new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                tablelayoutLP.topMargin = getResources().getDimensionPixelSize(R.dimen.d8);
                headRow.setLayoutParams(tablelayoutLP);

                TableRow.LayoutParams headTvLp = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView mandiNameHead = new TextView(newsFeedActivity);
                TextView minPriceHead = new TextView(newsFeedActivity);
                TextView maxPriceHead = new TextView(newsFeedActivity);
                TextView modalPriceHead = new TextView(newsFeedActivity);

                mandiNameHead.setLayoutParams(headTvLp);
                minPriceHead.setLayoutParams(headTvLp);
                maxPriceHead.setLayoutParams(headTvLp);
                modalPriceHead.setLayoutParams(headTvLp);

                mandiNameHead.setText(getString(R.string.mandi));
                mandiNameHead.setTextSize(14);
                mandiNameHead.setTextColor(getResources().getColor(android.R.color.black));
                minPriceHead.setText(getString(R.string.min_price));
                minPriceHead.setTextSize(14);
                minPriceHead.setTextColor(getResources().getColor(android.R.color.black));
                maxPriceHead.setText(getString(R.string.max_price));
                maxPriceHead.setTextSize(14);
                maxPriceHead.setTextColor(getResources().getColor(android.R.color.black));
                modalPriceHead.setText(getString(R.string.modal_price));
                modalPriceHead.setTextSize(14);
                modalPriceHead.setTextColor(getResources().getColor(android.R.color.black));

                headRow.addView(mandiNameHead);
                headRow.addView(minPriceHead);
                headRow.addView(maxPriceHead);
                headRow.addView(modalPriceHead);

                tableLayout.addView(headRow);

                for (int i = 0; i < valueArray.length(); i++) {

                    JSONObject obj = valueArray.optJSONObject(i);
                    if (obj != null) {
                        TableRow row = new TableRow(newsFeedActivity);
                        row.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                        row.setPadding(getResources().getDimensionPixelSize(R.dimen.d2), getResources().getDimensionPixelSize(R.dimen.d4), getResources().getDimensionPixelSize(R.dimen.d2), getResources().getDimensionPixelSize(R.dimen.d4));

                        TableRow.LayoutParams textLP = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        TextView mandiName = new TextView(newsFeedActivity);
                        TextView minPrice = new TextView(newsFeedActivity);
                        TextView maxPrice = new TextView(newsFeedActivity);
                        TextView modalPrice = new TextView(newsFeedActivity);

                        mandiName.setLayoutParams(new TableRow.LayoutParams(getResources().getDimensionPixelSize(R.dimen.d120), ViewGroup.LayoutParams.WRAP_CONTENT));
                        mandiName.setMaxLines(1);
                        mandiName.setEllipsize(TextUtils.TruncateAt.END);
                        minPrice.setLayoutParams(textLP);
                        minPrice.setGravity(Gravity.CENTER);
                        maxPrice.setLayoutParams(textLP);
                        maxPrice.setGravity(Gravity.CENTER);
                        modalPrice.setLayoutParams(textLP);
                        modalPrice.setGravity(Gravity.CENTER);

                        mandiName.setText(obj.optString("Mandi"));
                        mandiName.setTextSize(14);
                        mandiName.setTextColor(getResources().getColor(R.color.dialog_background));

                        if (obj.optInt("MinPrice") == 0){
                            minPrice.setText("-");
                            minPrice.setTextSize(14);
                        }else {
                            minPrice.setText(String.valueOf(obj.optInt("MinPrice")));
                            minPrice.setTextSize(14);
                        }
                        minPrice.setTextColor(getResources().getColor(R.color.dialog_background));

                        if (obj.optInt("MaxPrice") == 0){
                            maxPrice.setText("-");
                            maxPrice.setTextSize(14);
                        }else {
                            maxPrice.setText(String.valueOf(obj.optInt("MaxPrice")));
                            maxPrice.setTextSize(14);
                        }
                        maxPrice.setTextColor(getResources().getColor(R.color.dialog_background));

                        if (obj.optInt("ModalPrice") == 0){
                            modalPrice.setText("-");
                            modalPrice.setTextSize(14);
                        }else {
                            modalPrice.setText(String.valueOf(obj.optInt("ModalPrice")));
                            modalPrice.setTextSize(14);
                        }
                        modalPrice.setTextColor(getResources().getColor(R.color.dialog_background));

                        row.addView(mandiName);
                        row.addView(minPrice);
                        row.addView(maxPrice);
                        row.addView(modalPrice);

                        tableLayout.addView(row);
                    }
                }
            }

            TextView note = new TextView(newsFeedActivity);
            note.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
            note.setPadding(getResources().getDimensionPixelSize(R.dimen.d16), getResources().getDimensionPixelSize(R.dimen.d16), 0, getResources().getDimensionPixelSize(R.dimen.d10));
            note.setText(getString(R.string.mandi_prices_note));
            note.setTextSize(16);
            note.setTextColor(Color.BLACK);
            tableLayout.addView(note);
        } else {
            TextView emptyText = new TextView(newsFeedActivity);
            emptyText.setTextSize(24);
            emptyText.setTextColor(getResources().getColor(R.color.dialog_background));
            emptyText.setText(getString(R.string.data_unavailable));
            TableLayout.LayoutParams emptyTextLp = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
            emptyText.setGravity(Gravity.CENTER);
            emptyTextLp.gravity = Gravity.CENTER;
            emptyTextLp.leftMargin = getResources().getDimensionPixelSize(R.dimen.d20);
            emptyTextLp.rightMargin = getResources().getDimensionPixelSize(R.dimen.d20);
            emptyText.setLayoutParams(emptyTextLp);
            tableLayout.addView(emptyText);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commodity_btn: {
                Intent intent = new Intent(newsFeedActivity, FragmentWrapperActivity.class);
                intent.putExtra("className", "agroconnectapp.agroconnect.in.agroconnect.fragments.CityAndCommodityFragment");
                intent.putExtra("isCity", false);
                startActivity(intent);
                newsFeedActivity.overridePendingTransition(R.anim.flipin, R.anim.flipout);
 //               commodityDialog.show();
                break;
            }
            case R.id.mandi_btn: {
                Intent intent = new Intent(newsFeedActivity, FragmentWrapperActivity.class);
                intent.putExtra("className", "agroconnectapp.agroconnect.in.agroconnect.fragments.CityAndCommodityFragment");
                intent.putExtra("isCity", true);
                startActivity(intent);
                newsFeedActivity.overridePendingTransition(R.anim.flipin, R.anim.flipout);
 //               cityDialog.show();
                break;
            }
            case R.id.date_btn:
                datePickerDialog.show();
                break;
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_whatsapp, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            newsFeedActivity.onBackPressed();
        } else if (id == R.id.whatsappId) {
            Utils.takeScreenShotAndShare(newsFeedActivity, tableLayout);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        currentDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
        dateBtn.setText(currentDate);
        if (!selectedCommodityId.isEmpty() && !selectedCityId.isEmpty())
            getMandiPrices(selectedCommodityId, selectedCityId, currentDate);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AgroConnect.agroEventBus.unregister(this);
    }

}
