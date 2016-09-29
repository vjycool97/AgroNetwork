package agroconnectapp.agroconnect.in.agroconnect.networkclient.response;


import android.widget.Toast;

import com.android.volley.NetworkResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import agroconnectapp.agroconnect.in.agroconnect.AgroConnect;

public class TResponse {

    protected TServerResponse response;

    public TServerResponse getResponse() {
        return response;
    }

    public void setResponse(TServerResponse response) {
        this.response = response;
    }

    public TResponse(TServerResponse response) {
        this.response = response;
    }

    public TResponse() {
        this.response = new TServerResponse();
    }

    public static TServerResponse parse(NetworkResponse networkResponse) {
        if(networkResponse == null) {
            TServerResponse serverResponse = new TServerResponse();
            serverResponse.setServerResponse(TServerResponse.ServerResponse.UNKNOWN_ERROR);
            serverResponse.setMessage("Getting a null response server might be down");
            if (Setup.DEBUG_SERVER)
                Toast.makeText(AgroConnect.getInstance().getContext(), "Server Down", Toast.LENGTH_LONG).show();
            return null;
        }

        int statusCode = networkResponse.statusCode;
        //some server condition was not met
        if (statusCode == 500) {
            String responseBody;
            try {
                responseBody = new String(networkResponse.data, "utf-8");
                JSONObject object = new JSONObject(responseBody);

                TServerResponse retval = new TServerResponse();
                retval.setServerResponse(object.getString("serverResponse"));
                retval.setMessage(object.getString("message"));
                if (Setup.DEBUG_SERVER)
                    Toast.makeText(AgroConnect.getInstance().getContext(), retval.getMessage(), Toast.LENGTH_SHORT).show();
                return retval;
            }catch (Exception ex) {
                ex.printStackTrace();
                TServerResponse serverResponse = new TServerResponse();
                serverResponse.setServerResponse(TServerResponse.ServerResponse.UNKNOWN_ERROR);
                serverResponse.setMessage("Exception while parsing server error:");
                if (Setup.DEBUG_SERVER)
                    Toast.makeText(AgroConnect.getInstance().getContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                return serverResponse;
            }
        } else if(statusCode == 401){
            TServerResponse serverResponse = new TServerResponse();
            serverResponse.setServerResponse(TServerResponse.ServerResponse.LOGIN_REQUIRED);
            serverResponse.setMessage("User not logged in");
            if (Setup.DEBUG_SERVER)
                Toast.makeText(AgroConnect.getInstance().getContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
            return serverResponse;
        } else {
            TServerResponse serverResponse = new TServerResponse();
            serverResponse.setServerResponse(TServerResponse.ServerResponse.UNKNOWN_ERROR);
            serverResponse.setMessage("Unknown error from server, error code :" + networkResponse.statusCode);
            if (Setup.DEBUG_SERVER)
                Toast.makeText(AgroConnect.getInstance().getContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
            return serverResponse;
        }
    }

    public static List<TServerResponse> parseBatchedResponse(TServerResponse response) {
        if (response != null && response.getServerResponse() == TServerResponse.ServerResponse.BATCHED_RESPONSE) {
            List<TServerResponse> responses = new ArrayList<TServerResponse>();
            try {
                JSONObject object = new JSONObject(response.getMessage());
                JSONArray objArray = object.getJSONArray("responseList");
                for (int i = 0; i < objArray.length(); i++) {
                    TServerResponse serverResponse = new TServerResponse();
                    JSONObject arrObj =objArray.getJSONObject(i);
                    serverResponse.setServerResponse(arrObj.getString("serverResponse"));
                    serverResponse.setMessage(arrObj.getString("message"));
                    responses.add(serverResponse);
                }
            } catch (Exception ex) {
            }
            return responses;
        }
        return null;
    }
}
