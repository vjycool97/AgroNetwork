package agroconnectapp.agroconnect.in.agroconnect.uicomponents;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

/**
 * Created by sumanta on 1/8/16.
 */
public class CustomWebview extends WebView {

    public CustomWebview(Context context) {
        super(context);
    }

    public CustomWebview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomWebview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        requestDisallowInterceptTouchEvent(true);
        return super.onTouchEvent(event);
    }
}