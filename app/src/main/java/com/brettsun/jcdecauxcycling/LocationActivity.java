package com.brettsun.jcdecauxcycling;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import org.json.JSONArray;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Location selection activity. Loads a list of cities to select.
 * Shows retry button if network request failed to get contracts.
 *
 * Location services adapted from https://developer.android.com/training/location/retrieve-current.html
 */
public class LocationActivity extends ListActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener{

    private static final String TAG = "LocationActivity";

    private final Context mContext = this;
    private View mSelectedProgressBar = null;
    private LocationClient mLocationClient = null;
    private boolean mLocationServicesActivated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_activity_location);
        }

        // Check that Google Play Services is installed for maps and location data
        PlayServicesUtils.checkPlayServices(this);
        mLocationClient = new LocationClient(this, this, this);

        // Load contracts and set up list view
        requestContractsJson();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLocationClient.connect();
    }

    @Override
    protected void onStop() {
        mLocationServicesActivated = false;
        mLocationClient.disconnect();
        super.onStop();
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
                    List<Contract> contracts = Contract.parseArrayFromJson(response);
                    setUpListView(contracts);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Failed to receive JSON response; hide progress bar and inflate retry button
                    if (error.networkResponse != null) {
                        Log.i(TAG, "JSON request for stations failed. Received status code: " + error.networkResponse.statusCode);
                    } else {
                        Log.i(TAG, "JSON request for stations failed. With message: " + error.getMessage());
                    }
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

    private void setUpListView(List<Contract> contracts) {
        // Sort the contracts received so there's some semblance of order here
        Collections.sort(contracts, new Comparator<Contract>() {
            @Override
            public int compare(Contract lhs, Contract rhs) {
                // Alphabetically order first by country and then city name
                int countryComparison = lhs.getCountry().compareToIgnoreCase(rhs.getCountry());
                if (countryComparison != 0) {
                    return countryComparison;
                } else {
                    return lhs.getName().compareToIgnoreCase(rhs.getName());
                }
            }
        });
        ContractAdapter contractAdapter = new ContractAdapter(mContext, contracts);
        setListAdapter(contractAdapter);
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
                    // Got JSON response, launch the station activity
                    Log.i(TAG, "JSON request for stations succeeded");

                    Intent stationActivityIntent = new Intent(mContext, StationActivity.class);
                    // Send the station name and current coordinates as extras
                    stationActivityIntent.putExtra(StationActivity.STATIONS_NAME_EXTRA, contractName);
                    addCurrentCoordinatesToIntent(stationActivityIntent);
                    // But for the JSON response, there is an upper limit on how large the Extras can be
                    // in an intent (~1MB). Thus, given that some of the station responses are fairly large,
                    // we instead use a singleton data handler to transfer this data to the station activity.
                    StationDataHandler.getInstance().setDataFromJsonResponse(response);

                    Log.i(TAG, "Starting station activity...");
                    startActivity(stationActivityIntent);
                    hideSelectedProgressBar();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Failed to receive JSON response, tell user and reset state
                    if (error.networkResponse != null) {
                        Log.i(TAG, "JSON request for stations failed. Received status code: " + error.networkResponse.statusCode);
                    } else {
                        Log.i(TAG, "JSON request for stations failed. With message: " + error.getMessage());
                    }

                    Toast toast = Toast.makeText(mContext, R.string.network_failed_text, Toast.LENGTH_SHORT);
                    toast.show();
                    hideSelectedProgressBar();
                }
        });

        volleyHandler.addToRequestQueue(jsonRequest);
        Log.i(TAG, "Added JSON request for station: " + contractName + " to volley");
    }

    private void addCurrentCoordinatesToIntent(Intent intent) {
        if (mLocationServicesActivated) {
            Location currentLocation = mLocationClient.getLastLocation();
            intent.putExtra(StationActivity.CURRENT_LAT_EXTRA, currentLocation.getLatitude());
            intent.putExtra(StationActivity.CURRENT_LNG_EXTRA, currentLocation.getLongitude());
        }
    }

    private void hideSelectedProgressBar() {
        if (mSelectedProgressBar != null) {
            mSelectedProgressBar.setVisibility(View.GONE);
            mSelectedProgressBar = null;
        }
    }

    /********* For Location Services *************/
    // Called by Location Services when connected
    @Override
    public void onConnected(Bundle dataBundle) {
        mLocationServicesActivated = true;
    }

    // Called by Location Services when disconnected due to an error
    @Override
    public void onDisconnected() {
        mLocationServicesActivated = false;
    }

    // Called by Location Services if the attempt to Location Services fails.
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, PlayServicesUtils.PLAY_SERVICES_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                // Thrown if Google Play services canceled the original PendingIntent
                // Log the error
                e.printStackTrace();
            }
        } else {
            // For the sake of the demo, instead of showing a dialog, let's just log that there's no resolution
            Log.e(TAG, "Location Services failed to connect without a resolvable error");
        }
    }

}
