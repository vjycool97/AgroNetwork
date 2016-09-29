package agroconnectapp.agroconnect.in.agroconnect;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.crashlytics.android.Crashlytics;
import java.util.List;
import java.util.Vector;
import agroconnectapp.agroconnect.in.agroconnect.fragments.FeedFragment;
import agroconnectapp.agroconnect.in.agroconnect.fragments.ProfileFragment;
import agroconnectapp.agroconnect.in.agroconnect.fragments.ShortlistFragment;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;

public class MainActivity extends BaseActivity{

    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupUpEnabled();
        
        fragments = new Vector<Fragment>();
        Fragment f1 = Fragment.instantiate(MainActivity.this, FeedFragment.class.getName());
        Bundle args = new Bundle();
        args.putInt(Constants.agentIdKey, -1);
        f1.setArguments(args);
        Fragment f2 = Fragment.instantiate(MainActivity.this, ShortlistFragment.class.getName());
        Fragment f3 = Fragment.instantiate(MainActivity.this, ProfileFragment.class.getName());
        fragments.add(f1);
        fragments.add(f2);
        fragments.add(f3);
        SectionPagerAdapter mSectionsPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());

        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickerDialog pickerDialog = new PickerDialog(MainActivity.this);
                pickerDialog.show();
            }
        });
    }

    public void refreshShortlistFragment() {
        try {
            ((ShortlistFragment)fragments.get(1)).refresh();
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(new Throwable("Error in shortlist refresh call - " + e.toString()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_search) {
            startActivity(new Intent(this,SearchActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class SectionPagerAdapter extends FragmentPagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.feed);
                case 1:
                    return getString(R.string.shortlist);
                case 2:
                    return getString(R.string.profile);
            }
            return null;
        }
    }



}
