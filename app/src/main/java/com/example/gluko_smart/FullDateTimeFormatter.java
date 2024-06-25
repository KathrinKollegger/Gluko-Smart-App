package com.example.gluko_smart;

import android.util.Log;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FullDateTimeFormatter extends ValueFormatter {
    private final SimpleDateFormat mFullDateFormat;

    public FullDateTimeFormatter() {
        mFullDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault());
    }

    @Override
    public String getFormattedValue(float value) {
        Date date = new Date((long) value);

        Log.i("FullDateTimeFormatter", "Value: " + value + ", Date: " + mFullDateFormat.format(date));

        return mFullDateFormat.format(date);
    }

}


