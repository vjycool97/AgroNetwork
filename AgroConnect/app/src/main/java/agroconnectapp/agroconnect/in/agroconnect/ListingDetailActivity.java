package agroconnectapp.agroconnect.in.agroconnect;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import agroconnectapp.agroconnect.in.agroconnect.model.FeedData;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;

public class ListingDetailActivity extends BaseActivity implements View.OnClickListener{

    private FeedData feedData;
    private TextView mandi;
    private TextView city;
    private TextView availibility;
    private TextView commodityName;
    private TextView inventory;
    private TextView agentName;
    private TextView date;
    private TextView quantity;
    private TextView price;
    private TextView description;
    private ImageView agentImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_detail);

        if(Build.VERSION.SDK_INT >= 21)
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        mandi = (TextView) findViewById(R.id.mandi_tv);
        city = (TextView) findViewById(R.id.city_tv);
        availibility = (TextView) findViewById(R.id.availability_tv);
        commodityName = (TextView) findViewById(R.id.variety_tv);
        inventory = (TextView) findViewById(R.id.inventory_tv);
        agentName = (TextView) findViewById(R.id.agent_name_tv);
        date = (TextView) findViewById(R.id.date_tv);
        quantity = (TextView) findViewById(R.id.quantity_tv);
        price = (TextView) findViewById(R.id.price_tv);
        description = (TextView) findViewById(R.id.description_tv);
        agentImage = (ImageView) findViewById(R.id.agent_iv);
        agentImage.setOnClickListener(this);

        if(getIntent() != null && getIntent().getExtras() != null)
            feedData = getIntent().getExtras().getParcelable(Constants.feedParcelKey);
        else {
            finish();
            return;
        }

//        mandi.setText(feedData.getMandi());
        city.setText(getString(R.string.location) + ": " + feedData.getLocation());
        if(feedData.isSell())
            availibility.setText(getString(R.string.available_to_sell));
        else
            availibility.setText(getString(R.string.available_to_buy));
        commodityName.setText(feedData.getCommodityName());
        inventory.setText(getString(R.string.inventory_by));
        agentName.setText(feedData.getAgentName());
        agentName.setOnClickListener(this);
        if(feedData.getLastUpdated() != null){
            try {
                String dt = feedData.getLastUpdated().split("T")[0];
                date.setText(dt);
            } catch (Exception e){
                date.setText("");
                Crashlytics.logException(new Throwable("Error in date conversion (Listing detail) - " + e.toString()));
            }
        }
        quantity.setText(getString(R.string.quantity) + ": " + feedData.getQuantity());
        price.setText(getString(R.string.price) + ": " + feedData.getPrice());
        description.setText(getString(R.string.description) + ": " + feedData.getDescription());

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

    @Override
    public void onClick(View v) {
        Intent profileIntent = new Intent(ListingDetailActivity.this,ProfileActivity.class);
        profileIntent.putExtra(Constants.agentIdKey, feedData.getAgentId());
        startActivity(profileIntent);
    }
}
