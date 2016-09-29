package agroconnectapp.agroconnect.in.agroconnect.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.List;

import agroconnectapp.agroconnect.in.agroconnect.AdvisoryActivity;
import agroconnectapp.agroconnect.in.agroconnect.MainActivity;
import agroconnectapp.agroconnect.in.agroconnect.fragments.MandiPricesFragment;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.components.ServerCommunication;
import agroconnectapp.agroconnect.in.agroconnect.databases.CityDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.databases.CommodityDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.databases.PrefDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.entities.CityEntity;
import agroconnectapp.agroconnect.in.agroconnect.entities.CommodityEntity;
import agroconnectapp.agroconnect.in.agroconnect.services.RegistrationService;
import agroconnectapp.agroconnect.in.agroconnect.uicomponents.AgroAlertDialog;
import agroconnectapp.agroconnect.in.agroconnect.uicomponents.FeedBackDialog;
import agroconnectapp.agroconnect.in.agroconnect.uicomponents.LanguageDialog;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;
import agroconnectapp.agroconnect.in.agroconnect.utility.HttpRequest;
import agroconnectapp.agroconnect.in.agroconnect.utility.Utils;
import agroconnectapp.agroconnect.in.agroconnect.fragments.CropProtectionFragment;

/**
 * Created by sumanta on 6/6/16.
 */
public class HomeActivity extends AgroActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!PrefDataHandler.getInstance().getSharedPref().getBoolean(IS_SYNC_CONTACT, false))
            sendContacts();
        setContentView(R.layout.activity_home);
        setToolbar();
        LinearLayout news_feed = (LinearLayout) findViewById(R.id.news_feed);
        LinearLayout feed = (LinearLayout) findViewById(R.id.buy_sell_feed);
        LinearLayout mandiPrices = (LinearLayout) findViewById(R.id.mandi_prices);
        LinearLayout gyanBhandar = (LinearLayout) findViewById(R.id.advisory);
        LinearLayout inputSuppliers = (LinearLayout) findViewById(R.id.input_suppliers);

        news_feed.setOnClickListener(this);
        feed.setOnClickListener(this);
        mandiPrices.setOnClickListener(this);
        gyanBhandar.setOnClickListener(this);
        inputSuppliers.setOnClickListener(this);

        boolean tokenSent = PrefDataHandler.getInstance().getSharedPref().getBoolean(SENT_TOKEN_TO_SERVER, false);
        if(Utils.checkPlayServices(this)) {
            if(!tokenSent) {
                Intent intent = new Intent(this, RegistrationService.class);
                startService(intent);
            }
        }

        if(getIntent().hasExtra("id")) {
            String id = getIntent().getStringExtra("id");
            try {
                if ("1".equals(id)) {
                    Intent intent = new Intent(this, ProfileActivity.class);
                    intent.putExtra(Constants.agentIdKey, getIntent().getIntExtra(Constants.agentIdKey, 0));
                    startActivity(intent);
                } else if ("3".equals(id)) {
                    Intent intent = new Intent(this, MandiPricesFragment.class);
                    intent.putExtra(Constants.commodityId, getIntent().getStringExtra(Constants.commodityId));
                    intent.putExtra(Constants.cityId, getIntent().getStringExtra(Constants.cityId));
                    startActivity(intent);
                } else if ("4".equals(id)) {
                    Intent intent = new Intent(this, AdvisoryActivity.class);
                    intent.putExtra(Constants.postId, getIntent().getIntExtra(Constants.postId, 0));
                    startActivity(intent);
                } else if ("5".equals(id)) {
                    Intent intent = new Intent(this, FirstNewsFeedActivity.class);
                    intent.putExtra("feedId", getIntent().getIntExtra("feedId", 0));
                    startActivity(intent);
                } else {
                    throw new Exception();
                }
            } catch (Exception ex) {
                startActivity(new Intent(this, HomeActivity.class));
            }
        } else {
            if (!PrefDataHandler.getInstance().getSharedPref().getBoolean(IS_FEEDBACK_SEND, false)) {
                int count = PrefDataHandler.getInstance().getSharedPref().getInt(FEEDBACK_COUNTER, 0);
                if (count < 4) {
                    PrefDataHandler.getInstance().getEditor().putInt(FEEDBACK_COUNTER, ++count).apply();
                } else {
                    FeedBackDialog feedBackDialog = new FeedBackDialog(HomeActivity.this);
                    feedBackDialog.show();
                }
            }
            if (!PrefDataHandler.getInstance().getSharedPref().getBoolean(IS_SHARE, false)) {
                int count = PrefDataHandler.getInstance().getSharedPref().getInt(SHARE_COUNT, 0);
                if (count < 2) {
                    PrefDataHandler.getInstance().getEditor().putInt(SHARE_COUNT, ++count).apply();
                } else {
                    AgroAlertDialog dialog = new AgroAlertDialog(HomeActivity.this, getString(R.string.please_share), getString(R.string.app_share), getString(R.string.share), getString(R.string.later), new AgroAlertDialog.Confirmation() {
                        @Override
                        public void onPositiveConfirmation() {
                            PrefDataHandler.getInstance().getEditor().putBoolean(IS_SHARE, true);
                            Intent share = new Intent(Intent.ACTION_SEND);
                            share.setType("text/plain");
                            String shareBody = getString(R.string.share_text);
                            shareBody = shareBody + Constants.playStoreUrl + getPackageName();
                            share.putExtra(Intent.EXTRA_TEXT, shareBody);
                            if (Utils.isPackageInstalled("com.whatsapp", HomeActivity.this)) {
                                share.setPackage("com.whatsapp");
                                startActivity(Intent.createChooser(share, "Share Image"));
                            } else {
                                Toast.makeText(HomeActivity.this, "Whatsapp application not available", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onNegativeConfirmation() {
                            PrefDataHandler.getInstance().getEditor().putInt(SHARE_COUNT, 0).apply();
                        }
                    });
                    dialog.show();
                }
            }
            if(PrefDataHandler.getInstance().getSharedPref().getBoolean(IS_UPDATE, false)) {
                int count = PrefDataHandler.getInstance().getSharedPref().getInt(UPDATE_COUNT, 0);
                if (count < 2) {
                    PrefDataHandler.getInstance().getEditor().putInt(UPDATE_COUNT, ++count).apply();
                } else {
                    AgroAlertDialog dialog = new AgroAlertDialog(HomeActivity.this, getString(R.string.update)+" AgroConnect", getString(R.string.update_message), getString(R.string.update), getString(R.string.later), new AgroAlertDialog.Confirmation() {
                        @Override
                        public void onPositiveConfirmation() {
                            PrefDataHandler.getInstance().getEditor().putBoolean(IS_UPDATE, false).apply();
                            Uri uri = Uri.parse("market://details?id=" + getPackageName());
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }

                        @Override
                        public void onNegativeConfirmation() {
                            PrefDataHandler.getInstance().getEditor().putInt(UPDATE_COUNT, 0).apply();
                        }
                    });
                    dialog.show();
                }
            }
            getGeneralInformation();
        }
    }

    private void getGeneralInformation() {
        ServerCommunication.INSTANCE.getServerData(HomeActivity.this, true, false, com.android.volley.Request.Method.GET, GENERAL_INFROMATION_URI, GENERAL_INFROMATION_URI,
                null, new ServerCommunication.OkuTaskListener() {
                    @Override
                    public void onPostExecute(String result) {
                        if(!TextUtils.isEmpty(result)) {
                            try {
                                JSONObject resultObject = new JSONObject(result);
                                int cityVersion = resultObject.optInt("CityVersion");
                                if(PrefDataHandler.getInstance().getSharedPref().getInt(CITY_VERSION, 0) != cityVersion) {
                                    getCities();
                                }
                                int commodityVersion = resultObject.optInt("CommodityVersion");
                                if(PrefDataHandler.getInstance().getSharedPref().getInt(COMMODITY_VERSION, 0) != commodityVersion) {
                                    getCommodities();
                                }
                                JSONObject versionObject = resultObject.optJSONObject("AppVersionDetail");
                                String appVersion = versionObject.optString("AppVersion");
                                if(!Utils.getCurrentVersion(HomeActivity.this).equals(appVersion)) {
                                    PrefDataHandler.getInstance().getEditor().putBoolean(IS_UPDATE, true).apply();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onError() {

                    }

                    @Override
                    public void noNetwork() {

                    }
                });
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Argo Connect");
        toolbar.setTitleTextColor(getResources().getColor(R.color.color_white));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.news_feed:
                startActivity(new Intent(this, NewsFeedActivity.class));
                break;
            case R.id.buy_sell_feed:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.mandi_prices:
                startActivity(new Intent(this, MandiPricesFragment.class));
                break;

            case R.id.advisory:
                int type = PrefDataHandler.getInstance().getSharedPref().getInt(AGENT_TYPE, 0);
                if(type == 2)
                    startActivity(new Intent(this, NewAdvisoryActivity.class));
                else
                    startActivity(new Intent(this, UserAdvisoryActivity.class));
                break;

            case R.id.input_suppliers:
                startActivity(new Intent(this, CropProtectionFragment.class));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_rate: {
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(myAppLinkToMarket);
                break;
            }
            case R.id.action_contact: {
                FeedBackDialog feedBackDialog = new FeedBackDialog(HomeActivity.this);
                feedBackDialog.show();
                break;
            }
            case R.id.action_invite: {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = getString(R.string.share_text);
                shareBody = shareBody + Constants.playStoreUrl + getPackageName();
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                break;
            }
            case R.id.action_language: {
                LanguageDialog languageDialog = new LanguageDialog(HomeActivity.this);
                languageDialog.show();
                break;
            }
            case R.id.action_my_profile: {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                intent.putExtra(EDIT_PROFILE, true);
                startActivity(intent);
                break;
            }
            case R.id.action_notification: {
                Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendContacts() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray contactsArray = fetchContacts();
                    HttpRequest request = new HttpRequest(true, TOKEN, PrefDataHandler.getInstance().getSharedPref().getString(TOKEN, ""));
                    Callback cb = new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                        }
                        @Override
                        public void onResponse(Response response) throws IOException {

                            Log.e("Contacts",String.valueOf(response.isSuccessful()));
                            PrefDataHandler.getInstance().getEditor().putBoolean(IS_SYNC_CONTACT, true).apply();
                        }
                    };
                    RequestBody rb = RequestBody.create(request.JSON,contactsArray.toString());
                    request.doPostRequest(Constants.sendContactsUrl, rb, cb);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private JSONArray fetchContacts() {
        JSONArray array = new JSONArray();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    try {
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            JSONObject obj = new JSONObject();
                            obj.put("ContactId", id);
                            obj.put("ContactName", name);
                            obj.put("ContactPhoneNumber", phoneNo);
                            array.put(obj);
                        }
                        pCur.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return array;
    }

    private void getCities() {
        ArrayMap<String, String> paramMap = new ArrayMap<>();
        paramMap.put("cityVersion", String.valueOf(PrefDataHandler.getInstance().getSharedPref().getInt(CITY_VERSION, 0)));
        paramMap.put("LanguageId", PrefDataHandler.getInstance().getSharedPref().getString(SELECTED_LANGUAGE, "hi"));
        paramMap.put("lat", PrefDataHandler.getInstance().getSharedPref().getString(LATITUDE, ""));
        paramMap.put("lng", PrefDataHandler.getInstance().getSharedPref().getString(LONGITUDE, ""));
        ServerCommunication.INSTANCE.getServerData(HomeActivity.this, false, true, com.android.volley.Request.Method.GET, CITY_URI, CITY_URI, paramMap, new ServerCommunication.OkuTaskListener() {
            @Override
            public void onPostExecute(String result) {
                if(!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject responseObject = new JSONObject(result);
                        int version = responseObject.optInt("CityVersion");
                        PrefDataHandler.getInstance().getEditor().putInt(CITY_VERSION, version).apply();
                        List<CityEntity> cityList = new Gson().fromJson(responseObject.getJSONArray("CityDTO").toString(), new TypeToken<List<CityEntity>>() {}.getType());
                        CityDataHandler.addAllCityData(HomeActivity.this, cityList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError() {

            }

            @Override
            public void noNetwork() {

            }
        });
    }

    private void getCommodities() {
        ArrayMap<String, String> paramMap = new ArrayMap<>();
        paramMap.put("commodityVersion", String.valueOf(PrefDataHandler.getInstance().getSharedPref().getInt(COMMODITY_VERSION, 0)));
        paramMap.put("LanguageId", PrefDataHandler.getInstance().getSharedPref().getString(SELECTED_LANGUAGE, "hi"));
        paramMap.put("lat", PrefDataHandler.getInstance().getSharedPref().getString(LATITUDE, ""));
        paramMap.put("lng", PrefDataHandler.getInstance().getSharedPref().getString(LONGITUDE, ""));
        ServerCommunication.INSTANCE.getServerData(HomeActivity.this, false, true, com.android.volley.Request.Method.GET, COMMODITY_URI, COMMODITY_URI, paramMap, new ServerCommunication.OkuTaskListener() {
            @Override
            public void onPostExecute(String result) {
                if(!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject responseObject = new JSONObject(result);
                        int version = responseObject.optInt("CommodityVersion");
                        PrefDataHandler.getInstance().getEditor().putInt(COMMODITY_VERSION, version).apply();
                        List<CommodityEntity> commodityList = new Gson().fromJson(responseObject.getJSONArray("CommodityDTO").toString(), new TypeToken<List<CommodityEntity>>() {}.getType());
                        CommodityDataHandler.addAllCommodityData(HomeActivity.this, commodityList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError() {

            }

            @Override
            public void noNetwork() {

            }
        });
    }
}
