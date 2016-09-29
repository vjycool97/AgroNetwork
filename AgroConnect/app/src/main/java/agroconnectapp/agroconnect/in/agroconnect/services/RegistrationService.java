package agroconnectapp.agroconnect.in.agroconnect.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;

import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.databases.PrefDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;
import agroconnectapp.agroconnect.in.agroconnect.utility.HttpRequest;
import agroconnectapp.agroconnect.in.agroconnect.utility.KeyIDS;

/**
 * Created by niteshtarani on 31/01/16.
 */
public class RegistrationService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    public RegistrationService() {
        super(TAG);
    }
    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]

            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(token);

            Log.e("GCM Token",token);

            // Subscribe to topic channels
            subscribeTopics(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.

            // [END register_for_gcm]
        } catch (Exception e) {
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            PrefDataHandler.getInstance().getEditor().putBoolean(Constants.SENT_TOKEN_TO_SERVER, false).apply();
        }

    }
    private void sendRegistrationToServer(String token) {

        try {

            JSONObject gcmToken = new JSONObject();
            gcmToken.put("AppRegistrationId",token);

            HttpRequest request = new HttpRequest(true,Constants.token,PrefDataHandler.getInstance().getSharedPref().getString(KeyIDS.TOKEN, ""));
            Callback cb = new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                }

                @Override
                public void onResponse(Response response) throws IOException {

                    PrefDataHandler.getInstance().getEditor().putBoolean(KeyIDS.SENT_TOKEN_TO_SERVER, true).apply();
                    Log.e("GCM Token sent", String.valueOf(response.isSuccessful()));
                }
            };
            RequestBody rb = RequestBody.create(request.JSON,gcmToken.toString());
            request.doPostRequest(Constants.sendGcmTokenUrl,rb,cb);
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(new Throwable("Error in sending gcm token - " + e.toString()));
        }

    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]
}
