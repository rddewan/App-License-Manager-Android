package biz.binarysolution.licensemanager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.TestLooperManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;

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

import biz.binarysolution.licensemanager.license.CheckLicenseValidity;
import biz.binarysolution.licensemanager.license.License;
import biz.binarysolution.licensemanager.settings.AppSettingPreferences;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private String imei,key,app_id = "1";
    Activity context = this;
    AlertDialog.Builder builder;
    JSONObject jsonObject;
    AppSettingPreferences settingPreferences;
    TextView txtMachineId,txtLicenseKey,txtExpiryDate,txtRemainingDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //
        builder = new AlertDialog.Builder(context,R.style.AlertDialogStyle);
        settingPreferences = new AppSettingPreferences();
        key = String.valueOf(settingPreferences.getLicenseKey(context));

        //
        txtMachineId = findViewById(R.id.txtMachineId);
        txtLicenseKey = findViewById(R.id.txtLicenseKey);
        txtExpiryDate = findViewById(R.id.txtExpiryDate);
        txtRemainingDays = findViewById(R.id.txtRemainingDays);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //request permission
        requestPermission();
        //check license validity
        CheckValidity();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_license) {
            Intent intent = new Intent(getApplicationContext(), License.class);
            startActivity(intent);

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                            getLicense();

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
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
        key = String.valueOf(settingPreferences.getLicenseKey(context));
        if (!key.equals("0")){
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
                        txtMachineId.setText(imei);
                        txtExpiryDate.setText(expiry_date);
                        txtLicenseKey.setText(String.valueOf(license_key));
                        txtRemainingDays.setText(String.valueOf(remaining_days));

                        //check the license validity
                        CheckValidity();

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
                                Log.e(TAG, context.getString(R.string
                                        .error_204));
                                break;
                            case 401:
                                Log.e(TAG, context.getString(R.string
                                        .error_401));
                                break;
                            case 500:
                                Log.e(TAG, context.getString(R.string
                                        .error_500));
                                break;
                            case 400:
                                Log.e(TAG, context.getString(R.string
                                        .error_400));
                                break;
                            case 411:
                                Log.e(TAG, context.getString(R.string
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
                                            MainActivity.this.finish();
                                        }
                                    });
                                    builder.show();
                                } catch (JSONException e) {
                                   Log.e(TAG,e.getMessage());
                                }

                                break;
                            case 502:
                                Log.e(TAG, context.getString(R.string
                                        .error_502));
                                break;
                            case 503:
                                Log.e(TAG, context.getString(R.string
                                        .error_503));
                                break;
                            case 504:
                                Log.e(TAG, context.getString(R.string
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
            VolleySingleton.getInstance(context).addRequestQueue(stringRequest);
        }
        else {
            Log.e(TAG,"Key is missing.");
        }

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
    /*
    check the license validity
    if license is expired exit the app
     */
    private void CheckValidity(){
        key = String.valueOf(settingPreferences.getLicenseKey(context));
        String expiry_date = settingPreferences.getExpiryDate(context);
        if (!expiry_date.equals("")){
            CheckLicenseValidity licenseValidity = new CheckLicenseValidity(context);
            long remainingDay = licenseValidity.CheckExpiryDate();
            if (remainingDay <= 0){
                finishActivity();
            }
        }
        else {
            showActivation();
        }
    }

    private void finishActivity(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context, R.style.AlertDialogStyle);
        builder.setMessage("Sorry! Your App Has Expired!.Please Contact Us If You Would Like To Buy A Full Version Of This App.");
        builder.setCancelable(false);
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                MainActivity.this.finish();


            }
        });
        builder.setNegativeButton("Activate", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(context, License.class);
                context.startActivity(intent);

            }
        });
        builder.show();
    }

    private void showActivation(){
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(context, R.style.AlertDialogStyle);
        alert.setMessage("Please Activate Your App.");
        alert.setCancelable(false);
        alert.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                MainActivity.this.finish();
            }
        });
        alert.setNegativeButton("Activate", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), License.class);
                startActivity(intent);

            }
        });
        alert.show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //check permission
        requestPermission();
        //check validity
        CheckValidity();
    }
}
