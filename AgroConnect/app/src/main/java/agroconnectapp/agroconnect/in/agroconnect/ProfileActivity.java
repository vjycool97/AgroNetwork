package agroconnectapp.agroconnect.in.agroconnect;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;

import agroconnectapp.agroconnect.in.agroconnect.adapters.ProfilePagerAdapter;
import agroconnectapp.agroconnect.in.agroconnect.model.ProfileData;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;
import agroconnectapp.agroconnect.in.agroconnect.utility.HttpRequest;
import agroconnectapp.agroconnect.in.agroconnect.utility.Utils;

public class ProfileActivity extends BaseActivity {

    private ImageView agentImage ;
    private TextView agentName;
    private TextView city;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    private int agentId;
    private RelativeLayout errorLayout;
    private TextView errorText;
    private Button tryAgainBtn;
    private RelativeLayout topLayout;
    private ProfilePagerAdapter mProfilePagerAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if(Build.VERSION.SDK_INT >= 21)
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        errorLayout = (RelativeLayout) findViewById(R.id.error_layout);
        errorText = (TextView) findViewById(R.id.error_tv);
        tryAgainBtn = (Button) findViewById(R.id.try_again_btn);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        try {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        topLayout = (RelativeLayout) findViewById(R.id.top_layout);
        agentImage = (ImageView) findViewById(R.id.agent_iv);
        agentName = (TextView) findViewById(R.id.agent_name_tv);
        city = (TextView) findViewById(R.id.city_tv);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.container);

        agentId = getIntent().getExtras().getInt(Constants.agentIdKey);
        if(Utils.isNetworkAvailable(getApplicationContext()))
            fetchProfile();
        else {
            errorText.setText(getResources().getString(R.string.error_msg_internet));
            errorLayout.setVisibility(View.VISIBLE);
        }

        tryAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.isNetworkAvailable(getApplicationContext())) {
                    fetchProfile();
                } else {
                    errorText.setText(getResources().getString(R.string.error_msg_internet));
                    errorLayout.setVisibility(View.VISIBLE);
                }
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void fetchProfile(){
        progressBar.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
        try {
            HttpRequest request = new HttpRequest(true,Constants.token,sharedPreferences.getString(Constants.token,Constants.defaultToken));
            Callback callBack = new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            errorText.setText(getResources().getString(R.string.error_msg_unknown));
                            errorLayout.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onResponse(final Response response) throws IOException {
                    if(response.isSuccessful()){
                        try {
                            JSONObject responseObj = new JSONObject(response.body().string());
                            parseData(responseObj);
                        }catch (Exception e){
                            e.printStackTrace();
                            Crashlytics.logException(new Throwable("Error in parsing fetch profile - " + e.toString()));
                        }
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                errorText.setText(getResources().getString(R.string.error_msg_unknown));
                                errorLayout.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                Crashlytics.logException(new Throwable("Exception in fetching profile - " + response.toString()));
                            }
                        });
                    }

                }
            };
            request.doGetRequest(Constants.profileUrl + agentId, callBack);
        } catch (Exception e){
            Crashlytics.logException(new Throwable("Exception in fetching profile call - " + e.toString()));
        }
    }

    public void parseData(final JSONObject obj){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                topLayout.setVisibility(View.VISIBLE);
                tabLayout.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.VISIBLE);
                agentName.setText(obj.optString("Name"));
                city.setText(obj.optString("City"));
                ProfileData pd = new ProfileData();
                pd.setMandi(getString(R.string.mandi) + ": " + obj.optString("Mandi"));
                pd.setPhoneNumber(getString(R.string.mobile_number) + ": " + obj.optString("PhoneNumber"));
                if(obj.optString("Description").isEmpty())
                    pd.setDescription(getString(R.string.description) + ": " + getString(R.string.not_available));
                else
                    pd.setDescription(getString(R.string.description) + ": " + obj.optString("Description"));
                pd.setId(obj.optInt("Id"));
                pd.setAgentType(obj.optInt("AgentType"));
                pd.setCiy(getString(R.string.location) + ": " + obj.optString("City"));
                mProfilePagerAdapter = new ProfilePagerAdapter(ProfileActivity.this,getSupportFragmentManager(),pd);
                viewPager.setAdapter(mProfilePagerAdapter);
                tabLayout.setupWithViewPager(viewPager);
            }
        });

    }

}
