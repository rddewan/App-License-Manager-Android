package biz.binarysolution.licensemanager.license;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import biz.binarysolution.licensemanager.settings.AppSettingPreferences;

public class CheckLicenseValidity {
    private static final String TAG = CheckLicenseValidity.class.getSimpleName();
    //milliseconds in a day
    private final long ONE_DAY = 24 * 60 * 60 * 1000;
    private Context context;
    private long days;
    private long diff;
    AppSettingPreferences settingPreferences;


    public CheckLicenseValidity(Context context){
        this.context = context;
        settingPreferences = new AppSettingPreferences();

    }

    public long CheckExpiryDate(){
        Date expiryDate = new Date();
        Date now = new Date();
        //get expiry date from shared preferences
        String expiry_date = settingPreferences.getExpiryDate(context);
        //convert date to date and time
        expiry_date = expiry_date + " 00:00:00";
        //format date with date formatter with given pattern
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
        //convert date to string
        String current_date = simpleDateFormat.format(now);

        try {
            //parse the string to date
            expiryDate = simpleDateFormat.parse(expiry_date);
            now = simpleDateFormat.parse(current_date);

            //get the difference between expiry date and current date in milli sec
            diff = expiryDate.getTime() - now.getTime() + ONE_DAY;
            //get no of days from milli sec
            days = diff / ONE_DAY;

        } catch (ParseException e) {
            Log.e(TAG,e.getMessage());
        }

        return days;
    }
}
