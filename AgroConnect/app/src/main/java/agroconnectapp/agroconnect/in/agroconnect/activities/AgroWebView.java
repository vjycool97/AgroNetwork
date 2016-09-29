package agroconnectapp.agroconnect.in.agroconnect.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.components.ServerCommunication;
import agroconnectapp.agroconnect.in.agroconnect.entities.NewsFeedEntity;
import agroconnectapp.agroconnect.in.agroconnect.utility.Utils;

public class AgroWebView extends AgroActivity {

    private ProgressBar progressBar;
    private WebView okuWebView;
    private int feedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agro_web_view);
        setToolbar();
        feedId = getIntent().getIntExtra("feedId", 0);
        getDataFromServer();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        okuWebView = (WebView) findViewById(R.id.webView);

        okuWebView.setWebViewClient(new MyBrowser());
        okuWebView.getSettings().setLoadsImagesAutomatically(true);
        okuWebView.getSettings().setJavaScriptEnabled(true);
        okuWebView.getSettings().setSupportZoom(true);
        okuWebView.getSettings().setBuiltInZoomControls(true);
        okuWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

    }

    private void getDataFromServer() {
        showDialog("Loading detail...");
        ArrayMap<String, String> paramMap = new ArrayMap<>();
        ServerCommunication.INSTANCE.getServerData(AgroWebView.this, true, true, Request.Method.GET, NEWS_FEED_URI + "/" + feedId, NEWS_FEED_URI, paramMap, new ServerCommunication.OkuTaskListener() {
            @Override
            public void onPostExecute(String result) {
                hideDialog();
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String content = jsonObject.getString("Content");
                        String sourceURL = jsonObject.getString("SourceUrl");
                        if(TextUtils.isEmpty(sourceURL) || sourceURL.equalsIgnoreCase("null")) {
                            findViewById(R.id.buttomLinear).setVisibility(View.GONE);
                        } else {
                            findViewById(R.id.buttomLinear).setVisibility(View.VISIBLE);
                            ((TextView)findViewById(R.id.sourceTV)).setText(sourceURL);
                        }
                        okuWebView.loadData(content, "text/html", "UTF-8");
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
            Utils.takeScreenShotAndShare(AgroWebView.this, okuWebView);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && okuWebView.canGoBack()) {
            okuWebView.goBack();
            return true;
        } else {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (okuWebView.canGoBack()) {
            okuWebView.goBack();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.flipin_reverse, R.anim.flipout_reverse);
        }
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.setVisibility(View.GONE);
        }
    }
}