package agroconnectapp.agroconnect.in.agroconnect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;

import io.fabric.sdk.android.Fabric;

/**
 * Created by niteshtarani on 05/02/16.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!Fabric.isInitialized()) {
            Fabric.with(this, new Crashlytics());
            Fabric.with(this, new Answers());
        }
    }

    public void setupUpEnabled() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
