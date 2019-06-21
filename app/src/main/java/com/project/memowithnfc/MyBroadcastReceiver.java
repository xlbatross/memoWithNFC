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
        int memo_id = intent.getIntExtra("memo_id", 0);
        String category_name = intent.getStringExtra("category_name");
        String memo_content = intent.getStringExtra("content");

        String CHANNEL_ID = "test3"; // + String.valueOf(memo_id);

        Toast.makeText(context, "Alarm...." + memo_id + " " + category_name + "  " + memo_content, Toast.LENGTH_SHORT).show();//디버그

        Intent m_intent = new Intent(context, WholeMemoActivity.class);
        m_intent.putExtra("memo_id", memo_id);

        context.sendBroadcast(m_intent);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(m_intent);

        m_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent stackPendingitent =  stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, m_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.arrow_down)
                    .setContentTitle(category_name)
                    .setContentText(memo_content)
                    .setStyle(new NotificationCompat.BigTextStyle()
                      .bigText(memo_content))
                    .setContentIntent(stackPendingitent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
            .setWhen(System.currentTimeMillis());

        NotificationManagerCompat notificationmanager =  NotificationManagerCompat.from(context);
        notificationmanager.notify(0, builder.build());

        /*
        NotificationManager notificationmanager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build());*/
    }
    /*
    private void original(Context context, Intent intent) {

    int memo_id = intent.getIntExtra("memo_id", 0);
    String category_name = intent.getStringExtra("category_name");
    String memo_content = intent.getStringExtra("content");
    String ChanelID = "test" + String.valueOf(memo_id);

    String CHANNEL_ID = "test3"; // + String.valueOf(memo_id);


        Toast.makeText(context,"Alarm...."+memo_id +category_name +"  "+memo_content,Toast.LENGTH_SHORT).

    show();//디버그


    Intent m_intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, m_intent, 0);

    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.arrow_down)
            .setContentTitle(category_name)
            .setContentText(memo_content)
            .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(memo_content))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            //.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
            .setWhen(System.currentTimeMillis());


    NotificationManager notificationmanager = (NotificationManager) context
            .getSystemService(Context.NOTIFICATION_SERVICE);
    // Build Notification with Notification Manager
        notificationmanager.notify(0,builder.build());


    }*/
}