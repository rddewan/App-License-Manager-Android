package biz.binarysolution.licensemanager.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.app.job.JobParameters;
import com.firebase.jobdispatcher.JobService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import biz.binarysolution.licensemanager.services.utilities.GetLicenseInfo;

public class LicenseJobService extends JobService {
    private static final String TAG = LicenseJobService.class.getSimpleName();
    private AsyncTask mBackgroundTask;
    public LicenseJobService() {
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(final com.firebase.jobdispatcher.JobParameters job) {
        Log.e(TAG,"License job service started");
        // Here's where we make an AsyncTask so that this is no longer on the main thread
        mBackgroundTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                //get license from api
                Log.e(TAG,"preparing to get license data from api");
                GetLicenseInfo getLicenseInfo = new GetLicenseInfo(getApplicationContext());
                getLicenseInfo.getLicense();

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                /*
                 * Once the AsyncTask is finished, the job is finished. To inform JobManager that
                 * you're done, you call jobFinished with the jobParamters that were passed to your
                 * job and a boolean representing whether the job needs to be rescheduled. This is
                 * usually if something didn't work and you want the job to try running again.
                 */

                jobFinished(job,false);

            }
        };
        // Execute the AsyncTask
        mBackgroundTask.execute();
        /* return true if there is more work remaining in the worker thread,
        false if the  job was completed.
        */
        return true;
    }

    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters job) {
        Log.e(TAG,"License job service stopped");
        // If mBackgroundTask is valid, cancel it
        //Return true to signify the job should be retried
        if (mBackgroundTask != null) mBackgroundTask.cancel(true);
        return true;
    }

}
