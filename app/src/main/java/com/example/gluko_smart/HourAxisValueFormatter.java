package com.example.gluko_smart;

import android.util.Log;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * The class extends the ValueFormatter class from the MPAndroidChart library.
 * The getFormattedValue() method is overridden to return the formatted time value for each x-axis point.
 */
public class HourAxisValueFormatter extends ValueFormatter {
    private final SimpleDateFormat mAxisDateFormat;
    private long startOfToday;
    private long referenceTimestamp; // Midnight of the day

    public HourAxisValueFormatter() {
        mAxisDateFormat = new SimpleDateFormat("HH:mm");


        Calendar calStart = Calendar.getInstance();
        calStart.setTimeInMillis(System.currentTimeMillis());
        calStart.set(Calendar.HOUR_OF_DAY, 0);
        calStart.set(Calendar.MINUTE, 0);
        calStart.set(Calendar.SECOND, 0);
        calStart.set(Calendar.MILLISECOND, 0);
        startOfToday = calStart.getTimeInMillis();

        Calendar calEnd = Calendar.getInstance();
        calEnd.set(Calendar.HOUR_OF_DAY, 23);
        calEnd.set(Calendar.MINUTE, 59);
        calEnd.set(Calendar.SECOND, 59);
        long endOfToday = calEnd.getTimeInMillis();

    }

    @Override
    public String getFormattedValue(float value) {
        // `value` sollte die Millisekunden seit Mitternacht darstellen
        long actualTimestamp = referenceTimestamp + (long) value;

        Log.i("HourAxisValueFormatter", "Value: " + value + ", Date: " + mAxisDateFormat.format(new Date(actualTimestamp)));

        return mAxisDateFormat.format(new Date(actualTimestamp));

        /*long millis = (long) (value * 60 * 60 * 1000) + startOfToday;
        Date nDate = new Date(millis);
        String formattedDate = mAxisDateFormat.format(nDate);
        Log.d("HourAxisValueFormatter", "Value: " + value + ", Date: " + formattedDate);
        return formattedDate;*/
    }
}