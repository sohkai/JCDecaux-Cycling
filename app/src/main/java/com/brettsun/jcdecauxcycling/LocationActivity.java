package com.brettsun.jcdecauxcycling;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Location selection activity. Loads a list of cities to select.
 * Shows retry button if network request failed to get contracts.
 *
 * Notes: for now, locations are based on "contract_name" instead of something more
 * familiar to most users (ie. country -> city or similar). The API forces us to be
 * at this granularity unless we do some bookkeeping ourselves to know which cities have
 * which "station_number"s associated with each of them for the next activity.
 * Could be done, but not too interesting right now.
 */
public class LocationActivity extends Activity {

    private static final String TAG = "LocationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_selector);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_activity_location);
        }

        requestContractsJson();
    }

    private void requestContractsJson() {
        Log.i(TAG, "Requesting contracts JSON...");
        VolleyHandler volleyHandler = VolleyHandler.getInstance(this);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, NetworkUtils.CONTRACTS_ENDPOINT, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "JSON request for contracts succeeded");
                        // Got JSON response, parse it and inflate locations adapter
                        //TODO

                        // Change the views to the listview
                        ViewGroup rootLayout = (ViewGroup) findViewById(R.id.location_activity_root_container);
                        rootLayout.removeAllViews();
                        LayoutInflater inflater = getLayoutInflater();
                        inflater.inflate(R.layout.activity_location_base, rootLayout, true);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Failed to get JSON response; hide progress bar and inflate retry button
                Log.i(TAG, "JSON request for contracts failed. Received status code: " + error.networkResponse.statusCode);
                toggleNetworkActivityLayout(false);
            }
        });

        // Add this json request to the volley queue
        volleyHandler.addToRequestQueue(jsonRequest);
        Log.i(TAG, "Added JSON request for contracts to volley");
    }

    public void retryJsonRequest(View view) {
        Log.i(TAG, "Retrying JSON request...");
        toggleNetworkActivityLayout(true);
        requestContractsJson();
    }

    // Toggle between how the activity looks when it is trying to grab the json to support retries
    private void toggleNetworkActivityLayout(boolean requestingJson) {
        if (requestingJson) {
            findViewById(R.id.network_progress_bar).setVisibility(View.VISIBLE);
            findViewById(R.id.network_failed_text).setVisibility(View.GONE);
            findViewById(R.id.network_retry_button).setVisibility(View.GONE);
        } else {
            findViewById(R.id.network_progress_bar).setVisibility(View.GONE);
            findViewById(R.id.network_failed_text).setVisibility(View.VISIBLE);
            findViewById(R.id.network_retry_button).setVisibility(View.VISIBLE);
        }
    }

}
