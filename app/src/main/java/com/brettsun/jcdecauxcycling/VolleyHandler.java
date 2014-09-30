package com.brettsun.jcdecauxcycling;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Adapted from http://developer.android.com/training/volley/requestqueue.html
 * Utility singleton for accessing Volley
 */
public class VolleyHandler {
    private static VolleyHandler mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    private VolleyHandler(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized VolleyHandler getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyHandler(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
