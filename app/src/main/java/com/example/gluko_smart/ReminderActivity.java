package com.example.gluko_smart;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class ReminderActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private EditText reminderName;
    private Button btnSetReminder;
    private ListView reminderList;
    private ReminderAdapter reminderAdapter;
    private List<Reminder> reminders;

    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        sharedPreferences = getSharedPreferences("reminder_prefs", MODE_PRIVATE);

        reminderName = findViewById(R.id.reminderName);

        // Initialize the TimePickers and Buttons
        timePicker = findViewById(R.id.reminder_timePicker);
        btnSetReminder = findViewById(R.id.btnSetReminder);

        // Initialize the reminderList
        reminderList = findViewById(R.id.reminderList);

        btnSetReminder.setOnClickListener(v -> setReminder());

        reminders = new ArrayList<>();
        reminderAdapter = new ReminderAdapter(this, reminders);
        reminderList.setAdapter(reminderAdapter);

        loadReminders();
    }

    private void setReminder() {
        String name = reminderName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Bitte einen Namen für den Reminder eingeben", Toast.LENGTH_SHORT).show();
            return;
        }

        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        int reminderId = (int) System.currentTimeMillis();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // Sicherstellen, dass die Alarmzeit in der Zukunft liegt
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            showAlertAndSetReminder(calendar, name, reminderId, hour, minute);
        } else {
            setExactAlarm(calendar, name, reminderId, hour, minute);
        }

    }

    private void showAlertAndSetReminder(Calendar calendar, String name, int reminderId, int hour, int minute) {
        new AlertDialog.Builder(this)
                .setTitle("Alarm auf nächsten Tag verschoben")
                .setMessage("Die ausgewählte Uhrzeit ist bereits vergangen. Der Alarm wird auf die gleiche Zeit am nächsten Tag verschoben.")
                .setPositiveButton("OK", (dialog, which) -> {
                    calendar.add(Calendar.DAY_OF_YEAR, 1);  // Setzt den Alarm auf die gleiche Zeit am nächsten Tag
                    setExactAlarm(calendar, name, reminderId, hour, minute);
                })
                .setNegativeButton("Abbrechen", null)
                .show();
    }

    private void loadReminders() {
        reminders.clear();
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("name")) {
                int reminderId = Integer.parseInt(key.replace("name", ""));
                String name = (String) entry.getValue();
                int hour = sharedPreferences.getInt("hour" + reminderId, -1);
                int minute = sharedPreferences.getInt("minute" + reminderId, -1);
                if (hour != -1 && minute != -1) {
                    reminders.add(new Reminder(reminderId, name, hour, minute));
                }
            }
        }
        reminderAdapter.notifyDataSetChanged();
    }

    public void deleteReminder(int reminderId) {
        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, reminderId, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }

        //Name of the reminder
        String name = sharedPreferences.getString("name" + reminderId, "");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("name" + reminderId);
        editor.remove("hour" + reminderId);
        editor.remove("minute" + reminderId);
        editor.apply();

        reminders.removeIf(reminder -> reminder.getId() == reminderId);
        reminderAdapter.notifyDataSetChanged();

        Toast.makeText(this, "Reminder " + name + " gelöscht!", Toast.LENGTH_SHORT).show();

        // Log
        Log.i("ReminderActivity", "Reminder deleted: " + reminderId);
    }

    private void setExactAlarm(Calendar calendar, String name, int reminderId, int hour, int minute) {

        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("reminderId", reminderId);
        intent.putExtra("reminderName", name);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, reminderId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {

            // Set the alarm once at the specified time
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);

            //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name" + reminderId, name);
        editor.putInt("hour" + reminderId, hour);
        editor.putInt("minute" + reminderId, minute);
        editor.apply();

        Log.d("ReminderActivity", "Reminder "+reminderId+ " set" + name + " at " + hour + ":" + minute);

        reminders.add(new Reminder(reminderId, name, hour, minute));
        reminderAdapter.notifyDataSetChanged();

        Toast.makeText(this, "Reminder "+name+" gesetzt!", Toast.LENGTH_SHORT).show();

        // Clear the EditText
        reminderName.setText("");
    }

}




