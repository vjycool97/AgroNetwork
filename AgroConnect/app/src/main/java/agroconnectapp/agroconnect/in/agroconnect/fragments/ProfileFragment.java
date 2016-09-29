package agroconnectapp.agroconnect.in.agroconnect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import agroconnectapp.agroconnect.in.agroconnect.activities.ProfileActivity;
import agroconnectapp.agroconnect.in.agroconnect.databases.PrefDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;
import agroconnectapp.agroconnect.in.agroconnect.MyInventoryActivity;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.utility.KeyIDS;

/**
 * Created by nitin.gupta on 12/6/2015.
 */
public class ProfileFragment extends AgroFragment implements View.OnClickListener{

    private ImageView profileImage;
    private ImageView profileEdit;
    private TextView nameTv;
    private TextView mandiTv;
    private TextView cityTv;
    private TextView phoneNumberTv;
    private TextView descriptionTv;
    private TextView organizationTv;
    private TextView departmentTv;
    private TextView emailTv;
    private TextView agentTypeTv;
    private Button myInventoryBtn;

    public static ProfileFragment newInstance() {
        ProfileFragment f = new ProfileFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        profileImage = (ImageView)rootView.findViewById(R.id.profile_iv);
        nameTv = (TextView) rootView.findViewById(R.id.name_tv);
        cityTv = (TextView) rootView.findViewById(R.id.city_tv);
        phoneNumberTv = (TextView) rootView.findViewById(R.id.phone_number_tv);
        descriptionTv = (TextView) rootView.findViewById(R.id.description_tv);
        organizationTv = (TextView) rootView.findViewById(R.id.organization_tv);
        departmentTv = (TextView) rootView.findViewById(R.id.department_tv);
        emailTv = (TextView) rootView.findViewById(R.id.email_tv);
        agentTypeTv = (TextView) rootView.findViewById(R.id.agent_type_tv);
        profileEdit = (ImageView) rootView.findViewById(R.id.profile_edit_iv);
        profileEdit.setOnClickListener(this);

        myInventoryBtn = (Button) rootView.findViewById(R.id.my_inventory_btn);
        myInventoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), MyInventoryActivity.class));
            }
        });

        if(getArguments() != null && getArguments().containsKey(Constants.userGeneralProfile) && getArguments().getBoolean(Constants.userGeneralProfile))
            myInventoryBtn.setVisibility(View.GONE);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        intent.putExtra(EDIT_PROFILE, true);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        String name = PrefDataHandler.getInstance().getSharedPref().getString(KeyIDS.NAME, "");
        String city = getString(R.string.city) + ": "+PrefDataHandler.getInstance().getSharedPref().getString(KeyIDS.CITY, "");
        String number = getString(R.string.mobile_number) + ": "+PrefDataHandler.getInstance().getSharedPref().getString(KeyIDS.PHONE_NUMBER, "");
        String[] agentTypeArray = getResources().getStringArray(R.array.agent_types);
        String type = getString(R.string.profession) + ": " + agentTypeArray[PrefDataHandler.getInstance().getSharedPref().getInt(KeyIDS.AGENT_TYPE, 5)];
        String desc = getString(R.string.description) + ": "+PrefDataHandler.getInstance().getSharedPref().getString(KeyIDS.DESCRIPTION, "");
        String organization = getString(R.string.organization) + ": " +PrefDataHandler.getInstance().getSharedPref().getString(KeyIDS.ORGANIZATION, "");
        String department = getString(R.string.department) + ": " +PrefDataHandler.getInstance().getSharedPref().getString(KeyIDS.DEPARTMENT, "");
        String email = getString(R.string.email) + ": " +PrefDataHandler.getInstance().getSharedPref().getString(KeyIDS.EMAIL, "");

        nameTv.setText(name);
        cityTv.setText(city);
        phoneNumberTv.setText(number);
        descriptionTv.setText(desc);
        agentTypeTv.setText(type);

        if(PrefDataHandler.getInstance().getSharedPref().getInt(KeyIDS.AGENT_TYPE, 5) == 2 || PrefDataHandler.getInstance().getSharedPref().getInt(KeyIDS.AGENT_TYPE, 5) == 3) {
            organizationTv.setText(organization);
            departmentTv.setText(department);
            emailTv.setText(email);
        } else {
            organizationTv.setVisibility(View.GONE);
            departmentTv.setVisibility(View.GONE);
            emailTv.setVisibility(View.GONE);
        }

        /*try {
            String imgString = sharedPreferences.getString(Constants.encodedProfileBitmap, "");
            if (!imgString.isEmpty()) {
                byte[] imgAsBytes = Base64.decode(imgString, Base64.DEFAULT);
                profileImage.setImageBitmap(BitmapFactory.decodeByteArray(imgAsBytes, 0, imgAsBytes.length));
                Bitmap circularBitmap = Utils.getCircleBitmap(BitmapFactory.decodeByteArray(imgAsBytes, 0, imgAsBytes.length));
                profileImage.setImageBitmap(circularBitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(new Throwable("Exception in showing profile pic"));
        }*/
        super.onResume();

    }
}
