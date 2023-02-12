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
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView tv_graphTitle;

    private final static int TITLE_REFRESH_INTERVAL = 1000; // 1 second
    private final Handler handler = new Handler();

    //MyVersion
    private final List<GlucoseValues> storedGlucoValues = new ArrayList<GlucoseValues>();

    private Runnable updateTitleTask = new Runnable() {
        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            Date currentDate = new Date((long) System.currentTimeMillis());

            tv_graphTitle.setText("Verlauf Blutzuckerwerte " + currentDate.toLocaleString());
            handler.postDelayed(updateTitleTask, TITLE_REFRESH_INTERVAL);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verlauf_tag, container, false);
        chart = view.findViewById(R.id.linechartTag);
        tv_graphTitle = view.findViewById(R.id.graphTitle);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://gluko-smart-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        handler.post(updateTitleTask);

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();

        //Setzt das ValueDisplay
        setValueDisplayData("Wähle einen Datenpunkt");

        Calendar calStar = Calendar.getInstance();
        calStar.set(Calendar.HOUR_OF_DAY, 0);
        calStar.set(Calendar.MINUTE, 0);
        calStar.set(Calendar.SECOND, 0);

        Calendar calEnd = Calendar.getInstance();
        calEnd.set(Calendar.HOUR_OF_DAY, 23);
        calEnd.set(Calendar.MINUTE, 59);
        calEnd.set(Calendar.SECOND, 59);

        long startOfToday = calStar.getTimeInMillis();
        long endOfToday = calEnd.getTimeInMillis();

        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("users").child(userId).child("GlucoseValues")
                .addValueEventListener(new ValueEventListener() {

                    /**
                     * Will be invoked any time the data on the database changes.
                     * Will also be invoked as soon as we connect the Listener, to get initial shanpshot of stored Data of DB-Reference
                     * @param dataSnapshot
                     */
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {

                            //Alle Einträge werden direkt in GlucoseValue-Objekte gespeichert
                            GlucoseValues retrivedValue = child.getValue(GlucoseValues.class);
                            //Danach in Liste 'storedGlucoValues aufgenommen'
                            storedGlucoValues.add(retrivedValue);
                            //Log.i("onDataChanged", "Wert:" + retrivedValue.getBzWert());
                        }

                        //Liste wird nach Zeitpunkt der Messung sortiert
                        Collections.sort(storedGlucoValues);

                        //Liste 'entries' für LineDataSet von Tagesverlauf
                        List<Entry> entries = new ArrayList<>();

                        for (GlucoseValues glucoValue : storedGlucoValues) {

                            //Timestamp als String [z.B. "2023-02-05T07:27:53"]
                            String timestamp = glucoValue.getTimestamp();
                            Log.i("String timestamp", "=" + timestamp);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            Date date = null;
                            try {
                                date = simpleDateFormat.parse(timestamp);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (date == null) {
                                continue;
                            }

                          /*  Log.i("dateObject","="+date.toString());
                            Log.i("hours","="+hours);
                            Log.i("minutes","="+minutes);*/
                            long timeInMillis = date.getTime();
                            Log.i("timeInMillis", "=" + timeInMillis);

                            LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(timeInMillis, 0, ZoneOffset.UTC);
                            Log.i("LocalDateTimeObject", "=" + localDateTime.toString());

                            //Sortierung nach timeInMillis(Timestamp) aufsteigend benötigt - BLE Werte werden nicht richtig eingeordnet (entries Sortieren!)
                            if (timeInMillis >= startOfToday && timeInMillis <= endOfToday) {

                                float hours = date.getHours();
                                float minutes = (float) (Math.round((float) ((100.0 / 60.0 * date.getMinutes()))) / 100.0);
                                entries.add(new Entry((float) hours + minutes, glucoValue.getBzWert()));
                                Log.i("Entries", "added: Hour=" + hours + "min=" + minutes + "Bz=" + glucoValue.getBzWert());
                            }

                        }

                        if (!entries.isEmpty()) {
                            // Find the minimum,maximum,current timestamp value for xAxisSetting

                            long currentTimeInMs = System.currentTimeMillis();
                            float currentTimestamp = (float) (currentTimeInMs - startOfToday) / (60 * 60 * 1000);

                            float minTimestamp = entries.get(0).getX();
                            int lastIndex = entries.size() - 1;
                            float maxTimestamp = entries.get(lastIndex).getX();
                            for (Entry entry : entries) {
                                if (entry.getX() < minTimestamp) {
                                    minTimestamp = entry.getX();
                                }
                            }

                            LineDataSet dataSet = new LineDataSet(entries, "Tages-Blutzuckerwerte");
                            dataSet.setColor(Color.BLUE);
                            //textSize > 0 falls Beschriftung notwendig
                            dataSet.setValueTextSize(0f);
                            dataSet.setValueTextColor(Color.BLACK);
                            dataSet.setLineWidth(5f);
                            dataSet.setDrawHighlightIndicators(true);

                            LineData lineData = new LineData(dataSet);

                           /*
                            //Wandelt Darstellung der Datenpunkt-Labels von Float zu Int [133.00 -> 133]
                            //Nur notwendig wenn Datenpunkt-Beschriftung angezeigt wird
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
                            //xAxis.setAxisMinimum(0);

                            xAxis.setAxisMaximum(currentTimestamp);
                            //oder xAxis.setAxisMaximum(maxTimestamp)
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

                            //Anpassung Chart Legende
                            Legend l = chart.getLegend();
                            l.setEnabled(true);
                            l.setXEntrySpace(25f);
                            l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
                            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                            l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                            l.setDrawInside(false);

                            LegendEntry legEntry1 = new LegendEntry();
                            legEntry1.formColor = Color.BLUE;
                            legEntry1.label = "Bluzuckerwerte";

                            LegendEntry legEntry2 = new LegendEntry();
                            legEntry2.formColor = Color.RED;
                            legEntry2.label = "Erhöhter Blutzuckerwert";

                            LegendEntry[] legEntries = new LegendEntry[]{legEntry1, legEntry2};


                            l.setCustom(legEntries);
                            l.setTextSize(14f);


                            chart.setExtraOffsets(0, 10f, 0, 15f);
                            xAxis.setDrawLabels(true);
                            chart.setData(lineData);
                            chart.invalidate();

                            if(getContext() != null){
                            CustomMarkerView mv = new CustomMarkerView(getContext(), R.layout.marker_view);
                            chart.setMarkerView(mv);
                            //chart.moveViewToX(chart.getLowestVisibleX());

                            //Zeigt Marker für jeweiligen Entry
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
                    }}

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        Log.e("ERROR", "something went wrong");
                        // handle error
                    }
                });
    }
    /**
     *
     * @param data
     */
    public void setValueDisplayData(String data) {
        if (getActivity() != null) {
            ((Verlauf) getActivity()).updateValueDisplay(data);
        }
    }

}

