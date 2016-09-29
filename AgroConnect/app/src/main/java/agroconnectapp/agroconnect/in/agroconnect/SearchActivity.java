package agroconnectapp.agroconnect.in.agroconnect;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import agroconnectapp.agroconnect.in.agroconnect.activities.AgroActivity;
import agroconnectapp.agroconnect.in.agroconnect.adapters.AutoCompleteTextViewAdapter;
import agroconnectapp.agroconnect.in.agroconnect.fragments.FeedFragment;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;
import agroconnectapp.agroconnect.in.agroconnect.utility.CustomAutoCompleteTextView;
import agroconnectapp.agroconnect.in.agroconnect.utility.KeypadUtil;
import agroconnectapp.agroconnect.in.agroconnect.utility.Utils;

public class SearchActivity extends AgroActivity implements AdapterView.OnItemClickListener,View.OnKeyListener{

    private CustomAutoCompleteTextView commodityEt;
    private InputMethodManager imm;
    private FeedFragment fragmentFeed;
    private RelativeLayout mainLayout;
    private PickerDialog pickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        pickerDialog = new PickerDialog(this);

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle args = new Bundle();
        args.putInt(Constants.agentIdKey, -1);
        fragmentFeed = (FeedFragment)Fragment.instantiate(SearchActivity.this, FeedFragment.class.getName(), args);
        fragmentTransaction.add(R.id.main_layout, fragmentFeed);
        fragmentTransaction.commit();

        setToolbar();
        commodityEt = (CustomAutoCompleteTextView) findViewById(R.id.auto_complete_text_view); //the text editor
        ProgressBar pb = (android.widget.ProgressBar) findViewById(R.id.pb_loading_indicator);
        pb.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        commodityEt.setLoadingIndicator(pb);
        commodityEt.setHint(" Search commodity or city ");
        commodityEt.setThreshold(2);
        commodityEt.setAdapter(new AutoCompleteTextViewAdapter(this, Constants.TYPE_SEARCH));
        commodityEt.setOnItemClickListener(this);
        commodityEt.requestFocus();
        commodityEt.setOnKeyListener(this);

        //open the keyboard focused in the edtSearch
//        KeypadUtil.showKeypad(this);

        /*try {
            ActionBar action = getSupportActionBar();
            action.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_SHOW_HOME
                    | ActionBar.DISPLAY_HOME_AS_UP);
            action.setCustomView(R.layout.custom_search_box);//add the custom view

            commodityEt = (CustomAutoCompleteTextView) action.getCustomView().findViewById(R.id.auto_complete_text_view); //the text editor
            ProgressBar pb = (android.widget.ProgressBar) action.getCustomView().findViewById(R.id.pb_loading_indicator);
            pb.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
            commodityEt.setLoadingIndicator(pb);
            commodityEt.setHint(" Search commodity or city ");
            commodityEt.setThreshold(2);
            commodityEt.setAdapter(new AutoCompleteTextViewAdapter(this, Constants.TYPE_SEARCH));
            commodityEt.setOnItemClickListener(this);
            commodityEt.requestFocus();
            commodityEt.setOnKeyListener(this);

            //open the keyboard focused in the edtSearch
            imm.showSoftInput(commodityEt, InputMethodManager.SHOW_IMPLICIT);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.color_white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void hideKeyboard() {
        try {
            if (this.getCurrentFocus() != null)
                imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        try {
            fragmentFeed.getFeedData(1, parent.getAdapter().getItem(position).toString());
            KeypadUtil.hideKeypad(SearchActivity.this);
            Utils.addInfo(this, pickerDialog, mainLayout);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            KeypadUtil.hideKeypad(SearchActivity.this);
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN)
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                try {
                    fragmentFeed.getFeedData(1, commodityEt.getText().toString());
                    hideKeyboard();
                    Utils.addInfo(this, pickerDialog, mainLayout);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }

        return false;
    }
}
