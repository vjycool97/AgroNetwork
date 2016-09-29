package agroconnectapp.agroconnect.in.agroconnect.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import agroconnectapp.agroconnect.in.agroconnect.AgroConnect;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.components.AgroServerCommunication;
import agroconnectapp.agroconnect.in.agroconnect.databases.PrefDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.events.NewsFeedEvent;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;
import agroconnectapp.agroconnect.in.agroconnect.utility.HttpRequest;
import agroconnectapp.agroconnect.in.agroconnect.utility.Utils;

/**
 * Created by sumanta on 31/7/16.
 */
@Keep
public class ComposeFragment extends AgroFragment implements View.OnClickListener {

    private EditText cropNameET, seedVarietyET, howOldIsCropET, areaET, pesticideET, contentET;
    private LinearLayout imgContainer, extraInfoLinear;
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int REQUEST_IMAGE_PICK = 2;
    private Dialog imgPickerDialog;
    boolean isGeneral;
    private int feedID;
    private ArrayList<File> fileList = new ArrayList<>();
    private int fileSize;
    private int count = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compose, container, false);
        isGeneral = getArguments().getBoolean("isGeneral");
        ((TextView)view.findViewById(R.id.nameTV)).setText(PrefDataHandler.getInstance().getSharedPref().getString(NAME, ""));
        cropNameET = (EditText) view.findViewById(R.id.cropNameET);
        seedVarietyET = (EditText) view.findViewById(R.id.seedVarietyET);
        howOldIsCropET = (EditText) view.findViewById(R.id.howOldIsCropET);
        areaET = (EditText) view.findViewById(R.id.areaET);
        pesticideET = (EditText) view.findViewById(R.id.pesticideET);
        contentET = (EditText) view.findViewById(R.id.contentET);
        if(isGeneral)
            contentET.setHint(getString(R.string.general_post_label));
        else
            contentET.setHint(getString(R.string.crop_related_label));
        extraInfoLinear = (LinearLayout) view.findViewById(R.id.extraInfoLinear);
        if(isGeneral)
            extraInfoLinear.setVisibility(View.GONE);
        imgContainer = (LinearLayout) view.findViewById(R.id.img_container);
        view.findViewById(R.id.submitBtn).setOnClickListener(this);
        view.findViewById(R.id.imageBtn).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitBtn: {
                if(isGeneral) {
                    String message = "";
                    try {
                        if (TextUtils.isEmpty(contentET.getText().toString().trim())) {
                            message = "Please provide content";
                            throw new Exception();
                        }
                        final JSONObject paramObject = new JSONObject();
                        try {
                            paramObject.put("FeedType", "3");
                            paramObject.put("Content", contentET.getText().toString().trim());
                            pushFeedToServer(paramObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception ex) {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String message = "";
                    try {
                        if (TextUtils.isEmpty(contentET.getText().toString().trim())) {
                            message = "Please provide Details";
                            throw new Exception();
                        }
                        final JSONObject paramObject = new JSONObject();
                        try {
                            paramObject.put("FeedType", "4");
                            String content = "";
                            if(!TextUtils.isEmpty(cropNameET.getText().toString().trim())) {
                                content = content + cropNameET.getText().toString().trim()+", ";
                            }
                            if(!TextUtils.isEmpty(seedVarietyET.getText().toString().trim())) {
                                content = content + "(Beej)-" + seedVarietyET.getText().toString().trim() + ", ";
                            }
                            if(!TextUtils.isEmpty(howOldIsCropET.getText().toString().trim())) {
                                content = content + "(bovia ka samay)-" + howOldIsCropET.getText().toString().trim() +", ";
                            }
                            if(!TextUtils.isEmpty(areaET.getText().toString().trim())) {
                                content = content + "(farm size)-" + areaET.getText().toString().trim() +", ";
                            }
                            if(!TextUtils.isEmpty(pesticideET.getText().toString().trim())) {
                                content = content + "(koi dawai daali)-" + pesticideET.getText().toString().trim() +", ";
                            }
                            content = content + contentET.getText().toString().trim();

                            paramObject.put("Content", content);
                            pushFeedToServer(paramObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception ex) {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
            case R.id.imageBtn: {
                initializeAndShowImgPickerDialog();
                break;
            }
            case R.id.camara_btn:
                try {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.unsupported_operation),Toast.LENGTH_LONG).show();
                }
                imgPickerDialog.dismiss();

                break;
            case R.id.gallery_btn:
                try {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, REQUEST_IMAGE_PICK);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.unsupported_operation),Toast.LENGTH_LONG).show();
                }
                imgPickerDialog.dismiss();
                break;
        }
    }

    private void pushFeedToServer(JSONObject paramObject) {
        showDialog("Sharing...");
        AgroServerCommunication.INSTANCE.getServerData(getActivity(), FEED_UPLOAD_URI, FEED_UPLOAD_URI, paramObject, new AgroServerCommunication.OkuTaskListener() {
            @Override
            public void onPostExecute(JSONObject resultObject) {
                hideDialog();
                feedID = resultObject.optInt("Id");
                if(!fileList.isEmpty()) {
                    fileSize = fileList.size() -1;
                    uploadImage(fileList.get(count));
                } else {
                    getActivity().onBackPressed();
                    AgroConnect.agroEventBus.post(new NewsFeedEvent());
                }
            }

            @Override
            public void onError() {
                hideDialog();
            }

            @Override
            public void noNetwork() {
                hideDialog();
            }
        });
    }

    private void initializeAndShowImgPickerDialog() {
        imgPickerDialog = new Dialog(getActivity());
        imgPickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        imgPickerDialog.setContentView(R.layout.dialog_img_picker);
        Button cameraBtn = (Button) imgPickerDialog.findViewById(R.id.camara_btn);
        Button galleryBtn = (Button) imgPickerDialog.findViewById(R.id.gallery_btn);
        cameraBtn.setOnClickListener(this);
        galleryBtn.setOnClickListener(this);
        imgPickerDialog.show();
    }

    private void uploadImage(final File file) {
        try {
            showDialog("Uploading Image...");
            RequestBody rb = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addFormDataPart("image.png","image.png",RequestBody.create(MediaType.parse("image/png"), file))
                    .build();

            HttpRequest request = new HttpRequest(true, Constants.token, PrefDataHandler.getInstance().getSharedPref().getString(Constants.token, Constants.defaultToken));
            Callback cb = new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(final Response response) throws IOException {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                hideDialog();
                                if(response.isSuccessful()) {
                                    JSONObject responseObj = new JSONObject(response.body().string());
                                    count++;
                                    if(count <= fileSize) {
                                        uploadImage(fileList.get(count));
                                    } else {
                                        getActivity().onBackPressed();
                                        AgroConnect.agroEventBus.post(new NewsFeedEvent());
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            };
            String url =  "https://mandiex.com/farmville/api/feed/"+feedID+"/AddImage";
            request.doPostRequest(url,rb,cb);
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(new Throwable("Exception in upload image - " + e.toString()));
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK && data != null){
            try {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                Bitmap scaled = Utils.getResizedBitmap(bitmap, 720);
                bitmap.recycle();

                File f = Utils.saveImageToStorage(scaled);
                fileList.add(f);
                Bitmap imgBitmap = BitmapFactory.decodeFile(f.getPath());
                ImageView iv = new ImageView(getActivity());
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(400, 400);
                param.setMargins(5, 20, 5, 20);
                iv.setLayoutParams(param);
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                iv.setImageBitmap(imgBitmap);
                imgContainer.addView(iv);

            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.logException(new Throwable("error in image from camera - " + e.toString()));
            }
        }
        if(requestCode == REQUEST_IMAGE_PICK && resultCode == getActivity().RESULT_OK && data != null){
            try {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                Bitmap scaled = Utils.getScaledBitmap(picturePath, 720);
                File f = Utils.saveImageToStorage(scaled);
                fileList.add(f);
                Bitmap imgBitmap = BitmapFactory.decodeFile(f.getPath());
                ImageView iv = new ImageView(getActivity());
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(400, 400);
                param.setMargins(5, 20, 5, 20);
                iv.setLayoutParams(param);
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                iv.setImageBitmap(imgBitmap);
                imgContainer.addView(iv);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
