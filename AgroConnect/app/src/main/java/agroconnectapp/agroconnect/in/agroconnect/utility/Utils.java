package agroconnectapp.agroconnect.in.agroconnect.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import agroconnectapp.agroconnect.in.agroconnect.PickerDialog;
import agroconnectapp.agroconnect.in.agroconnect.R;

/**
 * Created by nitin.gupta on 12/13/2015.
 */
public class Utils {
    public static  boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null)
                    for (int i = 0; i < info.length; i++)
                        if (info[i].getState() == NetworkInfo.State.CONNECTED)
                            return true;
            }
            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    public static String getDateDifference( String lastUpdatedDate){
        String lastUpdate = "";
        //HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date d1 = null;
        Date d2 = new Date();

        try {
            d1 = inputFormat.parse(lastUpdatedDate);
            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            if(diffDays >= 1){
                lastUpdate = diffDays + " days ago";
            }else if(diffHours >= 1){
                lastUpdate = diffHours + " hours ago";
            }else if(diffMinutes >= 1){
                lastUpdate = diffMinutes + " minutes ago";
            }else
                lastUpdate = "Just now";

        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(new Throwable("Error parsing date in utils - " + e.toString()));
        }
        return lastUpdate;
    }

    public static String showTimeStamp(Long timeStamp) {
        long currentTimeStamp = System.currentTimeMillis();
        long diff = (currentTimeStamp - timeStamp) / 1000;
        int diffVal = 0;

        String diffSuffix = "sec";
        if (diff > 86400 * 365) {
            diffVal = (int) Math.floor((double) diff / (86400 * 365));
            diffVal = (diffVal == 0) ? 1 : diffVal;
            diffSuffix = diffVal > 1 ? "years" : "year";
        } else if (diff > 86400 * 30) {
            diffVal = (int) Math.floor((double) diff / (86400 * 30));
            diffVal = (diffVal == 0) ? 1 : diffVal;
            diffSuffix = diffVal > 1 ? "months" : "month";
        } else if (diff > 86400) {
            diffVal = (int) Math.floor((double) diff / (86400));
            diffVal = (diffVal == 0) ? 1 : diffVal;
            diffSuffix = diffVal > 1 ? "days" : "day";
        } else if (diff > 3600) {
            diffVal = (int) Math.floor((double) diff / (3600));
            diffVal = (diffVal == 0) ? 1 : diffVal;
            diffSuffix = diffVal > 1 ? "hours" : "hour";
        } else if (diff > 60) {
            diffVal = (int) Math.floor((double) diff / (60));
            diffVal = (diffVal == 0) ? 1 : diffVal;
            diffSuffix = diffVal > 1 ? "minutes" : "minute";
        } else if (diff > 0) {
            diffVal = (int) diff;
        }
        return diffVal + " " + diffSuffix + " ago";
    }

    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        try {
            final int sideLength = Math.min(bitmap.getWidth(), bitmap.getHeight());
            final Bitmap output = Bitmap.createBitmap(sideLength, sideLength, Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(output);
            final int color = Color.RED;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, sideLength, sideLength);
            final RectF rectF = new RectF(rect);

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawOval(rectF, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            return output;
        } catch (Exception e) {
            return bitmap;
        } catch (OutOfMemoryError e) {
            return bitmap;
        }
    }

    public static void addInfo(Context context, final PickerDialog pickerDialog, final RelativeLayout mainLayout) {

        try {
            View view = LayoutInflater.from(context).inflate(R.layout.info_layout, null);

            final LinearLayout layout = (LinearLayout) view.findViewById(R.id.root_info_layout);
            RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rllp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layout.setLayoutParams(rllp);

            Button b = (Button) view.findViewById(R.id.btn);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickerDialog.show();
                }
            });
            ImageView closeBtn = (ImageView) view.findViewById(R.id.close);
            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainLayout.removeView(layout);
                }
            });
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mainLayout.addView(layout);
                    layout.bringToFront();
                }
            }, 2000);
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(new Throwable("Exception in showing info dialog - " + e.toString()));
        }
    }

    public static String getCurrentVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "0.0.1";
        }
    }

    public static boolean checkPlayServices(Activity activity) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, 9000)
                        .show();
            } else {
                Toast.makeText(activity,
                        R.string.unsupported_operation, Toast.LENGTH_LONG)
                        .show();
                activity.finish();
            }
            return false;
        }
        return true;
    }

    public static File saveImageToStorage(Bitmap bitmap, String name){
        File myDir = new File(Constants.rootPath);
        if(!myDir.exists())
            myDir.mkdirs();
        String fname = name +".jpg";
        File file = new File (myDir, fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            //bm2.compress(CompressFormat.JPEG, 80, stream);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
            out.flush();
            out.close();
            return file;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File saveImageToStorage(Bitmap bitmap){
        File myDir = new File(Constants.rootPath);
        if(!myDir.exists())
            myDir.mkdirs();
        String fname = System.currentTimeMillis() +".jpg";
        File file = new File (myDir, fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            //bm2.compress(CompressFormat.JPEG, 80, stream);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
            out.flush();
            out.close();
            return file;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static Bitmap getScaledBitmap(String imageFilePath, int maxSize) {

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFilePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = 1;
        if(maxSize != -1) {
            float bitmapRatio = (float) photoW / (float) photoH;
            if (bitmapRatio > 1) {
                scaleFactor = (int) ((float) photoW / (float) maxSize);
                photoW = maxSize;
                photoH = (int) (photoW / bitmapRatio);
            } else {
                scaleFactor = (int) ((float) photoH / (float) maxSize);
                photoH = maxSize;
                photoW = (int) (photoH * bitmapRatio);
            }
        }

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath, bmOptions);
        return bitmap;
    }

    public static void takeScreenShotAndShare (Context context, View view) {
        View rootview = view.getRootView();
        rootview.setDrawingCacheEnabled(true);
        Bitmap bitmap = rootview.getDrawingCache();
        if(bitmap!=null) {
            shareOnWhatsApp(context, bitmap, rootview);
        }
    }
    public static void shareOnWhatsApp (Context context, Bitmap bitmap, View rootView) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String fileName = System.currentTimeMillis()+".jpg";
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + fileName);
        try {
            f.createNewFile();
            new FileOutputStream(f).write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        share.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.whatsapp_string));
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse(Environment.getExternalStorageDirectory() + File.separator + fileName));
        if (isPackageInstalled("com.whatsapp", context)) {
            share.setPackage("com.whatsapp");
            context.startActivity(Intent.createChooser(share, "Share Image"));
        } else {
            Toast.makeText(context, "Please Install Whatsapp", Toast.LENGTH_LONG).show();
        }
        rootView.setDrawingCacheEnabled(false);
    }

    public static boolean isPackageInstalled(String packagename, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
