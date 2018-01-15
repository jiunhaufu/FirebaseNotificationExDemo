package idv.alfie.firebasenotificationexdemo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


/**
 * Created by Jiunhau.Fu on 2017/12/8.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String TAG = "MyFirebaseMsgService";
    public static final String INTENT_FILTER = "INTENT_FILTER";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        //從Notification來的訊息
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
            sendBroadcast(new Intent(INTENT_FILTER));
        }
        //從Data發送來的資料
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload: " + remoteMessage.getData());
            if (true) {
                //另開一條執行緒執行data指令
                scheduleJob();
            } else {
                //不動作
                handleNow();
            }
        }
    }

    private void sendNotification(String title, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void scheduleJob() {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
    }

    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /************nodeJS server*************

    var request = require('request-promise');
    var await = require('asyncawait/await');

    var cloud_msg_key = 'AIza*******0kHI'
    var msgLife = 60*60*24  //一天

    var Firebase = {};

    Firebase.systemMsg = (tle, msg) => {
        var result = await(request({
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'key='+ cloud_msg_key,
            },
            uri: 'https://fcm.googleapis.com/fcm/send',
            body: JSON.stringify({
                condition: "'SYSTEM' in topics",
                content_available: true,
                time_to_live: msgLife,
                notification : {
                    title : tle,
                    body : msg,
                    sound : 'default',
                },
                data : {
                    title : tle,
                    message : msg,
                },
             }),
            method: 'POST'
        }, function (err, res, body) {
            var obj = JSON.parse(body);
            if(!err) return obj;
        }));
        return JSON.parse(result);
    }
    module.exports = Firebase;

    */
}
