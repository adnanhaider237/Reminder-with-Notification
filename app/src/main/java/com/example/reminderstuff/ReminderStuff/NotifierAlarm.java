package com.example.reminderstuff.ReminderStuff;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;


import com.example.reminderstuff.MainActivity;
import com.example.reminderstuff.R;

import java.util.Date;
import java.util.Random;

public class NotifierAlarm extends BroadcastReceiver {

    private AppDatabase appDatabase;

    @Override
    public void onReceive(Context context, Intent intent) {

        appDatabase = AppDatabase.geAppdatabase(context.getApplicationContext());
        RoomDAO roomDAO = appDatabase.getRoomDAO();
        Reminders reminder = new Reminders();
        reminder.setMessage(intent.getStringExtra("Message"));
        reminder.setRemindDate(new Date(intent.getStringExtra("RemindDate")));
        reminder.setId(intent.getIntExtra("id", 0));
        roomDAO.Delete(reminder);
        AppDatabase.destroyInstance();

        Uri alarmsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        Intent intent1 = new Intent(context, MainActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent1.putExtra("gson", intent.getStringExtra("gson"));
        intent1.putExtra("arr", intent.getStringExtra("arr"));
        intent1.putExtra("id", intent.getIntExtra("id", 0));

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(MainActivity.class);
        taskStackBuilder.addNextIntent(intent1);

        PendingIntent intent2 = taskStackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);



        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context);

        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel("my_channel_01",
                    "hello", NotificationManager.IMPORTANCE_HIGH);
        }

        Notification notification = builder.setContentTitle(intent.getStringExtra("Title"))
                .setContentText(intent.getStringExtra("Message"))
                .setAutoCancel(true)
                .setSound(alarmsound).setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(intent2)
                .setChannelId("my_channel_01")
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }

        Random rand = new Random();

        notificationManager.notify(rand.nextInt((999999999) + 1), notification);
    }
}
