package com.example.gluko_smart;

import android.util.Log;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DayAxisValueFormatter extends ValueFormatter {
    private final SimpleDateFormat mDateFormat;

    public DayAxisValueFormatter() {
        mDateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
    }

    @Override
    public String getFormattedValue(float value) {
        Date date = new Date((long) value);

        Log.i("DayAxisValueFormatter", "Value: " + value + ", Date: " + mDateFormat.format(date));

        return mDateFormat.format(date);
    }
}
