package idv.alfie.firebasenotificationexdemo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import me.leolin.shortcutbadger.ShortcutBadger;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //推播訊息
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW));
        }
        //訂閱
        FirebaseMessaging.getInstance().subscribeToTopic("SYSTEM");
        //前景通知
        registerReceiver(broadcastReceiver, new IntentFilter(MyFirebaseMessagingService.INTENT_FILTER));

        //移除通知計數
        Button button = (Button)findViewById(R.id.button2);
        button.setText("重設通知計數");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeBadge();
            }
        });
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(MainActivity.this,"收到新訊息",Toast.LENGTH_SHORT).show();
        }
    };

    public void removeBadge(){
        SharedPreferences sharedPreferences = getSharedPreferences("Badge", MODE_PRIVATE);
        sharedPreferences.edit().putInt("Badge", 0).apply();
        ShortcutBadger.removeCount(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}
