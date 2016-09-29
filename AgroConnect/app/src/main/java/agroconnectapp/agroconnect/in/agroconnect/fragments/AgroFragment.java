package agroconnectapp.agroconnect.in.agroconnect.fragments;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import agroconnectapp.agroconnect.in.agroconnect.utility.KeyIDS;

/**
 * Created by sumanta on 4/6/16.
 */
public class AgroFragment extends Fragment implements KeyIDS {

    protected ProgressDialog dialog;
    protected void showDialog(String message) {
        if (dialog == null) {
            dialog = new ProgressDialog(getActivity());
        }
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.show();
    }
    protected void hideDialog() {
        if (dialog!=null && dialog.isShowing())
            dialog.dismiss();
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            uiVisible();
        }
    }
    public void uiVisible() {}
}
