package agroconnectapp.agroconnect.in.agroconnect.networkclient.network;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agroconnectapp.agroconnect.in.agroconnect.AgroConnect;
import agroconnectapp.agroconnect.in.agroconnect.databases.PrefDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.model.CPNode;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.model.City;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.model.CityData;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.model.Commodity;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.model.CommodityData;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.model.User;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.response.CPCropsResponse;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.response.CitiesResponse;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.response.CommodityResponse;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.response.ErrorResponse;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.response.OKResponse;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.response.TResponse;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.response.UserResponse;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;
import agroconnectapp.agroconnect.in.agroconnect.utility.KeyIDS;
import google.volly.helper.VolleyErrorHelper;
import google.volly.helper.VollyHelper;


public class TService {

    // Instance
    private static TService ourIntance = null;

    public static TService getInstance() {
        if (ourIntance == null) {
            ourIntance = new TService();
        }
        return ourIntance;
    }


    public Map<String, String> getDefaultHeaders() {
        Map<String, String> headers = new HashMap<>();
        String token = PrefDataHandler.getInstance().getSharedPref().getString(KeyIDS.TOKEN, "");
        if(!token.isEmpty())
            headers.put(Constants.token, token);
        return headers;
    }

    public Map<String, String> getDefaultHeaddersWithUUID() {
        Map <String, String> headers;
        headers = getDefaultHeaders();
        return headers;
    }

    public void getUser(String phoneNumber, final TRequestDelegate delegate) {
        // Creating volley request obj
        String url = Constants.getUserDetailsUrl + phoneNumber;
        JsonObjectRequest menuItemsReq = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        User user = null;
                        try {
                            JSONObject storeObject = response;//response.getJSONObject("store");
                            String data = storeObject.toString();
                            user = gson.fromJson(data, User.class);
                            Log.e("USER", user.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        delegate.run(new UserResponse(user));

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TService", "Error: " + error.getMessage());

                delegate.run(new ErrorResponse( TResponse.parse(error.networkResponse),
                        VolleyErrorHelper.getMessage(error, AgroConnect.getInstance().getContext())));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getDefaultHeaders();
            }
        };

        // Adding request to request queue
        VollyHelper.getInstance().addToRequestQueue(menuItemsReq);
    }

    public void updateLanguageOnServer(String languageId, String token, final TRequestDelegate delegate) {
        String url = Constants.updateLanguageUrl + languageId;
        StringRequest signinRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        delegate.run(new OKResponse());
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TService updateLanguageOnServer", "Error: " + error.getMessage());
                delegate.run(new ErrorResponse(TResponse.parse(error.networkResponse),
                        VolleyErrorHelper.getMessage(error, AgroConnect.getInstance().getContext())));
            }
        }) {
            @Override
            protected Map<String, String> getParams () {
                Map<String, String> params = new HashMap<>();
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getDefaultHeaders();
            }
        };
        VollyHelper.getInstance().addToRequestQueue(signinRequest);
    }



    public void getAllCities(String langId, String token, int currentCityVer, String lat, String lng, final TRequestDelegate delegate) {
        // Creating volley request obj
        String url = Constants.cityUrl + currentCityVer + "&" + Constants.languageId + "=" + langId;
        if(!lat.isEmpty() && !lng.isEmpty()) {
            url = url + "&lat=" + lat + "&lng=" + lng;
        }
        JsonObjectRequest menuItemsReq = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        /*if (!Verifier.verify(response)) {
                            Timber.e("getAllCities1: JSON verification failed");
                        }*/
                        /*Gson gson = new Gson();
                        Store store = null;
                        try {
                            JSONObject storeObject = response.getJSONObject("store");
                            String data = storeObject.toString();
                            store = gson.fromJson(data, Store.class);
                            setAbsoluteImageUrls(store);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/

                        try {
                            JSONObject responseObj = response;
                            int cityVersion = responseObj.optInt(Constants.cityVersion);
                            JSONArray newCityArray = responseObj.optJSONArray("CityDTO");
                            CityData cityData = new CityData(cityVersion);
                            for(int i = 0; i < newCityArray.length(); i++) {
                                JSONObject obj = newCityArray.optJSONObject(i);
                                City city = new City(obj.optString("CityId"), obj.optString("Name"), obj.optString("LocalName"));
                                cityData.cities.add(city);
                            }
                            delegate.run(new CitiesResponse(cityData));

                        } catch (Exception e) {
                            e.printStackTrace();
                            Crashlytics.logException(new Throwable("Exception in fetching all cities - " + e.toString()));
                            delegate.run(new ErrorResponse("Exception in fetching all cities - " + e.toString()));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TService", "Error: " + error.getMessage());

                delegate.run(new ErrorResponse( TResponse.parse(error.networkResponse),
                        VolleyErrorHelper.getMessage(error, AgroConnect.getInstance().getContext())));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getDefaultHeaders();
            }
        };

        // Adding request to request queue
        VollyHelper.getInstance().addToRequestQueue(menuItemsReq);
    }

    public void getAllCommodities(String langId, String token, int currentCommodityVer, String lat, String lng, final TRequestDelegate delegate) {
        // Creating volley request obj
        String url = Constants.commodityUrl + currentCommodityVer + "&" + Constants.languageId + "=" + langId;
        if(!lat.isEmpty() && !lng.isEmpty()) {
            url = url + "&lat=" + lat + "&lng=" + lng;
        }
        JsonObjectRequest menuItemsReq = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        /*if (!Verifier.verify(response)) {
                            Timber.e("getAllCities1: JSON verification failed");
                        }*/
                        /*Gson gson = new Gson();
                        Store store = null;
                        try {
                            JSONObject storeObject = response.getJSONObject("store");
                            String data = storeObject.toString();
                            store = gson.fromJson(data, Store.class);
                            setAbsoluteImageUrls(store);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/

                        try {
                            JSONObject responseObj = response;
                            int commodityVersion = responseObj.optInt(Constants.commodityVersion);
                            JSONArray newCityArray = responseObj.optJSONArray("CommodityDTO");
                            CommodityData commodityData = new CommodityData(commodityVersion);
                            for(int i = 0; i < newCityArray.length(); i++) {
                                JSONObject obj = newCityArray.optJSONObject(i);
                                Commodity commodity = new Commodity(obj.optString("CommodityId"), obj.optString("Name"), obj.optString("LocalName"));
                                commodityData.commodities.add(commodity);
                            }
                            delegate.run(new CommodityResponse(commodityData));

                        } catch (Exception e) {
                            e.printStackTrace();
                            Crashlytics.logException(new Throwable("Exception in fetching all commodities - " + e.toString()));
                            delegate.run(new ErrorResponse("Exception in fetching all commodities - " + e.toString()));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TService", "Error: " + error.getMessage());

                delegate.run(new ErrorResponse( TResponse.parse(error.networkResponse),
                        VolleyErrorHelper.getMessage(error, AgroConnect.getInstance().getContext())));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getDefaultHeaders();
            }
        };

        // Adding request to request queue
        VollyHelper.getInstance().addToRequestQueue(menuItemsReq);
    }


    public void getCPCrops(final TRequestDelegate delegate) {
        // Creating volley request obj
        String url = Constants.cropProtectionUrl;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.e("CP", "getCPCrops " + response.toString());
                Gson gson = new Gson();
                List<CPNode> cpNodes = new ArrayList<>();
                try {
                    JSONArray jsonArray = response;
                    for (int count = 0; count < jsonArray.length(); count++) {
                        JSONObject obj = jsonArray.optJSONObject(count);
                        CPNode node = new CPNode();
                        node.setData(obj);
                        node.setNode_type(CPNode.NODE_TYPE.ROOT);
                        cpNodes.add(node);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                delegate.run(new CPCropsResponse(cpNodes));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TService", "Error: " + error.getMessage());

                delegate.run(new ErrorResponse( TResponse.parse(error.networkResponse),
                        VolleyErrorHelper.getMessage(error, AgroConnect.getInstance().getContext())));
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getDefaultHeaders();
            }
        };

        // Adding request to request queue
        VollyHelper.getInstance().addToRequestQueue(jsonArrayRequest);
    }


    public void getCPCropData(final CPNode crop, final TRequestDelegate delegate) {
        // Creating volley request obj
        String url = Constants.cropProtectionForCropUrl + crop.getNodeId();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONArray jsonArray = response;
                    crop.setChidlen(jsonArray);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                delegate.run(new OKResponse());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TService", "Error: " + error.getMessage());

                delegate.run(new ErrorResponse( TResponse.parse(error.networkResponse),
                        VolleyErrorHelper.getMessage(error, AgroConnect.getInstance().getContext())));
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getDefaultHeaders();
            }
        };

        // Adding request to request queue
        VollyHelper.getInstance().addToRequestQueue(jsonArrayRequest);
    }


}
