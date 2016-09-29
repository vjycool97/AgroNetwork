package agroconnectapp.agroconnect.in.agroconnect.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import agroconnectapp.agroconnect.in.agroconnect.entities.CityEntity;

public class CityDataHandler {

    public static void addAllCityData (Context context, List<CityEntity> cityList) {
        deleteAllCity(context);
        SQLiteDatabase db = AgroSqliteHelper.getWritableDatabase(context);
        db.beginTransaction();
        try {
            for(CityEntity entity : cityList) {
                ContentValues values = new ContentValues();
                values.put(AgroSqliteHelper.CITY_ID, entity.getId());
                values.put(AgroSqliteHelper.CITY_NAME, entity.getName());
                values.put(AgroSqliteHelper.CITY_LOCAL_NAME, entity.getLocalName());
                db.insertWithOnConflict(AgroSqliteHelper.CITY_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } catch (Exception ex) {
            ex.getStackTrace();
        } finally {
            db.endTransaction();
        }
    }
    public static List<String> getAllLocalNameFromDB (Context context) {
        List<String> cityList = new ArrayList<String>();
        String selectQuery = "SELECT "+ AgroSqliteHelper.CITY_LOCAL_NAME+" FROM " + AgroSqliteHelper.CITY_TABLE;
        SQLiteDatabase db = AgroSqliteHelper.getWritableDatabase(context);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                cityList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        Collections.sort(cityList);
        return cityList;
    }
    public static List<CityEntity> getAllCityEntityFromDB(Context context) {
        List<CityEntity> cityList = new ArrayList<CityEntity>();
        String selectQuery = "SELECT " + AgroSqliteHelper.CITY_ID + ", " + AgroSqliteHelper.CITY_NAME
                + ", " + AgroSqliteHelper.CITY_LOCAL_NAME + " FROM " + AgroSqliteHelper.CITY_TABLE;
        SQLiteDatabase db = AgroSqliteHelper.getWritableDatabase(context);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                CityEntity entity = new CityEntity(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
                cityList.add(entity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return cityList;
    }
    public static List<CityEntity> getAllMatchingCity(Context context, String search_str) {
        String likeTemp = search_str.replaceAll("'", "");
        ArrayList<CityEntity> entityList = new ArrayList<>();
        String selectQuery = "SELECT  " + AgroSqliteHelper.CITY_ID + "," + AgroSqliteHelper.CITY_NAME
                + ", " + AgroSqliteHelper.CITY_LOCAL_NAME + " FROM " + AgroSqliteHelper.CITY_TABLE + " WHERE  ("
                + AgroSqliteHelper.CITY_LOCAL_NAME + " LIKE '%" + likeTemp + "%' OR "
                + AgroSqliteHelper.CITY_NAME + " LIKE '%" + likeTemp + "%' ) ";
        SQLiteDatabase db = AgroSqliteHelper.getWritableDatabase(context);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                CityEntity entity = new CityEntity(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
                entityList.add(entity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return entityList;
    }

    public static CityEntity getCityById (Context context, int id) {
        CityEntity entity = new CityEntity();
        String selectQuery = "SELECT " + AgroSqliteHelper.CITY_ID + ", " + AgroSqliteHelper.CITY_NAME
                + ", " + AgroSqliteHelper.CITY_LOCAL_NAME + " FROM " + AgroSqliteHelper.CITY_TABLE
                + " WHERE " + AgroSqliteHelper.CITY_ID + "=" + id ;
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

    public static void deleteAllCity (Context context) {
        SQLiteDatabase db = AgroSqliteHelper.getWritableDatabase(context);
        db.beginTransaction();
        try {
            db.execSQL("delete from "+ AgroSqliteHelper.CITY_TABLE);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }
}
