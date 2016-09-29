package agroconnectapp.agroconnect.in.agroconnect.databases;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import agroconnectapp.agroconnect.in.agroconnect.AgroConnect;
import agroconnectapp.agroconnect.in.agroconnect.activities.InitActivity;
import agroconnectapp.agroconnect.in.agroconnect.activities.ProfileActivity;
import agroconnectapp.agroconnect.in.agroconnect.components.ServerCommunication;
import agroconnectapp.agroconnect.in.agroconnect.entities.CityEntity;
import agroconnectapp.agroconnect.in.agroconnect.entities.CommodityEntity;
import agroconnectapp.agroconnect.in.agroconnect.utility.KeyIDS;

public class AgroSqliteHelper extends SQLiteOpenHelper implements KeyIDS {

    private static final String DATABASE_NAME = "agro.db";
    private static final int DATABASE_VERSION = 5;

    private final String CREATE_TABLE_CITY = "CREATE TABLE "
            + CITY_TABLE + "(" + CITY_ID + " INTEGER," + CITY_NAME
            + " TEXT," + CITY_LOCAL_NAME + " TEXT);";

    private final String CREATE_TABLE_COMMODITY = "CREATE TABLE "
            + COMMODITY_TABLE + "(" + COMMODITY_ID + " INTEGER," + COMMODITY_NAME
            + " TEXT," + COMMODITY_LOCAL_NAME + " TEXT);";

    private final String CREATE_TABLE_NOTIFICATION = "CREATE TABLE "
            + NOTIFICATION_TABLE + "(" + NOTIFICATION_MESSAGE + " TEXT," + NOTIFICATION_ID + " TEXT," + NOTIFICATION_FEED_ID + " TEXT," + NOTIFICATION_FEED_TYPE + " TEXT,"
            + NOTIFICATION_AGENT_ID + " TEXT," + NOTIFICATION_CITY_ID + " TEXT,"
            + NOTIFICATION_COMMODITY_ID + " TEXT," + NOTIFICATION_ADVISOR_POST_ID + " TEXT," + NOTIFICATION_TIMESTAMP + " INTEGER);";


    private static AgroSqliteHelper agroSqliteHelper;

    private AgroSqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static AgroSqliteHelper getAgroSqliteHelper(Context context) {
        if (agroSqliteHelper == null) {
            agroSqliteHelper = new AgroSqliteHelper(context);
        }
        return agroSqliteHelper;
    }

    public static SQLiteDatabase getWritableDatabase(Context context) {
        return getAgroSqliteHelper(context).getWritableDatabase();
    }

    public static SQLiteDatabase getReadableDatabase(Context context) {
        return getAgroSqliteHelper(context).getReadableDatabase();
    }

    public static void closeDatabase(Context context) {
        getAgroSqliteHelper(context).close();
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_CITY);
        database.execSQL(CREATE_TABLE_COMMODITY);
        database.execSQL(CREATE_TABLE_NOTIFICATION);
        getCities();
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        if(newVersion>oldVersion) {
            database.execSQL("DROP TABLE IF EXISTS " + AgroSqliteHelper.NOTIFICATION_TABLE);
            database.execSQL("DROP TABLE IF EXISTS " + AgroSqliteHelper.CITY_TABLE);
            database.execSQL("DROP TABLE IF EXISTS " + AgroSqliteHelper.COMMODITY_TABLE);
//            database.execSQL("ALTER TABLE notif_t ADD COLUMN notif_feed_id TEXT");
//            database.execSQL("ALTER TABLE notif_t ADD COLUMN notif_feed_type TEXT");
            onCreate(database);
        }
    }

    private void getCommodities() {
        final Context context = AgroConnect.getInstance().getContext();
        ArrayMap<String, String> paramMap = new ArrayMap<>();
        paramMap.put("commodityVersion", "0");
        paramMap.put("LanguageId", PrefDataHandler.getInstance().getSharedPref().getString(SELECTED_LANGUAGE, "hi"));
        paramMap.put("lat", PrefDataHandler.getInstance().getSharedPref().getString(LATITUDE, ""));
        paramMap.put("lng", PrefDataHandler.getInstance().getSharedPref().getString(LONGITUDE, ""));
        ServerCommunication.INSTANCE.getServerData(context, false, true, Request.Method.GET, COMMODITY_URI, COMMODITY_URI, paramMap, new ServerCommunication.OkuTaskListener() {
            @Override
            public void onPostExecute(String result) {
                if(!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject responseObject = new JSONObject(result);
                        int version = responseObject.optInt("CommodityVersion");
                        PrefDataHandler.getInstance().getEditor().putInt(COMMODITY_VERSION, version).apply();
                        List<CommodityEntity> commodityList = new Gson().fromJson(responseObject.getJSONArray("CommodityDTO").toString(), new TypeToken<List<CommodityEntity>>() {}.getType());
                        CommodityDataHandler.addAllCommodityData(context, commodityList);
                        PrefDataHandler.getInstance().getEditor().putBoolean(PROFILE_PAGE, true).apply();

                        /*
                        *  after finished syncing start profile activity.
                        */
//                        if(!TextUtils.isEmpty(PrefDataHandler.getInstance().getSharedPref().getString(NAME, ""))) {
//                            final String deviceId = Settings.Secure.getString(InitActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
//                            getToken(deviceId);
//                        } else{
//                        Intent profileIntent = new Intent(InitActivity.this, ProfileActivity.class);
//                        startActivity(profileIntent);
//                        finish();
//                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError() {

            }

            @Override
            public void noNetwork() {

            }
        });
    }


    private void getCities() {
        final Context context = AgroConnect.getInstance().getContext();
        ArrayMap<String, String> paramMap = new ArrayMap<>();
        paramMap.put("cityVersion", "0");
        paramMap.put("LanguageId", PrefDataHandler.getInstance().getSharedPref().getString(SELECTED_LANGUAGE, "hi"));
        paramMap.put("lat", PrefDataHandler.getInstance().getSharedPref().getString(LATITUDE, ""));
        paramMap.put("lng", PrefDataHandler.getInstance().getSharedPref().getString(LONGITUDE, ""));
        ServerCommunication.INSTANCE.getServerData(context, false, true, Request.Method.GET, CITY_URI, CITY_URI, paramMap, new ServerCommunication.OkuTaskListener() {
            @Override
            public void onPostExecute(String result) {
                if(!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject responseObject = new JSONObject(result);
                        int version = responseObject.optInt("CityVersion");
                        PrefDataHandler.getInstance().getEditor().putInt(CITY_VERSION, version).apply();
                        List<CityEntity> cityList = new Gson().fromJson(responseObject.getJSONArray("CityDTO").toString(), new TypeToken<List<CityEntity>>() {}.getType());
                        CityDataHandler.addAllCityData(context, cityList);
                        getCommodities();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError() {

            }

            @Override
            public void noNetwork() {

            }
        });
    }

}
