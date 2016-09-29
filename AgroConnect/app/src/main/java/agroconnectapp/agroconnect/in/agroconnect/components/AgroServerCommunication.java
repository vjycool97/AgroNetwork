package agroconnectapp.agroconnect.in.agroconnect.components;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import org.json.JSONObject;
import agroconnectapp.agroconnect.in.agroconnect.AgroConnect;
import agroconnectapp.agroconnect.in.agroconnect.utility.KeyIDS;
import agroconnectapp.agroconnect.in.agroconnect.utility.Utils;

/**
 * Created by sumanta on 22/6/16.
 */
public enum AgroServerCommunication implements KeyIDS {
    INSTANCE;

    public void getServerData(Context contex, final String actionID, String tag, JSONObject paramObject, final OkuTaskListener okuTaskListener) {
        AgroJsonRequest agroRequest = new AgroJsonRequest(Request.Method.POST, getActionUrl(actionID), paramObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                okuTaskListener.onPostExecute(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                okuTaskListener.onError();
            }
        });
        agroRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(contex))
            addToRequestQueue(agroRequest, tag);
        else
            okuTaskListener.noNetwork();
    }

    public void getServerDataByUrl (Context contex, String url, String tag, JSONObject paramObject, final OkuTaskListener okuTaskListener) {
        AgroJsonRequest agroRequest = new AgroJsonRequest(Request.Method.POST, url, paramObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                okuTaskListener.onPostExecute(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                okuTaskListener.onError();
            }
        });
        agroRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        if (Utils.isNetworkAvailable(contex))
            addToRequestQueue(agroRequest, tag);
        else
            okuTaskListener.noNetwork();
    }

    private void addToRequestQueue(AgroJsonRequest req, String tag) {
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
        void onPostExecute(JSONObject resultObject);
        void onError();
        void noNetwork();
    }
}
