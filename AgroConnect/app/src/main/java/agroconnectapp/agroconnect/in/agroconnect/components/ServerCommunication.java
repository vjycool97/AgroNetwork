package agroconnectapp.agroconnect.in.agroconnect.components;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import agroconnectapp.agroconnect.in.agroconnect.AgroConnect;
import agroconnectapp.agroconnect.in.agroconnect.utility.KeyIDS;
import agroconnectapp.agroconnect.in.agroconnect.utility.Utils;

/**
 * Created by sumanta on 24/2/16.
 */
public enum ServerCommunication implements KeyIDS {
    INSTANCE;

    public void getServerData(Context contex, boolean isHeader, boolean isUrlAppend, int method, final String actionID, String tag, ArrayMap<String, String> okuParam, final OkuTaskListener okuTaskListener) {
        AgroRequest agroRequest = new AgroRequest(method, isHeader, isUrlAppend, getActionUrl(actionID), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (!TextUtils.isEmpty(response)) {
                        okuTaskListener.onPostExecute(response);
                    } else {
                        okuTaskListener.onPostExecute("");
                    }
                } catch (Exception e) {
                    okuTaskListener.onPostExecute("");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                okuTaskListener.onError();
            }
        });
        if(okuParam!=null)
            agroRequest.setParamMap(okuParam);
        agroRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(contex))
            addToRequestQueue(agroRequest, tag);
        else
            okuTaskListener.noNetwork();
    }

    public void getServerDataByGet (Context contex, boolean isHeader, String url, String tag, final OkuTaskListener okuTaskListener) {
        AgroRequest agroRequest = new AgroRequest(Request.Method.GET, isHeader, false, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (!TextUtils.isEmpty(response)) {
                        okuTaskListener.onPostExecute(response);
                    } else {
                        okuTaskListener.onPostExecute("");
                    }
                } catch (Exception e) {
                    okuTaskListener.onPostExecute("");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                okuTaskListener.onError();
            }
        });
        agroRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(contex))
            addToRequestQueue(agroRequest, tag);
        else
            okuTaskListener.noNetwork();
    }

    private void addToRequestQueue(AgroRequest req, String tag) {
        if (!TextUtils.isEmpty(tag))
            req.setTag(tag);
        AgroConnect.getInstance().getRequestQueue().add(req);
    }

    public void cancelRequests(Object tag) {
        AgroConnect.getInstance().getRequestQueue().cancelAll(tag);
    }

    private String getActionUrl(String actionId) {
        String urlString = BASE_URI;
        return TextUtils.isEmpty(actionId) ? urlString : urlString + actionId;
    }

    public interface OkuTaskListener {
        void onPostExecute(String result);
        void onError();
        void noNetwork();
    }
}
