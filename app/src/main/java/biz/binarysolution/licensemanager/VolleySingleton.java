package biz.binarysolution.licensemanager;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Richard-IT on 01/12/2017.
 */

public class VolleySingleton {
    private static VolleySingleton sInstance =  null;
    private RequestQueue mRequestQueus;


    private VolleySingleton(Context context){
        mRequestQueus = getRequestQueus();

    }

    public static synchronized VolleySingleton getInstance(Context context){
        //check if instance is null
        if(sInstance == null){
            sInstance = new VolleySingleton(context);
        }
        return sInstance;
    }

    public RequestQueue getRequestQueus(){

        if(mRequestQueus == null){
            mRequestQueus = Volley.newRequestQueue(MyApplication.getAppContext());
        }
        return  mRequestQueus;
    }
    public  void addRequestQueue(Request request){
        mRequestQueus.add(request);

    }
}
