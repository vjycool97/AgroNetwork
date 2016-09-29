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
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.databases.PrefDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;
import agroconnectapp.agroconnect.in.agroconnect.utility.HttpRequest;
import agroconnectapp.agroconnect.in.agroconnect.utility.Utils;

/**
 * Created by sumanta on 6/6/16.
 */
@Keep
public class QuestionFragment extends AgroFragment implements View.OnClickListener {

    private EditText questionET;
    private LinearLayout imgContainer;
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int REQUEST_IMAGE_PICK = 2;
    private Dialog imgPickerDialog;
    private int newPostId = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question, container, false);
        questionET = (EditText) view.findViewById(R.id.questionET);
        imgContainer = (LinearLayout) view.findViewById(R.id.img_container);
        view.findViewById(R.id.submitBtn).setOnClickListener(this);
        view.findViewById(R.id.imageLinear).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitBtn: {
                String question = questionET.getText().toString();
                if (TextUtils.isEmpty(question)) {
                    Toast.makeText(getActivity(), R.string.description_required, Toast.LENGTH_SHORT).show();
                } else {
                    addNewPost(question, "", "", "", "", null);
                }
                break;
            }
            case R.id.imageLinear: {
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
                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(i, REQUEST_IMAGE_PICK);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.unsupported_operation),Toast.LENGTH_LONG).show();
                }
                imgPickerDialog.dismiss();
                break;
        }
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

            HttpRequest request = new HttpRequest(true,Constants.token, PrefDataHandler.getInstance().getSharedPref().getString(Constants.token, Constants.defaultToken));
            Callback cb = new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                    Log.e("Request","failed");
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
                                    newPostId = responseObj.optInt("AdvisoryPostId");
                                }
                                Bitmap imgBitmap = BitmapFactory.decodeFile(file.getPath());
                                ImageView iv = new ImageView(getActivity());
                                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(400, 400);
                                param.setMargins(5, 20, 5, 20);
                                iv.setLayoutParams(param);
                                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                iv.setImageBitmap(imgBitmap);
                                imgContainer.addView(iv);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Crashlytics.logException(new Throwable("Exception in upload image - " + e.toString()));
                            }
                        }
                    });
                    Log.e("Upload Img", "" + response.code());

                }
            };
            String url = Constants.uploadImgUrl;
//            if(newPostId != -1 && postId != -1) {
//                url = url + "?advisoryPostId=" + newPostId + "&advisoryPostParentId=" + postId;
//            } else {
                if(newPostId != -1)
                    url = url + "?advisoryPostId=" + newPostId;
//                if(postId != -1)
//                    url = url + "?advisoryPostParentId=" + postId;
//            }

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

                File f = Utils.saveImageToStorage(scaled,"image");
                uploadImage(f);

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
                File f = Utils.saveImageToStorage(scaled,"image");


                //File file = new File(picturePath);
                uploadImage(f);

            } catch (Exception e) {
                e.printStackTrace();
//                Crashlytics.logException(new Throwable("error picking profile image from gallery - " + e.toString()));
            }
        }

    }

    private void addNewPost(String description, String problemDiagnosed, String producToApply, String dosage,
                            String applicationTime, final Dialog dialog) {




        showDialog("Uploading...");
        String token = PrefDataHandler.getInstance().getSharedPref().getString(Constants.token,Constants.defaultToken);
        HttpRequest request = new HttpRequest(true,Constants.token,token);
        Callback cb = new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideDialog();
                        Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.error_msg_unknown), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {


                if(response.isSuccessful()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideDialog();
                            Toast.makeText(getActivity(), R.string.post_successful,Toast.LENGTH_SHORT).show();
                            newPostId = -1;
                            if(dialog != null)
                                dialog.dismiss();
                            getActivity().onBackPressed();
                            /*searchET.setText("");
                            entityList.clear();
                            adapterNew.notifyDataSetChanged();
                            recyclerView.setVisibility(View.GONE);
                            match = "";
                            pageId = 1;
                            emptyTV.setVisibility(View.VISIBLE);
                            emptyTV.setText(getString(R.string.loading));
                            getAdvisorData(match);*/
                        }
                    });
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideDialog();
                            Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.error_msg_unknown), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        };
        try {
            String url = Constants.advisoryPostUrl;
            JSONObject params = new JSONObject();
            params.put(Constants.description,description);
            /*if(postId != -1) {
                params.put(Constants.parentId, postId);
                params.put(Constants.problemAsDiagnosedByAdvisor,problemDiagnosed);
                params.put(Constants.productToBeApplied,producToApply);
                params.put(Constants.dosage,dosage);
                params.put(Constants.applicationTime,applicationTime);
            }*/
            if(newPostId != -1) {
                params.put("Id", newPostId);
                url = url + "/" + newPostId;
            }
            RequestBody rb = RequestBody.create(request.JSON,params.toString());
            request.doPostRequest(url, rb, cb);

        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(new Throwable("Exception in add new post - " + e.toString()));
        }
    }
}
