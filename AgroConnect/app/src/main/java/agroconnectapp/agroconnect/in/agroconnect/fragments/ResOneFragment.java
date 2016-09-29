package agroconnectapp.agroconnect.in.agroconnect.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import agroconnectapp.agroconnect.in.agroconnect.AgroConnect;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.activities.InitActivity;
import agroconnectapp.agroconnect.in.agroconnect.activities.RegistrationActivity;
import agroconnectapp.agroconnect.in.agroconnect.components.ServerCommunication;
import agroconnectapp.agroconnect.in.agroconnect.databases.PrefDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.events.OtpEvent;
import agroconnectapp.agroconnect.in.agroconnect.receivers.SmsReceiver;
import agroconnectapp.agroconnect.in.agroconnect.uicomponents.AgroAlertDialog;
import agroconnectapp.agroconnect.in.agroconnect.utility.KeypadUtil;

public class ResOneFragment extends AgroFragment {
    private EditText phoneET;
    private Button registerButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_res_one, container, false);
        phoneET = (EditText) view.findViewById(R.id.phoneET);
        registerButton = (Button) view.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        phoneET.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    register();
                    return true;
                }
                return false;
            }
        });
        return view;
    }

    private void register() {
        KeypadUtil.hideKeypad(getActivity(), phoneET);
        final String phone = phoneET.getText().toString();
        if (!TextUtils.isEmpty(phone) && phone.length() == 10) {
            registerButton.setEnabled(false);
            /*
            *  Open registering dialog
            */
            showDialog("Registering...");
            registerButton.setEnabled(true);
            ArrayMap<String, String> paramMap = new ArrayMap<>();
            paramMap.put("mobileNumber", phone);
            ServerCommunication.INSTANCE.getServerData(getActivity(), false, true, Request.Method.POST, REGISTER_URI, REGISTER_URI, paramMap, new ServerCommunication.OkuTaskListener() {
                @Override
                public void onPostExecute(String result) {
                    hideDialog();
                    if (!TextUtils.isEmpty(result)) {
                        PrefDataHandler.getInstance().getEditor().putBoolean(PHONE_SUBMITTED, true).apply();
                        PrefDataHandler.getInstance().getEditor().putString(PHONE_NUMBER, phone).apply();
                        Intent intent = new Intent(getActivity(), InitActivity.class);
                        startActivity(intent);
                        if(getActivity() instanceof RegistrationActivity) {
                            ((RegistrationActivity)getActivity()).finish();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Some error occur, try again", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError() {
                    hideDialog();
                    Toast.makeText(getActivity(), "Some error occur, try again", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void noNetwork() {
                    hideDialog();
                    Toast.makeText(getActivity(), "No network available", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            phoneET.setError(getString(R.string.enter_valid_number));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ServerCommunication.INSTANCE.cancelRequests(REGISTER_URI);
    }
}
