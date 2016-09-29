package agroconnectapp.agroconnect.in.agroconnect.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;
import agroconnectapp.agroconnect.in.agroconnect.utility.HttpRequest;

/**
 * Created by niteshtarani on 06/04/16.
 */
public class FreshDataService extends IntentService {

    private static final String TAG = "FreshDataService";
    SharedPreferences sharedPreferences;
    boolean reinitialize = false;
    private String lat="", lng="";

    public FreshDataService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Location Permission denied");
        } else {
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (location != null) {
                try {
                    lat = String.valueOf(location.getLatitude());
                    lng = String.valueOf(location.getLongitude());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if(intent.hasExtra(Constants.reinitialize))
            reinitialize = intent.getBooleanExtra(Constants.reinitialize, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String selectedLocale = sharedPreferences.getString(Constants.selectedLocale, "en");
        // Why do we have this check here
        if(sharedPreferences.getBoolean(Constants.languageSelected, false)) {
            getAllCommodities(selectedLocale);
            getAllCities(selectedLocale);
        }
    }

    private void getAllCommodities(String langId) {
        String token = sharedPreferences.getString(Constants.token, "");
        HttpRequest request;
        if(!token.isEmpty()) {
            request = new HttpRequest(true,Constants.token,token);
        } else
            request = new HttpRequest(false,"","");
        Callback callback = new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                if(response.isSuccessful()) {

                    try {
                        JSONObject responseObj = new JSONObject(response.body().string());
                        JSONArray newCommodityArray = responseObj.optJSONArray("CommodityDTO");
                        if(sharedPreferences.getBoolean(Constants.profileSaved,false))
                            sharedPreferences.edit().putInt(Constants.commodityVersion, responseObj.optInt(Constants.commodityVersion)).commit();

                        if(newCommodityArray != null && newCommodityArray.length() > 0) {
                            sharedPreferences.edit().putString(Constants.allCommodities, newCommodityArray.toString()).commit();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Crashlytics.logException(new Throwable("Exception in fetching all commodities - " + e.toString()));
                    }

                }
            }
        };
        try {
            int currentCommodityVer = sharedPreferences.getInt(Constants.commodityVersion,0);
            if(reinitialize)
                currentCommodityVer = 0;
            String url = Constants.commodityUrl+currentCommodityVer + "&" + Constants.languageId + "=" + langId;
            if(!lat.isEmpty() && !lng.isEmpty()) {
                url = url + "&lat=" + lat + "&lng=" + lng;
            }
            request.doGetRequest(url, callback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getAllCities(String langId) {

        String token = sharedPreferences.getString(Constants.token, "");
        HttpRequest request;
        if(!token.isEmpty()) {
            request = new HttpRequest(true,Constants.token,token);
        } else
            request = new HttpRequest(false,"","");
        Callback callback = new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                if(response.isSuccessful()) {
                    try {
                        JSONObject responseObj = new JSONObject(response.body().string());
                        JSONArray newCityArray = responseObj.optJSONArray("CityDTO");
                        if(sharedPreferences.getBoolean(Constants.profileSaved,false))
                            sharedPreferences.edit().putInt(Constants.cityVersion, responseObj.optInt(Constants.cityVersion)).commit();
                        if(newCityArray != null && newCityArray.length() > 0) {
                            sharedPreferences.edit().putString(Constants.allCities, newCityArray.toString()).commit();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Crashlytics.logException(new Throwable("Exception in fetching all cities - " + e.toString()));
                    }
                }
            }
        };
        try {
            int currentCityVer = sharedPreferences.getInt(Constants.cityVersion,0);
            if(reinitialize)
                currentCityVer = 0;
            String url = Constants.cityUrl+currentCityVer + "&" + Constants.languageId + "=" + langId;
            if(!lat.isEmpty() && !lng.isEmpty()) {
                url = url + "&lat=" + lat + "&lng=" + lng;
            }
            request.doGetRequest(url, callback);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
