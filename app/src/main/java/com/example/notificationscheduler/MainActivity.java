package com.example.notificationscheduler;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    RadioGroup mNetworkOptions;
    Switch mDeviceIdle;
    Switch mDeviceCharging;
    TextView mSeekBarValue;
    SeekBar mOverrideSchedule;
    private JobScheduler mJobScheduler;

    int selectedNetworkOption;
    int seekBarValue;

    private static final int JOB_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNetworkOptions = findViewById(R.id.networkOptions);
        mDeviceIdle = findViewById(R.id.deviceIdle);
        mDeviceCharging = findViewById(R.id.deviceCharging);
        mSeekBarValue = findViewById(R.id.seekBarValue);
        mOverrideSchedule = findViewById(R.id.overrideSeekBar);

        selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;

        mOverrideSchedule.setOnSeekBarChangeListener(getOnSeekBarChangeListener());
    }

    private SeekBar.OnSeekBarChangeListener getOnSeekBarChangeListener() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i>0) {
                    mSeekBarValue.setText(i + " s");
                } else {
                    mSeekBarValue.setText("Not Set");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
    }

    public void scheduleJob(View view) {
        int selectedNetworkId = mNetworkOptions.getCheckedRadioButtonId();
        selectedNetworkOption = getSelectedNetworkOption(selectedNetworkId);

        seekBarValue = mOverrideSchedule.getProgress();

        if (isConstraintSet()) {
            JobInfo mNotificationJobInfo = buildNotificationJobInfo();
            mJobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            mJobScheduler.schedule(mNotificationJobInfo);

            Toast.makeText(this, "Job scheduled to execute when constraint is met"
                    , Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "At least one constraint is required"
                    , Toast.LENGTH_SHORT).show();
        }
    }

    private int getSelectedNetworkOption(int selectedNetworkId) {
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
        return selectedNetworkOption;
    }

    private boolean isConstraintSet() {
        return (selectedNetworkOption != JobInfo.NETWORK_TYPE_NONE)
                || mDeviceIdle.isChecked()
                || mDeviceCharging.isChecked()
                || (seekBarValue > 0)
                ;
    }

    private JobInfo buildNotificationJobInfo() {
        ComponentName serviceName = new ComponentName(getPackageName()
                                    , NotificationJobService.class.getName());

        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceName)
                                    .setRequiredNetworkType(selectedNetworkOption)
                                    .setRequiresDeviceIdle(mDeviceIdle.isChecked())
                                    .setRequiresCharging(mDeviceCharging.isChecked());

        if (seekBarValue > 0) { builder.setOverrideDeadline(seekBarValue * 1000); }

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
