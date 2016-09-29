package agroconnectapp.agroconnect.in.agroconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import agroconnectapp.agroconnect.in.agroconnect.AgroConnect;
import agroconnectapp.agroconnect.in.agroconnect.NetworkController;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.components.ServerCommunication;
import agroconnectapp.agroconnect.in.agroconnect.databases.CityDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.databases.CommodityDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.databases.PrefDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.entities.CityEntity;
import agroconnectapp.agroconnect.in.agroconnect.entities.CommodityEntity;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.model.User;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.network.TRequestDelegate;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.response.ErrorResponse;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.response.TResponse;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.response.UserResponse;
import agroconnectapp.agroconnect.in.agroconnect.receivers.SmsReceiver;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;
import agroconnectapp.agroconnect.in.agroconnect.utility.HttpRequest;
import agroconnectapp.agroconnect.in.agroconnect.utility.KeyIDS;
import google.volly.helper.VolleyErrorHelper;
import google.volly.helper.VollyHelper;

/**
 * Created by sumanta on 11/6/16.
 */
public class InitActivity extends AgroActivity {

    Chronometer timer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        SmsReceiver.getInstance().registerReceiver(InitActivity.this);
        timer = (Chronometer) findViewById(R.id.timer);
        timer.start();
        getUserDetails(PrefDataHandler.getInstance().getSharedPref().getString(PHONE_NUMBER, ""));
    }

    private void getUserDetails(String phoneNumber) {
        ArrayMap<String, String> paramMap = new ArrayMap<>();
        String url = "/api/agents/GetAgentByPhoneNumber/" + phoneNumber;
        ServerCommunication.INSTANCE.getServerData(InitActivity.this, false, false, Request.Method.GET, url, url, paramMap, new ServerCommunication.OkuTaskListener() {
            @Override
            public void onPostExecute(String result) {
                getCities();
                 /*
                  *  Saving user data to preference...
                  */
                if(!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject userObject = new JSONObject(result);
                        if(userObject.has("Id"))
                            PrefDataHandler.getInstance().getEditor().putInt(AGENT_ID, userObject.getInt("Id")).apply();
                        if(!TextUtils.isEmpty(userObject.getString("Name")))
                            PrefDataHandler.getInstance().getEditor().putString(NAME, userObject.getString("Name")).apply();
                        if(!TextUtils.isEmpty(userObject.getString("City")))
                            PrefDataHandler.getInstance().getEditor().putString(CITY, userObject.getString("City")).apply();
                        if(userObject.has("CityId"))
                            PrefDataHandler.getInstance().getEditor().putInt(CITYID, userObject.getInt("CityId")).apply();
                        if(!TextUtils.isEmpty(userObject.getString("Commodity")))
                            PrefDataHandler.getInstance().getEditor().putString(COMMODITY, userObject.getString("Commodity")).apply();
                        if(userObject.has("CommodityId"))
                            PrefDataHandler.getInstance().getEditor().putInt(COMMODITYID, userObject.getInt("CommodityId")).apply();
                        if(userObject.has("AgentType"))
                            PrefDataHandler.getInstance().getEditor().putInt(AGENT_TYPE, userObject.getInt("AgentType")).apply();
                        if(!TextUtils.isEmpty(userObject.getString("Organisation")))
                            PrefDataHandler.getInstance().getEditor().putString(ORGANIZATION, userObject.getString("Organisation")).apply();
                        if(!TextUtils.isEmpty(userObject.getString("Department")))
                            PrefDataHandler.getInstance().getEditor().putString(DEPARTMENT, userObject.getString("Department")).apply();
                        if(!TextUtils.isEmpty(userObject.getString("Email")))
                            PrefDataHandler.getInstance().getEditor().putString(EMAIL, userObject.getString("Email")).apply();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError() {
                getCities();
            }

            @Override
            public void noNetwork() {

            }
        });
    }

    private void getCities() {
        ArrayMap<String, String> paramMap = new ArrayMap<>();
        paramMap.put("cityVersion", "0");
        paramMap.put("LanguageId", PrefDataHandler.getInstance().getSharedPref().getString(SELECTED_LANGUAGE, "hi"));
        paramMap.put("lat", PrefDataHandler.getInstance().getSharedPref().getString(LATITUDE, ""));
        paramMap.put("lng", PrefDataHandler.getInstance().getSharedPref().getString(LONGITUDE, ""));
        ServerCommunication.INSTANCE.getServerData(InitActivity.this, false, true, Request.Method.GET, CITY_URI, CITY_URI, paramMap, new ServerCommunication.OkuTaskListener() {
            @Override
            public void onPostExecute(String result) {
                if(!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject responseObject = new JSONObject(result);
                        int version = responseObject.optInt("CityVersion");
                        PrefDataHandler.getInstance().getEditor().putInt(CITY_VERSION, version).apply();
                        List<CityEntity> cityList = new Gson().fromJson(responseObject.getJSONArray("CityDTO").toString(), new TypeToken<List<CityEntity>>() {}.getType());
                        CityDataHandler.addAllCityData(InitActivity.this, cityList);
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
        paramMap.put("LanguageId", PrefDataHandler.getInstance().getSharedPref().getString(SELECTED_LANGUAGE, "hi"));
        paramMap.put("lat", PrefDataHandler.getInstance().getSharedPref().getString(LATITUDE, ""));
        paramMap.put("lng", PrefDataHandler.getInstance().getSharedPref().getString(LONGITUDE, ""));
        ServerCommunication.INSTANCE.getServerData(InitActivity.this, false, true, Request.Method.GET, COMMODITY_URI, COMMODITY_URI, paramMap, new ServerCommunication.OkuTaskListener() {
            @Override
            public void onPostExecute(String result) {
                if(!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject responseObject = new JSONObject(result);
                        int version = responseObject.optInt("CommodityVersion");
                        PrefDataHandler.getInstance().getEditor().putInt(COMMODITY_VERSION, version).apply();
                        List<CommodityEntity> commodityList = new Gson().fromJson(responseObject.getJSONArray("CommodityDTO").toString(), new TypeToken<List<CommodityEntity>>() {}.getType());
                        CommodityDataHandler.addAllCommodityData(InitActivity.this, commodityList);
                        PrefDataHandler.getInstance().getEditor().putBoolean(PROFILE_PAGE, true).apply();

                        /*
                        *  after finished syncing start profile activity.
                        */
//                        if(!TextUtils.isEmpty(PrefDataHandler.getInstance().getSharedPref().getString(NAME, ""))) {
//                            final String deviceId = Settings.Secure.getString(InitActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
//                            getToken(deviceId);
//                        } else{
                            Intent profileIntent = new Intent(InitActivity.this, ProfileActivity.class);
                            startActivity(profileIntent);
                            finish();
//                        }
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

    private void getToken(String password) {
        String usernamePassword = PrefDataHandler.getInstance().getSharedPref().getString(PHONE_NUMBER, "")  + ":" + password;
        String encodedUsernamePassword  = Base64.encodeToString(usernamePassword.getBytes(),Base64.NO_WRAP);
        HttpRequest request = new HttpRequest(true,"Authorization"," Basic " + encodedUsernamePassword);
        Callback cb = new Callback() {
            @Override
            public void onFailure(com.squareup.okhttp.Request request, IOException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_msg_unknown),Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
            }

            @Override
            public void onResponse(com.squareup.okhttp.Response response) throws IOException {

                if(response.isSuccessful()) {
                    PrefDataHandler.getInstance().getEditor().putString(TOKEN, response.header("Token")).apply();
                    PrefDataHandler.getInstance().getEditor().putBoolean(PROFILE_SAVE, true).apply();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.profile_saved, Toast.LENGTH_LONG).show();
                        }
                    });
                    startActivity(new Intent(InitActivity.this, HomeActivity.class));
                    finish();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_msg_unknown),Toast.LENGTH_SHORT).show();
//                            Crashlytics.logException(new Throwable("Unsuccessful response in get token"));
                        }
                    });
                    finish();
                }
            }
        };
        try {
            RequestBody rb = RequestBody.create(request.JSON,"{}");
            request.doPostRequest(Constants.authenticationUrl,rb,cb);
        } catch (IOException e) {
            e.printStackTrace();
            Crashlytics.logException(new Throwable("Error in get token - " + e.toString()));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.stop();
    }
}
