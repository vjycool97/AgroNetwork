package agroconnectapp.agroconnect.in.agroconnect.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import agroconnectapp.agroconnect.in.agroconnect.AgroConnect;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.adapters.RegPagerAdapter;
import agroconnectapp.agroconnect.in.agroconnect.databases.PrefDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.uicomponents.AgroViewPager;
import agroconnectapp.agroconnect.in.agroconnect.uicomponents.LanguageDialog;

/**
 * Created by sumanta on 4/6/16.
 */
public class RegistrationActivity extends AgroActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private AgroViewPager pager;
    private RegPagerAdapter adapter;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private GoogleApiClient googleApiClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        pager = (AgroViewPager) findViewById(R.id.pager);
        pager.setPagingEnabled(false);
        adapter = new RegPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        if(!PrefDataHandler.getInstance().getSharedPref().getBoolean(IS_LANGUAGE_SET, false)) {
            PrefDataHandler.getInstance().getEditor().putString(SELECTED_LANGUAGE, "hi").apply();
            LanguageDialog languageDialog = new LanguageDialog(RegistrationActivity.this);
            languageDialog.show();
        }
        if(checkPlayServices()) {
            LocationRequest locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            mLocationSettingsRequest = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).setAlwaysShow(true).build();
            buildGoogleApiClient();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        SmsReceiver.getInstance().unRegisterReceiver(RegistrationActivity.this, SmsReceiver.getInstance());
    }


    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),"This device is not supported.", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient!=null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }
    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    @Override
    public void onConnected(Bundle bundle) {
        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public void enableGpsAlert() {
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, mLocationSettingsRequest);
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        displayLocation();
//                        startLocationUpdates();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(RegistrationActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        displayLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        enableGpsAlert();
                        break;
                }
                break;
        }
    }

    private void displayLocation() {
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastLocation != null) {
            double latitude = lastLocation.getLatitude();
            double longitude = lastLocation.getLongitude();
            PrefDataHandler.getInstance().getEditor().putString(LATITUDE, String.valueOf(latitude)).apply();
            PrefDataHandler.getInstance().getEditor().putString(LONGITUDE, String.valueOf(longitude)).apply();
        } else {
            enableGpsAlert();
        }
    }
}
