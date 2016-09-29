package agroconnectapp.agroconnect.in.agroconnect.components;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import agroconnectapp.agroconnect.in.agroconnect.databases.PrefDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.utility.KeyIDS;

/**
 * Created by sumanta on 6/6/16.
 */
public class AgroJsonRequest extends JsonObjectRequest {
    public AgroJsonRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = new HashMap<>();
            String token = PrefDataHandler.getInstance().getSharedPref().getString(KeyIDS.TOKEN, "");
            if(!token.isEmpty()) {
                headers.put("Token", token);
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            } else {
            return super.getHeaders();
        }
    }

    /*@Override
    public String getBodyContentType() {
        return "application/json; charset=utf-8";
    }*/
}
