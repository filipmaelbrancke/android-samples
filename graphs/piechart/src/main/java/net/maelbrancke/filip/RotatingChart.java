package net.maelbrancke.filip;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom view that shows a chart.
 */
public class RotatingChart extends ViewGroup {

    private static final String TAG = RotatingChart.class.getSimpleName();

    private List<ChartItem> mData = new ArrayList<ChartItem>();

    private float mTotal = 0.0f;

    private RectF mChartBounds = new RectF();

    private Paint mChartPaint;
    private Paint mTextPaint;

    private boolean mShowTopLayer = false;

    private int mTextColor;
    private float mTextWidth = 0.0f;
    private float mTextHeight = 0.0f;

    private float mInnerCircleX;
    private float mInnerCircleY;
    private float mInnerCircleRadius = 200.0f;

    private float mHighlightStrength = 1.15f;

    private ChartView mChartView;
    private int mChartRotation;
    private MiddleView mInnerCircleView;
    private GestureDetector mDetector;

    // the index of the current item
    private int mCurrentItem = 0;
    private boolean mAutoCenterInSlice;
    private CurrentItemChangeListener mCurrentItemChangeListener = sDummyCallback;

    /**
     * The initial fling velocity is divided by this amount.
     */
    public static final int FLING_VELOCITY_DOWNSCALE = 4;

    /**
     * Duration of the auto-centering animation.
     */
    public static final int AUTOCENTER_ANIM_DURATION = 250;

    /**
     * Callback interface for current item change detection.
     */
    public interface CurrentItemChangeListener {
        void onCurrentItemChanged(RotatingChart source, int currentItem);
    }

    public RotatingChart(Context context) {
        super(context);
        init();
    }

    public RotatingChart(Context context, AttributeSet attrs) {
        super(context, attrs);

        // attrs contains the xml attributes that were specified
        // for the layout
        // Here we call R.styleable.RotatingChart, which is an array of
        // the custom attributes that were declared in attrs.xml
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RotatingChart,
                0, 0
        );

        try {
            // Retrieve the values from the TypedArray and store into class fields

            mChartRotation = a.getInt(R.styleable.RotatingChart_chartRotation, 0);
            mShowTopLayer = a.getBoolean(R.styleable.RotatingChart_showTopLayer, false);
            mTextWidth = a.getDimension(R.styleable.RotatingChart_labelWidth, 0.0f);
            mTextHeight = a.getDimension(R.styleable.RotatingChart_labelHeight, 0.0f);
        } finally {
            a.recycle();
        }

        init();
    }

    /**
     * Initialize the component.
     */
    private void init() {

        // Setup the Paints
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.WHITE);
        if (mTextHeight == 0) {
            mTextHeight = mTextPaint.getTextSize();
        } else {
            mTextPaint.setTextSize(mTextHeight);
        }
        mChartPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mChartPaint.setStyle(Paint.Style.FILL);
        mChartPaint.setTextSize(mTextHeight);

        // Add a child view to draw the chart. Putting this in a child view
        // makes it possible to draw it on a separate hardware layer that rotates
        // independently
        mChartView = new ChartView(getContext());
        addView(mChartView);
        mChartView.rotateTo(mChartRotation);

        // The inner circle doesn't need hardware acceleration, but in order to show up
        // in front of the chart it also needs to be on a separate view.
        mInnerCircleView = new MiddleView(getContext());
        addView(mInnerCircleView);




        // Create a gesture detector to handle onTouch messages
        mDetector = new GestureDetector(RotatingChart.this.getContext(), new GestureListener());

        // Turn off long press. This component doesn't use it, and if long press is enabled,
        // you can't scroll for a bit, pause, and then scroll some more (the pause is interpreted
        // as a long press...
        mDetector.setIsLongpressEnabled(false);



        // In edit mode: add some demo data
        if (this.isInEditMode()) {
            addItem("Test 1", 3, Color.BLUE);
            addItem("Test 2", 4, Color.GREEN);
            addItem("Test 3", 2, Color.RED);
            addItem("Test 4", 3, Color.BLACK);
            addItem("Test 5", 1, Color.MAGENTA);
        }
    }

    /**
     * Returns the index of the currently selected data item.
     *
     * @return The index of the currently selected data item (zero-based)
     */
    public int getCurrentItem() {
        return mCurrentItem;
    }

    /**
     * Set the currently selected item. This function will set the current selection
     * and rotate the chart.
     *
     * @param currentItem The index of the item to select (zero-based)
     */
    public void setCurrentItem(int currentItem) {
        setCurrentItem(currentItem, true);
    }

    /**
     * Set the currently selected item by index. Optionally, if will rotate the current
     * view into position. This function is for internal use -- the scrollIntoPlace option
     * is always true for external callers.
     *
     * @param currentItem The index of the current item
     * @param scrollIntoPlace True if the chart should rotate until the current item is in place
     *                        (= centered). False otherwise. If this parameter is false, the chart
     *                        rotation will not change.
     */
    private void setCurrentItem(int currentItem, boolean scrollIntoPlace) {
        mCurrentItem = currentItem;
        mCurrentItemChangeListener.onCurrentItemChanged(this, currentItem);
        if (scrollIntoPlace) {
            centerOnCurrentItem();
        }
        invalidate();
    }

    /**
     * Register a callback to be invoked when the currently selected item changes.
     *
     * @param listener The current item change listener to attach to this view. Can be null.
     */
    public void setCurrentItemChangeListener(CurrentItemChangeListener listener) {
        if (listener == null) {
            mCurrentItemChangeListener = sDummyCallback;
        } else {
            mCurrentItemChangeListener = listener;
        }
    }

    @Override
    protected void onLayout(boolean b, int i, int i2, int i3, int i4) {
        // Do nothing. Do not call the superclass method--that would start a layout pass
        // on this view's children. RotatingChart lays out its children in onSizeChanged().
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


    }

    // Measurement functions. Here we assume that the chart should be at least
    // as wide as the text we will put in it.
    @Override
    protected int getSuggestedMinimumHeight() {
        return (int) mTextWidth;
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return (int) mTextWidth * 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // Try for a width based on our minimum
        int minWidth = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();

        int width = Math.max(minWidth, MeasureSpec.getSize(widthMeasureSpec));

        // Whatever the width ends up being, ask for a height that would let the chart
        // get as big as it can
        int minHeight = (width - (int) mTextWidth) + getPaddingBottom() + getPaddingTop();
        int height = Math.min(MeasureSpec.getSize(heightMeasureSpec), minHeight);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Set the dimensions for the component pieces

        // Account for padding
        float xPadding = (float) (getPaddingLeft() + getPaddingRight());
        float yPadding = (float) (getPaddingTop() + getPaddingBottom());

        // Check how big the chart can be
        float diameter = (float) w - xPadding;
        mChartBounds = new RectF(0.0f, 0.0f, diameter, diameter);
        mChartBounds.offsetTo(getPaddingLeft(), getPaddingTop());

        // ...
        mInnerCircleX = mChartBounds.centerX();
        mInnerCircleY = mChartBounds.centerY();

        // Layout the child view that actually draws the pie.
        mChartView.layout((int) mChartBounds.left,
                (int) mChartBounds.top,
                (int) mChartBounds.right,
                (int) mChartBounds.bottom);
        mChartView.setPivot(mChartBounds.width() / 2, mChartBounds.height() / 2);

        // ...
        mInnerCircleView.layout(0, 0, w, h);
        onDataChanged();
    }

    private void setLayerToSoftware(View v) {
        if (!v.isInEditMode() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            Log.d(TAG, "Setting layer to software : " + v.toString());
        }
    }

    private void setLayerToHardware(View v) {
        if (!v.isInEditMode() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
            Log.d(TAG, "Setting layer to hardware : " + v.toString());
        }
    }

    /**
     * Force a stop to the chart motion. Called when the user taps during a fling.
     */
    private void stopScrolling() {
        // TODO


        onScrollFinished();
    }

    /**
     * Returns the current rotation of the chart.
     *
     * @return The current chart rotation, in degrees.
     */
    public int getChartRotation() {
        return mChartRotation;
    }

    /**
     * Set the current rotation of the chart. Setting this value may change the current item.
     *
     * @param rotation The current chart rotation, in degrees.
     */
    public void setChartRotation(int rotation) {
        rotation = (rotation % 360 + 360) % 360;
        mChartRotation = rotation;
        mChartView.rotateTo(rotation);

        // ...
    }

    /**
     * Add a new data item to this view. Adding an item adds a slice to the pie chart whose
     * size is proportional to the item's value. As new items are added, the size of each
     * existing slice is recalculated so that the proportions remain correct.
     *
     * @param label The label text that belongs to this item.
     * @param value The value of this item.
     * @param color The ARGB color of the pie chart slice associated with this item.
     * @return The index of the newly added item.
     */
    public int addItem(String label, float value, int color) {
        ChartItem item = new ChartItem();
        item.mLabel = label;
        item.mValue = value;
        item.mColor = color;

        // Calculate the highlight color. Saturate at 0xff to make sure that high values
        // don't result in aliasing.
        item.mHighlight = Color.argb(
                0xff,
                Math.min((int) (mHighlightStrength * (float) Color.red(color)), 0xff),
                Math.min((int) (mHighlightStrength * (float) Color.green(color)), 0xff),
                Math.min((int) (mHighlightStrength * (float) Color.blue(color)), 0xff)
        );
        mTotal += value;

        mData.add(item);

        onDataChanged();

        return mData.size() - 1;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Let the GestureDetector interpres this event
        boolean result = mDetector.onTouchEvent(event);

        // If the GestureDetector doesn't want this event, do some custom processing.
        // This code tries to detect when the user is done scrolling by looking for
        // ACTION_UP events.
        if (!result) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // User is done scrolling, it's now safe to do things like autocenter
                stopScrolling();;
                result = true;
            }
        }
        return result;
    }

    /**
     * Do all of the recalculations needed when the data array changes.
     */
    private void onDataChanged() {
        // When the data changes, we have to recalculate all of the angles.
        int currentAngle = 0;
        int lastItemIndex = mData.size() - 1;
        int i = 0;
        for (ChartItem item : mData) {
            item.mStartAngle = currentAngle;
            if (i == lastItemIndex) {
                item.mEndAngle = 360;
            } else {
                item.mEndAngle = (int) ((float) currentAngle + item.mValue * 360.0f / mTotal);
            }
            currentAngle = item.mEndAngle;

            // Recalculate the gradient shaders. There are three values in this
            // gradient, even though only two are necessary, in order to work
            // around a bug in certain versions of the graphics engine that expects
            // at least three values if the positions array is non-null.
            item.mShader = new SweepGradient(
                    mChartBounds.width() / 2.0f,
                    mChartBounds.height() / 2.0f,
                    new int[]{
                            item.mHighlight,
                            item.mHighlight,
                            item.mColor,
                            item.mColor
                    },
                    new float[]{
                            0,
                            (float) (360 - item.mEndAngle) / 360.0f,
                            (float) (360 - item.mStartAngle) / 360.0f,
                            1.0f
                    }
            );
            i++;
        }

        calcCurrentItem();
        onScrollFinished();
    }

    /**
     * Called when the user finishes a scroll action.
     */
    private void onScrollFinished() {
        if (mAutoCenterInSlice) {
            centerOnCurrentItem();

        } else {
            // TODO : decelerate
            mChartView.stopHardwareAcceleration();
        }
    }

    /**
     * Animate the chart so that it centers the slice of the currently
     * selected item.
     */
    private void centerOnCurrentItem() {
    }

    /**
     * Calculate which pie chart slice is at the bottom, and set the current item
     * field accordingly.
     */
    private void calcCurrentItem() {
        // TODO

    }

    private class ChartView extends View {
        // Used for SDK < 11
        private float mRotation = 0;
        private Matrix mTransform = new Matrix();
        private PointF mPivot = new PointF();

        /**
         * Constructor.
         * @param context the context
         */
        public ChartView(Context context) {
            super(context);
        }

        /**
         * Enable hardware acceleration
         */
        public void startHardwareAcceleration() {
            setLayerToHardware(this);
        }

        /**
         * Disable hardware acceleration
         */
        public void stopHardwareAcceleration() {
            setLayerToSoftware(this);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                mTransform.set(canvas.getMatrix());
                mTransform.preRotate(mRotation, mPivot.x, mPivot.y);
                canvas.setMatrix(mTransform);
            }

            for (ChartItem item : mData) {
                mChartPaint.setShader(item.mShader);
                canvas.drawArc(mBounds,
                        360 - item.mEndAngle,
                        item.mEndAngle - item.mStartAngle,
                        true, mChartPaint);
            }
        }

        RectF mBounds;

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            mBounds = new RectF(0, 0, w, h);
        }

        public void rotateTo(float chartRotation) {
            mRotation = chartRotation;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                setRotation(chartRotation);
            } else {
                invalidate();
            }
        }

        public void setPivot(float x, float y) {
            mPivot.x = x;
            mPivot.y = y;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                setPivotX(x);
                setPivotY(y);
            } else {
                invalidate();
            }
        }
    }

    /**
     * View that draws the inner circle on top of the chart
     */
    private class MiddleView extends View {

        /**
         * Constructor.
         * @param context the context
         */
        public MiddleView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawCircle(mInnerCircleX, mInnerCircleY, mInnerCircleRadius, mTextPaint);
        }
    }

    /**
     * Maintains the state for a chart data item.
     */
    private class ChartItem {
        public String mLabel;
        public float mValue;
        public int mColor;

        // Computed values
        public int mStartAngle;
        public int mEndAngle;

        public int mHighlight;
        public Shader mShader;
    }

    /**
     * Gesture detector that extends {@link GestureDetector.SimpleOnGestureListener} to
     * handle the gesture processing.
     */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d(TAG, "GestureListener::onScroll");
            // Set the chart rotation directly.
            float scrollTheta = vectorToScalarScroll(
                    distanceX,
                    distanceY,
                    e2.getX() - mChartBounds.centerX(),
                    e2.getY() - mChartBounds.centerY()
            );
            setChartRotation(getChartRotation() - (int) scrollTheta / FLING_VELOCITY_DOWNSCALE);
            Log.d(TAG, "GestureListener::onScroll:: chart rotation set");
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // ...


            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.d(TAG, "GestureListener::onDown");
            // The user is interacting with the chart, turn on acceleration so the interaction is smooth
            mChartView.startHardwareAcceleration();
            if (isAnimationRunning()) {
                stopScrolling();
            }
            return true;
        }
    }

    private boolean isAnimationRunning() {
        // ...
        return false;
    }

    private static float vectorToScalarScroll(float dx, float dy, float x, float y) {
        // get the length of the vector
        float l = (float) Math.sqrt(dx * dx + dy * dy);

        // decide if the scalar should be nagative or positive by finding
        // the dot product of the vector perpendicular to (x, y)
        float crossX = -y;
        float crossY = x;

        float dot = (crossX * dx + crossY * dy);
        float sign = Math.signum(dot);

        return l * sign;
    }

    private static CurrentItemChangeListener sDummyCallback = new CurrentItemChangeListener() {
        @Override
        public void onCurrentItemChanged(RotatingChart source, int currentItem) {
            // dummy
        }
    };
}
