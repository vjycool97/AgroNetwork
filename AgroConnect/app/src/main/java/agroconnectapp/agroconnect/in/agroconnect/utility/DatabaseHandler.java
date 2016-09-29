package agroconnectapp.agroconnect.in.agroconnect.utility;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;

import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.model.FeedData;

/**
 * Created by niteshtarani on 10/01/16.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "ShortlistDB";

    // Contacts table name
    private static final String TABLE_NAME = "ShortlistTable";

    private static final String listingId = "listing_id";
    private static final String agentId = "agent_id";
    private static final String agentName = "agent_name";
    private static final String agentType = "agent_type";
    private static final String lastUpdated = "last_updated";
    private static final String location = "location";
    private static final String quantity = "quantity";
    private static final String price = "price";
    private static final String isSell = "is_sell";
    private static final String commodityId = "commodity_id";
    private static final String commodityName = "commodity_name";
    private static final String description = "desciption";

    private Activity mContext;

    public DatabaseHandler(Activity context, SQLiteDatabase.CursorFactory factory) {
        super(context.getApplicationContext(), DATABASE_NAME, factory, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createShortlistTable = "CREATE TABLE " + TABLE_NAME + "(" +
                listingId + " INTEGER PRIMARY KEY," +
                agentId + " INTEGER," +
                agentName + " TEXT," +
                agentType + " TEXT," +
                lastUpdated + " TEXT," +
                location + " TEXT," +
                quantity + " TEXT," +
                price + " TEXT," +
                isSell + " TEXT," +
                commodityId + " INTEGER," +
                commodityName + " TEXT," +
                description + " TEXT" + ")";
        db.execSQL(createShortlistTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean recordExists(FeedData feedData) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(
                    TABLE_NAME,
                    new String[]{listingId}, listingId + " = ?",
                    new String[]{String.valueOf(feedData.getListingId())},
                    null, null, null, null);
            if (cursor.getCount() > 0) {
                db.close();
                cursor.close();
                return true;
            } else {
                db.close();
                cursor.close();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(new Throwable("Error checking shortlists from db - " + e.toString()));
            return false;
        }
    }

    public void addToShortlist(FeedData feedData) {

        if(recordExists(feedData)) {

            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.already_shortlisted), Toast.LENGTH_SHORT).show();
                }
            });

        } else {

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(listingId,feedData.getListingId());
            values.put(agentId,feedData.getAgentId());
            values.put(agentName,feedData.getAgentName());
            values.put(agentType,feedData.getAgentType());
            values.put(lastUpdated,feedData.getLastUpdated());
            values.put(location,feedData.getLocation());
            values.put(quantity,feedData.getQuantity());
            values.put(price,feedData.getPrice());
            values.put(isSell,String.valueOf(feedData.isSell()));
            values.put(commodityId,feedData.getCommodityId());
            values.put(commodityName,feedData.getCommodityName());
            values.put(description,feedData.getDescription());
            db.insert(TABLE_NAME, null, values);
            db.close();

            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.added_to_shortlist), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void removeFromShortlist(FeedData feedData) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,listingId + " = ?",new String[]{String.valueOf(feedData.getListingId())});
        db.close();
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.removed_from_shortlist), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public List<FeedData> getAllItems() {
        List<FeedData> feedList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    FeedData fd = new FeedData();
                    fd.setListingId(cursor.getInt(0));
                    fd.setAgentId(cursor.getInt(1));
                    fd.setAgentName(cursor.getString(2));
                    fd.setAgentType(cursor.getString(3));
                    fd.setLastUpdated(cursor.getString(4));
                    fd.setLocation(cursor.getString(5));
                    fd.setQuantity(cursor.getString(6));
                    fd.setPrice(cursor.getString(7));
                    String isSellStr = cursor.getString(8);
                    if (isSellStr != null && Boolean.parseBoolean(isSellStr))
                        fd.setIsSell(true);
                    else
                        fd.setIsSell(false);
                    fd.setCommodityId(cursor.getInt(9));
                    fd.setCommodityName(cursor.getString(10));
                    fd.setDescription(cursor.getString(11));
                    feedList.add(fd);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(new Throwable("Error fetching shortlist from db - " + e.toString()));
        }
        return feedList;
    }

}
