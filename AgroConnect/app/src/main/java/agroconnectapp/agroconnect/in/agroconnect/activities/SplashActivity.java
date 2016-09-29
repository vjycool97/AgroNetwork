package agroconnectapp.agroconnect.in.agroconnect.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import java.util.Locale;

import agroconnectapp.agroconnect.in.agroconnect.BuildConfig;
import agroconnectapp.agroconnect.in.agroconnect.NetworkController;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.databases.PrefDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.uicomponents.AgroAlertDialog;
import agroconnectapp.agroconnect.in.agroconnect.utility.LoggerExceptionHandler;

/**
 * Created by sumanta on 4/6/16.
 */
public class SplashActivity extends AgroActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
//            Thread.setDefaultUncaughtExceptionHandler(new LoggerExceptionHandler(this, null));
        }
        setContentView(R.layout.activity_splash);
        NetworkController.getInstance().loadCPDataFromDB();
        if(!TextUtils.isEmpty(PrefDataHandler.getInstance().getSharedPref().getString(NAME, ""))) {
            PrefDataHandler.getInstance().getEditor().putBoolean(PHONE_SUBMITTED, true).apply();
        }
        final boolean isProfileSave = PrefDataHandler.getInstance().getSharedPref().getBoolean(PROFILE_SAVE, false);
        final boolean isProfilePage = PrefDataHandler.getInstance().getSharedPref().getBoolean(PROFILE_PAGE, false);
        final boolean phoneSubmitted = PrefDataHandler.getInstance().getSharedPref().getBoolean(PHONE_SUBMITTED, false);
        String locale = PrefDataHandler.getInstance().getSharedPref().getString(SELECTED_LANGUAGE, "hi");
        getResources().getConfiguration().locale = new Locale(locale);
        getResources().updateConfiguration(getResources().getConfiguration(), getResources().getDisplayMetrics());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                   if (isProfileSave) {
                        Intent intent = new Intent(SplashActivity.this, NewsFeedActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (isProfilePage) {
                        Intent intent = new Intent(SplashActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (phoneSubmitted) {
                        Intent intent = new Intent(SplashActivity.this, InitActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(SplashActivity.this, RegistrationActivity.class);
                        startActivity(intent);
                        finish();
                    }
            }
        }, 1000);

    }
}
