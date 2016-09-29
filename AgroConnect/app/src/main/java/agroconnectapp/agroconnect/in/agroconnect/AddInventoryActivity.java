package agroconnectapp.agroconnect.in.agroconnect;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;

import agroconnectapp.agroconnect.in.agroconnect.adapters.AutoCompleteTextViewAdapter;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;
import agroconnectapp.agroconnect.in.agroconnect.utility.CustomAutoCompleteTextView;
import agroconnectapp.agroconnect.in.agroconnect.utility.HttpRequest;

public class AddInventoryActivity extends BaseActivity implements AdapterView.OnItemClickListener{

    private EditText quantityEt;
    private EditText rateEt;
    private CustomAutoCompleteTextView locationEt;
    private EditText desciprionEt;
    private CustomAutoCompleteTextView commodityEt;
    private Spinner quantityUnitSpinner;
    private Spinner rateUnitSpinner;
    private Button submitBtn;
    private boolean isSell = false;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;
    private Long commodityId = -1l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_inventory);
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(Build.VERSION.SDK_INT >= 21)
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        quantityEt = (EditText) findViewById(R.id.quantity_et);
        rateEt = (EditText) findViewById(R.id.rate_et);
        desciprionEt = (EditText) findViewById(R.id.descrption_et);

        FrameLayout fl;
        fl = (FrameLayout) findViewById(R.id.commodity_et);
        commodityEt = (CustomAutoCompleteTextView) fl.findViewById(R.id.auto_complete_text_view);
        commodityEt.setLoadingIndicator((android.widget.ProgressBar) fl.findViewById(R.id.pb_loading_indicator));
        commodityEt.setHint(getResources().getString(R.string.commodity_name_example));
        commodityEt.setThreshold(2);
        commodityEt.setAdapter(new AutoCompleteTextViewAdapter(this, Constants.TYPE_COMMODITY));
        commodityEt.setOnItemClickListener(this);

        fl = (FrameLayout) findViewById(R.id.location_et);
        locationEt = (CustomAutoCompleteTextView) fl.findViewById(R.id.auto_complete_text_view);
        locationEt.setLoadingIndicator((android.widget.ProgressBar) fl.findViewById(R.id.pb_loading_indicator));
        locationEt.setHint(getResources().getString(R.string.location));
        locationEt.setThreshold(2);
        locationEt.setAdapter(new AutoCompleteTextViewAdapter(this, Constants.TYPE_CITY));

        quantityUnitSpinner = (Spinner) findViewById(R.id.quantity_unit_spinner);
        rateUnitSpinner = (Spinner) findViewById(R.id.rate_unit_spinner);
        submitBtn = (Button) findViewById(R.id.submit_btn);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        ArrayAdapter quantityUnitAdapter = ArrayAdapter.createFromResource(this,R.array.quantity_units_array,R.layout.view_spinner_item);
        quantityUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quantityUnitSpinner.setAdapter(quantityUnitAdapter);
        quantityUnitSpinner.setSelection(0);

        ArrayAdapter rateUnitAdapter = ArrayAdapter.createFromResource(this,R.array.rate_units_array,R.layout.view_spinner_item);
        rateUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rateUnitSpinner.setAdapter(rateUnitAdapter);
        rateUnitSpinner.setSelection(0);

        if(getIntent() != null && getIntent().hasExtra(Constants.isSell)) {
            isSell = getIntent().getBooleanExtra(Constants.isSell,false);
        }

        if(!isSell) {
            desciprionEt.setHint(getResources().getString(R.string.specify_requirement));
        }

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commodityEt.getText().toString().isEmpty()) {
                    commodityEt.setError(getString(R.string.commodity_required));
                } else if (quantityEt.getText().toString().isEmpty()) {
                    quantityEt.setError(getString(R.string.quantity_required));
                } else {
                    sendInventoryData();
                }
            }
        });

    }

    private void sendInventoryData() {

        progressBar.setVisibility(View.VISIBLE);
        submitBtn.setVisibility(View.GONE);
        String commodity = commodityEt.getText().toString();
        String quantity = quantityEt.getText().toString() + " " + quantityUnitSpinner.getSelectedItem();
        String rate;
        if(rateEt.getText() != null && rateEt.getText().toString() != null)
            rate = rateEt.getText().toString() + " " + rateUnitSpinner.getSelectedItem();
        else
            rate = "";
        String location = locationEt.getText().toString();
        String desc = desciprionEt.getText().toString();

        if(location.isEmpty())
            location = sharedPreferences.getString(Constants.city,"");

        try {
            JSONObject requestObj = new JSONObject();
            requestObj.put("description",desc);
            requestObj.put("CommodityId",commodityId);
            requestObj.put("CommodityName",commodity);
            requestObj.put("isSell",isSell);
            requestObj.put("Quantity",quantity);
            requestObj.put("Price",rate);
            requestObj.put("Location",location);
            requestObj.put("AgentId",sharedPreferences.getInt(Constants.agentIdKey,0));

            HttpRequest request = new HttpRequest(true,Constants.token,sharedPreferences.getString(Constants.token,Constants.defaultToken));
            Callback callback = new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            submitBtn.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_msg_unknown), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Response response) throws IOException {

                    if(response.isSuccessful()) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), R.string.inventory_add_success, Toast.LENGTH_LONG).show();
                            }
                        });
                        finish();

                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                submitBtn.setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_msg_unknown),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            };

            RequestBody rb = RequestBody.create(request.JSON,requestObj.toString());
            request.doPostRequest(Constants.feedUrl,rb,callback);

        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(new Throwable("Error in add inventory - " + e.toString()));
        }

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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        commodityId = parent.getAdapter().getItemId(position);
    }
}
