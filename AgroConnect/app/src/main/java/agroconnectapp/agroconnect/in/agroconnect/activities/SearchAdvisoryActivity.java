package agroconnectapp.agroconnect.in.agroconnect.activities;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import agroconnectapp.agroconnect.in.agroconnect.AdvisoryActivity;
import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.adapters.AdvisorAdapterNew;
import agroconnectapp.agroconnect.in.agroconnect.components.ServerCommunication;
import agroconnectapp.agroconnect.in.agroconnect.databases.CommodityDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.databases.PrefDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.entities.AdvisorEntity;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;
import agroconnectapp.agroconnect.in.agroconnect.utility.HttpRequest;
import agroconnectapp.agroconnect.in.agroconnect.utility.KeypadUtil;
import agroconnectapp.agroconnect.in.agroconnect.utility.Utils;

/**
 * Created by sumanta on 9/7/16.
 */
public class SearchAdvisoryActivity extends AgroActivity implements View.OnClickListener {

    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int REQUEST_IMAGE_PICK = 2;
    private int pageId = 1;
    private RecyclerView recyclerView;
    private FloatingActionButton fabBtn;
    private List<AdvisorEntity> entityList = new ArrayList<>();
    private AdvisorAdapterNew adapterNew;
    private LinearLayout imgContainer;
    private Dialog imgPickerDialog;
    private int postId = -1;
    private int agentId = -1;
    private int newPostId = -1;
    private AutoCompleteTextView searchET;
    private String match = "";
    private TextView emptyTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advisory_new);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            if(bundle.containsKey(Constants.postId))
                postId = bundle.getInt(Constants.postId);
            if(bundle.containsKey(Constants.agentIdKey))
                agentId = bundle.getInt(Constants.agentIdKey);
        }

        emptyTV = (TextView) findViewById(R.id.emptyTV);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterNew = new AdvisorAdapterNew(this, entityList, recyclerView);
        recyclerView.setAdapter(adapterNew);

        adapterNew.setOnLoadMoreListener(new AdvisorAdapterNew.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if(entityList.size()>14) {
                    entityList.add(null); //add null , so the adapter will check view_type and show progress bar at bottom
                    adapterNew.notifyItemInserted(entityList.size() - 1);
                    getAdvisorData(match);
                }
            }
        });

        searchET = (AutoCompleteTextView) findViewById(R.id.searchET);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.tag));
        searchET.setAdapter(arrayAdapter);
        searchET.setThreshold(1);
        searchET.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                KeypadUtil.hideKeypad(SearchAdvisoryActivity.this, searchET);
                String input = adapterView.getAdapter().getItem(i).toString().trim();
                if(!match.equals(input)) {
                    entityList.clear();
                    adapterNew.notifyDataSetChanged();
                    recyclerView.setVisibility(View.GONE);
                    match = input;
                    pageId = 1;
                    emptyTV.setVisibility(View.VISIBLE);
                    emptyTV.setText(getString(R.string.loading));
                    getAdvisorData(match);
                }
            }
        });
        searchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    KeypadUtil.hideKeypad(SearchAdvisoryActivity.this, searchET);
                    String input = searchET.getText().toString().trim();
                    if(!match.equals(input)) {
                        entityList.clear();
                        adapterNew.notifyDataSetChanged();
                        recyclerView.setVisibility(View.GONE);
                        match = input;
                        pageId = 1;
                        emptyTV.setVisibility(View.VISIBLE);
                        emptyTV.setText(getString(R.string.loading));
                        getAdvisorData(match);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void getAdvisorData(final String match) {
        ArrayMap<String, String> paramMap = new ArrayMap<>();
        paramMap.put("pageId", String.valueOf(pageId));
        if(!TextUtils.isEmpty(match))
            paramMap.put("match", match);
        if(pageId == 1) {
            showDialog(getString(R.string.loading));
        }
        ServerCommunication.INSTANCE.getServerData(this, true, true, com.android.volley.Request.Method.GET, ADVISOR_DATA_URI, ADVISOR_DATA_URI, paramMap, new ServerCommunication.OkuTaskListener() {
            @Override
            public void onPostExecute(String result) {
                recyclerView.setVisibility(View.VISIBLE);
                if (pageId == 1) {
                    hideDialog();
                }
                if (entityList.size() > 14) {
                    entityList.remove(entityList.size() - 1); //remove progress item
                    adapterNew.notifyItemRemoved(entityList.size());
                }
                if(!TextUtils.isEmpty(result)) {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        List<AdvisorEntity> dataList = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<AdvisorEntity>>(){}.getType());

                        if (!dataList.isEmpty()) {
                            emptyTV.setVisibility(View.GONE);
                            entityList.addAll(dataList);
                            adapterNew.notifyDataSetChanged();
                            pageId++;
                        } else {
                            if(pageId==1) {
                                emptyTV.setVisibility(View.VISIBLE);
                                emptyTV.setText(getString(R.string.no_record_found));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapterNew.setLoaded();
            }

            @Override
            public void onError() {
                if(pageId == 1)
                    hideDialog();
                if (entityList.contains(null)) {
                    entityList.remove(null); //remove progress item
                    adapterNew.notifyDataSetChanged();
                }
            }

            @Override
            public void noNetwork() {
                if(pageId == 1)
                    hideDialog();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageLinear: {
                initializeAndShowImgPickerDialog();
                break;
            }
            case R.id.camara_btn:
                try {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                } catch (Exception e) {
                    Toast.makeText(this,getResources().getString(R.string.unsupported_operation),Toast.LENGTH_LONG).show();
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
                    Toast.makeText(this,getResources().getString(R.string.unsupported_operation),Toast.LENGTH_LONG).show();
                }
                imgPickerDialog.dismiss();
                break;
        }
    }

    private void initializeAndShowImgPickerDialog() {
        imgPickerDialog = new Dialog(this);
        imgPickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        imgPickerDialog.setContentView(R.layout.dialog_img_picker);
        Button cameraBtn = (Button) imgPickerDialog.findViewById(R.id.camara_btn);
        Button galleryBtn = (Button) imgPickerDialog.findViewById(R.id.gallery_btn);
        cameraBtn.setOnClickListener(this);
        galleryBtn.setOnClickListener(this);
        imgPickerDialog.show();
    }

    private void initializeAndShowChooserDialog() {
        final Dialog chooserDialog = new Dialog(this,R.style.DialogThemeTransparent);
        chooserDialog.setContentView(R.layout.dialog_chooser);
        ((TextView)chooserDialog.findViewById(R.id.symbol1)).setText("Q");
        ((TextView)chooserDialog.findViewById(R.id.symbol2)).setText("P");
        ((TextView)chooserDialog.findViewById(R.id.button1)).setText(getString(R.string.add_new_question));
        ((TextView)chooserDialog.findViewById(R.id.button2)).setText(getString(R.string.view_all_posts));
        (chooserDialog.findViewById(R.id.buy_layout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                initializeAndShowPostDialog(getString(R.string.add_new_question));
                questionDialog();
                chooserDialog.dismiss();
            }
        });
        (chooserDialog.findViewById(R.id.sell_layout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SearchAdvisoryActivity.this, AdvisoryActivity.class);
                intent.putExtra(Constants.agentIdKey, PrefDataHandler.getInstance().getSharedPref().getInt(AGENT_ID, 0));
                startActivity(intent);
                chooserDialog.dismiss();
            }
        });
        chooserDialog.show();
    }

    private void questionDialog () {
        final Dialog questionDialog = new Dialog(this);
        questionDialog.setContentView(R.layout.fragment_question);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = questionDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);

        final EditText questionET = (EditText) questionDialog.findViewById(R.id.questionET);
        imgContainer = (LinearLayout) questionDialog.findViewById(R.id.img_container);
        questionDialog.findViewById(R.id.imageLinear).setOnClickListener(this);

        questionDialog.findViewById(R.id.submitBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (questionET.getText().toString().isEmpty()) {
                    Toast.makeText(SearchAdvisoryActivity.this, R.string.description_required, Toast.LENGTH_SHORT).show();
                }
                else
                    addNewPost(questionET.getText().toString(), "", "", "", "", questionDialog);

            }
        });
        questionDialog.show();
    }

    private void addNewPost(String description, String problemDiagnosed, String producToApply, String dosage,
                            String applicationTime, final Dialog dialog) {

        showDialog("Uploading...");
        String token = PrefDataHandler.getInstance().getSharedPref().getString(Constants.token,Constants.defaultToken);
        HttpRequest request = new HttpRequest(true,Constants.token,token);
        Callback cb = new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideDialog();
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_msg_unknown), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {


                if(response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideDialog();
                            Toast.makeText(SearchAdvisoryActivity.this,R.string.post_successful,Toast.LENGTH_SHORT).show();
                            newPostId = -1;
                            if(dialog != null)
                                dialog.dismiss();
                            searchET.setText("");
                            entityList.clear();
                            adapterNew.notifyDataSetChanged();
                            recyclerView.setVisibility(View.GONE);
                            match = "";
                            pageId = 1;
                            emptyTV.setVisibility(View.VISIBLE);
                            emptyTV.setText(getString(R.string.loading));
                            getAdvisorData(match);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideDialog();
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_msg_unknown), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        };
        try {
            String url = Constants.advisoryPostUrl;
            JSONObject params = new JSONObject();
            params.put(Constants.description,description);
            if(postId != -1) {
                params.put(Constants.parentId, postId);
                params.put(Constants.problemAsDiagnosedByAdvisor,problemDiagnosed);
                params.put(Constants.productToBeApplied,producToApply);
                params.put(Constants.dosage,dosage);
                params.put(Constants.applicationTime,applicationTime);
            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null){
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
        if(requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null){

            try {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
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

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                hideDialog();
                                if(response.isSuccessful()) {
                                    JSONObject responseObj = new JSONObject(response.body().string());
                                    newPostId = responseObj.optInt("AdvisoryPostId");
                                }
                                /*Bitmap imgBitmap = BitmapFactory.decodeFile(file.getPath());
                                ImageView iv = new ImageView(AdvisoryActivity.this);
                                iv.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.d100), getResources().getDimensionPixelSize(R.dimen.d100)));
                                int padding = getResources().getDimensionPixelSize(R.dimen.d10);
                                iv.setPadding(padding, padding, padding, padding);
                                iv.setImageBitmap(imgBitmap);
                                imgContainer.addView(iv);*/


                                Bitmap imgBitmap = BitmapFactory.decodeFile(file.getPath());
                                ImageView iv = new ImageView(SearchAdvisoryActivity.this);
//                                iv.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.d100), getResources().getDimensionPixelSize(R.dimen.d100)));
                                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(400, 400);
                                param.setMargins(5, 20, 5, 20);
                                iv.setLayoutParams(param);
                                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                                iv.setAdjustViewBounds(true);
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
            if(newPostId != -1 && postId != -1) {
                url = url + "?advisoryPostId=" + newPostId + "&advisoryPostParentId=" + postId;
            } else {
                if(newPostId != -1)
                    url = url + "?advisoryPostId=" + newPostId;
                if(postId != -1)
                    url = url + "?advisoryPostParentId=" + postId;
            }

            request.doPostRequest(url,rb,cb);
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(new Throwable("Exception in upload image - " + e.toString()));
        }
    }
}
