package agroconnectapp.agroconnect.in.agroconnect.activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import agroconnectapp.agroconnect.in.agroconnect.utility.KeyIDS;

/**
 * Created by sumanta on 4/6/16.
 */
public class AgroActivity extends AppCompatActivity implements KeyIDS {
    protected ProgressDialog dialog;
    protected void showDialog(String message) {
        if (dialog == null) {
            dialog = new ProgressDialog(AgroActivity.this);
        }
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.show();
    }
    protected void hideDialog() {
        if (dialog!=null && dialog.isShowing())
            dialog.dismiss();
    }
}
