package agroconnectapp.agroconnect.in.agroconnect;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import agroconnectapp.agroconnect.in.agroconnect.activities.AgroActivity;
import agroconnectapp.agroconnect.in.agroconnect.databases.PrefDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.fragments.FeedFragment;

public class MyInventoryActivity extends AgroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container);
        setToolbar();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle args = new Bundle();
        args.putInt(AGENT_ID, PrefDataHandler.getInstance().getSharedPref().getInt(AGENT_ID, 0));
        fragmentTransaction.add(R.id.main_layout, Fragment.instantiate(MyInventoryActivity.this, FeedFragment.class.getName(), args));
        fragmentTransaction.commit();

    }
    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Inventory");
        toolbar.setTitleTextColor(getResources().getColor(R.color.color_white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
