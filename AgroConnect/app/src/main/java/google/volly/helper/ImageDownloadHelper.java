package google.volly.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.io.File;
import java.io.FileOutputStream;

import agroconnectapp.agroconnect.in.agroconnect.AgroConnect;


public class ImageDownloadHelper {

    Context myContext;
    String imageUrl;
    public String myFilePath;
    public ImageDownloadHelperDelegate delegate;

    public interface ImageDownloadHelperDelegate {
        void didFinishLoadingFile(File file);
        void didFailedLoadingFile();
        void didChangedLoadProgress();
    }

    public ImageDownloadHelper(Context context, String url) {
        myContext = context;
        imageUrl = url;
        myFilePath = MD5(url) + ".jpg";
    }

    public void startDownload() {
        //Picasso.with(myContext).load(imageUrl).into(target);
        Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                Picasso.with(myContext).load(imageUrl).into(target);
                //Picasso.with(mContext).load(formatStaticImageUrlWith(location)).transform(new StaticMapTransformation()).into(remoteView, R.id.static_map_image, Constants.NOTIFICATION_ID_NEW_TRIP_OFFER, notification);
            }
        });
    }

    Target target = new Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    File file = new File(getCacheDir(), myFilePath);
                    try
                    {
                        file.createNewFile();
                        FileOutputStream ostream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, ostream);
                        ostream.close();
                        delegate.didFinishLoadingFile(file);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        delegate.didFailedLoadingFile();
                    }
                }
            }).start();
        }
        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            delegate.didFailedLoadingFile();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            if (placeHolderDrawable != null) {
            }
        }
    };

    public void downloadUsingVolly() {
        ImageRequest ir = new ImageRequest(imageUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(final Bitmap bitmap) {
                // callback
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        File file = new File(getCacheDir(), myFilePath);
                        try
                        {
                            file.createNewFile();
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, ostream);
                            ostream.close();
                            delegate.didFinishLoadingFile(file);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            delegate.didFailedLoadingFile();
                        }
                    }
                }).start();

            }
        }, 0, 0, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                delegate.didFailedLoadingFile();
            }
        });
        VollyHelper.getInstance().addToRequestQueue(ir);
    }


    public static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte anArray : array) {
                sb.append(Integer.toHexString((anArray & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            Log.e("tmessages", e.getMessage());
        }
        return null;
    }

    public static File getCacheDir() {
        if (Environment.getExternalStorageState() == null || Environment.getExternalStorageState().startsWith(Environment.MEDIA_MOUNTED)) {
            try {
                Log.e("getCacheDir", " : getExternalCacheDir");
                File file = AgroConnect.getInstance().getContext().getExternalCacheDir();
                if (file != null) {
                    return file;
                }
            } catch (Exception e) {
                Log.e("tmessages", e.getMessage());
            }
        }
        try {
            Log.e("getCacheDir", " : getCacheDir");
            File file = AgroConnect.getInstance().getContext().getCacheDir();
            if (file != null) {
                return file;
            }
        } catch (Exception e) {
            Log.e("tmessages", e.getMessage());
        }

        Log.e("getCacheDir", " : return  junk");
        return new File("");
    }
}
