package agroconnectapp.agroconnect.in.agroconnect.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import agroconnectapp.agroconnect.in.agroconnect.fragments.ResOneFragment;

public class RegPagerAdapter extends FragmentPagerAdapter {

    public RegPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0 : return new ResOneFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 1;
    }
}
