package com.example.gluko_smart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GlucoseValueAdapter extends BaseAdapter {
    private List<GlucoseValues> storedGlucoValues;

    public GlucoseValueAdapter(List<GlucoseValues> storedGlucoValues) {
        this.storedGlucoValues=storedGlucoValues;
    }

    @Override
    public int getCount() {
        return storedGlucoValues.size();
    }

    @Override
    public Object getItem(int position) {
        return storedGlucoValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_list_item_view, parent, false);
        }

        GlucoseValues glucoseValue = storedGlucoValues.get(position);
        String timestamp = glucoseValue.getTimestamp();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat niceDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String niceDate = niceDateFormat.format(date);

        TextView valueTextView = convertView.findViewById(R.id.textGlucoseValue);
        valueTextView.setText(String.valueOf(glucoseValue.getBzWert())+" mg/dl");
        TextView timeTextView = convertView.findViewById(R.id.textTimestamp);
        timeTextView.setText(niceDate);

        return convertView;

    }
}
