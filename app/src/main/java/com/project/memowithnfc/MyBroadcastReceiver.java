package com.project.memowithnfc;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String category_name = intent.getStringExtra("category_name");
        String memo_content = intent.getStringExtra("content");

        String CHANNEL_ID = "test";

        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(context, WholeMemoActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.arrow_down)
                    .setContentTitle(category_name)
                    .setContentText(memo_content)
                    .setStyle(new NotificationCompat.BigTextStyle()
                      .bigText(memo_content))
                    .setContentIntent(resultPendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    .setWhen(System.currentTimeMillis());

        NotificationManagerCompat notificationManager =  NotificationManagerCompat.from(context);
        notificationManager.notify(0, builder.build());
    }
}