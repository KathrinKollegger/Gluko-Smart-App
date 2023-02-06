package com.example.gluko_smart;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HourAxisValueFormatter extends ValueFormatter {

    private long startOfToday;

    public HourAxisValueFormatter() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        startOfToday = cal.getTimeInMillis();

    }

    @Override
    public String getFormattedValue(float value) {
        long millis = (long) (value * 60 * 60 * 1000) + startOfToday;
        Date date = new Date(millis);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return format.format(date);
    }
}