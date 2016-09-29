package agroconnectapp.agroconnect.in.agroconnect.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import agroconnectapp.agroconnect.in.agroconnect.AgroConnect;
import agroconnectapp.agroconnect.in.agroconnect.NetworkController;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.adapters.AgroSpinnerAdapter;
import agroconnectapp.agroconnect.in.agroconnect.databases.CityDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.databases.CommodityDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.databases.PrefDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.entities.CityEntity;
import agroconnectapp.agroconnect.in.agroconnect.entities.CommodityEntity;
import agroconnectapp.agroconnect.in.agroconnect.events.CityEvent;
import agroconnectapp.agroconnect.in.agroconnect.events.CommodityEvent;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.model.User;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;
import agroconnectapp.agroconnect.in.agroconnect.utility.HttpRequest;

public class ProfileActivity extends AgroActivity implements View.OnClickListener{

    private Spinner agentTypeSP;
    private EditText nameET, organizationET, departmentET, emailET;
    private TextView locationTV, commodityTV;
    private LinearLayout commmodityLayout, advisorLayout;
    private CityEntity cityEntity;
    private CommodityEntity commodityEntity;
    boolean editProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AgroConnect.agroEventBus.register(this);
        setContentView(R.layout.activity_profile_new);
//        if(getIntent() != null) {
//            editProfile = getIntent().getBooleanExtra(EDIT_PROFILE, false);
//        }
//        if(editProfile)
//            setToolbar("Edit Profile");
//        else
            setToolbar("Profile");
        nameET = (EditText) findViewById(R.id.nameET);
        organizationET = (EditText) findViewById(R.id.organizationET);
        departmentET = (EditText) findViewById(R.id.departmentET);
        emailET = (EditText) findViewById(R.id.emailET);
        agentTypeSP = (Spinner) findViewById(R.id.agentTypeSP);
        locationTV = (TextView) findViewById(R.id.locationTV);
        commodityTV = (TextView) findViewById(R.id.commodityTV);
        commmodityLayout = (LinearLayout) findViewById(R.id.commmodityLayout);
        advisorLayout = (LinearLayout) findViewById(R.id.advisorLayout);
        String[] typeArray = getResources().getStringArray(R.array.agent_types);
        AgroSpinnerAdapter agentAdapter = new AgroSpinnerAdapter(this, R.layout.spinner_text_layout, typeArray, agentTypeSP);
        agentTypeSP.setAdapter(agentAdapter);
        agentTypeSP.setOnItemSelectedListener(agentItemSelect);
        agentTypeSP.setSelection(1);
        locationTV.setOnClickListener(locationClick);
        commodityTV.setOnClickListener(commodityClick);
        findViewById(R.id.saveBtn).setOnClickListener(this);

        if(editProfile || !TextUtils.isEmpty(PrefDataHandler.getInstance().getSharedPref().getString(NAME, ""))) {
            cityEntity = CityDataHandler.getCityById(this, PrefDataHandler.getInstance().getSharedPref().getInt(CITYID, 0));
            commodityEntity = CommodityDataHandler.getCommodityById(this, PrefDataHandler.getInstance().getSharedPref().getInt(COMMODITYID, 0));
            int type = PrefDataHandler.getInstance().getSharedPref().getInt(AGENT_TYPE, 0);
            agentTypeSP.setSelection(type);
            if(type == 2 || type == 3) {
                organizationET.setText(PrefDataHandler.getInstance().getSharedPref().getString(ORGANIZATION, ""));
                departmentET.setText(PrefDataHandler.getInstance().getSharedPref().getString(DEPARTMENT, ""));
                emailET.setText(PrefDataHandler.getInstance().getSharedPref().getString(EMAIL, ""));
            }
            nameET.setText(PrefDataHandler.getInstance().getSharedPref().getString(NAME, ""));
            locationTV.setText(PrefDataHandler.getInstance().getSharedPref().getString(CITY, ""));
            commodityTV.setText(PrefDataHandler.getInstance().getSharedPref().getString(COMMODITY, ""));
        }

    }

    @Subscribe
    public void cityEvent(CityEvent event) {
        cityEntity = event.getEntity();
        if (cityEntity != null) {
            locationTV.setText(cityEntity.getLocalName());
        }
    }

    @Subscribe
    public void CommodityEvent(CommodityEvent event) {
        commodityEntity = event.getEntity();
        if (commodityEntity != null) {
            commodityTV.setText(commodityEntity.getLocalName());
        }
    }

    private View.OnClickListener locationClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ProfileActivity.this, FragmentWrapperActivity.class);
            intent.putExtra("className", "agroconnectapp.agroconnect.in.agroconnect.fragments.CityAndCommodityFragment");
            intent.putExtra("isCity", true);
            startActivity(intent);
            overridePendingTransition(R.anim.flipin, R.anim.flipout);
        }
    };

    private View.OnClickListener commodityClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ProfileActivity.this, FragmentWrapperActivity.class);
            intent.putExtra("className", "agroconnectapp.agroconnect.in.agroconnect.fragments.CityAndCommodityFragment");
            intent.putExtra("isCity", false);
            startActivity(intent);
            overridePendingTransition(R.anim.flipin, R.anim.flipout);
        }
    };

    private AdapterView.OnItemSelectedListener agentItemSelect = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            if (position == 2 || position == 3) {
                advisorLayout.setVisibility(View.VISIBLE);
                commmodityLayout.setVisibility(View.GONE);
            } else {
                advisorLayout.setVisibility(View.GONE);
                commmodityLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void setToolbar(String message) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(message);
        toolbar.setTitleTextColor(getResources().getColor(R.color.color_white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void saveUserProfileData() {
        PrefDataHandler.getInstance().getEditor().putString(NAME, nameET.getText().toString()).apply();
        PrefDataHandler.getInstance().getEditor().putString(CITY, locationTV.getText().toString()).apply();
        PrefDataHandler.getInstance().getEditor().putInt(CITYID, cityEntity.getId()).apply();
        PrefDataHandler.getInstance().getEditor().putString(COMMODITY, commodityTV.getText().toString()).apply();
        PrefDataHandler.getInstance().getEditor().putInt(COMMODITYID, commodityEntity.getId()).apply();
        PrefDataHandler.getInstance().getEditor().putInt(AGENT_TYPE, agentTypeSP.getSelectedItemPosition()).apply();
        PrefDataHandler.getInstance().getEditor().putString(ORGANIZATION, organizationET.getText().toString()).apply();
        PrefDataHandler.getInstance().getEditor().putString(DEPARTMENT, departmentET.getText().toString()).apply();
        PrefDataHandler.getInstance().getEditor().putString(EMAIL, emailET.getText().toString()).apply();

       /* try {
            if (imgBitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imgBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                String encodedBitmap = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
                e.putString(Constants.encodedProfileBitmap, encodedBitmap);
                e.apply();
            }
        } catch (Exception exc) {
            exc.printStackTrace();
            Crashlytics.logException(new Throwable("error saving image in string - " + exc.toString()));
        }*/
        saveProfileRequest();
    }

    private void saveProfileRequest() {
        final String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        showDialog("Saving Profile...");
        try {
            JSONObject requestFormObject = new JSONObject();
            requestFormObject.put(NAME, nameET.getText().toString());
            requestFormObject.put(CITYID, String.valueOf(cityEntity.getId()));
            requestFormObject.put(Constants.description, "description");
            if (agentTypeSP.getSelectedItemPosition() == 2 || agentTypeSP.getSelectedItemPosition() == 3) {
                requestFormObject.put(ORGANIZATION, organizationET.getText().toString());
                requestFormObject.put(DEPARTMENT, departmentET.getText().toString());
                requestFormObject.put(EMAIL, emailET.getText().toString());
            } else
                requestFormObject.put(COMMODITYID, String.valueOf(commodityEntity.getId()));
            requestFormObject.put(LANGUAGE, PrefDataHandler.getInstance().getSharedPref().getString(SELECTED_LANGUAGE, "en"));
            requestFormObject.put(PHONE_NUMBER, PrefDataHandler.getInstance().getSharedPref().getString(PHONE_NUMBER, "9971332741"));
            requestFormObject.put(AGENT_TYPE, agentTypeSP.getSelectedItemPosition());
            requestFormObject.put(PASSWORD, Base64.encodeToString(deviceId.getBytes(),Base64.NO_WRAP));
            HttpRequest request = null;
            if(editProfile) {
                requestFormObject.put(ID, PrefDataHandler.getInstance().getSharedPref().getInt(AGENT_ID, 0));
                request = new HttpRequest(true, TOKEN, PrefDataHandler.getInstance().getSharedPref().getString(TOKEN, ""));
            }
            else {
                request = new HttpRequest(false, "", "");
            }
            Callback callBack = new Callback() {

                @Override
                public void onFailure(Request request, IOException e) {
                    hideDialog();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_msg_unknown),Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(final com.squareup.okhttp.Response response) throws IOException {
                    hideDialog();
                    if(response.isSuccessful()) {
                        if(editProfile) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), R.string.profile_saved, Toast.LENGTH_LONG).show();
                                }
                            });
                            finish();
                        } else {
                        try {
                            JSONObject responseObj = new JSONObject(response.body().string());
                            PrefDataHandler.getInstance().getEditor().putInt(AGENT_ID, responseObj.optInt("Id")).apply();
                            getToken(deviceId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_msg_unknown), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                finish();
                        }

                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_msg_unknown),Toast.LENGTH_SHORT).show();
                            }
                        });
                        finish();
                    }

                }
            };
            RequestBody rb = RequestBody.create(request.JSON,requestFormObject.toString());
            if(editProfile) {
                request.doPostRequest(Constants.profileUrl + PrefDataHandler.getInstance().getSharedPref().getInt(AGENT_ID, 0), rb, callBack);
            }
            else
                request.doPostRequest(Constants.profileUrl, rb, callBack);
        } catch (Exception e) {
            hideDialog();
            e.printStackTrace();
        }
    }

    public void getToken(String password) {
        String usernamePassword = PrefDataHandler.getInstance().getSharedPref().getString(PHONE_NUMBER, "9971332741")  + ":" + password;
        String encodedUsernamePassword  = Base64.encodeToString(usernamePassword.getBytes(),Base64.NO_WRAP);
        HttpRequest request = new HttpRequest(true,"Authorization"," Basic " + encodedUsernamePassword);
        Callback cb = new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_msg_unknown),Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
            }

            @Override
            public void onResponse(com.squareup.okhttp.Response response) throws IOException {

                if(response.isSuccessful()) {
                    PrefDataHandler.getInstance().getEditor().putString(TOKEN, response.header("Token")).apply();
                    PrefDataHandler.getInstance().getEditor().putBoolean(PROFILE_SAVE, true).apply();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.profile_saved, Toast.LENGTH_LONG).show();
                        }
                    });
                    startActivity(new Intent(ProfileActivity.this, NewsFeedActivity.class));
                    finish();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_msg_unknown),Toast.LENGTH_SHORT).show();
                            Crashlytics.logException(new Throwable("Unsuccessful response in get token"));
                        }
                    });
                    finish();
                }
            }
        };
        try {
            RequestBody rb = RequestBody.create(request.JSON,"{}");
            request.doPostRequest(Constants.authenticationUrl,rb,cb);
        } catch (IOException e) {
            e.printStackTrace();
            Crashlytics.logException(new Throwable("Error in get token - " + e.toString()));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        String message = "";
        try {
            if (nameET.getText().toString().isEmpty()) {
                message = getString(R.string.name_required);
                throw new Exception();
            }
            if (locationTV.getText().toString().isEmpty()) {
                message = getString(R.string.city_required);
                throw new Exception();
            }
            if (agentTypeSP.getSelectedItemPosition() == 2 || agentTypeSP.getSelectedItemPosition() == 3) {
                if (organizationET.getText().toString().isEmpty()) {
                    message = getString(R.string.organization_required);
                    throw new Exception();
                }
                if (departmentET.getText().toString().isEmpty()) {
                    message = getString(R.string.department_required);
                    throw new Exception();
                }
                if (emailET.getText().toString().isEmpty()) {
                    message = getString(R.string.email_required);
                    throw new Exception();
                }
            } else {
                if (commodityTV.getText().toString().isEmpty()) {
                    message = getString(R.string.commodity_required);
                    throw new Exception();
                }
            }
            saveUserProfileData();
        } catch (Exception ex) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AgroConnect.agroEventBus.unregister(this);
    }
}
