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
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.List;

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
        } else {
            setValueDisplayData(getString(R.string.ValueInputRequired));
        }
    }

    private void setupButtonListeners() {
        btn_day.setOnClickListener(v -> loadData("day"));
        btn_week.setOnClickListener(v -> loadData("week"));
        btn_month.setOnClickListener(v -> loadData("month"));
    }

    private void loadData(String viewMode) {
        glucoseDataManager.loadData(viewMode, this::updateChart);
        setupXAxis(viewMode);
    }

    private void updateChart(List<Entry> entries) {
        LineDataSet dataSet = new LineDataSet(entries, "Glucose Levels");
        dataSet.setColor(Color.BLUE);
        dataSet.setLineWidth(2f);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.notifyDataSetChanged(); // Chart benachrichtigen, dass Daten geändert wurden
        chart.invalidate(); // Chart neu zeichnen
    }

    // Method for setting xAxis of the chart
    private void setupXAxis(String viewMode) {
        XAxis xAxis = chart.getXAxis();
        if ("day".equals(viewMode)) {
            xAxis.setValueFormatter(new HourAxisValueFormatter());
        } else {
            xAxis.setValueFormatter(new DayAxisValueFormatter());
        }
        xAxis.setEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(6, true);
        xAxis.setGranularity(1f); // Setzt den minimalen Abstand der Achsenlabels
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
                HourAxisValueFormatter formatter = new HourAxisValueFormatter();
                setValueDisplayData("Zeit: " + formatter.getFormattedValue(e.getX()) + " Uhr \n Wert: " + e.getY() + " mg/dl");
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

}
