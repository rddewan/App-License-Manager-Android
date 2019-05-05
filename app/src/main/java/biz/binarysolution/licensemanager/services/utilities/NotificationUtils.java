package biz.binarysolution.licensemanager.services.utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.strictmode.ResourceMismatchViolation;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import java.util.BitSet;

import biz.binarysolution.licensemanager.R;
import biz.binarysolution.licensemanager.license.ActivateLicense;

public class NotificationUtils {
    /**
     * This pending intent id is used to uniquely reference the pending intent
     */
    private static final int LICENSE_PENDING_INTENT_ID = 3417;
    //notification channel id is used to link notifications to this channel
    private static final String LICENSE_NOTIFICATION_CHANNEL_ID = "license_notification_channel";
    /*
     * This notification ID can be used to access our notification after we've displayed it. This
     * can be handy when we need to cancel the notification, or perhaps update it. This number is
     * arbitrary and can be set to whatever you like. 1138 is in no way significant.
     */
    private static final int LICENSE_NOTIFICATION_ID = 1138;

    /*
    This method will create a notification
     */
    public static void createNotification(Context context){
        //Get the NotificationManager using context.getSystemService
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //notification channel for Android O devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(LICENSE_NOTIFICATION_CHANNEL_ID
                    ,"License",NotificationManager.IMPORTANCE_HIGH);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        //NotificationCompat.Builder to create a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,LICENSE_NOTIFICATION_CHANNEL_ID)
                //use ContextCompat.getColor to get a compatible color
                .setColor(ContextCompat.getColor(context,R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_menu_share)
                .setLargeIcon(notificationIcon(context))
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.msg_license_expired))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.msg_license_expired)))
                //notification defaults to vibrate this will need manifests permission
                .setDefaults(Notification.DEFAULT_VIBRATE)
                //content intent returned by the contentIntent helper method for the contentIntent
                .setContentIntent(contentIntent(context))
                //automatically cancels the notification when the notification is clicked
                .setAutoCancel(true);

        //if the build version is greater than than OREO
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            //set the notification's priority to PRIORITY_HIGH.
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        if (notificationManager != null) {
            notificationManager.notify(LICENSE_NOTIFICATION_ID,builder.build());
        }

    }

    /*
    helper method called contentIntent with a single parameter for a Context. It
    should return a PendingIntent. This method will create the pending intent which will trigger when
    the notification is pressed. This pending intent should open up the ActivateLicense.
     */

    private static PendingIntent contentIntent(Context context){
        //Create an intent that opens up the ActivateLicense
        Intent intent = new Intent(context,ActivateLicense.class);
        /*
        Create a PendingIntent using getActivity that:
        Take the context passed in as a parameter
        Takes an unique integer ID for the pending intent
        Takes the intent to open the ActivateLicense
        FLAG_UPDATE_CURRENT, if the intent is created again, keep the intent but update the data
         */
        return PendingIntent.getActivity(context,LICENSE_PENDING_INTENT_ID,intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /*
    helper method called largeIcon which takes in a Context as a parameter and
    returns a Bitmap. This method is necessary to decode a bitmap needed for the notification.
     */

    private static Bitmap notificationIcon(Context context){
        //Get a Resources object from the context.
        Resources resources = context.getResources();
        //Create and return a bitmap using BitmapFactory.decodeResource
        Bitmap icon = BitmapFactory.decodeResource(resources, R.drawable.ic_menu_share);
        return  icon;
    }
}
