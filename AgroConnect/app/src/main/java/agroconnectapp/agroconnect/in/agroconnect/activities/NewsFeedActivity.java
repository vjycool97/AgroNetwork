package agroconnectapp.agroconnect.in.agroconnect.activities;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.adapters.NewsFeedPagerAdapter;
import agroconnectapp.agroconnect.in.agroconnect.components.AgroServerCommunication;
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
import agroconnectapp.agroconnect.in.agroconnect.uicomponents.agrobutton.FloatingActionButton;
import agroconnectapp.agroconnect.in.agroconnect.uicomponents.agrobutton.FloatingActionsMenu;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;
import agroconnectapp.agroconnect.in.agroconnect.utility.HttpRequest;
import agroconnectapp.agroconnect.in.agroconnect.utility.KeyIDS;
import agroconnectapp.agroconnect.in.agroconnect.utility.Utils;
import agroconnectapp.agroconnect.in.agroconnect.fragments.CropProtectionFragment;

/**
 * Created by sumanta on 26/6/16.
 */
public class NewsFeedActivity extends AgroActivity implements View.OnClickListener {

    private NewsFeedPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private ImageView profileImage;
    private TextView nameTv;
    private FloatingActionsMenu actionMenu;
    private FloatingActionButton questionBtn, cropQuestionBtn;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    /***
     * NewsFeedActivity is the Main Activity
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!PrefDataHandler.getInstance().getSharedPref().getBoolean(IS_SYNC_CONTACT, false))
            sendContacts();
        setContentView(R.layout.activity_news_feed);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.container);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        // Tie DrawerLayout events to the ActionBarToggle
        drawerLayout.addDrawerListener(drawerToggle);

        navigationView = (NavigationView) findViewById(R.id.navView);
        setupDrawerContent(navigationView);
        View hView = navigationView.getHeaderView(0);
        profileImage = (ImageView) hView.findViewById(R.id.profile_iv);
        nameTv = (TextView) hView.findViewById(R.id.name_tv);
        String name = PrefDataHandler.getInstance().getSharedPref().getString(KeyIDS.NAME, "");
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                nameTv.setText(sharedPreferences.getString(KeyIDS.NAME, ""));
            }
        };
        PrefDataHandler.getInstance().getSharedPref().registerOnSharedPreferenceChangeListener(listener);
        nameTv.setText(name);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        actionMenu = (FloatingActionsMenu) findViewById(R.id.actionMenu);
        questionBtn = (FloatingActionButton) findViewById(R.id.questionBtn);
        cropQuestionBtn = (FloatingActionButton) findViewById(R.id.cropQuestionBtn);
        questionBtn.setImageResource(R.mipmap.question_mark);
        cropQuestionBtn.setImageResource(R.mipmap.question_mark);
        questionBtn.setOnClickListener(this);
        cropQuestionBtn.setOnClickListener(this);
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                advisorShareDialog();
            }
        });*/

        ImageView notification_btn = (ImageView) findViewById(R.id.action_notification);
        notification_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewsFeedActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.searchTV).setOnClickListener(this);

        Intent receivedIntent = getIntent();
        String receivedAction = receivedIntent.getAction();
        if (Intent.ACTION_SEND.equals(receivedAction)) {
            showShareDialog(receivedIntent);
        } else {
            pagerAdapter = new NewsFeedPagerAdapter(NewsFeedActivity.this, getSupportFragmentManager());
            viewPager.setAdapter(pagerAdapter);
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setOffscreenPageLimit(1);
        }
        setFloatingButtonControls();


        boolean tokenSent = PrefDataHandler.getInstance().getSharedPref().getBoolean(SENT_TOKEN_TO_SERVER, false);
        if (Utils.checkPlayServices(this)) {
            if (!tokenSent) {
                Intent intent = new Intent(this, RegistrationService.class);
                startService(intent);
            }
        }

        if (getIntent().hasExtra("id")) {
            String id = getIntent().getStringExtra("id");
            String feedType = getIntent().getStringExtra("feedType");
            try {
                if ("1".equals(id)) {
                    Intent intent = new Intent(this, ProfileActivity.class);
                    intent.putExtra(Constants.agentIdKey, getIntent().getIntExtra(Constants.agentIdKey, 0));
                    startActivity(intent);
                } else if ("3".equals(id)) {
//                    Intent intent = new Intent(this, MandiPricesFragment.class);
//                    intent.putExtra(Constants.commodityId, getIntent().getStringExtra(Constants.commodityId));
//                    intent.putExtra(Constants.cityId, getIntent().getStringExtra(Constants.cityId));
                    viewPager.setCurrentItem(2);
//                    startActivity(intent);
                } /*else if ("4".equals(id)) {
                    Intent intent = new Intent(this, AdvisoryActivity.class);
                    intent.putExtra(Constants.postId, getIntent().getIntExtra(Constants.postId, 0));
                    startActivity(intent);
                }*/ else if ("5".equals(id)) {
//                    Intent intent = new Intent(this, FirstNewsFeedActivity.class);
//                    intent.putExtra("feedId", getIntent().getStringExtra("feedId"));
//                    startActivity(intent);
                    viewPager.setCurrentItem(0);
//                    if ("1".equals(feedType)){
//                        Intent intent = new Intent(this, FirstNewsFeedActivity.class);
//                       intent.putExtra("feedId", getIntent().getStringExtra("feedId"));
//                    startActivity(intent);
//                    }
//                    if("2".equals(feedType)){
//                        Intent intent = new Intent(this, SecondNewsFeedActivity.class);
//                        intent.putExtra("feedId", getIntent().getStringExtra("feedId"));
//                    }else{
//                        Intent intent = new Intent(this, ThirdNewsFeedActivity.class);
//                        intent.putExtra("feedId", getIntent().getStringExtra("feedId"));
//                    }

                } else {
                    throw new Exception();
                }
            } catch (Exception ex) {
                startActivity(new Intent(this, NewsFeedActivity.class));
            }
        } else {
            if (!PrefDataHandler.getInstance().getSharedPref().getBoolean(IS_FEEDBACK_SEND, false)) {
                int count = PrefDataHandler.getInstance().getSharedPref().getInt(FEEDBACK_COUNTER, 0);
                if (count < 4) {
                    PrefDataHandler.getInstance().getEditor().putInt(FEEDBACK_COUNTER, ++count).apply();
                } else {
                    FeedBackDialog feedBackDialog = new FeedBackDialog(NewsFeedActivity.this);
                    //feedBackDialog.show();
                }
            }
            if (!PrefDataHandler.getInstance().getSharedPref().getBoolean(IS_SHARE, false)) {
                int count = PrefDataHandler.getInstance().getSharedPref().getInt(SHARE_COUNT, 0);
                if (count < 2) {
                    PrefDataHandler.getInstance().getEditor().putInt(SHARE_COUNT, ++count).apply();
                } else {

                    /*** Share dialog for WhatsApp at the time of main screen loading */

                   final AgroAlertDialog dialog = new AgroAlertDialog(NewsFeedActivity.this, getString(R.string.please_share), getString(R.string.app_share), getString(R.string.share), getString(R.string.later), new AgroAlertDialog.Confirmation() {
                        @Override
                        public void onPositiveConfirmation() {
                            PrefDataHandler.getInstance().getEditor().putBoolean(IS_SHARE, true);
                            Intent share = new Intent(Intent.ACTION_SEND);
                            share.setType("text/plain");
                            String shareBody = getString(R.string.share_text);
                            shareBody = shareBody + Constants.playStoreUrl + getPackageName();
                            share.putExtra(Intent.EXTRA_TEXT, shareBody);
                            if (Utils.isPackageInstalled("com.whatsapp", NewsFeedActivity.this)) {
                                share.setPackage("com.whatsapp");
                                startActivity(Intent.createChooser(share, "Share Image"));
                            } else {
                                Toast.makeText(NewsFeedActivity.this, "Whatsapp application not available", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onNegativeConfirmation() {
                            PrefDataHandler.getInstance().getEditor().putInt(SHARE_COUNT, 0).apply();
                        }
                    });
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            dialog.show();
//                        }
//                    }, 10000);
                }
            }
            if (PrefDataHandler.getInstance().getSharedPref().getBoolean(IS_UPDATE, false)) {
                int count = PrefDataHandler.getInstance().getSharedPref().getInt(UPDATE_COUNT, 0);
                if (count < 2) {
                    PrefDataHandler.getInstance().getEditor().putInt(UPDATE_COUNT, ++count).apply();
                } else {
                    final AgroAlertDialog dialog = new AgroAlertDialog(NewsFeedActivity.this, getString(R.string.update) + " AgroConnect", getString(R.string.update_message), getString(R.string.update), getString(R.string.later), new AgroAlertDialog.Confirmation() {
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
                    //dialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.show();
                        }
                    }, 8000);


                }
            }
            getGeneralInformation();
        }
    }



    View bckgroundDimmer;

    private void setFloatingButtonControls() {
        this.bckgroundDimmer = findViewById(R.id.background_dimmer);
        bckgroundDimmer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                actionMenu.collapseImmediately();
                return true;
            }
        });
        this.actionMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                bckgroundDimmer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuCollapsed() {
                bckgroundDimmer.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.questionBtn: {
                actionMenu.toggle();
                Intent intent = new Intent(NewsFeedActivity.this, FragmentWrapperActivity.class);
                intent.putExtra("className", "agroconnectapp.agroconnect.in.agroconnect.fragments.ComposeFragment");
                intent.putExtra("isGeneral", true);
                startActivity(intent);
                overridePendingTransition(R.anim.flipin, R.anim.flipout);
                break;
            }
            case R.id.cropQuestionBtn: {
                actionMenu.toggle();
                Intent intent = new Intent(NewsFeedActivity.this, FragmentWrapperActivity.class);
                intent.putExtra("className", "agroconnectapp.agroconnect.in.agroconnect.fragments.ComposeFragment");
                intent.putExtra("isGeneral", false);
                startActivity(intent);
                overridePendingTransition(R.anim.flipin, R.anim.flipout);
                break;
            }
            case R.id.searchTV: {
                Intent intent = new Intent(NewsFeedActivity.this, NewsFeedSearchActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    private void getGeneralInformation() {
        ServerCommunication.INSTANCE.getServerData(NewsFeedActivity.this, true, false, com.android.volley.Request.Method.GET, GENERAL_INFROMATION_URI, GENERAL_INFROMATION_URI,
                null, new ServerCommunication.OkuTaskListener() {
                    @Override
                    public void onPostExecute(String result) {
                        if (!TextUtils.isEmpty(result)) {
                            try {
                                JSONObject resultObject = new JSONObject(result);
                                int cityVersion = resultObject.optInt("CityVersion");
                                if (PrefDataHandler.getInstance().getSharedPref().getInt(CITY_VERSION, 0) != cityVersion) {
                                    getCities();
                                }
                                int commodityVersion = resultObject.optInt("CommodityVersion");
                                if (PrefDataHandler.getInstance().getSharedPref().getInt(COMMODITY_VERSION, 0) != commodityVersion) {
                                    getCommodities();
                                }
                                JSONObject versionObject = resultObject.optJSONObject("AppVersionDetail");
                                String appVersion = versionObject.optString("AppVersion");
                                if (!Utils.getCurrentVersion(NewsFeedActivity.this).equals(appVersion)) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PrefDataHandler.getInstance().getSharedPref().unregisterOnSharedPreferenceChangeListener(listener);
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

                            Log.e("Contacts", String.valueOf(response.isSuccessful()));
                            PrefDataHandler.getInstance().getEditor().putBoolean(IS_SYNC_CONTACT, true).apply();
                        }
                    };
                    RequestBody rb = RequestBody.create(request.JSON, contactsArray.toString());
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
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
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


    private void showShareDialog(Intent receivedIntent) {
        final Dialog shareDialog = new Dialog(this);
        shareDialog.setContentView(R.layout.share_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = shareDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        shareDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                pagerAdapter = new NewsFeedPagerAdapter(NewsFeedActivity.this, getSupportFragmentManager());
                viewPager.setAdapter(pagerAdapter);
                tabLayout.setupWithViewPager(viewPager);
                viewPager.setOffscreenPageLimit(1);
            }
        });
        ((TextView) shareDialog.findViewById(R.id.nameTV)).setText(PrefDataHandler.getInstance().getSharedPref().getString(NAME, ""));
        String subject = receivedIntent.getStringExtra(Intent.EXTRA_SUBJECT);
        String text = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
        final JSONObject paramObject = new JSONObject();
        try {
            if (text.contains("https://youtu.be/")) {
                paramObject.put("FeedType", "2");
                String videoId = text.replace("https://youtu.be/", "");
                String imageUrl = "http://img.youtube.com/vi/" + videoId + "/0.jpg";
                paramObject.put("ImageUrl", imageUrl);
                Picasso.with(NewsFeedActivity.this)
                        .load(imageUrl)
                        .fit()
                        .placeholder(R.mipmap.icon_launcher)
                        .error(R.mipmap.icon_launcher)
                        .into((ImageView) shareDialog.findViewById(R.id.imageView));
            } else {
                paramObject.put("FeedType", "1");
                Uri imageUri = (Uri) receivedIntent.getParcelableExtra("share_screenshot_as_stream");
                paramObject.put("ImageUrl", imageUri);
                Picasso.with(NewsFeedActivity.this)
                        .load(imageUri)
                        .fit()
                        .placeholder(R.mipmap.icon_launcher)
                        .error(R.mipmap.icon_launcher)
                        .into((ImageView) shareDialog.findViewById(R.id.imageView));
            }
            paramObject.put("Title", subject);
            paramObject.put("SourceUrl", text);
            ((TextView) shareDialog.findViewById(R.id.sourceTV)).setText(text);
            ((TextView) shareDialog.findViewById(R.id.titleTV)).setText(subject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        shareDialog.findViewById(R.id.shareBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushFeedToServer(paramObject, shareDialog);
            }
        });
        shareDialog.show();
    }

    private void advisorShareDialog() {
        final Dialog shareDialog = new Dialog(this);
        shareDialog.setContentView(R.layout.advisor_share_layout);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = shareDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        ((TextView) shareDialog.findViewById(R.id.nameTV)).setText(PrefDataHandler.getInstance().getSharedPref().getString(NAME, ""));
        final EditText titleET = (EditText) shareDialog.findViewById(R.id.titleET);
        final EditText contentET = (EditText) shareDialog.findViewById(R.id.contentET);

        shareDialog.findViewById(R.id.shareBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "";
                try {
                    if (TextUtils.isEmpty(titleET.getText().toString())) {
                        message = "Please provide title";
                        throw new Exception();
                    }
                    if (TextUtils.isEmpty(contentET.getText().toString())) {
                        message = "Please provide content";
                        throw new Exception();
                    }
                    final JSONObject paramObject = new JSONObject();
                    try {
                        paramObject.put("FeedType", "3");
                        paramObject.put("Title", titleET.getText().toString());
                        paramObject.put("Content", contentET.getText().toString());
                        pushFeedToServer(paramObject, shareDialog);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception ex) {
                    Toast.makeText(NewsFeedActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
        shareDialog.show();
    }

    private void pushFeedToServer(JSONObject paramObject, final Dialog shareDialog) {
        showDialog("Sharing...");
        AgroServerCommunication.INSTANCE.getServerData(NewsFeedActivity.this, FEED_UPLOAD_URI, FEED_UPLOAD_URI, paramObject, new AgroServerCommunication.OkuTaskListener() {
            @Override
            public void onPostExecute(JSONObject resultObject) {
                hideDialog();
                shareDialog.cancel();
            }

            @Override
            public void onError() {
                hideDialog();
            }

            @Override
            public void noNetwork() {
                hideDialog();
            }
        });
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }


    private void selectDrawerItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_profile:
                Intent intent = new Intent(NewsFeedActivity.this, ProfileActivity.class);
                intent.putExtra(EDIT_PROFILE, true);
                startActivity(intent);
                break;
            case R.id.nav_notification:
                Intent intentN = new Intent(NewsFeedActivity.this, NotificationActivity.class);
                startActivity(intentN);
                break;
            case R.id.nav_lang:
                LanguageDialog languageDialog = new LanguageDialog(NewsFeedActivity.this);
                languageDialog.show();
                break;
            case R.id.nav_contact:
                FeedBackDialog feedBackDialog = new FeedBackDialog(NewsFeedActivity.this);
                feedBackDialog.show();
                break;
            case R.id.nav_rating:
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(myAppLinkToMarket);
                break;
            case R.id.nav_invite:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = getString(R.string.share_text);
                shareBody = shareBody + Constants.playStoreUrl + getPackageName();
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                break;

        }


        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        drawerLayout.closeDrawers();
    }

    private void getCities() {
        ArrayMap<String, String> paramMap = new ArrayMap<>();
        paramMap.put("cityVersion", String.valueOf(PrefDataHandler.getInstance().getSharedPref().getInt(CITY_VERSION, 0)));
        paramMap.put("LanguageId", PrefDataHandler.getInstance().getSharedPref().getString(SELECTED_LANGUAGE, "hi"));
        paramMap.put("lat", PrefDataHandler.getInstance().getSharedPref().getString(LATITUDE, ""));
        paramMap.put("lng", PrefDataHandler.getInstance().getSharedPref().getString(LONGITUDE, ""));
        ServerCommunication.INSTANCE.getServerData(NewsFeedActivity.this, false, true, com.android.volley.Request.Method.GET, CITY_URI, CITY_URI, paramMap, new ServerCommunication.OkuTaskListener() {
            @Override
            public void onPostExecute(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject responseObject = new JSONObject(result);
                        int version = responseObject.optInt("CityVersion");
                        PrefDataHandler.getInstance().getEditor().putInt(CITY_VERSION, version).apply();
                        List<CityEntity> cityList = new Gson().fromJson(responseObject.getJSONArray("CityDTO").toString(), new TypeToken<List<CityEntity>>() {
                        }.getType());
                        CityDataHandler.addAllCityData(NewsFeedActivity.this, cityList);
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
        ServerCommunication.INSTANCE.getServerData(NewsFeedActivity.this, false, true, com.android.volley.Request.Method.GET, COMMODITY_URI, COMMODITY_URI, paramMap, new ServerCommunication.OkuTaskListener() {
            @Override
            public void onPostExecute(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject responseObject = new JSONObject(result);
                        int version = responseObject.optInt("CommodityVersion");
                        PrefDataHandler.getInstance().getEditor().putInt(COMMODITY_VERSION, version).apply();
                        List<CommodityEntity> commodityList = new Gson().fromJson(responseObject.getJSONArray("CommodityDTO").toString(), new TypeToken<List<CommodityEntity>>() {
                        }.getType());
                        CommodityDataHandler.addAllCommodityData(NewsFeedActivity.this, commodityList);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE! Make sure to override the method with only a single `Bundle` argument
//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    @Override
    public void onBackPressed() {
        int currentItem = viewPager.getCurrentItem();
        if (currentItem == pagerAdapter.getCount() - 1) {
            CropProtectionFragment fragment = (CropProtectionFragment) pagerAdapter.getItem(currentItem);
//        CropProtectionFragment fragment = (CropProtectionFragment) getSupportFragmentManager().findFragmentById();
            boolean result = false;
            if (fragment != null) {
                result = fragment.onBackPressed();
            }
            if (result || fragment == null) {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }
}