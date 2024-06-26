package com.example.gluko_smart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class ReminderAdapter extends ArrayAdapter {
    private Context context;
    private List<Reminder> reminders;

    public ReminderAdapter(Context context, List<Reminder> reminders) {
            super(context, 0, reminders);
            this.context = context;
            this.reminders = reminders;

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.reminder_item, parent, false);
        }

        Reminder reminder = reminders.get(position);

        TextView reminderText = convertView.findViewById(R.id.reminderText);
        Button deleteButton = convertView.findViewById(R.id.deleteButton);

        reminderText.setText(reminder.getName() + " - " + String.format("%02d:%02d", reminder.getHour(), reminder.getMinute())+" Uhr");

        deleteButton.setOnClickListener(v -> {
            if (context instanceof ReminderActivity) {
                ((ReminderActivity) context).deleteReminder(reminder.getId());
            }
        });

        return convertView;
    }

}

