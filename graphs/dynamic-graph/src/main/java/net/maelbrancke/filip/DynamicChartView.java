package net.maelbrancke.filip;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;

/**
 * Chart component.
 */
public class DynamicChartView extends View {

    private static final String TAG = DynamicChartView.class.getSimpleName();

    private static final int MIN_LINES = 4;
    private static final int MAX_LINES = 7;
    private static final int[] DISTANCES = { 1, 2, 5 };

    private static final float GRAPH_SMOOTHNESS = 0.15f;

    private float[] datapoints = new float[] {};
    //private Dynamics[] datapoints;
    private Paint paint = new Paint();

    /*private Runnable animator = new Runnable() {
        public void run() {
            boolean needNewFrame = false;
            final long now = AnimationUtils.currentAnimationTimeMillis();
            for (Dynamics dynamics : datapoints) {
                dynamics.update(now);
                if (!dynamics.isAtRest()) {
                    needNewFrame = true;
                }
            }
            if (needNewFrame) {
                postDelayed(this, 20);
            }
            invalidate();
        }
    };*/

    public DynamicChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDatapoints(float[] datapoints) {
        this.datapoints = datapoints;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        drawChart(canvas);
    }

    private void drawBackground(Canvas canvas) {
        float maxValue = getMax(this.datapoints);
        Log.d(TAG, "maxValue = " + maxValue);
        int range = getLineDistance(maxValue);
        Log.d(TAG, "range = " + range);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GRAY);
        for (int y = 0; y < maxValue; y+=range) {
            final float yPos = getYPosition(y);
            canvas.drawLine(0, yPos, getWidth(), yPos, paint);
        }
    }

    private void drawChart(Canvas canvas) {
        Path path = new Path();
        path.moveTo(getXposition(0), getYPosition(datapoints[0]));
        for (int i = 1; i < datapoints.length; i++) {
            path.lineTo(getXposition(i), getYPosition(datapoints[i]));
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        paint.setColor(0xFF33B5E5);
        paint.setAntiAlias(true);
        paint.setShadowLayer(4, 2, 2, 0x80000000);
        canvas.drawPath(path, paint);
        paint.setShadowLayer(0, 0, 0, 0);
    }

    private float getMax(float[] array) {
        float max = array[0];
        for(int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    private int getLineDistance(float maxValue) {
        int distance;
        int distanceIndex = 0;
        int distanceMultiplier = 1;
        int numberOfLines = MIN_LINES;

        do {
            distance = DISTANCES[distanceIndex] * distanceMultiplier;
            numberOfLines = (int) FloatMath.ceil(maxValue / distance);

            distanceIndex++;
            if (distanceIndex == DISTANCES.length) {
                distanceIndex = 0;
                distanceMultiplier *= 10;
            }
        } while (numberOfLines < MIN_LINES || numberOfLines > MAX_LINES);

        Log.d(TAG, "line distance = " + distance);
        return distance;
    }

    private float getYPosition(float value) {
        float height = getHeight() - getPaddingTop() - getPaddingBottom();
        float maxValue = getMax(datapoints);
        // scale to the view size
        value = (value / maxValue) * height;
        // invert since higher values should have a lower y value
        value = height - value;
        // offset to adjust for padding
        value += getPaddingTop();
        return value;
    }

    private float getXposition(float value) {
        float width = getWidth() - getPaddingLeft() - getPaddingRight();
        float maxValue = datapoints.length - 1;
        // scale to the view size
        value = (value / maxValue) * width;
        // offset to adjust for padding
        value += getPaddingLeft();
        return value;
    }
}
