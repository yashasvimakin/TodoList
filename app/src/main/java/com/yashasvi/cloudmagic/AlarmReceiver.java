package com.yashasvi.cloudmagic;

/**
 * Created by makin on 08/11/16.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle b = intent.getExtras();
        String ids,name,description="",reminder_date="",reminder_time="";
        Intent notificationIntent = new Intent(context, EnterDataActivity.class);
//        ids =(String) b.get("id");
        name =(String) b.get("name");
        description =(String)b.get("description");
        reminder_date=(String)b.get("reminder_date");
        reminder_time = (String)b.get("reminder_time");
        notificationIntent.putExtra("name", name);
        notificationIntent.putExtra("description", description);
        notificationIntent.putExtra("reminder_date", reminder_date);
        notificationIntent.putExtra("reminder_time", reminder_time);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        //stackBuilder.addParentStack(NotificationActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        Notification notification = builder.setContentTitle("Task:"+name)
                .setContentText("Details.."+description)
                .setTicker("New Todo Alert!")
                .setSmallIcon(R.drawable.todo)
                .setContentIntent(pendingIntent).build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
}
