package org.justcards.android.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;

import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.justcards.android.R;

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

            new LovelyStandardDialog(mContext)
                    .setTopColorRes(R.color.red)
                    .setButtonsColorRes(R.color.colorAccent)
                    .setTitle("Oops! Network failure!!")
                    .setIcon(R.drawable.ic_network_check_36dp)
                    .setMessage("We are unable to connect to our servers. Please check network connection!")
                    .setPositiveButton(R.string.msg_okay, v -> mContext.startActivity(new Intent(Settings.ACTION_SETTINGS)))
                    .show();

        }

        return result;
    }
}