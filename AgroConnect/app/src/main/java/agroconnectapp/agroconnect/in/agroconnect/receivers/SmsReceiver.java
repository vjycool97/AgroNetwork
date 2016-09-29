package agroconnectapp.agroconnect.in.agroconnect.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;
import agroconnectapp.agroconnect.in.agroconnect.databases.PrefDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.utility.HttpRequest;
import agroconnectapp.agroconnect.in.agroconnect.utility.KeyIDS;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.io.IOException;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;

public class SmsReceiver extends BroadcastReceiver implements KeyIDS {
        private static boolean isRegister;
        private SmsReceiver(){}
        private static class SingletonHelper{
            private static final SmsReceiver INSTANCE = new SmsReceiver();
        }

        public static SmsReceiver getInstance(){
            return SingletonHelper.INSTANCE;
        }
        public void onReceive(final Context context, Intent intent) {
            final Bundle bundle = intent.getExtras();
            unRegisterReceiver(context, SmsReceiver.getInstance());
            try {
                if (bundle != null) {
                    final Object[] pdusObj = (Object[]) bundle.get("pdus");
                    for (int i = 0; i < pdusObj.length; i++) {
                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String message = currentMessage.getDisplayMessageBody();
                        message.trim();
                        if(message.length() >= 0 && message.toLowerCase().contains("agroconnect")) {
                            String[] stringList = message.split(" ");
                            String code = stringList[0];
                            validateOtp(PrefDataHandler.getInstance().getSharedPref().getString(PHONE_NUMBER, ""), code);
                        }
                    }
                }

            } catch (Exception e) {
            }
        }
        public void registerReceiver(Context context) {
            try {
                if (!isRegister) {
                    isRegister = true;
                    context.registerReceiver(SmsReceiver.getInstance(), new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
                }
            } catch (Exception ex) {}
        }
        public void unRegisterReceiver (Context context, BroadcastReceiver receiver) {
            try {
                if (isRegister) {
                    isRegister = false;
                    context.unregisterReceiver(receiver);
                }
            } catch (Exception ex) {}
        }
    private void validateOtp(String number, String otp) {

        HttpRequest request = new HttpRequest(false, "", "");
        try {
            Callback callback = new Callback() {

                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(final Response response) throws IOException {

                    if (response.isSuccessful()) {

                    } else {

                    }

                }
            };

            RequestBody rb = RequestBody.create(request.JSON, "");
            request.doPostRequest(Constants.validateOtpUrl + number + "&OTP=" + otp, rb, callback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
