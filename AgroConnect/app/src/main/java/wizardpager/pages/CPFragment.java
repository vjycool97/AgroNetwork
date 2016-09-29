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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tech.freak.wizardpager.model.Page;
import com.tech.freak.wizardpager.ui.PageFragmentCallbacks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.activities.ImageActivity;
import agroconnectapp.agroconnect.in.agroconnect.networkclient.model.CPNode;

public class CPFragment extends ListFragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private CPNodeCallbacks cpNodeCallbacks;
    private List<String> mChoices;
    private String mKey;
    private Page mPage;
    private ListView listView;

    CPNodeAdapter mAdapter;

    public static CPFragment create(String key, Fragment fragment2) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        CPFragment fragment = new CPFragment();
        fragment.setArguments(args);
        fragment.setCallback(fragment2);
        return fragment;
    }

    private void setCallback(Object fragment2) {
        cpNodeCallbacks = (CPNodeCallbacks) fragment2;
        mCallbacks = (PageFragmentCallbacks) fragment2;
    }

    public CPFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mAdapter = new CPNodeAdapter();

            Bundle args = getArguments();
            mKey = args.getString(ARG_KEY);
            mPage = mCallbacks.onGetPage(mKey);

            CPChoicePage fixedChoicePage = (CPChoicePage) mPage;
            mChoices = new ArrayList<String>();
            for (int i = 0; i < fixedChoicePage.getOptionCount(); i++) {
                mChoices.add(fixedChoicePage.getOptionAt(i));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        mCallbacks = null;
        cpNodeCallbacks = null;
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.cp_fragment_page, container, false);
        ((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

        listView = (ListView) rootView.findViewById(android.R.id.list);
        listView.setAdapter(mAdapter);
        /*setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_single_choice,
                android.R.id.text1,
                mChoices));*/
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // Pre-select currently selected item.
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Parcelable state = mPage.getData().getParcelable(Page.SCROLL_POS);
                if(state != null)
                    listView.onRestoreInstanceState(state);

                String selection = mPage.getData().getString(Page.SIMPLE_DATA_KEY);
                if(getCPNode().isLeaf())
                    return;
                for (int i = 0; i < mChoices.size(); i++) {
                    if (mChoices.get(i).equals(selection)) {
                        listView.setItemChecked(i, true);
                        break;
                    }
                }
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mPage.getData().putString(Page.SIMPLE_DATA_KEY, ((CPNode)mAdapter.getItem(position)).getName());
        mPage.notifyDataChanged();

        if(!getCPNode().isLeaf())
            cpNodeCallbacks.onItemClick(getCPNode().getChildrenCropList().get(position));
    }

    private CPNode getCPNode() {
        CPChoicePage fixedChoicePage = (CPChoicePage) mPage;
        return  fixedChoicePage.getmCPNode();
    }



    public interface CPNodeCallbacks {
        void onItemClick(CPNode pageKey);
    }



    private class CPNodeAdapter extends BaseAdapter implements View.OnClickListener{
        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public int getCount() {
            return getCPNode().getChildrenCropList().size();
        }

        @Override
        public Object getItem(int position) {
            return getCPNode().getChildrenCropList().get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).hashCode();
        }

        @Override
        public View getView(int position, View view, ViewGroup container) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
          //  View rootView = null;

          View rootView = inflater.inflate(R.layout.cp_leaf_container, container, false);

            CPNode node = (CPNode) getItem(position);
            if(!getCPNode().isLeaf()) {
                View childView = inflater.inflate(R.layout.cp_branch_item, container, false);
                ((TextView) childView.findViewById(R.id.cp_title)).setText(node.getName());
                ImageView imageView = (ImageView) childView.findViewById(R.id.imageView);
                ImageView magnifyIV = (ImageView) childView.findViewById(R.id.magnifyIV);
                magnifyIV.setOnClickListener(this);
                magnifyIV.setTag(position);
                String url = node.getImageUrl();
                if(url != null) {
                    Picasso.with(getActivity()).load(url).placeholder(R.drawable.crop_protection_placeholder).into(imageView);
                    magnifyIV.setVisibility(View.VISIBLE);
                }
                else {
                    Picasso.with(getActivity()).load(R.drawable.crop_protection_placeholder).into(imageView);
                    magnifyIV.setVisibility(View.GONE);
                }
                ((LinearLayout) rootView.findViewById(R.id.main_layout)).addView(childView);

            } else {
                if(node.leaf != null) {
                    View childView = getLeafView(node.leaf, container);
                    ((LinearLayout) rootView.findViewById(R.id.main_layout)).addView(childView);

                } else { // Somebody messed up at backend
                    View childView = inflater.inflate(R.layout.cp_leaf_item, container, false);
                    ((TextView) childView.findViewById(R.id.cp_key)).setText("Sorry something is broken!!");
                    ((TextView) childView.findViewById(R.id.cp_value)).setText("We will fix it soon");
                    ((LinearLayout) rootView.findViewById(R.id.main_layout)).addView(childView);

                }

            }
            return rootView;
        }


        @Override
        public void onClick(View view) {
            int position = Integer.parseInt(view.getTag().toString());
            CPNode node = (CPNode) getItem(position);
            Intent intent = new Intent(getContext(), ImageActivity.class);
            intent.putExtra("url", node.getImageUrl());
            startActivity(intent);
        }
    }

    private View getLeafView(JSONObject leaf, ViewGroup container) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        Iterator<String> iter = leaf.keys();
        View childView = inflater.inflate(R.layout.cp_branch_item, container, false);
        LinearLayout linearLayout = ((LinearLayout) childView.findViewById(R.id.container_layout));
        linearLayout.setVisibility(View.VISIBLE);

        while (iter.hasNext()) {
            String key = iter.next();
            if(key.equals("Id"))
                continue;

            try {
                String  value = leaf.get(key).toString();
                if(key.equalsIgnoreCase("Brand"))
                    ((TextView) childView.findViewById(R.id.cp_title)).setText(value);
                else if(key.equals("ImageUrl")) {
                    ImageView imageView = (ImageView) childView.findViewById(R.id.imageView);
                    JSONArray urlArray = leaf.optJSONArray(key);
                    if(urlArray != null && urlArray.length() > 0) {
                        String url = urlArray.optString(0);
                        Picasso.with(getActivity()).load(url).placeholder(R.drawable.crop_protection_placeholder).into(imageView);
                    }

                } else {
                    View keyValueLayout = inflater.inflate(R.layout.cp_leaf_item, container, false);
                    ((TextView) keyValueLayout.findViewById(R.id.cp_key)).setText(key.replaceAll("_", " ") + " : ");
                    ((TextView) keyValueLayout.findViewById(R.id.cp_value)).setText(value.toString());
                    linearLayout.addView(keyValueLayout);
                }

            } catch (JSONException e) {
                // Something went wrong!
            }
        }
        return childView;
    }



    @Override
    public void onPause() {
        // Save ListView state @ onPause
        Parcelable state = listView.onSaveInstanceState();
        mPage.getData().putParcelable(Page.SCROLL_POS, state);
        super.onPause();
    }
}
