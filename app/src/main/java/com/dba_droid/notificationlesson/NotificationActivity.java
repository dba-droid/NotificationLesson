package com.dba_droid.notificationlesson;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.concurrent.TimeUnit;

public class NotificationActivity extends AppCompatActivity {

    private static final String GROUP_ID = "GROUP_ID";
    private static final String CHANNEL_ID = "CHANNEL_ID";

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
        createNotificationGroup();
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

        findViewById(R.id.delete_channel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNotificationChannel();
            }
        });

        findViewById(R.id.delete_group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNotificationGroup();
            }
        });
    }

    //for verions < OREO
    private void sendNotification() {
        NotificationCompat.Builder notificationBuilder
                = new NotificationCompat.Builder(this, CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.android)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle("Default notification")
                .setContentText("Lorem ipsum")
                .setContentInfo("Info")
                .setAutoCancel(true);

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void createNotificationGroup() {
        notificationManager.createNotificationChannelGroup(new NotificationChannelGroup(GROUP_ID, "Group 1"));
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "CHANNEL_NAME", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("channel description");
        channel.setGroup(GROUP_ID);
        channel.enableLights(true);
        channel.setLightColor(Color.GREEN);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
    }

    private void deleteNotificationChannel() {
        notificationManager.deleteNotificationChannel(CHANNEL_ID);
    }

    private void deleteNotificationGroup() {
        notificationManager.deleteNotificationChannelGroup(GROUP_ID);
    }

    public void sendNotification(String title, String body, String subText) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://developer.android.com/reference/android/app/Notification.html"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Icon okIcon = Icon.createWithResource(this, android.R.drawable.sym_action_call);
        Notification.Action okAction = new Notification.Action.Builder(okIcon, "OK", pendingIntent).build();

        Icon icon = Icon.createWithResource(this, android.R.drawable.sym_action_chat);
        Notification.Action cancelAction = new Notification.Action.Builder(icon, "Cancel", pendingIntent).build();

        Notification.Builder builder = new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setActions(okAction, cancelAction)
                .setContentIntent(pendingIntent)
                .setSubText(subText)
                .setColor(Color.RED) // ICON COLOR
                .setShowWhen(true) // SHOW NOTIFICATION TIME
                .setWhen(System.currentTimeMillis() - 100100) // NOTIFICATION TIME, DEFAULT = System.currentTimeMillis()
                .setUsesChronometer(true)  //CHANGE TIME MODE TO CHRONOMETER
                .setSmallIcon(R.drawable.android)
                .setTimeoutAfter(10000 /*10 sec*/) //AFTER THAT TIME NOTIFICATION AUTOMATICALLY CLOSED
                .setProgress(100, 0, true)
                .setAutoCancel(true);

        notificationManager.notify(NOTIFICATION_ID, builder.build());

        startUpdateNotificationProgressTask(builder);
    }


    private void startUpdateNotificationProgressTask(final Notification.Builder builder) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int maxProgress = 100;
                for (int progress = 0; progress <= maxProgress; progress += 10) {

                    try {
                        TimeUnit.MILLISECONDS.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // show notification with current progress
                    builder.setProgress(100, progress, false).setContentText(progress + " of " + maxProgress);
                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                }

                // show notification without progressbar
                builder.setProgress(0, 10, false).setContentText("Completed");
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
        }).start();
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
