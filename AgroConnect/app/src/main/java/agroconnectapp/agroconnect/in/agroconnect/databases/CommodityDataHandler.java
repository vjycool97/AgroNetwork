package agroconnectapp.agroconnect.in.agroconnect.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import agroconnectapp.agroconnect.in.agroconnect.entities.CommodityEntity;

/**
 * Created by sumanta on 5/6/16.
 */
public class CommodityDataHandler {

    public static void addAllCommodityData (Context context, List<CommodityEntity> commodityList) {
        deleteAllCommodity(context);
        SQLiteDatabase db = AgroSqliteHelper.getWritableDatabase(context);
        db.beginTransaction();
        try {
            for(CommodityEntity entity : commodityList) {
                ContentValues values = new ContentValues();
                values.put(AgroSqliteHelper.COMMODITY_ID, entity.getId());
                values.put(AgroSqliteHelper.COMMODITY_NAME, entity.getName());
                values.put(AgroSqliteHelper.COMMODITY_LOCAL_NAME, entity.getLocalName());
                db.insertWithOnConflict(AgroSqliteHelper.COMMODITY_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } catch (Exception ex) {
            ex.getStackTrace();
        } finally {
            db.endTransaction();
        }
    }
    public static ArrayList<String> getAllLocalNameFromDB(Context context) {
        ArrayList<String> commodityList = new ArrayList<String>();
        String selectQuery = "SELECT "+ AgroSqliteHelper.COMMODITY_LOCAL_NAME+" FROM " + AgroSqliteHelper.COMMODITY_TABLE;
        SQLiteDatabase db = AgroSqliteHelper.getWritableDatabase(context);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                commodityList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return commodityList;
    }
    public static ArrayList<String> getAllNameFromDB(Context context) {
        ArrayList<String> commodityList = new ArrayList<String>();
        String selectQuery = "SELECT "+ AgroSqliteHelper.COMMODITY_NAME+" FROM " + AgroSqliteHelper.COMMODITY_TABLE;
        SQLiteDatabase db = AgroSqliteHelper.getWritableDatabase(context);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                commodityList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return commodityList;
    }
    public static ArrayList<CommodityEntity> getAllCommodityEntityFromDB(Context context) {
        ArrayList<CommodityEntity> commodityList = new ArrayList<>();
        String selectQuery = "SELECT " + AgroSqliteHelper.COMMODITY_ID + ", " + AgroSqliteHelper.COMMODITY_NAME
                + ", " + AgroSqliteHelper.COMMODITY_LOCAL_NAME + " FROM " + AgroSqliteHelper.COMMODITY_TABLE;
        SQLiteDatabase db = AgroSqliteHelper.getWritableDatabase(context);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                CommodityEntity entity = new CommodityEntity(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
                commodityList.add(entity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return commodityList;
    }

    public static List<CommodityEntity> getAllMatchingCommodity (Context context, String search_str) {
        String likeTemp = search_str.replaceAll("'", "");
        ArrayList<CommodityEntity> entityList = new ArrayList<>();
        String selectQuery = "SELECT  " + AgroSqliteHelper.COMMODITY_ID + "," + AgroSqliteHelper.COMMODITY_NAME
                + ", " + AgroSqliteHelper.COMMODITY_LOCAL_NAME + " FROM " + AgroSqliteHelper.COMMODITY_TABLE + " WHERE  ("
                + AgroSqliteHelper.COMMODITY_LOCAL_NAME + " LIKE '%" + likeTemp + "%' OR "
                + AgroSqliteHelper.COMMODITY_NAME + " LIKE '%" + likeTemp + "%' ) ";
        SQLiteDatabase db = AgroSqliteHelper.getWritableDatabase(context);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                CommodityEntity entity = new CommodityEntity(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
                entityList.add(entity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return entityList;
    }

    public static CommodityEntity getCommodityById (Context context, int id) {
        CommodityEntity entity = new CommodityEntity();
        String selectQuery = "SELECT " + AgroSqliteHelper.COMMODITY_ID + ", " + AgroSqliteHelper.COMMODITY_NAME
                + ", " + AgroSqliteHelper.COMMODITY_LOCAL_NAME + " FROM " + AgroSqliteHelper.COMMODITY_TABLE
                + " WHERE " + AgroSqliteHelper.COMMODITY_ID + "=" + id ;
        SQLiteDatabase db = AgroSqliteHelper.getWritableDatabase(context);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            entity.setId(cursor.getInt(0));
            entity.setName(cursor.getString(1));
            entity.setLocalName(cursor.getString(2));
        }
        cursor.close();
        return entity;
    }

    public static void deleteAllCommodity (Context context) {
        SQLiteDatabase db = AgroSqliteHelper.getWritableDatabase(context);
        db.beginTransaction();
        try {
            db.execSQL("delete from "+ AgroSqliteHelper.COMMODITY_TABLE);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }
}
