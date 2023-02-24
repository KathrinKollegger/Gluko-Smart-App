package com.example.gluko_smart;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * The class extends the ValueFormatter class from the MPAndroidChart library.
 * The getFormattedValue() method is overridden to return the formatted time value for each x-axis point.
 */
public class HourAxisValueFormatter extends ValueFormatter {

    private final SimpleDateFormat mAxisDateFormat;

    private long startOfToday;

    public HourAxisValueFormatter() {

        mAxisDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

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

        long millis = (long) (value *60*60*1000) +startOfToday;
        Date nDate = new Date(millis);

        return mAxisDateFormat.format(nDate);
    }
}