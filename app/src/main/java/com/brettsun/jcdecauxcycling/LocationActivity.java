package com.brettsun.jcdecauxcycling;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Location selection activity. Loads a list of cities to select.
 * Shows retry button if network request failed to get contracts.
 *
 * Notes: for now, locations are based on "contract_name" instead of something more
 * familiar to most users (ie. country -> city or similar). The API forces us to be
 * at this granularity unless we do some bookkeeping ourselves to know which cities have
 * which "station_number"s associated with each of them for the next activity.
 * Could be done, but not too interesting right now.
 *
 * TODO: handle config changes nicely
 */
public class LocationActivity extends ListActivity {

    private static final String TAG = "LocationActivity";
    private final Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_activity_location);
        }

        requestContractsJson();
    }

    private void requestContractsJson() {
        Log.i(TAG, "Requesting contracts JSON...");
        VolleyHandler volleyHandler = VolleyHandler.getInstance(this);
        JsonArrayRequest jsonRequest = new JsonArrayRequest(NetworkUtils.contractsUrlWithParams(),
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    // Got JSON response, parse it and inflate locations adapter
                    Log.i(TAG, "JSON request for contracts succeeded");

                    ArrayList<Contract> contracts = Contract.parseFromJson(response);
                    ContractAdapter contractAdapter = new ContractAdapter(mContext, contracts.toArray(new Contract[contracts.size()]));
                    setListAdapter(contractAdapter);
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

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        //FIXME: launch activity instead
        Toast toast = Toast.makeText(this, "clicked position " + position + " and id " + id, Toast.LENGTH_LONG);
        toast.show();
    }

}
