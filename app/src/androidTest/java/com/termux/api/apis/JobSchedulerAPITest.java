package com.termux.api.apis;

import android.app.job.JobInfo;
import android.content.ComponentName;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.PersistableBundle;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

@RunWith(AndroidJUnit4.class)
public class JobSchedulerAPITest {

    private String format(JobInfo job) throws Exception {
        Method method = JobSchedulerAPI.class
                .getDeclaredMethod("formatJobInfo", JobInfo.class);

        method.setAccessible(true);
        return (String) method.invoke(null, job);
    }

    private JobInfo.Builder createJobBuilder() {
        PersistableBundle extras = new PersistableBundle();
        extras.putString(
                JobSchedulerAPI.JobSchedulerService.SCRIPT_FILE_PATH,
                "/data/local/tmp/test.sh"
        );

        return new JobInfo.Builder(
                924,
                new ComponentName(
                        InstrumentationRegistry.getInstrumentation().getTargetContext(),
                        JobSchedulerAPI.JobSchedulerService.class
                )
        )
        .setExtras(extras);
    }

    @Test
    public void testNoNetworkRequirement() throws Exception {
        JobInfo job = createJobBuilder().build();

        assertNull(job.getRequiredNetwork());

        String result = format(job);

        assertTrue(result, result.contains("(network: none)"));
    }

    @Test
    public void testNetworkRequirement() throws Exception {
        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        JobInfo job = createJobBuilder()
                .setRequiredNetwork(request)
                .build();

        assertNotNull(job.getRequiredNetwork());

        String result = format(job);

        assertTrue(result, result.contains("(network: "));
    }
}
