package agroconnectapp.agroconnect.in.agroconnect.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import agroconnectapp.agroconnect.in.agroconnect.fragments.MandiPricesFragment;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.fragments.NewsFeedFragment;
import agroconnectapp.agroconnect.in.agroconnect.fragments.CropProtectionFragment;

/**
 * Created by sumanta on 26/6/16.
 */
public class NewsFeedPagerAdapter extends FragmentPagerAdapter {
    private Context context;
    private MandiPricesFragment mandiFragment = new MandiPricesFragment();
    private CropProtectionFragment cropFragment = new CropProtectionFragment();
    public NewsFeedPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        setCropFragmentArguments();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0 : {
                NewsFeedFragment newsFeedFragment = new NewsFeedFragment();
                Bundle bundle = new Bundle();
                bundle.putString("feedResultType", "1");
                newsFeedFragment.setArguments(bundle);
                return newsFeedFragment;
            }
            case 1 : {
                NewsFeedFragment newsFeedFragment = new NewsFeedFragment();
                Bundle bundle = new Bundle();
                bundle.putString("feedResultType", "2");
                newsFeedFragment.setArguments(bundle);
                return newsFeedFragment;
            }
            case 2 : {
                Bundle bundle = new Bundle();
                bundle.putString("feedResultType", "3");
                mandiFragment.setArguments(bundle);
                return mandiFragment;
            }
            case 3 : {
//                Bundle bundle = new Bundle();
//                bundle.putString("feedResultType", "4");
//                CropProtectionFragment fragment = new CropProtectionFragment();
//                fragment.setArguments(bundle);
//                return fragment;
                return cropFragment;
            }
        }
        return null;
    }

    private void setCropFragmentArguments() {
        Bundle bundle = new Bundle();
        bundle.putString("feedResultType", "4");
        cropFragment.setArguments(bundle);
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.all);
            case 1:
                return context.getString(R.string.my_crops);
            case 2:
                return context.getString(R.string.mandi);
            case 3:
                return context.getString(R.string.crop_protection);
        }
        return null;
    }

//    @Override
//    public int getItemPosition(Object object) {
//        //return super.getItemPosition(object);
//        return POSITION_NONE;
//    }
}
