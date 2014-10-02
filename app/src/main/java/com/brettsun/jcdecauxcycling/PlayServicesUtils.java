package com.brettsun.jcdecauxcycling;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Some utility functions for checking that play services exist on the device
 * Mostly as seen on https://developer.android.com/google/gcm/client.html
 */
public class PlayServicesUtils {

    private static final String TAG = "PlayServicesUtils";

    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static boolean checkPlayServices(Activity activity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device does not support play services.");
            }
            return false;
        }
        return true;
    }

}
