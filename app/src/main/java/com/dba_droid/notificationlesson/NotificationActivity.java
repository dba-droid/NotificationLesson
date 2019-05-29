package com.dba_droid.notificationlesson;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class NotificationActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "default";

    /* when sending a notification through the same identifier,
     * the notification is redrawn,
     * for create a new notification, use a different identifier !
     */
    private static final int NOTIFICATION_ID = 1100;

    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();

        final EditText title = findViewById(R.id.title);
        final EditText text = findViewById(R.id.text);
        final EditText subText = findViewById(R.id.sub_text);

        findViewById(R.id.send_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification(title.getText().toString(), text.getText().toString(), subText.getText().toString());
            }
        });

        findViewById(R.id.cancel_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationManager.cancel(NOTIFICATION_ID);
            }
        });

        findViewById(R.id.cancel_all_notifications).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationManager.cancelAll();
            }
        });

        findViewById(R.id.go_to_notification_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNotificationSettings();
            }
        });

        findViewById(R.id.go_to_channel_notification_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNotificationSettings(CHANNEL_ID);
            }
        });
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "CHANNEL_NAME", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setLightColor(Color.GREEN);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
    }

    public void sendNotification(String title, String body, String subText) {
        Notification.Builder builder = new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSubText(subText)
                .setSmallIcon(R.drawable.android)
                .setAutoCancel(true);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    //  Send Intent to load system Notification Settings for this app.
    public void goToNotificationSettings() {
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        startActivity(intent);
    }

    //  Send Intent to load system Notification Settings for this channel.
    public void goToNotificationSettings(String channelId) {
        Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        intent.putExtra(Settings.EXTRA_CHANNEL_ID, channelId);
        startActivity(intent);
    }
}
