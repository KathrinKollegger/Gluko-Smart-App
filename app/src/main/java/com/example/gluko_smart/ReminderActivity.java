package com.example.gluko_smart;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class ReminderActivity extends AppCompatActivity {

    private TimePicker timePicker1;
    private TimePicker timePicker2;
    private Button btnSetReminder1;
    private Button btnSetReminder2;
    LinearLayout reminderList;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        sharedPreferences = getSharedPreferences("reminder_prefs", MODE_PRIVATE);
        reminderList = findViewById(R.id.reminderList);

        // Initialize the TimePickers and Buttons
        timePicker1 = findViewById(R.id.timePicker1);
        timePicker2 = findViewById(R.id.timePicker2);
        btnSetReminder1 = findViewById(R.id.btnSetReminder1);
        btnSetReminder2 = findViewById(R.id.btnSetReminder2);

        btnSetReminder1.setOnClickListener(v -> setReminder(1));
        btnSetReminder2.setOnClickListener(v -> setReminder(2));

        loadReminders();

    }

    private void setReminder(int reminderId) {
        TimePicker timePicker = reminderId == 1 ? timePicker1 : timePicker2;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
        calendar.set(Calendar.MINUTE, timePicker.getMinute());
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("reminderId", reminderId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, reminderId, intent, PendingIntent.FLAG_MUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            //alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }

        // Save the reminder time in SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("hour" + reminderId, timePicker.getHour());
        editor.putInt("minute" + reminderId, timePicker.getMinute());
        editor.apply();

        // Reload the reminders
        loadReminders();

        String timeOfReminder = timePicker.getHour() + ":" + timePicker.getMinute();
        Toast.makeText(this, "Reminder " + reminderId + " gesetzt für "+ timeOfReminder + "Uhr", Toast.LENGTH_SHORT).show();
    }

    private void loadReminders() {
        reminderList.removeAllViews();
        for (int i = 1; i <= 2; i++) {
            int hour = sharedPreferences.getInt("hour" + i, -1);
            int minute = sharedPreferences.getInt("minute" + i, -1);
            if (hour != -1 && minute != -1) {
                addReminderView(i, hour, minute);
            }
        }
    }

    private void addReminderView(int reminderId, int hour, int minute) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(0, 10, 0, 10);

        TextView textView = new TextView(this);
        textView.setText("Reminder " + reminderId + " - " + String.format("%02d:%02d", hour, minute));
        layout.addView(textView);

        Button deleteButton = new Button(this);
        deleteButton.setText("Löschen");
        layout.addView(deleteButton);
        deleteButton.setOnClickListener(v -> deleteReminder(reminderId));
        reminderList.addView(layout);
    }

    private void deleteReminder(int reminderId) {
        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, reminderId, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("hour" + reminderId);
        editor.remove("minute" + reminderId);
        editor.apply();

        loadReminders();
        Toast.makeText(this, "Reminder " + reminderId + " gelöscht!", Toast.LENGTH_SHORT).show();
    }




}




