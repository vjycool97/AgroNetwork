package agroconnectapp.agroconnect.in.agroconnect.uicomponents;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import agroconnectapp.agroconnect.in.agroconnect.R;


public class AgroAlertDialog extends AlertDialog {
    private AgroAlertDialog.Builder dialog;
    private Confirmation confirmation;

    public AgroAlertDialog(Context context, String title, String message, String positiveButton, String negativeButton, Confirmation confirmation) {
        super(context);
        this.confirmation = confirmation;
        dialog = new AgroAlertDialog.Builder(context, R.style.AlertDialogStyle);
        dialog.setCancelable(false);
        setTitle(title);
        setMessage(message);
        setPositiveButton(positiveButton);
        setNegativeButton(negativeButton);
    }

    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title))
            dialog.setTitle(title);
    }

    public void setMessage(String message) {
        if (!TextUtils.isEmpty(message))
            dialog.setMessage(message);
    }

    public void setPositiveButton(String label) {
        if (!TextUtils.isEmpty(label)) {
            dialog.setPositiveButton(label, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    confirmation.onPositiveConfirmation();
                }
            });
        }
    }

    public void setNegativeButton(String label) {
        if (!TextUtils.isEmpty(label)) {
            dialog.setNegativeButton(label, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    confirmation.onNegativeConfirmation();
                }
            });
        }
    }

    public void show() {
        dialog.show();
    }

    public interface Confirmation {
        void onPositiveConfirmation();

        void onNegativeConfirmation();
    }
}
