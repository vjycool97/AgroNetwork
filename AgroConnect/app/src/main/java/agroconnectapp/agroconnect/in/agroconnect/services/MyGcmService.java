package agroconnectapp.agroconnect.in.agroconnect.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.gcm.GcmListenerService;

import agroconnectapp.agroconnect.in.agroconnect.R;
import agroconnectapp.agroconnect.in.agroconnect.activities.FirstNewsFeedActivity;
import agroconnectapp.agroconnect.in.agroconnect.activities.NewsFeedActivity;
import agroconnectapp.agroconnect.in.agroconnect.activities.SecondNewsFeedActivity;
import agroconnectapp.agroconnect.in.agroconnect.activities.ThirdNewsFeedActivity;
import agroconnectapp.agroconnect.in.agroconnect.databases.NotificationDataHandler;
import agroconnectapp.agroconnect.in.agroconnect.entities.NotificationEntity;
import agroconnectapp.agroconnect.in.agroconnect.utility.Constants;

/**
 * Created by niteshtarani on 31/01/16.
 */
public class MyGcmService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
//        {"NotificationType":5,"FeedId":1295,"Message":" blah blah blah"}
        String message = data.getString("Message");
        String id = data.getString("NotificationType");
        String feedId = data.getString("FeedId");
        String feedType = data.getString("FeedType");
        String agentId = data.getString("AgentId");
        String cityId = data.getString("CityId");
        String commodityId = data.getString("CommodityId");
        String advisoryPostId = data.getString("AdvisoryPostId");
//        Log.e(getClass().getSimpleName(), message);
//        Log.e(getClass().getSimpleName(), "Feed id String : " + data.getString("FeedId"));
//        Log.e(getClass().getSimpleName(), data.toString());
//        Log.e(getClass().getSimpleName(), "Feed id: " + String.valueOf(feedId));
        NotificationEntity entity = new NotificationEntity();
        entity.setMessage(message);
        entity.setId(id);
        entity.setFeedId(feedId);
        entity.setFeedType(feedType);
        entity.setAgentId(agentId);
        entity.setCityId(cityId);
        entity.setCommodityId(commodityId);
        entity.setAdvisoryPostId(advisoryPostId);
        entity.setTimestamp(System.currentTimeMillis());
        NotificationDataHandler.addNotification(this, entity);
        if (from.startsWith("/topics/")) {
            // message received from some topic.
        }
//        } else {
//            // normal downstream message.
//        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(message, id, feedId, feedType, agentId,commodityId,cityId, advisoryPostId);
        // [END_EXCLUDE]
    }
    // [END receive_message]
    private void sendNotification(String message, String id, String feedID, String feedType, String agentId, String commodityId, String cityId, String advisoryPostId) {
        try {
            Intent intent = new Intent(this, NewsFeedActivity.class);
            intent.putExtra("id", id);
//            intent.putExtra("feedType", Integer.parseInt(feedType));
            if(id.equals("1")) {
//                intent = new Intent(this, ProfileActivity.class);
                intent.putExtra(Constants.agentIdKey, Integer.parseInt(agentId));
            } else if(id.equals("3")) {
         //       intent = new Intent(this, MandiPricesFragment.class);
                if(commodityId != null && !commodityId.isEmpty())
                    intent.putExtra(Constants.commodityId,commodityId);
                if(cityId != null && !cityId.isEmpty())
                    intent.putExtra(Constants.cityId,cityId);
            } else if(id.equals("4")) {
//                intent = new Intent(this, AdvisoryActivity.class);
                int postId =Integer.parseInt(advisoryPostId);
                intent.putExtra(Constants.postId, postId);
            } else if (id.equals("5")) {
               intent.putExtra("feedId", Integer.parseInt(feedID));
               intent.putExtra("feedType", Integer.parseInt(feedType));
            }
            /*else
                intent = new Intent(this, HomeActivity.class);*/

//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

//            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
//            stackBuilder.addNextIntent(intent);
//            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.icon_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_launcher))
                    .setContentTitle("Agroconnect")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setColor(Color.DKGRAY)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(Integer.parseInt(id), notificationBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }
}