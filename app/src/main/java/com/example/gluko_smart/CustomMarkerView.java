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
     * @param context context for the marker
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public CustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        tv_dpContent = (TextView) findViewById(R.id.markerContent);
    }

    //updates the content of the DataPoint popup with the selected point's X and Y values
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        super.refreshContent(e, highlight);
        HourAxisValueFormatter formatter = new HourAxisValueFormatter();
        tv_dpContent.setText("Zeitpunkt: " + formatter.getFormattedValue(e.getX()) +" - "+ e.getY()+" mg/dl");
    }

    //sets the offset of the DataPoint popup and returns an MPPointF object with the offset values
    @Override
    public MPPointF getOffset() {
        MPPointF offset = super.getOffset();
        offset.x = -(getWidth()/2);
        offset.y = -(getHeight());

        return offset;
    }
}
