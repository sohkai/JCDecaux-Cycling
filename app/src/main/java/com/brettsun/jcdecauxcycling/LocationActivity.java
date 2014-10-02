package com.brettsun.jcdecauxcycling;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
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
 */
public class LocationActivity extends ListActivity {

    private static final String TAG = "LocationActivity";

    private final Context mContext = this;
    private View mSelectedProgressBar = null;

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

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        if (mSelectedProgressBar != null) {
            // Setting the focusable traits to false does not seem to eliminate click events
            // in the list view so this is a workaround to discard extra click events
            // while the initial event is processing
            return;
        }

        Log.i(TAG, "Contract at position " + position + " with id " + id + " clicked");

        // Show progressbar to let user know we're loading stations
        mSelectedProgressBar = view.findViewById(R.id.contract_progress_bar);
        mSelectedProgressBar.setVisibility(View.VISIBLE);

        // Make network request for stations associated with location and launch activity
        // if json is received
        String contractName = ((Contract) listView.getItemAtPosition(position)).getName();
        requestStationsJson(contractName);
    }

    /********* For contracts api *************/
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
                    // Failed to receive JSON response; hide progress bar and inflate retry button
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

    /********* For stations api *************/
    private void requestStationsJson(final String contractName) {
        Log.i(TAG, "Requesting stations JSON for: " + contractName);
        VolleyHandler volleyHandler = VolleyHandler.getInstance(this);
        JsonArrayRequest jsonRequest = new JsonArrayRequest(NetworkUtils.stationsUrlWithParams(contractName),
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    // Got JSON response, launch the station activity and send it the station JSON
                    Log.i(TAG, "JSON request for stations succeeded");

                    Intent stationActivityIntent = new Intent(mContext, StationActivity.class);
                    // Must send the response as a string for the extra
                    stationActivityIntent.putExtra(StationActivity.STATIONS_JSON_EXTRA, response.toString());
                    stationActivityIntent.putExtra(StationActivity.STATIONS_NAME_EXTRA, contractName);
                    Log.i(TAG, "Starting station activity...");
                    startActivity(stationActivityIntent);

                    hideSelectedProgressBar();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Failed to receive JSON response, tell user and reset state
                    Log.i(TAG, "JSON request for stations failed. Received status code: " + error.networkResponse.statusCode);
                    Toast toast = Toast.makeText(mContext, R.string.network_failed_text, Toast.LENGTH_SHORT);
                    toast.show();
                    hideSelectedProgressBar();
                }
        });

        volleyHandler.addToRequestQueue(jsonRequest);
        Log.i(TAG, "Added JSON request for station: " + contractName + " to volley");
    }

    private void hideSelectedProgressBar() {
        if (mSelectedProgressBar != null) {
            mSelectedProgressBar.setVisibility(View.GONE);
            mSelectedProgressBar = null;
        }
    }

}
