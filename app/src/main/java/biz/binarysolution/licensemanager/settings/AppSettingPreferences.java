package biz.binarysolution.licensemanager.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSettingPreferences {
    private static final String PREFS_NAME = "LICENSE_MANAGER_PREFS";
    private static final String LICENSE_KEY = "license_key";
    private static final String EXPIRY_DATE = "expiry_date";
    private static final String REMAINING_DAYS = "remaining_days";

    public AppSettingPreferences(){

    }

    public void saveLicenseKey(Context context,int data ){
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        sharedPreferences = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(LICENSE_KEY,data);
        editor.apply();
    }

    public void saveExpiryDate(Context context,String data ){
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        sharedPreferences = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(EXPIRY_DATE,data);
        editor.apply();
    }

    public void saveRemainingDays(Context context,int data ){
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        sharedPreferences = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(REMAINING_DAYS,data);
        editor.apply();
    }

    public int getLicenseKey(Context context){
        SharedPreferences sharedPreferences;
        int data;
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        data = sharedPreferences.getInt(LICENSE_KEY, 0);
        return data;
    }

    public String getExpiryDate(Context context){
        SharedPreferences sharedPreferences;
        String data;
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        data = sharedPreferences.getString(EXPIRY_DATE, "");
        return data;
    }

    public int getRemainingDays(Context context){
        SharedPreferences sharedPreferences;
        int data;
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        data = sharedPreferences.getInt(REMAINING_DAYS, 0);
        return data;
    }
}
