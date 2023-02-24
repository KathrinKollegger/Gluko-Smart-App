package com.example.gluko_smart;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class FragmentVerlaufTag extends Fragment {
    private LineChart chart;
    private TextView tv_graphTitle;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private final static int TITLE_REFRESH_INTERVAL = 1000; // 1 second
    private final Handler handler = new Handler();

    //ArrayList containing GlucoseValues Objects
    private final List<GlucoseValues> storedGlucoValues = new ArrayList<>();

    //Updates Date and Time in Title every second
    private Runnable updateTitleTask = new Runnable() {
        @Override
        public void run() {
            Date currentDate = new Date((long) System.currentTimeMillis());
            tv_graphTitle.setText("Verlauf Blutzuckerwerte " + currentDate.toLocaleString());
            handler.postDelayed(updateTitleTask, TITLE_REFRESH_INTERVAL);
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verlauf_tag, container, false);
        chart = view.findViewById(R.id.linechartTag);
        chart.setBackgroundColor(getResources().getColor(R.color.white));
        chart.setNoDataText(getString(R.string.NoValuesForToday));
        chart.setNoDataTextColor(Color.DKGRAY);
        tv_graphTitle = view.findViewById(R.id.graphTitle);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://gluko-smart-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        handler.post(updateTitleTask);

        return view;
    }

    //If View could be created -> LineChart has Values
    @Override
    public void onStart() {
        super.onStart();

        //sets text for Bottom Value Display
        setValueDisplayData(getString(R.string.chooseDP));

        //Only used if x0 = start of today
        Calendar calStart = Calendar.getInstance();
        calStart.set(Calendar.HOUR_OF_DAY, 0);
        calStart.set(Calendar.MINUTE, 0);
        calStart.set(Calendar.SECOND, 0);
        long startOfToday = calStart.getTimeInMillis();

        //Only used if xLAST = end of today
        Calendar calEnd = Calendar.getInstance();
        calEnd.set(Calendar.HOUR_OF_DAY, 23);
        calEnd.set(Calendar.MINUTE, 59);
        calEnd.set(Calendar.SECOND, 59);
        long endOfToday = calEnd.getTimeInMillis();

        //Retrieve Data from Database for current User
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("users").child(userId).child("GlucoseValues")
                .addValueEventListener(new ValueEventListener() {

                    /**
                     * Will be invoked any time the data on the database changes.
                     * Will also be invoked as soon as we connect the Listener, to get initial shanpshot of stored Data of DB-Reference
                     * @param dataSnapshot Snapshot of all children at this level and below
                     */
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {

                            //All Entries are directly stored into Objects of Class GlucoseValues
                            GlucoseValues retrivedValue = child.getValue(GlucoseValues.class);

                            //and add to ArrayList storedGlucoValues afterward
                            storedGlucoValues.add(retrivedValue);
                        }

                        //Sort List Entries with timestamps ascending
                        Collections.sort(storedGlucoValues);

                        //List 'entries' for LineDataSet of LineChart for Daily Course
                        List<Entry> entries = new ArrayList<>();

                        //Add GlucoseValues to LineDataSet-List 'entries'
                        for (GlucoseValues glucoValue : storedGlucoValues) {

                            //Timestamp als String [z.B. "2023-02-05T07:27:53"]
                            String timestamp = glucoValue.getTimestamp();
                            Log.i("String timestamp", "=" + timestamp);

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            Date date = null;

                            //Parse Timestamp to Date Object
                            try {
                                date = simpleDateFormat.parse(timestamp);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (date == null) {
                                continue;
                            }

                            //get time in Milliseconds
                            long timeInMillis = date.getTime();
                            Log.i("timeInMillis", "=" + timeInMillis);

                            LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(timeInMillis, 0, ZoneOffset.UTC);
                            Log.i("LocalDateTimeObject", "=" + localDateTime.toString());

                            //Filter for measurements of today
                            if (timeInMillis >= startOfToday && timeInMillis <= endOfToday) {

                                float hours = date.getHours();
                                float minutes = (float) (Math.round((float) ((100.0 / 60.0 * date.getMinutes()))) / 100.0);

                                //Entries are being added with correctly formatted x and y-values
                                entries.add(new Entry((float) hours + minutes, glucoValue.getBzWert()));
                                Log.i("Entries", "added: Hour=" + hours + "min=" + minutes + "Bz=" + glucoValue.getBzWert());
                            }
                        }

                        // Find the minimum,maximum,current timestamp value for xAxisSetting
                        if (!entries.isEmpty()) {

                            long currentTimeInMs = System.currentTimeMillis();
                            float currentTimestamp = (float) (currentTimeInMs - startOfToday) / (60 * 60 * 1000);

                            float minTimestamp = entries.get(0).getX();
                            int lastIndex = entries.size() - 1;
                            float maxTimestamp = entries.get(lastIndex).getX();
                            //Iterates through entries and looks for smallest timestamp (First Measurement of Today)
                            for (Entry entry : entries) {
                                if (entry.getX() < minTimestamp) {
                                    minTimestamp = entry.getX();
                                }
                            }

                            //create dataSet with entries and label
                            LineDataSet dataSet = new LineDataSet(entries, "Tages-Blutzuckerwerte");
                            dataSet.setColor(Color.BLUE);

                            //textSize > 0 if DataPoint Labels required
                            dataSet.setValueTextSize(0f);
                            dataSet.setValueTextColor(Color.BLACK);

                            //adjust LineWidth of BloodGlucose Line
                            dataSet.setLineWidth(5f);
                            dataSet.setDrawHighlightIndicators(true);

                            //Create LineDate Object for LineGraph
                            LineData lineData = new LineData(dataSet);

                           /*
                            //Changes Format of DataPoint-Labels from Float to Int [133.00 -> 133]
                            //only requiered if Labels are visible
                            lineData.setValueFormatter(new ValueFormatter() {
                                @Override
                                public String getFormattedValue(float value) {
                                    return String.valueOf((int) value);
                                }
                            });
                            */

                            //Design X-Achse
                            XAxis xAxis = chart.getXAxis();
                            xAxis.setValueFormatter(new HourAxisValueFormatter());

                            xAxis.setAxisMinimum(minTimestamp);
                            xAxis.setAxisMaximum(currentTimestamp);

                            //Alternative LineChart Axis Layout
                            //xAxis.setAxisMinimum(0);
                            //xAxis.setAxisMaximum(maxTimestamp)
                            //xAxis.setAxisMaximum(24);

                            xAxis.setEnabled(true);
                            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                            xAxis.setLabelCount(8);

                            LimitLine ref_untergrenze = new LimitLine(130);
                            ref_untergrenze.setLineWidth(2f);
                            ref_untergrenze.setLineColor(Color.RED);
                            ref_untergrenze.setTextSize(12f);
                            ref_untergrenze.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);

                            // Add the limit line to the y-axis of the chart
                            YAxis leftAxis = chart.getAxisLeft();
                            leftAxis.addLimitLine(ref_untergrenze);

                            //Adapt Custom Chart Legend
                            Legend l = chart.getLegend();
                            l.setEnabled(true);
                            l.setXEntrySpace(25f);
                            l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
                            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                            l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                            l.setDrawInside(false);

                            LegendEntry legEntry1 = new LegendEntry();
                            legEntry1.formColor = Color.BLUE;
                            legEntry1.label = getString(R.string.BloodGlucoseValues);

                            LegendEntry legEntry2 = new LegendEntry();
                            legEntry2.formColor = Color.RED;
                            legEntry2.label = getString(R.string.ThresholdLineLabel);

                            LegendEntry[] legEntries = new LegendEntry[]{legEntry1, legEntry2};
                            l.setCustom(legEntries);
                            l.setTextSize(14f);

                            chart.setExtraOffsets(0, 10f, 0, 15f);
                            xAxis.setDrawLabels(true);
                            chart.setData(lineData);
                            chart.invalidate();

                            if (getContext() != null) {
                                CustomMarkerView mv = new CustomMarkerView(getContext(), R.layout.marker_view);
                                chart.setMarkerView(mv);

                                //shows marker for selected DataPoint
                                chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                                    @Override
                                    public void onValueSelected(Entry e, Highlight h) {

                                        chart.highlightValue(h);
                                        Log.i("ContextDataPointInfo:", "x=" + e.getX() + "y=" + e.getY());

                                        //Refreshing ContextMenu und ValueDisplay
                                        mv.refreshContent(e, h);
                                        HourAxisValueFormatter formatter = new HourAxisValueFormatter();
                                        setValueDisplayData("Zeit: " + formatter.getFormattedValue(e.getX()) + " Uhr \n Wert: " + e.getY() + " mg/dl");
                                    }

                                    @Override
                                    public void onNothingSelected() {
                                    }
                                });
                            }
                        } else {
                            setValueDisplayData(getString(R.string.ValueInputRequired));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("ERROR", "" + databaseError.toString());
                    }

                });
    }

    /**
     * sets text at bottom of Page
     * @param data text for ValueDisplay
     */
    public void setValueDisplayData(String data) {
        if (getActivity() != null) {
            ((Verlauf) getActivity()).updateValueDisplay(data);
        }
    }
}

