package agroconnectapp.agroconnect.in.agroconnect;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import com.etiennelawlor.imagegallery.library.enums.PaletteColorType;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import agroconnectapp.agroconnect.in.agroconnect.activities.AgroActivity;
import agroconnectapp.agroconnect.in.agroconnect.adapters.AdvisoryAdapter;
import agroconnectapp.agroconnect.in.agroconnect.components.AgroBang;
import agroconnectapp.agroconnect.in.agroconnect.components.AgroServerCommunication;
import agroconnectapp.agroconnect.in.agroconnect.databases.PrefDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.model.AdvisoryData;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;
import agroconnectapp.agroconnect.in.agroconnect.utility.HttpRequest;
import agroconnectapp.agroconnect.in.agroconnect.utility.PaletteTransformation;
import agroconnectapp.agroconnect.in.agroconnect.utility.Utils;

public class AdvisoryActivity extends AgroActivity implements View.OnClickListener{
//public class AdvisoryActivity extends BaseActivity implements View.OnClickListener, ImageGalleryAdapter.ImageThumbnailLoader, FullScreenImageGalleryAdapter.FullScreenImageLoader{

    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int REQUEST_IMAGE_PICK = 2;
    private List<AdvisoryData> dataListMain;
    private List<AdvisoryData> dataList;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private boolean loading = false;
    private RelativeLayout errorLayout;
    private TextView errorText;
    private Button tryAgainBtn;
    private TextView answerBtn, labelTV;
    private FloatingActionButton floatingActionButton;
    private AdvisoryAdapter advisoryAdapter;
    private SharedPreferences sharedPreferences;
    private LinearLayout imgContainer, answerLinear, thankLinear;
    private Dialog postDialog;
    private Dialog imgPickerDialog;
    private ProgressBar dialogProgressBar;
    private int postId = -1;
    private int agentId = -1;
    private int newPostId = -1;
    private int parentID;
    private boolean isMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_recyclerview);
        setToolbar();
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            if(bundle.containsKey(Constants.postId))
                postId = bundle.getInt(Constants.postId);
            if(bundle.containsKey(Constants.agentIdKey))
                agentId = bundle.getInt(Constants.agentIdKey);
        }


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(this);
        answerLinear = (LinearLayout) findViewById(R.id.answerLinear);
        thankLinear = (LinearLayout) findViewById(R.id.thankLinear);
        thankLinear.setOnClickListener(this);
        labelTV = (TextView) findViewById(R.id.labelTV);
        answerBtn = (TextView) findViewById(R.id.answer_btn);
        answerBtn.setOnClickListener(this);
        errorLayout = (RelativeLayout) findViewById(R.id.error_layout);
        errorText = (TextView) findViewById(R.id.error_tv);
        tryAgainBtn = (Button) findViewById(R.id.try_again_btn);
        tryAgainBtn.setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.feed_list_view);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        dataListMain = new ArrayList<>();;
        dataList = new ArrayList<>();
        if(Utils.isNetworkAvailable(this)) {
            getAdvisoryData(1);
        } else {
            errorText.setText(getResources().getString(R.string.error_msg_internet));
            errorLayout.setVisibility(View.VISIBLE);
        }

        if(postId == -1) {
            isMenu = false;
            answerLinear.setVisibility(View.GONE);
            recyclerView.addOnScrollListener(new EndlessScrollListener(layoutManager));
            advisoryAdapter = new AdvisoryAdapter(this, dataListMain, Constants.TYPE_FEED);
            if(agentId == -1)
                floatingActionButton.setVisibility(View.VISIBLE);
        }
        else {
            isMenu = true;
            advisoryAdapter = new AdvisoryAdapter(this, dataListMain, Constants.TYPE_DETAIL);
            answerLinear.setVisibility(View.VISIBLE);
            /*LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.d56);
            recyclerView.setLayoutParams(layoutParams);*/
        }
        recyclerView.setAdapter(advisoryAdapter);

        //ImageGalleryActivity.setImageThumbnailLoader(this);
        //FullScreenImageGalleryActivity.setFullScreenImageLoader(this);

        // optionally set background color using Palette for full screen images
        mPaletteColorType = PaletteColorType.VIBRANT;
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.discuss));
        toolbar.setTitleTextColor(getResources().getColor(R.color.color_white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_whatsapp, menu);
        if (isMenu)
            menu.findItem(R.id.whatsappId).setVisible(true);
        else
            menu.findItem(R.id.whatsappId).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.whatsappId) {
            Utils.takeScreenShotAndShare(AdvisoryActivity.this, recyclerView);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                initializeAndShowChooserDialog();
                break;
            case R.id.answer_btn:
                initializeAndShowPostDialog(getString(R.string.add_new_answer));
                break;

            case R.id.imageLinear:
                initializeAndShowImgPickerDialog();
                break;

            case R.id.try_again_btn:
                if (Utils.isNetworkAvailable(AdvisoryActivity.this)) {
                    getAdvisoryData(1);
                } else {
                    errorText.setText(getResources().getString(R.string.error_msg_internet));
                    errorLayout.setVisibility(View.VISIBLE);
                }
                break;
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
            case R.id.thankLinear : {
                sayThank();
                break;
            }
        }
    }

    private void sayThank() {
        JSONObject paramObject = new JSONObject();
        try {
            paramObject.put("ParentId", parentID);
            paramObject.put("Description", "1001thx");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        showDialog(getString(R.string.loading));
        AgroServerCommunication.INSTANCE.getServerData(AdvisoryActivity.this, ADVISOR_DATA_URI, ADVISOR_DATA_URI, paramObject, new AgroServerCommunication.OkuTaskListener() {
            @Override
            public void onPostExecute(JSONObject resultObject) {
                hideDialog();
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

                Intent intent = new Intent(AdvisoryActivity.this, AdvisoryActivity.class);
                intent.putExtra(Constants.agentIdKey, sharedPreferences.getInt(Constants.agentIdKey, 0));
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
                    Toast.makeText(AdvisoryActivity.this, R.string.description_required, Toast.LENGTH_SHORT).show();
                }
                else
                    addNewPost(questionET.getText().toString(), "", "", "", "", questionDialog);

            }
        });
        questionDialog.show();
    }

    private void initializeAndShowPostDialog(final String dialogHeader) {
        final Dialog answerDialog = new Dialog(this);
        answerDialog.setContentView(R.layout.fragment_answer);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = answerDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);


        final EditText solutionET = (EditText) answerDialog.findViewById(R.id.solutionET);
        final EditText diagnoseET = (EditText) answerDialog.findViewById(R.id.diagnoseET);
        final EditText appliedET = (EditText) answerDialog.findViewById(R.id.appliedET);
        final EditText dosageET = (EditText) answerDialog.findViewById(R.id.dosageET);
        final EditText timeET = (EditText) answerDialog.findViewById(R.id.timeET);
        imgContainer = (LinearLayout) answerDialog.findViewById(R.id.img_container);

        answerDialog.findViewById(R.id.submitBtn).setOnClickListener(this);
        answerDialog.findViewById(R.id.imageLinear).setOnClickListener(this);

        answerDialog.findViewById(R.id.submitBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (solutionET.getText().toString().isEmpty()) {
                    Toast.makeText(AdvisoryActivity.this, R.string.solution_required, Toast.LENGTH_SHORT).show();
                }
                else
                    addNewPost(solutionET.getText().toString(), diagnoseET.getText().toString(), appliedET.getText().toString(), dosageET.getText().toString(),
                            timeET.getText().toString(), answerDialog);
            }
        });

        answerDialog.show();
    }

    private void addNewPost(String description, String problemDiagnosed, String producToApply, String dosage,
                            String applicationTime, final Dialog dialog) {

        showDialog("Uploading...");
        String token = sharedPreferences.getString(Constants.token,Constants.defaultToken);
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
                            Toast.makeText(AdvisoryActivity.this,R.string.post_successful,Toast.LENGTH_SHORT).show();
                            newPostId = -1;
                            if(dialog != null)
                                dialog.dismiss();
                            getAdvisoryData(1);
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

    private void getAdvisoryData(int pageId) {

        try {
            if(pageId == 1) {
                progressBar.setVisibility(View.VISIBLE);
                dataListMain.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        errorLayout.setVisibility(View.GONE);
        try {
            HttpRequest request = new HttpRequest(true,Constants.token,sharedPreferences.getString(Constants.token,Constants.defaultToken));
            Callback callback =  new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            errorText.setText(getResources().getString(R.string.error_msg_unknown));
                            errorLayout.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
                @Override
                public void onResponse(Response response) throws IOException {
                    if(response.isSuccessful()){
                        try {
                            JSONArray advisoryArray = new JSONArray(response.body().string());
                            if(advisoryArray != null) {
                                parseData(advisoryArray);
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Crashlytics.logException(new Throwable("Error parsing advisory posts - " + e.toString()));
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                errorText.setText(getResources().getString(R.string.error_msg_unknown));
                                errorLayout.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            };

            String dataUrl = "";
            if(postId == -1) {
                if(agentId == -1)
                    dataUrl = Constants.advisoryPostUrl + "?pageId=" + pageId;
                else
                    dataUrl = Constants.advisoryPostUrl + "?pageId=" + pageId + "&postedByAgentid=" + agentId;
            }
            else {
                dataUrl = Constants.advisoryPostUrl + "/" + postId + "?pageId=" + pageId;
            }

            request.doGetRequest(dataUrl, callback);
        } catch (IOException e) {
            e.printStackTrace();
            Crashlytics.logException(new Throwable("Exception in get advisory data - " + e.toString()));
        }
    }

    private void parseData(final JSONArray advisoryArray) {

        removeProgressBar();

        dataList.clear();
        if(advisoryArray.length() == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    advisoryAdapter.notifyDataSetChanged();
                }
            });
            return;
        }
        for (int count = 0; count < advisoryArray.length(); count++) {
            JSONObject Obj = null;
            Obj = advisoryArray.optJSONObject(count);
            if (Obj != null) {
                final AdvisoryData ad = new AdvisoryData();
                ad.setId(Obj.optInt("Id"));
                ad.setAgentId(Obj.optInt("AgentId"));
                ad.setAgentName(Obj.optString("AgentName"));
                ad.setAgentType(Obj.optInt("AgentType"));
                ad.setAgentCity(Obj.optString("AgentCity"));
                ad.setParentId(Obj.optString("ParentId"));
                ad.setProblemAsDiagnosedByAdvisor(Obj.optString("ProblemAsDiagnosedByAdvisor"));
                ad.setProductToBeApplied(Obj.optString("ProductToBeApplied"));
                ad.setDosage(Obj.optString("Dosage"));
                ad.setApplicationTime(Obj.optString("ApplicationTime"));
                ad.setAdditionalAdvice(Obj.optString("AdditionalAdvice"));
                ad.setLastUpdated(Utils.getDateDifference(Obj.optString("LastUpdated")));

                if (!Obj.optString("Description").equalsIgnoreCase("null") && !Obj.optString("Description").isEmpty())
                    ad.setDescription(Obj.optString("Description"));
                else
                    ad.setDescription(getString(R.string.not_available));

                if (!Obj.optString("AgentPhoneNumber").equalsIgnoreCase("null") && !Obj.optString("AgentPhoneNumber").isEmpty())
                    ad.setAgentPhone(Obj.optString("AgentPhoneNumber"));
                else
                    ad.setAgentPhone(getString(R.string.not_available));

                if (!Obj.optString("AgentCommodity").equalsIgnoreCase("null") && !Obj.optString("AgentCommodity").isEmpty())
                    ad.setAgentCommodity(Obj.optString("AgentCommodity"));
                else
                    ad.setAgentCommodity(getString(R.string.not_available));

                if (!Obj.optString("AgentOrganisation").equalsIgnoreCase("null") && !Obj.optString("AgentOrganisation").isEmpty())
                    ad.setAgentOrganisation(Obj.optString("AgentOrganisation"));
                else
                    ad.setAgentOrganisation(getString(R.string.not_available));

                if (!Obj.optString("AgentDepartment").equalsIgnoreCase("null") && !Obj.optString("AgentDepartment").isEmpty())
                    ad.setAgentDepartment(Obj.optString("AgentDepartment"));
                else
                    ad.setAgentDepartment(getString(R.string.not_available));

                if (!Obj.optString("Replies").equalsIgnoreCase("null") && !Obj.optString("Replies").isEmpty())
                    ad.setRepliesCount(Obj.optString("Replies"));
                else
                    ad.setRepliesCount(Obj.optString("0"));

                JSONArray resources = Obj.optJSONArray("Resources");
                if (resources != null)
                    ad.setResources(resources);

                ad.setType(Constants.TYPE_FEED);
                dataList.add(ad);
                if (count == 0) {
                    parentID = ad.getId();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (ad.getAgentId() != PrefDataHandler.getInstance().getSharedPref().getInt(AGENT_ID, 0)) {
                                int type = PrefDataHandler.getInstance().getSharedPref().getInt(AGENT_TYPE, 0);
                                if (type != 2) {
                                    labelTV.setVisibility(View.VISIBLE);
                                    labelTV.setText(getString(R.string.label));
                                    recyclerView.setPadding(0, 70, 0, 10);
                                }
                            } else {
                                if (advisoryArray.length() > 1) {
                                    thankLinear.setVisibility(View.VISIBLE);
                                    recyclerView.setPadding(0, 10, 0, 10);
                                }
                            }
                        }
                    });
                }

            }
        }
        dataListMain.addAll(dataList);
        loading = false;
        if(postId == -1)
            addProgressBar();
        else {
            AdvisoryData ad = new AdvisoryData();
            ad.setType(Constants.TYPE_SEPERATOR);
            dataListMain.add(1,ad);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                advisoryAdapter.notifyDataSetChanged();
            }
        });

    }

    private class EndlessScrollListener extends RecyclerView.OnScrollListener {

        private int pastItems, visibleItemCount,totalItemCount;
        private LinearLayoutManager mLayoutManager;
        private int pageId = 2;

        public EndlessScrollListener(LinearLayoutManager mLayoutManager) {
            this.mLayoutManager = mLayoutManager;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            totalItemCount = mLayoutManager.getItemCount();
            visibleItemCount = mLayoutManager.getChildCount();
            pastItems = mLayoutManager.findFirstVisibleItemPosition();
            if(!loading) {
                if (pastItems + visibleItemCount >= totalItemCount) {
                    getAdvisoryData(pageId);
                    loading = true;
                    pageId++;
                }
            }
        }
    }

    private void addProgressBar(){
        if(dataListMain != null && !dataListMain.isEmpty()) {
            AdvisoryData ad = new AdvisoryData();
            ad.setType(Constants.TYPE_PROGRESS_BAR);
            dataListMain.add(ad);
        }
    }

    private void removeProgressBar(){
        if(dataListMain != null && !dataListMain.isEmpty()) {
            dataListMain.remove(dataListMain.size() - 1);
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

            HttpRequest request = new HttpRequest(true,Constants.token,sharedPreferences.getString(Constants.token,Constants.defaultToken));
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
                                ImageView iv = new ImageView(AdvisoryActivity.this);
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


    //@Override
    public void loadImageThumbnail(ImageView iv, String imageUrl, int dimension) {
        if (!TextUtils.isEmpty(imageUrl)) {
            Picasso.with(iv.getContext())
                    .load(imageUrl)
                    .resize(dimension, dimension)
                    .centerCrop()
                    .into(iv);
        } else {
            iv.setImageDrawable(null);
        }
    }
    // endregion

    // region FullScreenImageGalleryAdapter.FullScreenImageLoader

    //@Override
    public void loadFullScreenImage(ImageView iv, String imageUrl, int width, final LinearLayout bglinearLayout) {
        if (!TextUtils.isEmpty(imageUrl)) {
            Picasso.with(iv.getContext())
                    .load(imageUrl)
                    .resize(width, 0)
                    .transform(PaletteTransformation.instance())
                    .into(iv, new PaletteTransformation.PaletteCallback(iv) {
                        @Override
                        public void onError() {

                        }

                        @Override
                        public void onSuccess(Palette palette) {
                            int bgColor = getBackgroundColor(palette);
                            if (bgColor != -1)
                                bglinearLayout.setBackgroundColor(bgColor);
                        }
                    });
        } else {
            iv.setImageDrawable(null);
        }
    }
    private PaletteColorType mPaletteColorType;

    // region Helper Methods
    private int getBackgroundColor(Palette palette) {
        int bgColor = -1;

        int vibrantColor = palette.getVibrantColor(0x000000);
        int lightVibrantColor = palette.getLightVibrantColor(0x000000);
        int darkVibrantColor = palette.getDarkVibrantColor(0x000000);

        int mutedColor = palette.getMutedColor(0x000000);
        int lightMutedColor = palette.getLightMutedColor(0x000000);
        int darkMutedColor = palette.getDarkMutedColor(0x000000);

        if (mPaletteColorType != null) {
            switch (mPaletteColorType) {
                case VIBRANT:
                    if (vibrantColor != 0) { // primary option
                        bgColor = vibrantColor;
                    } else if (lightVibrantColor != 0) { // fallback options
                        bgColor = lightVibrantColor;
                    } else if (darkVibrantColor != 0) {
                        bgColor = darkVibrantColor;
                    } else if (mutedColor != 0) {
                        bgColor = mutedColor;
                    } else if (lightMutedColor != 0) {
                        bgColor = lightMutedColor;
                    } else if (darkMutedColor != 0) {
                        bgColor = darkMutedColor;
                    }
                    break;
                case LIGHT_VIBRANT:
                    if (lightVibrantColor != 0) { // primary option
                        bgColor = lightVibrantColor;
                    } else if (vibrantColor != 0) { // fallback options
                        bgColor = vibrantColor;
                    } else if (darkVibrantColor != 0) {
                        bgColor = darkVibrantColor;
                    } else if (mutedColor != 0) {
                        bgColor = mutedColor;
                    } else if (lightMutedColor != 0) {
                        bgColor = lightMutedColor;
                    } else if (darkMutedColor != 0) {
                        bgColor = darkMutedColor;
                    }
                    break;
                case DARK_VIBRANT:
                    if (darkVibrantColor != 0) { // primary option
                        bgColor = darkVibrantColor;
                    } else if (vibrantColor != 0) { // fallback options
                        bgColor = vibrantColor;
                    } else if (lightVibrantColor != 0) {
                        bgColor = lightVibrantColor;
                    } else if (mutedColor != 0) {
                        bgColor = mutedColor;
                    } else if (lightMutedColor != 0) {
                        bgColor = lightMutedColor;
                    } else if (darkMutedColor != 0) {
                        bgColor = darkMutedColor;
                    }
                    break;
                case MUTED:
                    if (mutedColor != 0) { // primary option
                        bgColor = mutedColor;
                    } else if (lightMutedColor != 0) { // fallback options
                        bgColor = lightMutedColor;
                    } else if (darkMutedColor != 0) {
                        bgColor = darkMutedColor;
                    } else if (vibrantColor != 0) {
                        bgColor = vibrantColor;
                    } else if (lightVibrantColor != 0) {
                        bgColor = lightVibrantColor;
                    } else if (darkVibrantColor != 0) {
                        bgColor = darkVibrantColor;
                    }
                    break;
                case LIGHT_MUTED:
                    if (lightMutedColor != 0) { // primary option
                        bgColor = lightMutedColor;
                    } else if (mutedColor != 0) { // fallback options
                        bgColor = mutedColor;
                    } else if (darkMutedColor != 0) {
                        bgColor = darkMutedColor;
                    } else if (vibrantColor != 0) {
                        bgColor = vibrantColor;
                    } else if (lightVibrantColor != 0) {
                        bgColor = lightVibrantColor;
                    } else if (darkVibrantColor != 0) {
                        bgColor = darkVibrantColor;
                    }
                    break;
                case DARK_MUTED:
                    if (darkMutedColor != 0) { // primary option
                        bgColor = darkMutedColor;
                    } else if (mutedColor != 0) { // fallback options
                        bgColor = mutedColor;
                    } else if (lightMutedColor != 0) {
                        bgColor = lightMutedColor;
                    } else if (vibrantColor != 0) {
                        bgColor = vibrantColor;
                    } else if (lightVibrantColor != 0) {
                        bgColor = lightVibrantColor;
                    } else if (darkVibrantColor != 0) {
                        bgColor = darkVibrantColor;
                    }
                    break;
                default:
                    break;
            }
        }

        return bgColor;
    }
}
