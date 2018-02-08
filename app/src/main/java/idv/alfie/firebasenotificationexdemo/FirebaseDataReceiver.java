package idv.alfie.firebasenotificationexdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import me.leolin.shortcutbadger.ShortcutBadger;

import static android.content.Context.MODE_PRIVATE;


//WakefulBroadcastReceiver android 8.0 以下使用 8.0以上會自己計數
public class FirebaseDataReceiver extends WakefulBroadcastReceiver {

    private final String TAG = "FirebaseDataReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        //監控所有推播
        if (intent != null && intent.getExtras() != null) {
            Bundle extras = intent.getExtras();
            //String someData= extras.getString("badge");
            //Log.d(TAG, someData);
            //新增計數標記
            updateBagdeCount(context);
        }
    }

    private void updateBagdeCount(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Badge", MODE_PRIVATE);
        int badgeCount = sharedPreferences.getInt("Badge",0);
        badgeCount++;
        sharedPreferences.edit().putInt("Badge", badgeCount).apply();
        ShortcutBadger.applyCount(context, badgeCount);
    }
}
