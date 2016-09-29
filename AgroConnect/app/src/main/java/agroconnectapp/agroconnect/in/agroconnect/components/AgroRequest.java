package agroconnectapp.agroconnect.in.agroconnect.components;

import android.support.v4.util.ArrayMap;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import agroconnectapp.agroconnect.in.agroconnect.SharedPrefHandler;
import agroconnectapp.agroconnect.in.agroconnect.databases.PrefDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;
import agroconnectapp.agroconnect.in.agroconnect.utility.KeyIDS;

public class AgroRequest extends StringRequest {

    private ArrayMap<String, String> agroParam;
    private boolean isUrlAppend;
    private boolean isHeader;
    private String url;
    public AgroRequest(int method, boolean isHeader, boolean isUrlAppend, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.isHeader = isHeader;
        this.isUrlAppend = isUrlAppend;
        this.url = url;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if(isHeader) {
            Map<String, String> headers = new HashMap<>();
            String token = PrefDataHandler.getInstance().getSharedPref().getString(KeyIDS.TOKEN, "");
            if(!token.isEmpty()) {
                headers.put("Token", token);
                headers.put("Content-Type", "application/json; charset=utf-8");
            }
            return headers;
        } else {
            return super.getHeaders();
        }
    }

    @Override
    public String getUrl() {
        if(isUrlAppend) {
            StringBuilder stringBuilder = new StringBuilder(url);
            Iterator<Map.Entry<String, String>> iterator = agroParam.entrySet().iterator();
            int i = 1;
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                if(i == 1) {
                    stringBuilder.append("?" + entry.getKey() + "=" + entry.getValue());
                } else {
                    stringBuilder.append("&" + entry.getKey() + "=" + entry.getValue());
                }
                iterator.remove(); // avoids a ConcurrentModificationException
                i++;
            }
            url = stringBuilder.toString();
        }
        return url;
    }
    @Override
    protected ArrayMap<String, String> getParams() throws AuthFailureError {
        return agroParam;
    }
    public void setParamMap(ArrayMap<String, String> paramMap) {
        this.agroParam = paramMap;
    }
}