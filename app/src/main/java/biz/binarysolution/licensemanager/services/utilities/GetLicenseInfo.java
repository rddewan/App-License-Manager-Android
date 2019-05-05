package biz.binarysolution.licensemanager.services.utilities;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import biz.binarysolution.licensemanager.MainActivity;
import biz.binarysolution.licensemanager.R;
import biz.binarysolution.licensemanager.Utils;
import biz.binarysolution.licensemanager.VolleySingleton;
import biz.binarysolution.licensemanager.license.CheckLicenseValidity;
import biz.binarysolution.licensemanager.settings.AppSettingPreferences;

public class GetLicenseInfo {
    private static final String TAG = GetLicenseInfo.class.getSimpleName();
    Context mContext;
    JSONObject jsonObject;
    AppSettingPreferences settingPreferences;
    private String imei,key,app_id = "1";

    public GetLicenseInfo(Context context) {
        mContext = context;
        settingPreferences = new AppSettingPreferences();
        key = settingPreferences.getLicenseKey(mContext);
        imei = settingPreferences.getMachineId(context);
    }

    public void getLicense(){
        if (!key.equals("")){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.WEB_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        jsonObject = new JSONObject(response);
                        System.out.println(jsonObject.get("imei"));
                        System.out.println(jsonObject.get("key"));
                        System.out.println(jsonObject.get("name"));
                        System.out.println(jsonObject.get("email"));
                        System.out.println(jsonObject.get("registration_date"));
                        System.out.println(jsonObject.get("activation_date"));
                        System.out.println(jsonObject.get("expiration_date"));
                        System.out.println(jsonObject.get("validity"));
                        System.out.println(jsonObject.get("remaining_days"));
                        System.out.println(jsonObject.get("last_sync"));
                        System.out.println(jsonObject.get("last_sync_time"));
                        System.out.println(jsonObject.get("van_sales"));
                        System.out.println(jsonObject.get("van_sales"));
                        //save data
                        String expiry_date = jsonObject.get("expiration_date").toString();
                        String license_key = jsonObject.get("key").toString();
                        int remaining_days = Integer.parseInt(jsonObject.get("remaining_days").toString());
                        settingPreferences.saveExpiryDate(mContext,expiry_date);
                        settingPreferences.saveLicenseKey(mContext,license_key);
                        settingPreferences.saveRemainingDays(mContext,remaining_days);

                        Log.e(TAG, "License Synchronize successful.");

                    } catch (JSONException e) {
                        Log.e(TAG,e.getMessage());
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String json = null;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        switch (response.statusCode) {
                            case 204:
                                Log.e(TAG, mContext.getString(R.string
                                        .error_204));
                                break;
                            case 401:
                                Log.e(TAG, mContext.getString(R.string
                                        .error_401));
                                break;
                            case 500:
                                Log.e(TAG, mContext.getString(R.string
                                        .error_500));
                                break;
                            case 400:
                                Log.e(TAG, mContext.getString(R.string
                                        .error_400));
                                break;
                            case 411:
                                Log.e(TAG, mContext.getString(R.string
                                        .error_411));
                                break;
                            case 404:
                                json = new String(response.data);
                                Log.e(TAG, json);
                                break;
                            case 416:
                                json = new String(response.data);
                                json = trimMessage(json, "error");
                                Log.e(TAG, json);
                                break;
                            case 406:
                                json = new String(response.data);
                                try {
                                    //save data
                                    jsonObject = new JSONObject(json);
                                    String expiry_date = jsonObject.get("expiration_date").toString();
                                    String license_key = jsonObject.get("key").toString();
                                    int remaining_days = Integer.parseInt(jsonObject.get("remaining_days").toString());
                                    settingPreferences.saveExpiryDate(mContext,expiry_date);
                                    settingPreferences.saveLicenseKey(mContext,license_key);
                                    settingPreferences.saveRemainingDays(mContext,remaining_days);
                                    /*
                                    if license expired show notification
                                     */
                                    NotificationUtils.createNotification(mContext);

                                    Log.e(TAG, json);
                                } catch (JSONException e) {
                                    Log.e(TAG,e.getMessage());
                                }

                                break;
                            case 502:
                                Log.e(TAG, mContext.getString(R.string
                                        .error_502));
                                break;
                            case 503:
                                Log.e(TAG, mContext.getString(R.string
                                        .error_503));
                                break;
                            case 504:
                                Log.e(TAG, mContext.getString(R.string
                                        .error_504));
                                break;

                        }
                    }
                    else if (error instanceof TimeoutError) {
                        Log.e(TAG, "Oops. Timeout error!");

                    } else {
                        Log.e("Error", "No Response From Server");

                    }

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("Accept", "application/json; charset=UTF-8");
                    return params;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String,String> params = new HashMap<>();
                    params.put("imei",imei);
                    params.put("key",key);
                    params.put("app_id",app_id);

                    return params;
                }
            };
            VolleySingleton.getInstance(mContext).addRequestQueue(stringRequest);
        }
        else {
            Log.e(TAG,"Key is missing.");
        }

    }

    /*
    trim json message
    */
    private String trimMessage(String json, String key) {
        String trimmedString = null;

        try {
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return trimmedString;
    }


}
