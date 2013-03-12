package net.maelbrancke.filip;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

public class ChartTestActivity extends Activity {

    private static String TAG = ChartTestActivity.class.getSimpleName();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
        setContentView(R.layout.main);

        final RotatingChart chart = (RotatingChart) findViewById(R.id.piechart);

        chart.addItem("Test 1", 3, Color.BLUE);
        chart.addItem("Test 2", 4, Color.GREEN);
        chart.addItem("Test 3", 2, Color.RED);
        chart.addItem("Test 4", 3, Color.DKGRAY);
        chart.addItem("Test 5", 1, Color.MAGENTA);
        chart.addItem("Test 6", 2, Color.LTGRAY);
        chart.addItem("Test 7", 3, Color.YELLOW);
    }

}

