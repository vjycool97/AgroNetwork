package agroconnectapp.agroconnect.in.agroconnect.uicomponents;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.Request;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import java.util.Locale;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.activities.SplashActivity;
import agroconnectapp.agroconnect.in.agroconnect.components.ServerCommunication;
import agroconnectapp.agroconnect.in.agroconnect.databases.CityDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.databases.CommodityDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.databases.PrefDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.entities.CityEntity;
import agroconnectapp.agroconnect.in.agroconnect.entities.CommodityEntity;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.network.TRequestDelegate;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.network.TService;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.response.OKResponse;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.response.TResponse;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;
import agroconnectapp.agroconnect.in.agroconnect.utility.KeyIDS;

/**
 * Created by sumanta on 4/6/16.
 */
public class LanguageDialog extends Dialog {

    private Context context;
    private ProgressDialog dialog;
    private TextView englishTV, hindiTV;
    private String saveLocale, selectedLocale="";

    public LanguageDialog(Context context) {
        super(context);
        this.context = context;
        setCancelable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.language_dialog);
        englishTV = (TextView)findViewById(R.id.englishTV);
        hindiTV = (TextView)findViewById(R.id.hindiTV);
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        saveLocale = PrefDataHandler.getInstance().getSharedPref().getString(KeyIDS.SELECTED_LANGUAGE, "");
        if("en".equals(saveLocale)) {
            selectedLocale = "en";
            englishTV.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.green_tick, 0, 0, 0);
        }
        else if ("hi".equals(saveLocale)) {
            selectedLocale = "hi";
            hindiTV.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.green_tick, 0, 0, 0);
        }
        findViewById(R.id.englishLayout).setOnClickListener(clickListener);
        findViewById(R.id.hindiLayout).setOnClickListener(clickListener);
        findViewById(R.id.cancelBtn).setOnClickListener(clickListener);
        findViewById(R.id.setBtn).setOnClickListener(clickListener);
    }

    protected void showDialog(String message) {
        if (dialog == null) {
            dialog = new ProgressDialog(context);
        }
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.show();
    }
    protected void hideDialog() {
        if (dialog!=null && dialog.isShowing())
            dialog.dismiss();
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.englishLayout : {
                    selectedLocale = "en";
                    ((TextView)findViewById(R.id.headerTV)).setText("Choose Language");
                    ((Button)findViewById(R.id.cancelBtn)).setText("Cancel");
                    ((Button)findViewById(R.id.setBtn)).setText("Set");
                    hindiTV.setText("Hindi");
                    englishTV.setText("English");
                    englishTV.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.green_tick, 0, 0, 0);
                    hindiTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    break;
                }
                case R.id.hindiLayout : {
                    selectedLocale = "hi";
                    ((TextView)findViewById(R.id.headerTV)).setText("भाषा चुनें");
                    ((Button)findViewById(R.id.cancelBtn)).setText("रद्द करना");
                    ((Button)findViewById(R.id.setBtn)).setText("सेट");
                    englishTV.setText("अंग्रेज़ी");
                    hindiTV.setText("हिंदी");
                    hindiTV.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.green_tick, 0, 0, 0);
                    englishTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    break;
                }
                case R.id.cancelBtn : {
                    cancel();
                    break;
                }
                case R.id.setBtn : {
                    PrefDataHandler.getInstance().getEditor().putBoolean(KeyIDS.IS_LANGUAGE_SET, true).apply();
                    if(!saveLocale.equals(selectedLocale)) {
                        PrefDataHandler.getInstance().getEditor().putString(KeyIDS.SELECTED_LANGUAGE, selectedLocale).apply();
                        context.getResources().getConfiguration().locale = new Locale(selectedLocale);
                        context.getResources().updateConfiguration(context.getResources().getConfiguration(), context.getResources().getDisplayMetrics());
                        updateLanguageOnServer(selectedLocale);
                    } else {
                        cancel();
                    }
                    break;
                }
            }
        }
    };

    private void updateLanguageOnServer(String languageId) {
        String token = PrefDataHandler.getInstance().getSharedPref().getString(KeyIDS.TOKEN, "");
        if(TextUtils.isEmpty(token)) {
            cancel();
            Intent intent = new Intent(context, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
            if (context instanceof Activity)
                ((Activity) context).finish();
        } else {
            showDialog("Updating language...");
            TService.getInstance().updateLanguageOnServer(languageId, token, new TRequestDelegate() {
                @Override
                public void run(TResponse response) {
                    hideDialog();
                        getCities();
                    if(response instanceof OKResponse) {
                        dismiss();
                        Intent intent = new Intent(context, SplashActivity.class);
//                        intent.putExtra(Constants.reinitialize, true);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                        if (context instanceof Activity)
                            ((Activity) context).finish();
                    } else {
                        //alowing to switch language
                        dismiss();
                        Intent intent = new Intent(context, SplashActivity.class);
//                        intent.putExtra(Constants.reinitialize, true);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                        if (context instanceof Activity)
                            ((Activity) context).finish();
                    }
                }
            });
        }
    }

    private void getCities() {
        ArrayMap<String, String> paramMap = new ArrayMap<>();
        paramMap.put("cityVersion", "0");
        paramMap.put("LanguageId", PrefDataHandler.getInstance().getSharedPref().getString(KeyIDS.SELECTED_LANGUAGE, "hi"));
        paramMap.put("lat", PrefDataHandler.getInstance().getSharedPref().getString(KeyIDS.LATITUDE, ""));
        paramMap.put("lng", PrefDataHandler.getInstance().getSharedPref().getString(KeyIDS.LONGITUDE, ""));
        ServerCommunication.INSTANCE.getServerData(context, false, true, Request.Method.GET, KeyIDS.CITY_URI, KeyIDS.CITY_URI, paramMap, new ServerCommunication.OkuTaskListener() {
            @Override
            public void onPostExecute(String result) {
                if(!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject responseObject = new JSONObject(result);
                        int version = responseObject.getInt("CityVersion");
                        PrefDataHandler.getInstance().getEditor().putInt(KeyIDS.CITY_VERSION, version).apply();
                        List<CityEntity> cityList = new Gson().fromJson(responseObject.getJSONArray("CityDTO").toString(), new TypeToken<List<CityEntity>>() {}.getType());
                        CityDataHandler.addAllCityData(context, cityList);
                        getCommodities();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError() {

            }

            @Override
            public void noNetwork() {

            }
        });
    }

    private void getCommodities() {
        ArrayMap<String, String> paramMap = new ArrayMap<>();
        paramMap.put("commodityVersion", "0");
        paramMap.put("LanguageId", PrefDataHandler.getInstance().getSharedPref().getString(KeyIDS.SELECTED_LANGUAGE, "hi"));
        paramMap.put("lat", PrefDataHandler.getInstance().getSharedPref().getString(KeyIDS.LATITUDE, ""));
        paramMap.put("lng", PrefDataHandler.getInstance().getSharedPref().getString(KeyIDS.LONGITUDE, ""));
        ServerCommunication.INSTANCE.getServerData(context, false, true, Request.Method.GET, KeyIDS.COMMODITY_URI, KeyIDS.COMMODITY_URI, paramMap, new ServerCommunication.OkuTaskListener() {
            @Override
            public void onPostExecute(String result) {
                if(!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject responseObject = new JSONObject(result);
                        int version = responseObject.getInt("CommodityVersion");
                        PrefDataHandler.getInstance().getEditor().putInt(KeyIDS.COMMODITY_VERSION, version).apply();
                        List<CommodityEntity> commodityList = new Gson().fromJson(responseObject.getJSONArray("CommodityDTO").toString(), new TypeToken<List<CommodityEntity>>() {}.getType());
                        CommodityDataHandler.addAllCommodityData(context, commodityList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError() {

            }

            @Override
            public void noNetwork() {

            }
        });
    }
}