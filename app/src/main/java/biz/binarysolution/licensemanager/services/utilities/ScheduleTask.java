package biz.binarysolution.licensemanager.services.utilities;

import android.content.Context;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.io.FileReader;
import java.util.concurrent.TimeUnit;

import biz.binarysolution.licensemanager.services.LicenseJobService;


public class ScheduleTask {
    private static final String TAG = ScheduleTask.class.getSimpleName();
    private static final int REMINDER_INTERVAL_MINUTES = 15;
    //REMINDER_INTERVAL_SECONDS should be an integer constant storing the number of seconds in 15 minutes
    private static final int REMINDER_INTERVAL_SECONDS = (int) (TimeUnit.MINUTES.toSeconds(REMINDER_INTERVAL_MINUTES));
    //SYNC_FLEXTIME_SECONDS should also be an integer constant storing the number of seconds in 15 minutes
    private static final int SYNC_FLEXTIME_SECONDS = REMINDER_INTERVAL_SECONDS;
    //TAG
    private static final String SCHEDULE_JOB_TAG = "license_schedule_tag";

    //sInitialized should be a private static boolean variable which will store whether the job has been activated or not
    private static boolean sInitialized;

    synchronized public static void scheduleLicenseCheck(Context context){
        Log.e(TAG,"Schedule License Check");
        //If the job has already been initialized, return
        if (sInitialized){
            return;
        }
        //Create a new GooglePlayDriver
        Driver driver = new GooglePlayDriver(context);
        //Create a new FirebaseJobDispatcher with the driver
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        //Create the Job to periodically check license
        Job job = dispatcher.newJobBuilder()
                ///* The Service that will be used to check license
                .setService(LicenseJobService.class)
                //UNIQUE tag used to identify this Job.
                .setTag(SCHEDULE_JOB_TAG)
                //job only executes if the device has network connectivity
                .setConstraints(Constraint.ON_ANY_NETWORK)
                //Job forever even after next time the device boots up.
                .setLifetime(Lifetime.FOREVER)
                //run job continuously .
                .setRecurring(true)
                //We want the reminders to happen every 15 minutes
                .setTrigger(Trigger.executionWindow(REMINDER_INTERVAL_SECONDS,
                        REMINDER_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                //If a Job with the tag with provided already exists, this new job will replace the old one.
                .setReplaceCurrent(true)
                //Once the Job is ready, call the builder's build method to return the Job
                .build();
        //Use dispatcher's schedule method to schedule the job
        dispatcher.schedule(job);

        //Set sInitialized to true to mark that we're done setting up the job
        sInitialized = true;

    }
}
