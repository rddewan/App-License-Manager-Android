package biz.binarysolution.licensemanager.license;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import biz.binarysolution.licensemanager.MainActivity;
import biz.binarysolution.licensemanager.R;
import biz.binarysolution.licensemanager.Utils;
import biz.binarysolution.licensemanager.VolleySingleton;
import biz.binarysolution.licensemanager.settings.AppSettingPreferences;

public class License extends AppCompatActivity {
    private static final String TAG = License.class.getSimpleName();
    private String imei,key,app_id = "1";

    Activity context = this;
    JSONObject jsonObject;
    EditText etxtIMEI,etxtLicense;
    ImageView btn_nav_back;
    ProgressDialog progressDialog;
    AppSettingPreferences settingPreferences;
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
        Toolbar toolbar = findViewById(R.id.menu_bar);
        setSupportActionBar(toolbar);
        //setting preferences
        builder = new AlertDialog.Builder(context,R.style.AlertDialogStyle);
        settingPreferences = new AppSettingPreferences();
        key = String.valueOf(settingPreferences.getLicenseKey(context));

        etxtIMEI = findViewById(R.id.txtIMEI);
        etxtLicense = findViewById(R.id.txtLicense);
        btn_nav_back = findViewById(R.id.btn_nav_back);
        etxtIMEI.setFocusable(false);
        etxtIMEI.setEnabled(false);
        etxtLicense.requestFocus();
        //set edit text license value if exist
        if (!key.equals("0")){
            etxtLicense.setText(key);
        }
        else{
            key = "";
        }
        //request permission
        requestPermission();

        //Initialize Progress Dialog properties
        progressDialog = new ProgressDialog(this, R.style.AlertDialogStyle);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLicense();
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        btn_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void requestPermission(){
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_PHONE_STATE)
                .withListener(new MultiplePermissionsListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()){
                            Log.e(TAG,"All permission are granted");
                            getIMEI();

                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();

                    }
                }).withErrorListener(new PermissionRequestErrorListener() {
            @Override
            public void onError(DexterError error) {
                Log.e(TAG,"Error occurred! ");
            }
        }).onSameThread()
                .check();
    }

    @SuppressLint("MissingPermission")
    private void getIMEI(){
        TelephonyManager telephonyManager =
                (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imei = telephonyManager.getDeviceId();
        etxtIMEI.setText(imei);

    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(License.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void getLicense(){
        progressDialog.show();
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
                    int license_key = Integer.parseInt(jsonObject.get("key").toString());
                    int remaining_days = Integer.parseInt(jsonObject.get("remaining_days").toString());
                    settingPreferences.saveExpiryDate(context,expiry_date);
                    settingPreferences.saveLicenseKey(context,license_key);
                    settingPreferences.saveRemainingDays(context,remaining_days);
                    //dismiss progress dialog
                    progressDialog.dismiss();
                    //
                    builder.setTitle("Great!");
                    builder.setCancelable(false);
                    builder.setMessage("License registered successfully.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();

                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Log.e(TAG,e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                String json = null;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    switch (response.statusCode) {
                        case 204:
                            Log.e(TAG, getApplicationContext().getString(R.string
                                    .error_204));
                            break;
                        case 401:
                            Log.e(TAG, getApplicationContext().getString(R.string
                                    .error_401));
                            break;
                        case 500:
                            Log.e(TAG, getApplicationContext().getString(R.string
                                    .error_500));
                            break;
                        case 400:
                            Log.e(TAG, getApplicationContext().getString(R.string
                                    .error_400));
                            break;
                        case 411:
                            Log.e(TAG, getApplicationContext().getString(R.string
                                    .error_411));
                            break;
                        case 404:
                            json = new String(response.data);
                            //json = trimMessage(json, "message");
                            builder.setTitle("error!");
                            builder.setCancelable(false);
                            builder.setMessage(json);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                            Log.e(TAG, json);
                            break;
                        case 416:
                            json = new String(response.data);
                            json = trimMessage(json, "error");
                            builder.setTitle("error! field required");
                            builder.setCancelable(false);
                            builder.setMessage(json);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                            break;
                        case 406:
                            json = new String(response.data);

                            try {
                                //save data
                                jsonObject = new JSONObject(json);
                                String expiry_date = jsonObject.get("expiration_date").toString();
                                int license_key = Integer.parseInt(jsonObject.get("key").toString());
                                int remaining_days = Integer.parseInt(jsonObject.get("remaining_days").toString());
                                settingPreferences.saveExpiryDate(context,expiry_date);
                                settingPreferences.saveLicenseKey(context,license_key);
                                settingPreferences.saveRemainingDays(context,remaining_days);
                                //show dialog
                                builder.setMessage(context.getString(R.string.error_406));
                                builder.setCancelable(false);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                            } catch (JSONException e) {
                                Log.e(TAG,e.getMessage());
                            }
                            break;
                        case 502:
                            Log.e(TAG, getApplicationContext().getString(R.string
                                    .error_502));
                            break;
                        case 503:
                            Log.e(TAG, getApplicationContext().getString(R.string
                                    .error_503));
                            break;
                        case 504:
                            Log.e(TAG, getApplicationContext().getString(R.string
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
                key = etxtLicense.getText().toString();
                Map<String,String> params = new HashMap<>();
                params.put("imei",imei);
                params.put("key", key);
                params.put("app_id",app_id);

                return params;
            }
        };
        VolleySingleton.getInstance(context).addRequestQueue(stringRequest);
    }

    /*
    trim json message
    */
    public String trimMessage(String json, String key) {
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

    @Override
    protected void onStop() {
        super.onStop();

        if (progressDialog != null)
            progressDialog.dismiss();
    }

}
