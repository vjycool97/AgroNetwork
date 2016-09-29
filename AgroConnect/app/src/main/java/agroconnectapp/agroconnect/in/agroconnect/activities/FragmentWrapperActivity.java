package agroconnectapp.agroconnect.in.agroconnect.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;

import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.fragments.AgroFragment;
import agroconnectapp.agroconnect.in.agroconnect.fragments.AnswerFragment;
import agroconnectapp.agroconnect.in.agroconnect.fragments.CityAndCommodityFragment;
import agroconnectapp.agroconnect.in.agroconnect.fragments.ComposeFragment;
import agroconnectapp.agroconnect.in.agroconnect.fragments.QuestionFragment;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;
import agroconnectapp.agroconnect.in.agroconnect.utility.KeypadUtil;

public class FragmentWrapperActivity extends AgroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_wrapper);
        String className = getIntent().getStringExtra("className");
        fragmentFactory(className);
    }

    private void fragmentFactory(String className) {
        try {
            AgroFragment agroFragment = (AgroFragment) Class.forName(className).newInstance();
            if (agroFragment instanceof CityAndCommodityFragment) {
                boolean isCity = getIntent().getBooleanExtra("isCity", false);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isCity", isCity);
                agroFragment.setArguments(bundle);
                if (isCity)
                    setToolbar(getString(R.string.select_location));
                else
                    setToolbar(getString(R.string.select_commodity));
            } else if (agroFragment instanceof QuestionFragment) {
                setToolbar(getString(R.string.add_new_question));
            } else if (agroFragment instanceof AnswerFragment) {
                int postId = getIntent().getIntExtra(Constants.postId, 0);
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.postId, postId);
                agroFragment.setArguments(bundle);
                setToolbar(getString(R.string.add_new_answer));
            } else if (agroFragment instanceof ComposeFragment) {
                boolean isGeneral = getIntent().getBooleanExtra("isGeneral", false);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isGeneral", isGeneral);
                agroFragment.setArguments(bundle);
                setToolbar("Compose");
            }
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, agroFragment, "fragment");
            fragmentTransaction.commit();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setToolbar(String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(!TextUtils.isEmpty(title) ? title : "");
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        KeypadUtil.hideKeypad(FragmentWrapperActivity.this);
        overridePendingTransition(R.anim.flipin_reverse, R.anim.flipout_reverse);
    }
}
