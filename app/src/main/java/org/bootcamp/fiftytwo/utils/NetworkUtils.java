package org.bootcamp.fiftytwo.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by baphna on 11/26/2016.
 */
public class NetworkUtils {

    public static boolean isNetworkAvailable(final Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean result =  activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();

        if(!result){
            Log.e(Constants.TAG, "No network");

            SweetAlertDialog pDialog = new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Oops! Network failure!!");
            pDialog.setConfirmText("Okay");
            pDialog.setCancelable(true);
            pDialog.setContentText("We are unable to connect to our servers. Please check network connection!");

            pDialog.setConfirmClickListener(sDialog -> {
                mContext.startActivity(new Intent(Settings.ACTION_SETTINGS));
                sDialog.dismissWithAnimation();
            });
            pDialog.show();
        }

        return result;
    }
}