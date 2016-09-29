package agroconnectapp.agroconnect.in.agroconnect.activities;

import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.android.volley.Request;
import org.json.JSONException;
import org.json.JSONObject;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.components.ServerCommunication;
import agroconnectapp.agroconnect.in.agroconnect.utility.Utils;

/**
 * Created by sumanta on 27/6/16.
 */
public class FeedThreeActivity extends AgroActivity {

    private int feedId;
    private TextView contentTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_three);
        setToolbar();
        feedId = getIntent().getIntExtra("feedId", 0);
        getDataFromServer();
        contentTV = (TextView) findViewById(R.id.contentTV);
        Linkify.addLinks(contentTV, Linkify.ALL);
        contentTV.setMovementMethod(LinkMovementMethod.getInstance());
    }
    private void getDataFromServer() {
        showDialog("Loading detail...");
        ArrayMap<String, String> paramMap = new ArrayMap<>();
        ServerCommunication.INSTANCE.getServerData(FeedThreeActivity.this, true, true, Request.Method.GET, NEWS_FEED_URI + "/" + feedId, NEWS_FEED_URI, paramMap, new ServerCommunication.OkuTaskListener() {
            @Override
            public void onPostExecute(String result) {
                hideDialog();
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String content = jsonObject.getString("Content");
                        contentTV.setText(content);
                        Linkify.addLinks(contentTV, Linkify.ALL);
                        contentTV.setMovementMethod(LinkMovementMethod.getInstance());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
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

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("AgroConnect");
        toolbar.setTitleTextColor(getResources().getColor(R.color.color_white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_whatsapp, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }  else if (id == R.id.whatsappId) {
            Utils.takeScreenShotAndShare(FeedThreeActivity.this, contentTV);
        }
        return super.onOptionsItemSelected(item);
    }
}
