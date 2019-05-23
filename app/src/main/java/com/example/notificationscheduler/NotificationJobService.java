package com.example.notificationscheduler;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class NotificationJobService extends JobService {

    NotificationManager mNotificationManager;

    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private static final int JOBSERVICE_NOTIFICATION_ID = 0;

    @Override
    public boolean onStartJob(JobParameters params) {

        createNotificationChannel();

        PendingIntent contentPendingIntent = PendingIntent.getActivity(this
                    , JOBSERVICE_NOTIFICATION_ID, new Intent(this, MainActivity.class)
                    , PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this
                    , PRIMARY_CHANNEL_ID)
                    .setContentTitle("Job Service")
                    .setContentText("Job service completed")
                    .setContentIntent(contentPendingIntent)
                    .setSmallIcon(R.drawable.ic_job_running)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setAutoCancel(true);

        mNotificationManager.notify(JOBSERVICE_NOTIFICATION_ID, notiBuilder.build());

        return false;
    }

    private void createNotificationChannel() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    PRIMARY_CHANNEL_ID, "Job Service Notification Channel"
                    , NotificationManager.IMPORTANCE_HIGH);

            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
