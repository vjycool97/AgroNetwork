package agroconnectapp.agroconnect.in.agroconnect.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.tech.freak.wizardpager.model.AbstractWizardModel;
import com.tech.freak.wizardpager.model.ModelCallbacks;
import com.tech.freak.wizardpager.model.Page;
import com.tech.freak.wizardpager.ui.PageFragmentCallbacks;
import com.tech.freak.wizardpager.ui.ReviewFragment;
import com.tech.freak.wizardpager.ui.StepPagerStrip;

import java.util.List;

import agroconnectapp.agroconnect.in.agroconnect.AgroConnect;
import agroconnectapp.agroconnect.in.agroconnect.NetworkController;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.fragments.AgroFragment;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.model.CPNode;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.network.TRequestDelegate;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.response.ErrorResponse;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.response.OKResponse;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.response.TResponse;
import agroconnectapp.agroconnect.in.agroconnect.utility.Utils;
import dmax.dialog.SpotsDialog;
import wizardpager.CPWizardModel;
import wizardpager.pages.CPChoicePage;
import wizardpager.pages.CPFragment;

/*
Brand as header for leaf nodes


 */

public class CropProtectionFragment extends AgroFragment implements
        PageFragmentCallbacks, ReviewFragment.Callbacks, CPFragment.CPNodeCallbacks, ModelCallbacks {
    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;

    private boolean mEditingAfterReview;

    private AbstractWizardModel mWizardModel = new CPWizardModel(getContext());

    private boolean mConsumePageSelectedEvent;

    private Button mNextButton;
    private Button mPrevButton;

    private List<Page> mCurrentPageSequence;
    private StepPagerStrip mStepPagerStrip;
    private SpotsDialog spotDialog = null;
    CropProtectionFragment reference;

//    public void onCreateView(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main_cp);
//
//
//        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//
//        spotDialog = new SpotsDialog(this);
//
//
//        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
//        mPager = (ViewPager) findViewById(R.id.pager);
//        mStepPagerStrip = (StepPagerStrip) findViewById(R.id.strip);
//        mStepPagerStrip
//                .setOnPageSelectedListener(new StepPagerStrip.OnPageSelectedListener() {
//                    @Override
//                    public void onPageStripSelected(int position) {
//                        position = Math.min(mPagerAdapter.getCount() - 1,
//                                position);
//                        if (mPager.getCurrentItem() != position) {
//                            mPager.setCurrentItem(position);
//                        }
//                    }
//                });
//
//        mNextButton = (Button) findViewById(R.id.next_button);
//        mPrevButton = (Button) findViewById(R.id.prev_button);
//
//        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
//                mStepPagerStrip.setCurrentPage(position);
//                mWizardModel.deletePagesAfter(position);
//                onPageTreeChanged();
//                if (mConsumePageSelectedEvent) {
//                    mConsumePageSelectedEvent = false;
//                    return;
//                }
//
//                mEditingAfterReview = false;
//                updateBottomBar();
//            }
//        });
//
//        mNextButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //onNext();
//
//            }
//        });
//
//        mPrevButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
//                mWizardModel.deleteLastPage();
//                onPageTreeChanged();
//            }
//        });
//
//        //Just to initialize dummy data
//        //onPageTreeChanged();
//
//        loadCPData(savedInstanceState);
//    }
   public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View CropView = inflater.inflate(R.layout.activity_main_cp, container, false);

    reference = this;
    spotDialog = new SpotsDialog(getContext());

    mPagerAdapter = new MyPagerAdapter(getChildFragmentManager());
    mPager = (ViewPager)CropView.findViewById(R.id.pager);
    mStepPagerStrip = (StepPagerStrip)CropView.findViewById(R.id.strip);
    mStepPagerStrip
            .setOnPageSelectedListener(new StepPagerStrip.OnPageSelectedListener() {
                @Override
                public void onPageStripSelected(int position) {
                    position = Math.min(mPagerAdapter.getCount() - 1,
                            position);
                    if (mPager.getCurrentItem() != position) {
                        mPager.setCurrentItem(position);
                    }
                }
            });

    mNextButton = (Button)CropView.findViewById(R.id.next_button);
    mPrevButton = (Button)CropView.findViewById(R.id.prev_button);

    mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            mStepPagerStrip.setCurrentPage(position);
            mWizardModel.deletePagesAfter(position);
            onPageTreeChanged();
            if (mConsumePageSelectedEvent) {
                mConsumePageSelectedEvent = false;
                return;
            }

            mEditingAfterReview = false;
            updateBottomBar();
        }
    });


    mNextButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //onNext();

        }
    });

    mPrevButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            mWizardModel.deleteLastPage();
            onPageTreeChanged();
        }
    });

    //Just to initialize dummy data
    //onPageTreeChanged();

    loadCPData(savedInstanceState);




    return CropView;
}

    private void loadCPData(final Bundle savedInstanceState) {
        if(!AgroConnect.isConnected() && NetworkController.getInstance().mCPNodes.size() != 0) {
            refreshScreen(savedInstanceState);
            mPager.setAdapter(mPagerAdapter);
            return;
        }

        spotDialog.show();
        NetworkController.getInstance().populateCPCrops(new TRequestDelegate() {
            @Override
            public void run(TResponse response) {
                spotDialog.dismiss();
                if(response instanceof OKResponse) {

                } else {
                    ErrorResponse errorResponse = (ErrorResponse) response;
                    Toast.makeText(AgroConnect.getInstance().getContext(), errorResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
                refreshScreen(savedInstanceState);
                mPager.setAdapter(mPagerAdapter);
            }
        });
    }


    private void onNext(final CPNode crop) {
        if(crop.getNode_type() == CPNode.NODE_TYPE.ROOT && crop.getChildrenCropList() == null) {
            spotDialog.show();
            NetworkController.getInstance().populateCPDataForCrop(crop, new TRequestDelegate() {
                @Override
                public void run(TResponse response) {
                    spotDialog.dismiss();
                    if(response instanceof OKResponse) {
                        showNextPage(crop);
                    } else {
                        ErrorResponse errorResponse = (ErrorResponse) response;
                        Toast.makeText(AgroConnect.getInstance().getContext(), errorResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else
            showNextPage(crop);
    }

    private void showNextPage(CPNode crop) {
        if(crop == null || crop.getChildrenCropList() == null)
            return;

        String key = /*(mCurrentPageSequence.size()+1) + ". " +*/ crop.getName();
        Page page = new CPChoicePage(CropProtectionFragment.this, key).setChoices(crop).setRequired(true);
        mWizardModel.addPage(page);
        onPageTreeChanged();
        updateBottomBar();
        if (mPager.getCurrentItem() == mCurrentPageSequence.size()) {
            /*DialogFragment dg = new DialogFragment() {
                @Override
                public Dialog onCreateDialog(Bundle savedInstanceState) {
                    return new AlertDialog.Builder(getActivity())
                            .setMessage(R.string.submit_confirm_message)
                            .setPositiveButton(
                                    R.string.submit_confirm_button,
                                    null)
                            .setNegativeButton(android.R.string.cancel,
                                    null).create();
                }
            };
            dg.show(getSupportFragmentManager(), "place_order_dialog");*/
        } else {
            if (mEditingAfterReview) {
                mPager.setCurrentItem(mPagerAdapter.getCount() - 1);
            } else {
                mPager.setCurrentItem(mPager.getCurrentItem() + 1);
            }
        }

    }

    private void refreshScreen(final Bundle savedInstanceState) {
        mWizardModel = new CPWizardModel(getContext());
        if (savedInstanceState != null && savedInstanceState.getBundle("model") != null) {
            mWizardModel.load(savedInstanceState.getBundle("model"));
        }
        mWizardModel.registerListener(CropProtectionFragment.this);
        onPageTreeChanged();
        updateBottomBar();
    }


    @Override
    public void onPageTreeChanged() {
        mCurrentPageSequence = mWizardModel.getCurrentPageSequence();
        recalculateCutOffPage();
        mStepPagerStrip.setPageCount(mCurrentPageSequence.size()); // + 1 =
        // review
        // step
        mPagerAdapter.notifyDataSetChanged();
        updateBottomBar();
    }

    private void updateBottomBar() {
        int position = mPager.getCurrentItem();
        if (position == mCurrentPageSequence.size()) {
            mNextButton.setText(R.string.finish);
            mNextButton.setBackgroundResource(R.drawable.finish_background);
            mNextButton.setTextAppearance(getContext(), R.style.TextAppearanceFinish);
        } else {
            mNextButton.setText(mEditingAfterReview ? R.string.review : R.string.next);
            mNextButton.setBackgroundResource(R.drawable.selectable_item_background);
            TypedValue v = new TypedValue();
            getActivity().getTheme().resolveAttribute(android.R.attr.textAppearanceMedium, v,
                    true);
            mNextButton.setTextAppearance(getContext(), v.resourceId);
            mNextButton.setEnabled(position != mPagerAdapter.getCutOffPage());
        }

        mPrevButton.setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWizardModel.unregisterListener(this);
    }


//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putBundle("model", mWizardModel.save());
//    }

    @Override
    public AbstractWizardModel onGetModel() {
        return mWizardModel;
    }

    @Override
    public void onEditScreenAfterReview(String key) {
        for (int i = mCurrentPageSequence.size() - 1; i >= 0; i--) {
            if (mCurrentPageSequence.get(i).getKey().equals(key)) {
                mConsumePageSelectedEvent = true;
                mEditingAfterReview = true;
                mPager.setCurrentItem(i);
                updateBottomBar();
                break;
            }
        }
    }

    @Override
    public void onPageDataChanged(Page page) {
        if (page.isRequired()) {
            if (recalculateCutOffPage()) {
                mPagerAdapter.notifyDataSetChanged();
                updateBottomBar();
            }
        }

    }

    @Override
    public Page onGetPage(String key) {
        return mWizardModel.findByKey(key);
    }

    private boolean recalculateCutOffPage() {
        // Cut off the pager adapter at first required page that isn't completed
        int cutOffPage = mCurrentPageSequence.size() + 1;
        for (int i = 0; i < mCurrentPageSequence.size(); i++) {
            Page page = mCurrentPageSequence.get(i);
            if (page.isRequired() && !page.isCompleted()) {
                cutOffPage = i;
                break;
            }
        }

        if (mPagerAdapter.getCutOffPage() != cutOffPage) {
            mPagerAdapter.setCutOffPage(cutOffPage);
            return true;
        }

        return false;
    }

    @Override
    public void onItemClick(CPNode crop) {
        onNext(crop);
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {
        private int mCutOffPage;
        private Fragment mPrimaryItem;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if (i >= mCurrentPageSequence.size()) {
                return new ReviewFragment();
            }

            return mCurrentPageSequence.get(i).createFragment(reference);
        }

        @Override
        public int getItemPosition(Object object) {
            // TODO: be smarter about this
            if (object == mPrimaryItem) {
                // Re-use the current fragment (its position never changes)
                return POSITION_UNCHANGED;
            }

            return POSITION_NONE;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position,
                                   Object object) {
            super.setPrimaryItem(container, position, object);
            mPrimaryItem = (Fragment) object;
        }

        @Override
        public int getCount() {
            return Math.min(mCutOffPage + 1, mCurrentPageSequence == null ? 0 : mCurrentPageSequence.size());
        }

        public void setCutOffPage(int cutOffPage) {
            if (cutOffPage < 0) {
                cutOffPage = Integer.MAX_VALUE;
            }
            mCutOffPage = cutOffPage;
        }

        public int getCutOffPage() {
            return mCutOffPage;
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_whatsapp, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == android.R.id.home) {
//            onBackPressed();
//        } else if (id == R.id.whatsappId) {
//            Utils.takeScreenShotAndShare(CropProtectionFragment.this, mPager);
//        }
//        return super.onOptionsItemSelected(item);
//    }

    public boolean onBackPressed() {

        if(mPager != null) {
            int position = mPager.getCurrentItem();
            if (position == 0)
                return true;
            else {
                //mPager.setCurrentItem(mPager.getCurrentItem() - 1);
                mPager.setCurrentItem(mPager.getCurrentItem());
                mWizardModel.deleteLastPage();
                getChildFragmentManager().popBackStackImmediate();
                onPageTreeChanged();
//            CPChoicePage page = (CPChoicePage) mCurrentPageSequence.get(mCurrentPageSequence.size()-1);
//            if(position == 1) {
//                getSupportActionBar().setTitle(getString(R.string.crop_protection));
//            } else {
//            getSupportActionBar().setTitle(page.getTitle());
//            }
                return false;
            }
        }else {
            return true;
        }
    }
}