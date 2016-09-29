package agroconnectapp.agroconnect.in.agroconnect;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;

/**
 * Created by niteshtarani on 24/02/16.
 */
public class PickerDialog extends Dialog implements View.OnClickListener{

    private RelativeLayout rootLayout;
    private Context context;

    public PickerDialog(Context context) {
        super(context,R.style.DialogThemeTransparent);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= 21)
            getWindow().setStatusBarColor(context.getResources().getColor(R.color.colorPrimaryDark));
        setContentView(R.layout.dialog_chooser);
        rootLayout = (RelativeLayout) findViewById(R.id.root_layout);
        findViewById(R.id.buy_layout).setOnClickListener(this);
        findViewById(R.id.sell_layout).setOnClickListener(this);
        rootLayout.setOnClickListener(this);

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
        alphaAnimation.setDuration(300);
        rootLayout.startAnimation(alphaAnimation);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buy_layout:
                Intent buyInventoryIntent = new Intent(context,AddInventoryActivity.class);
                buyInventoryIntent.putExtra(Constants.isSell,false);
                context.startActivity(buyInventoryIntent);
                this.dismiss();
                break;
            case R.id.sell_layout:
                Intent sellInventoryIntent = new Intent(context,AddInventoryActivity.class);
                sellInventoryIntent.putExtra(Constants.isSell,true);
                context.startActivity(sellInventoryIntent);
                this.dismiss();
                break;
            case R.id.root_layout:
                this.dismiss();
                break;
        }
    }

    @Override
    public void dismiss() {
        try {
            AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
            alphaAnimation.setDuration(300);
            rootLayout.startAnimation(alphaAnimation);
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    PickerDialog.super.dismiss();
                }
            }, 310);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}