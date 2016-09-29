package agroconnectapp.agroconnect.in.agroconnect.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.adapters.NotificationAdapter;
import agroconnectapp.agroconnect.in.agroconnect.databases.NotificationDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.entities.NotificationEntity;

/**
 * Created by sumanta on 22/7/16.
 */
public class NotificationActivity extends AgroActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        setToolbar();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        ArrayList<NotificationEntity> entityList = NotificationDataHandler.getAllNotifications(this);
        NotificationAdapter adapter = new NotificationAdapter(this, entityList);
        recyclerView.setAdapter(adapter);
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.notification));
        toolbar.setTitleTextColor(getResources().getColor(R.color.color_white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
