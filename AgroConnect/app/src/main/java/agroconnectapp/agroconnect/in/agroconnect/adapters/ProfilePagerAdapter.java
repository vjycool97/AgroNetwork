package agroconnectapp.agroconnect.in.agroconnect.adapters;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.fragments.AgentDetailFragment;
import agroconnectapp.agroconnect.in.agroconnect.fragments.FeedFragment;
import agroconnectapp.agroconnect.in.agroconnect.model.ProfileData;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;

/**
 * Created by nitin.gupta on 12/20/2015.
 */
public class ProfilePagerAdapter extends FragmentPagerAdapter {

    private Activity mActivity;
    private ProfileData profileData;
    public ProfilePagerAdapter(Activity mActivity,FragmentManager fm, ProfileData pd) {
        super(fm);
        this.mActivity = mActivity;
        this.profileData = pd;
    }


    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            Bundle args = new Bundle();
            args.putInt(Constants.agentIdKey, profileData.getId());
            return Fragment.instantiate(mActivity,FeedFragment.class.getName(),args);
        }
        else if (position == 1)
            return AgentDetailFragment.newInstance(profileData);

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
                return mActivity.getString(R.string.listings);
            case 1:
                return mActivity.getString(R.string.details);
        }
        return null;
    }
}
