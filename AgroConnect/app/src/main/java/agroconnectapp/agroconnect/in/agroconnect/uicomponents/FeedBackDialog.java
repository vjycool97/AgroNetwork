package agroconnectapp.agroconnect.in.agroconnect.uicomponents;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.components.AgroServerCommunication;
import agroconnectapp.agroconnect.in.agroconnect.databases.PrefDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.utility.KeyIDS;

/**
 * Created by sumanta on 23/7/16.
 */
public class FeedBackDialog extends Dialog {

    private Context context;
    private ProgressDialog dialog;
    private EditText messageET;

    public FeedBackDialog(Context context) {
        super(context);
        this.context = context;
        setCancelable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.feedback_dialog);
        messageET = (EditText) findViewById(R.id.messageET);
        findViewById(R.id.cancelBtn).setOnClickListener(clickListener);
        findViewById(R.id.setBtn).setOnClickListener(clickListener);
    }

    protected void showDialog(String message) {
        if (dialog == null) {
            dialog = new ProgressDialog(context);
        }
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.show();
    }
    protected void hideDialog() {
        if (dialog!=null && dialog.isShowing())
            dialog.dismiss();
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cancelBtn : {
                    PrefDataHandler.getInstance().getEditor().putInt(KeyIDS.FEEDBACK_COUNTER, 0).apply();
                    cancel();
                    break;
                }
                case R.id.setBtn : {
                    String message = messageET.getText().toString();
                    if(TextUtils.isEmpty(message)) {
                        Toast.makeText(context, "Please write feedback", Toast.LENGTH_SHORT).show();
                    } else {
                        sendFeedback(message);
                    }
                    break;
                }
            }
        }
    };

    private void sendFeedback(String message) {
        showDialog("Loading...");
        JSONObject paramObject = new JSONObject();
        try {
            paramObject.put("Message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AgroServerCommunication.INSTANCE.getServerData(context, KeyIDS.FEEDBACK_URI, KeyIDS.FEEDBACK_URI, paramObject, new AgroServerCommunication.OkuTaskListener() {
            @Override
            public void onPostExecute(JSONObject resultObject) {
                hideDialog();
                PrefDataHandler.getInstance().getEditor().putBoolean(KeyIDS.IS_FEEDBACK_SEND, true).apply();
                dismiss();
            }

            @Override
            public void onError() {
                Toast.makeText(context, "Some error occur, try again later", Toast.LENGTH_SHORT).show();
                hideDialog();
            }

            @Override
            public void noNetwork() {
                Toast.makeText(context, "No connection available, try again later", Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        });
    }
}