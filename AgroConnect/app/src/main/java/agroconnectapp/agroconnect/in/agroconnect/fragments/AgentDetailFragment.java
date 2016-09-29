package agroconnectapp.agroconnect.in.agroconnect.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.model.ProfileData;


public class AgentDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LOCATION_KEY = "location";
    private static final String PHONE_KEY = "phone";
    private static final String DESCRIPTION_KEY = "description";
    private static final String TYPE_KEY = "type";
    private TextView locationTv;
    private TextView phoneNumberTv;
    private TextView agentTypeTv;
    private TextView descriptionTv;
    // TODO: Rename and change types of parameters
    private String location;
    private String phoneNumber;
    private String description;
    private int agentType;

    public AgentDetailFragment() {
        // Required empty public constructor
    }

    public static AgentDetailFragment newInstance(ProfileData profileData) {
        AgentDetailFragment fragment = new AgentDetailFragment();
        Bundle args = new Bundle();
        args.putString(LOCATION_KEY, profileData.getCiy());
        args.putString(PHONE_KEY, profileData.getPhoneNumber());
        args.putInt(TYPE_KEY,profileData.getAgentType());
        args.putString(DESCRIPTION_KEY, profileData.getDescription());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            location = getArguments().getString(LOCATION_KEY);
            phoneNumber = getArguments().getString(PHONE_KEY);
            agentType = getArguments().getInt(TYPE_KEY);
            description = getArguments().getString(DESCRIPTION_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout frameLayout = (FrameLayout) inflater.inflate(R.layout.fragment_agent_detail, container, false);
        locationTv = (TextView)frameLayout.findViewById(R.id.location_tv);
        phoneNumberTv = (TextView) frameLayout.findViewById(R.id.phone_number_tv);
        agentTypeTv = (TextView) frameLayout.findViewById(R.id.agent_type_tv);
        descriptionTv = (TextView) frameLayout.findViewById(R.id.description_tv);

        locationTv.setText(location);
        phoneNumberTv.setText(phoneNumber);
        String[] agentTypeArray = getResources().getStringArray(R.array.agent_types);
        agentTypeTv.setText(getString(R.string.profession) + ": " + agentTypeArray[agentType]);
        descriptionTv.setText(description);
        return frameLayout;
    }
}
