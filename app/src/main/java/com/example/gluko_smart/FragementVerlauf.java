package com.example.gluko_smart;


import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FragementVerlauf extends Fragment {
    private LineChart chart;
    private TextView tv_graphTitle;
    private GlucoseDataManager glucoseDataManager; // Instanz des Datenmanagers

    private Button btn_day;
    private Button btn_week;
    private Button btn_month;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verlauf_tag, container, false);
        chart = view.findViewById(R.id.linechartTag);
        tv_graphTitle = view.findViewById(R.id.graphTitle);

        glucoseDataManager = new GlucoseDataManager(); // DataManager instanzieren

        btn_day = view.findViewById(R.id.button_viewDay);
        btn_week = view.findViewById(R.id.button_viewWeek);
        btn_month = view.findViewById(R.id.button_viewMonth);

        setupButtonListeners(); // Buttons einrichten
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getContext() != null) {
            setupMarkerView();
            setupValueSelectedListener();
            setupXAxis("day");
            loadData("day");
            setupReferenceLines();
        } else {
            setValueDisplayData(getString(R.string.ValueInputRequired));
        }
    }

    private void setupReferenceLines() {
        // Obergrenze
        LimitLine ref_obergrenze = new LimitLine(130f, "Obergrenze");
        ref_obergrenze.setLineWidth(2f);
        ref_obergrenze.setLineColor(Color.RED);
        ref_obergrenze.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ref_obergrenze.setTextSize(12f);

        // Untergrenze
        LimitLine ref_untergrenze = new LimitLine(80f, "Untergrenze");
        ref_untergrenze.setLineWidth(2f);
        ref_untergrenze.setLineColor(Color.RED);
        ref_untergrenze.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ref_untergrenze.setTextSize(12f);

        // Y-Achse konfigurieren
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // Vorherige LimitLines entfernen
        leftAxis.addLimitLine(ref_obergrenze);
        leftAxis.addLimitLine(ref_untergrenze);

        // Anpassen der Legende
        Legend l = chart.getLegend();
        l.setEnabled(true);
        l.setXEntrySpace(25f);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        LegendEntry legEntry1 = new LegendEntry();
        legEntry1.formColor = Color.BLUE;
        legEntry1.label = "Blutzuckerwerte in mg/dl";

        LegendEntry legEntry2 = new LegendEntry();
        legEntry2.formColor = Color.RED;
        legEntry2.label = "Normalbereich";

        LegendEntry[] legEntries = new LegendEntry[]{legEntry1, legEntry2};
        l.setCustom(legEntries);
        l.setTextSize(14f);
    }

    private void setupButtonListeners() {
        btn_day.setOnClickListener(v -> loadData("day"));
        btn_week.setOnClickListener(v -> loadData("week"));
        btn_month.setOnClickListener(v -> loadData("month"));
    }

    private void loadData(String viewMode) {
        glucoseDataManager.loadData(viewMode, this::updateChart);
        setupXAxis(viewMode);
        updateValueDisplay(viewMode);
    }

    private void updateChart(List<Entry> entries) {
        if (entries == null || entries.isEmpty()) {
            tv_graphTitle.setText("Noch keine Messwerte vorhanden");
            chart.clear();
            return;
        }

        chart.clear(); // Chart leeren, um neue Daten zu setzen

        LineDataSet dataSet = new LineDataSet(entries, "Glucose Levels");
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setColor(Color.BLUE);
        dataSet.setLineWidth(2f);

        LineData lineData = new LineData(dataSet);

        chart.setData(lineData);
        chart.notifyDataSetChanged(); // Chart benachrichtigen, dass Daten geändert wurden

        // Dynamische Skalierung der x-Achse basierend auf den Daten
        float minX = entries.get(0).getX();
        float maxX = entries.get(entries.size() - 1).getX();

        for (Entry entry : entries) {
            if (entry.getX() < minX) {
                minX = entry.getX();
            }
            if (entry.getX() > maxX) {
                maxX = entry.getX();
            }
        }

        Log.d("updateChart", "MinX: " + minX + ", MaxX: " + maxX);

        float max_buffer = 3600000f; // 1 Stunde in Millisekunden
        float min_buffer = 180000f;
        maxX = maxX + max_buffer;
        minX = minX - min_buffer;

        XAxis xAxis = chart.getXAxis();
        xAxis.setAxisMinimum(minX); // Setzt die minimale Zeit basierend auf den Daten
        xAxis.setAxisMaximum(maxX); // Setzt die maximale Zeit basierend auf den Daten


        chart.invalidate(); // Chart neu zeichnen


    }

    // Method for setting xAxis of the chart
    private void setupXAxis(String viewMode) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        switch (viewMode) {
            case "day":
                xAxis.setValueFormatter(new HourAxisValueFormatter());
                xAxis.setLabelCount(12, true); // 24 Stunden
                xAxis.setGranularity(3600000f); // 1 Stunde in Millisekunden
                break;
            case "week":
                xAxis.setValueFormatter(new DayAxisValueFormatter());
                xAxis.setLabelCount(7, true); // 7 Tage
                xAxis.setGranularity(86400000f); // 1 Tag in Millisekunden
                break;
            case "month":
                xAxis.setValueFormatter(new DayAxisValueFormatter());
                xAxis.setLabelCount(10, true); // 30 Tage
                xAxis.setGranularity(86400000f); // 1 Tag in Millisekunden
                xAxis.setLabelRotationAngle(45);
                break;
        }

    }

    public void setupMarkerView() {
        CustomMarkerView mv = new CustomMarkerView(getContext(), R.layout.marker_view);
        chart.setMarker(mv);
    }

    private void setupValueSelectedListener() {
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                chart.highlightValue(h);
                Log.i("ContextDataPointInfo:", "x=" + e.getX() + "y=" + e.getY());

                // Refreshing ContextMenu und ValueDisplay
                if (chart.getMarker() instanceof CustomMarkerView) {
                    CustomMarkerView mv = (CustomMarkerView) chart.getMarker();
                    mv.refreshContent(e, h);
                }

                SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                String formattedDateTime = fullDateFormat.format(new Date((long) e.getX()));

                FullDateTimeFormatter formatter = new FullDateTimeFormatter();
                setValueDisplayData(formattedDateTime + " Uhr \n Wert: " + e.getY() + " mg/dl");
            }

            @Override
            public void onNothingSelected() {}
        });
    }

    // Sets text at bottom
    public void setValueDisplayData(String data) {
        if (getActivity() != null) {
            ((Verlauf) getActivity()).updateValueDisplay(data);
        }
    }

    // Updates the text at the bottom
    private void updateValueDisplay(String viewMode) {
        String displayText;
        switch (viewMode) {
            case "day":
                displayText = "Tagesansicht: Glukosewerte für den ausgewählten Tag";
                break;
            case "week":
                displayText = "Wochenansicht: Durchschnittliche Glukosewerte pro Halbtag";
                break;
            case "month":
                displayText = "Monatsansicht: Durchschnittliche Glukosewerte pro Tag";
                break;
            default:
                displayText = "";
                break;
        }
        setValueDisplayData(displayText);
    }

}
