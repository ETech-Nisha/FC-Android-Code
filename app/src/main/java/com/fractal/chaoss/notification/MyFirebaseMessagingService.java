package com.fractal.chaoss.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.fractal.chaoss.R;
import com.fractal.chaoss.SplashActivity;
import com.fractal.chaoss.manager.home.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e("test:...","notify::"+remoteMessage);

        String title = "";
        String body = "";
        String type = "";

        if (remoteMessage.getData().size() > 0) {
            title = remoteMessage.getData().get("title");
            body = remoteMessage.getData().get("body");
            type = remoteMessage.getData().get("type");
           // AppLogger.Logger.verbossendRegistrationToServere(TAG, "type:" + type);
        } else {
            title = remoteMessage.getNotification().getBody();
        }
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String channelId = "Default";
        NotificationCompat.Builder builder = new  NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.app_icon_round)
                .setContentTitle(title)
                .setContentText(body).setAutoCancel(true).setContentIntent(pendingIntent);;
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        manager.notify(0, builder.build());
    }

    @Override
    public void onNewToken(String refreshedToken) {
        super.onNewToken(refreshedToken);
        sendRegistrationToServer(refreshedToken);
       // AppLogger.Logger.verbose(TAG, "refreshed toke:" + refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        //mPreferencesUtil.savePreferences(PreferenceConnector.DEVICE_TOKEN, token);
    }

}
