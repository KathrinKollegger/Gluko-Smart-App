package com.example.gluko_smart;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

public class CustomMarkerView extends MarkerView {

    private TextView tv_dpContent;
    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public CustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        tv_dpContent = (TextView) findViewById(R.id.markerContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        super.refreshContent(e, highlight);
        HourAxisValueFormatter formatter = new HourAxisValueFormatter();
        tv_dpContent.setText("Zeitpunkt: " + formatter.getFormattedValue(e.getX()) +" - "+ e.getY()+" mg/dl");
    }

    @Override
    public MPPointF getOffset() {
        MPPointF offset = super.getOffset();
        offset.x = -(getWidth()/2);
        offset.y = -(getHeight());

        return offset;

    }
}
