package agroconnectapp.agroconnect.in.agroconnect.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import agroconnectapp.agroconnect.in.agroconnect.entities.NotificationEntity;

/**
 * Created by sumanta on 22/7/16.
 */
public class NotificationDataHandler {
    
    public static void addNotification(Context context, NotificationEntity entity) {
        SQLiteDatabase db = AgroSqliteHelper.getWritableDatabase(context);
        try {
                ContentValues values = new ContentValues();
                values.put(AgroSqliteHelper.NOTIFICATION_MESSAGE, entity.getMessage());
                values.put(AgroSqliteHelper.NOTIFICATION_ID, entity.getId());
                values.put(AgroSqliteHelper.NOTIFICATION_FEED_ID, entity.getFeedId());
                values.put(AgroSqliteHelper.NOTIFICATION_FEED_TYPE, entity.getFeedType());
                values.put(AgroSqliteHelper.NOTIFICATION_AGENT_ID, entity.getAgentId());
                values.put(AgroSqliteHelper.NOTIFICATION_CITY_ID, entity.getCityId());
                values.put(AgroSqliteHelper.NOTIFICATION_COMMODITY_ID, entity.getCommodityId());
                values.put(AgroSqliteHelper.NOTIFICATION_ADVISOR_POST_ID, entity.getAdvisoryPostId());
                values.put(AgroSqliteHelper.NOTIFICATION_TIMESTAMP, entity.getTimestamp());
                db.insertWithOnConflict(AgroSqliteHelper.NOTIFICATION_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        deleteOldNotification(context);
    }

    public static ArrayList<NotificationEntity> getAllNotifications(Context context) {
        ArrayList<NotificationEntity> notificationList = new ArrayList<NotificationEntity>();

        String selectQuery = "SELECT " + AgroSqliteHelper.NOTIFICATION_MESSAGE + ","
                + AgroSqliteHelper.NOTIFICATION_ID + "," + AgroSqliteHelper.NOTIFICATION_FEED_ID + "," + AgroSqliteHelper.NOTIFICATION_FEED_TYPE + "," + AgroSqliteHelper.NOTIFICATION_AGENT_ID + ","
                + AgroSqliteHelper.NOTIFICATION_CITY_ID + "," + AgroSqliteHelper.NOTIFICATION_COMMODITY_ID + "," + AgroSqliteHelper.NOTIFICATION_ADVISOR_POST_ID
                + "," + AgroSqliteHelper.NOTIFICATION_TIMESTAMP + " FROM " + AgroSqliteHelper.NOTIFICATION_TABLE
                + " ORDER BY " + AgroSqliteHelper.NOTIFICATION_TIMESTAMP + " DESC";

        SQLiteDatabase db = AgroSqliteHelper.getWritableDatabase(context);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                NotificationEntity notificationEntity = new NotificationEntity();
                notificationEntity.setMessage(cursor.getString(0));
                notificationEntity.setId(cursor.getString(1));
                notificationEntity.setFeedId(cursor.getString(2));
                notificationEntity.setFeedType(cursor.getString(3));
                notificationEntity.setAgentId(cursor.getString(4));
                notificationEntity.setCityId(cursor.getString(5));
                notificationEntity.setCommodityId(cursor.getString(6));
                notificationEntity.setAdvisoryPostId(cursor.getString(7));
                notificationEntity.setTimestamp(cursor.getLong(8));
                notificationList.add(notificationEntity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notificationList;
    }

    public static void deleteOldNotification(Context context) {
        SQLiteDatabase db = AgroSqliteHelper.getWritableDatabase(context);
        try {
            db.execSQL("DELETE FROM  " + AgroSqliteHelper.NOTIFICATION_TABLE + " WHERE "
                    + AgroSqliteHelper.NOTIFICATION_TIMESTAMP + " not in ("
                    + "SELECT " + AgroSqliteHelper.NOTIFICATION_TIMESTAMP + " FROM "
                    + AgroSqliteHelper.NOTIFICATION_TABLE + " ORDER BY "
                    + AgroSqliteHelper.NOTIFICATION_TIMESTAMP + " desc LIMIT 100)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
