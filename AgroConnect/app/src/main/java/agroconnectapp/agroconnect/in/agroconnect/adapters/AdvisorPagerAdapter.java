package agroconnectapp.agroconnect.in.agroconnect.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.databases.PrefDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.fragments.UserAdvisorFragment;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;
import agroconnectapp.agroconnect.in.agroconnect.utility.KeyIDS;

/**
 * Created by sumanta on 15/7/16.
 */
public class AdvisorPagerAdapter extends FragmentPagerAdapter {

    private Context context;

    public AdvisorPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        UserAdvisorFragment userAdvisorFragment = new UserAdvisorFragment();
        switch (position) {
            case 0: {
                Bundle bundle = new Bundle();
                bundle.putString("advisoryPostResultType", "4");
                userAdvisorFragment.setArguments(bundle);
                return userAdvisorFragment;
            }
            case 1: {
                Bundle bundle = new Bundle();
                bundle.putString("advisoryPostResultType", "3");
                bundle.putInt(Constants.agentIdKey, PrefDataHandler.getInstance().getSharedPref().getInt(KeyIDS.AGENT_ID, 0));
                userAdvisorFragment.setArguments(bundle);
                return userAdvisorFragment;
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.all);
            case 1:
                return context.getString(R.string.my_post);
        }
        return null;
    }
}
