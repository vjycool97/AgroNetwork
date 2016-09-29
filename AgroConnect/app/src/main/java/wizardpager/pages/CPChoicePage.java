/*
 * Copyright 2012 Roman Nurik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package wizardpager.pages;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.tech.freak.wizardpager.model.ModelCallbacks;
import com.tech.freak.wizardpager.model.Page;
import com.tech.freak.wizardpager.model.ReviewItem;

import java.util.ArrayList;
import java.util.Arrays;

import agroconnectapp.agroconnect.in.agroconnect.networkclient.model.CPNode;

/**
 * A page offering the user a number of mutually exclusive choices.
 */
public class CPChoicePage extends Page {
    public CPNode getmCPNode() {
        return mCPNode;
    }

    protected CPNode mCPNode = null;
    protected ArrayList<String> mChoices = new ArrayList<String>();

    public CPChoicePage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment(Fragment reference) {
        return CPFragment.create(getKey(), reference);
    }

    public String getOptionAt(int position) {
        return mChoices.get(position);
    }

    public int getOptionCount() {
        return mChoices.size();
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(getTitle(), mData.getString(SIMPLE_DATA_KEY), getKey()));
    }

    @Override
    public boolean isCompleted() {
        return !TextUtils.isEmpty(mData.getString(SIMPLE_DATA_KEY));
    }

    public CPChoicePage setChoices(String... choices) {
        mChoices.addAll(Arrays.asList(choices));
        return this;
    }

    public CPChoicePage setChoices(CPNode cpNode) {
        mCPNode = cpNode;
        String[] cropNames = new String[mCPNode.getChildrenCropList().size()];
        for (int i = 0; i < mCPNode.getChildrenCropList().size(); i++)
            cropNames[i] = mCPNode.getChildrenCropList().get(i).getName();
        mChoices.addAll(Arrays.asList(cropNames));
        return this;
    }


    public CPChoicePage setValue(String value) {
        mData.putString(SIMPLE_DATA_KEY, value);
        return this;
    }
}
