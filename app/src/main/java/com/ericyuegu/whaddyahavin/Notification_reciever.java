package com.ericyuegu.whaddyahavin;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class Notification_reciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent repeating_intent = new Intent(context, MainActivity.class);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "my_channel_Id");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationBuilder.setChannelId("123");
        }

        notificationBuilder.setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pendingIntent)
                .setContentTitle("REMEMBER TO ADD YOUR MEAL")
                .setContentText("Click Here to add your meal")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        if (intent.getAction().equals("MY_NOTIFICATION_MESSAGE")) {
            notificationManager.notify(100, notificationBuilder.build());
        }
//        int NOTIFICATION_ID = 234;
//
//        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
//
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//
//
//            String CHANNEL_ID = "my_channel_01";
//            CharSequence name = "my_channel";
//            String Description = "This is my channel";
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
//            mChannel.setDescription(Description);
//            mChannel.enableLights(true);
//            mChannel.setLightColor(Color.RED);
//            mChannel.enableVibration(true);
//            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//            mChannel.setShowBadge(false);
//            notificationManager.createNotificationChannel(mChannel);
//        }
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle(title)
//                .setContentText(message);
//
//        Intent resultIntent = new Intent(ctx, MainActivity.class);
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
//        stackBuilder.addParentStack(MainActivity.class);
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        builder.setContentIntent(resultPendingIntent);
//
//        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
