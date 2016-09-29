package google.volly.helper;

import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import agroconnectapp.agroconnect.in.agroconnect.AgroConnect;


/**
 * Created by shakti on 3/22/2015.
 */
public class VollyHelper  {

    public static final String TAG = VollyHelper.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static VollyHelper mInstance = null;

    public static VollyHelper getInstance() {
        if(mInstance == null)
            mInstance = new VollyHelper();
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(AgroConnect.getInstance().getContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue, new google.volly.helper.LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}