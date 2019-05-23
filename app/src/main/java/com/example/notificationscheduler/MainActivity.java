package com.example.notificationscheduler;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    RadioGroup mNetworkOptions;
    int selectedNetworkOption;
    private JobScheduler mJobScheduler;

    private static final int JOB_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNetworkOptions = findViewById(R.id.networkOptions);
        selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;
    }

    public void scheduleJob(View view) {
        int selectedNetworkId = mNetworkOptions.getCheckedRadioButtonId();

        switch (selectedNetworkId) {
            case R.id.noNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;
                break;
            case R.id.anyNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_ANY;
                break;
            case R.id.wifiNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_UNMETERED;
                break;
        }

        JobInfo mJobInfo = buildJobInfo(JOB_ID, NotificationJobService.class.getName());

        mJobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        mJobScheduler.schedule(mJobInfo);

        Toast.makeText(this, "Job scheduled to execute when constraint is met"
                , Toast.LENGTH_SHORT).show();
    }

    private JobInfo buildJobInfo(int jobId, String className) {
        ComponentName serviceName = new ComponentName(getPackageName(), className);
        JobInfo.Builder builder = new JobInfo.Builder(jobId, serviceName)
                                    .setRequiredNetworkType(selectedNetworkOption);
        return builder.build();
    }

    public void cancelJobs(View view) {
        if (mJobScheduler != null) {
            mJobScheduler.cancelAll();
            mJobScheduler = null;
            Toast.makeText(this, "All jobs cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}
