package agroconnectapp.agroconnect.in.agroconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.adapters.UserAdvisorPagerAdapter;

/**
 * Created by sumanta on 14/7/16.
 */
public class UserAdvisoryActivity extends AgroActivity {

    private UserAdvisorPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_advisory);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.container);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserAdvisoryActivity.this, FragmentWrapperActivity.class);
                intent.putExtra("className", "agroconnectapp.agroconnect.in.agroconnect.fragments.QuestionFragment");
                intent.putExtra("isCity", false);
                startActivity(intent);
                overridePendingTransition(R.anim.flipin, R.anim.flipout);
            }
        });

        findViewById(R.id.searchTV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserAdvisoryActivity.this, SearchAdvisoryActivity.class);
                startActivity(intent);
            }
        });

        pagerAdapter = new UserAdvisorPagerAdapter(UserAdvisoryActivity.this, getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(3);
    }
}
