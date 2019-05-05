package biz.binarysolution.licensemanager.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppSettingPreferences {
    private static final String MACHINE_ID = "machine_id";
    private static final String LICENSE_KEY = "license_key";
    private static final String EXPIRY_DATE = "expiry_date";
    private static final String REMAINING_DAYS = "remaining_days";

    public AppSettingPreferences(){

    }

    public void saveMachineId(Context context,String data ){
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        editor.putString(MACHINE_ID,data);
        editor.apply();
    }

    public void saveLicenseKey(Context context,String data ){
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        editor.putString(LICENSE_KEY,data);
        editor.apply();
    }

    public void saveExpiryDate(Context context,String data ){
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        editor.putString(EXPIRY_DATE,data);
        editor.apply();
    }

    public void saveRemainingDays(Context context,int data ){
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        editor.putInt(REMAINING_DAYS,data);
        editor.apply();
    }

    public String getMachineId(Context context){
        SharedPreferences sharedPreferences;
        String data;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        data = sharedPreferences.getString(MACHINE_ID, "");
        return data;
    }

    public String getLicenseKey(Context context){
        SharedPreferences sharedPreferences;
        String data;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        data = sharedPreferences.getString(LICENSE_KEY, "");
        return data;
    }

    public String getExpiryDate(Context context){
        SharedPreferences sharedPreferences;
        String data;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        data = sharedPreferences.getString(EXPIRY_DATE, "");
        return data;
    }

    public int getRemainingDays(Context context){
        SharedPreferences sharedPreferences;
        int data;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        data = sharedPreferences.getInt(REMAINING_DAYS, 0);
        return data;
    }
}
