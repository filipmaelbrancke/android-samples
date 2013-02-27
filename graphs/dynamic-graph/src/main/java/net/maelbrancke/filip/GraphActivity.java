package net.maelbrancke.filip;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class GraphActivity extends Activity {

    private static String TAG = "dynamic-graph";

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after 
     * previously being shut down then this Bundle contains the data it most 
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
        setContentView(R.layout.main);

        DynamicChartView chart = (DynamicChartView) findViewById(R.id.chart);
        chart.setDatapoints(getRandomData());
    }

    private float[] getRandomData() {
        return new float[] { 12, 10, 7, 15, 4, 9, 11, 12, 17, 15, 18, 6 };
    }

}

