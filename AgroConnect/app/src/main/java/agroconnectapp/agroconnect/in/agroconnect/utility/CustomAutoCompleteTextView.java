package agroconnectapp.agroconnect.in.agroconnect.utility;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

/**
 * Created by niteshtarani on 14/01/16.
 */

public class CustomAutoCompleteTextView extends AutoCompleteTextView {

    private static final int MESSAGE_TEXT_CHANGED = 100;
    private static final int DEFAULT_AUTOCOMPLETE_DELAY = 500;
    private ProgressBar mLoadingIndicator;
    private int mAutoCompleteDelay = DEFAULT_AUTOCOMPLETE_DELAY;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (CustomAutoCompleteTextView.this.enoughToFilter()) {
                CustomAutoCompleteTextView.super.performFiltering((CharSequence) msg.obj, msg.arg1);
            } else {
                if (mLoadingIndicator != null) {
                    mLoadingIndicator.setVisibility(View.GONE);
                }
            }
        }
    };

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAutoCompleteDelay(int autoCompleteDelay) {
        mAutoCompleteDelay = autoCompleteDelay;
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {

        if (mLoadingIndicator != null) {
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }
        mHandler.removeMessages(MESSAGE_TEXT_CHANGED);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_TEXT_CHANGED, text), mAutoCompleteDelay);
    }

    @Override
    public void onFilterComplete(int count) {
        if (mLoadingIndicator != null) {
            mLoadingIndicator.setVisibility(View.GONE);
        }
        super.onFilterComplete(count);
    }

    public void setLoadingIndicator(ProgressBar progressBar) {
        mLoadingIndicator = progressBar;
    }

}