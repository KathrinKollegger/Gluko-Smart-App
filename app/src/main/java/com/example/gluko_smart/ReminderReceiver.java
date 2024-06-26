package com.example.gluko_smart;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class ReminderReceiver extends BroadcastReceiver {
    private static final String TAG = "ReminderReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        int reminderId = intent.getIntExtra("reminderId", -1);
        String reminderName = intent.getStringExtra("reminderName");
        Log.d("ReminderReciver", "onReceive: Alarm received for reminderId: " + reminderId);
        if (reminderId != -1) {
            sendNotification(context, reminderId, reminderName);
            setNextExactAlarm(context, reminderId, reminderName);

        }
    }

    private void sendNotification(Context context, int reminderId, String reminderName){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "reminder_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Erinnerungen", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        Intent notificationIntent = new Intent(context, Home.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, reminderId, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        String reminderText = "Errinnerung an " + reminderName;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_alert_trans90)
                .setContentTitle(reminderText)
                .setContentText("Ich soll dich an etwas errinnern!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(reminderId, builder.build());
    }

    private void setNextExactAlarm(Context context, int reminderId, String reminderName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("reminder_prefs", Context.MODE_PRIVATE);
        int hour = sharedPreferences.getInt("hour" + reminderId, -1);
        int minute = sharedPreferences.getInt("minute" + reminderId, -1);
        String realReminderName = sharedPreferences.getString("name" + reminderId, "");

        Log.d(TAG, "setNextExactAlarm received: "+ reminderId + " " + reminderName + " " + hour + " " + minute);

        if (hour != -1 && minute != -1) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            //calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.add(Calendar.DAY_OF_YEAR, 1);

            Log.d(TAG, "setNextExactAlarm: Setting next alarm for reminderId: " + reminderId + " in 2 minutes at: " + calendar.getTime());

            Intent intent = new Intent(context, ReminderReceiver.class);
            intent.putExtra("reminderId", reminderId);
            intent.putExtra("reminderName", realReminderName);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, reminderId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                Log.d(TAG, "setNextExactAlarm: Alarm "+realReminderName+" prolonged successfully.");
            } else {
                Log.e(TAG, "setNextExactAlarm: AlarmManager is null.");
            }
        } else {
            Log.e(TAG, "setNextExactAlarm: Invalid hour or minute for reminderId: " + reminderId);
        }
    }
}
