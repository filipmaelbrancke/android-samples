package net.maelbrancke.filip.android.restservice.test;

import android.test.ActivityInstrumentationTestCase2;
import net.maelbrancke.filip.android.restservice.*;

public class ServiceActivityTest extends ActivityInstrumentationTestCase2<ServiceActivity> {

    public ServiceActivityTest() {
        super(ServiceActivity.class); 
    }

    public void testActivity() {
        ServiceActivity activity = getActivity();
        assertNotNull(activity);
    }
}

