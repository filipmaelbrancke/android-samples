package net.maelbrancke.filip;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class GraphActivity extends Activity {

    private static String TAG = "dynamic-graph";
    private DynamicChartView chart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button button1 = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);
        button1.setOnClickListener(button1Listener);
        button2.setOnClickListener(button2Listener);
        button3.setOnClickListener(button3Listener);

        chart = (DynamicChartView) findViewById(R.id.chart);
        chart.setDatapoints(getDataset1());
    }

    private float[] getDataset1() {
        return new float[] { 12, 10, 7, 15, 4, 9, 11, 12, 17, 15, 18, 6 };
    }

    private float[] getDataset2() {
        return new float[] { 12, 11, 12, 17, 15, 18, 6, 10, 7, 15, 4, 9 };
    }

    private float[] getRandomDataSet() {
        float[] dataArray = new float[10];
        for (int i = 0; i < 10; i++) {
            dataArray[i] = getRandomIntInRange(1, 19);
        }
        StringBuilder builder = new StringBuilder("array = ");
        for (float number : dataArray) {
            builder.append(number + " - ");
        }
        Log.d(TAG, builder.toString());
        return dataArray;
    }

    private int getRandomIntInRange(final int min, final int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    View.OnClickListener button1Listener = new View.OnClickListener() {
        public void onClick(View view) {
            chart.setDatapoints(getDataset1());
        }
    };

    View.OnClickListener button2Listener = new View.OnClickListener() {
        public void onClick(View view) {
            chart.setDatapoints(getDataset2());
        }
    };

    View.OnClickListener button3Listener = new View.OnClickListener() {
        public void onClick(View view) {
            chart.setDatapoints(getRandomDataSet());
        }
    };

}

