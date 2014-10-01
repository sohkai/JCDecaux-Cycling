package com.brettsun.jcdecauxcycling;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


/**
 * Station selection activity.
 *
 * Originally this was suppose to request the details of each station independently from each other
 * (ie. load station 1, then 2, then etc...) so we could load only a few at a time and keep loading
 * the rest with "endless" scrolling as the user went down the list.
 *
 * However, I made the incorrect assumption that the API started all stations numbers for any contract from
 * 1 and then kept incrementing by 1. It appears, for whatever reason, that Paris' contract doesn't
 * follow this and so I've had to make do with loading the entire station JSON and keeping it in memory.
 */
public class StationActivity extends ListActivity {

    static final String STATIONS_JSON_EXTRA = "com.brettsun.jcdecauxcycling.StationsJSON";

    private static final String TAG = "StationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);

        // Parse stations JSON that was sent with the intent
        Intent activityIntent = getIntent();
        if (activityIntent.hasExtra(STATIONS_JSON_EXTRA)) {
            String responseString = getIntent().getStringExtra(STATIONS_JSON_EXTRA);
            ArrayList<Station> stations = null;
            try {
                JSONArray responseJson = new JSONArray(responseString);
                stations = Station.parseFromJson(responseJson);
            } catch (JSONException jsonex) {
                Log.e(TAG, "Could not parse station JSON string into a JsonArray due to: " + jsonex.getMessage());
                stations = new ArrayList<Station>(0);
            }
            StationAdapter stationAdapter = new StationAdapter(this, stations.toArray(new Station[stations.size()]));
            setListAdapter(stationAdapter);
        }
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        Log.i(TAG, "Station at position " + position + " with id " + id + " clicked");
        // Disable clicks on ListView to prevent trying to select two stations
        listView.setFocusable(false);

        // Open map activity for station
        Station selectedStation = (Station) listView.getItemAtPosition(position);
        LatLng stationCoordinates = selectedStation.getCoordinates();
        Intent mapIntent = new Intent(this, StationMapActivity.class);
        mapIntent.putExtra(StationMapActivity.STATION_NAME_EXTRA, selectedStation.getName());
        mapIntent.putExtra(StationMapActivity.STATION_LAT_EXTRA, stationCoordinates.latitude);
        mapIntent.putExtra(StationMapActivity.STATION_LNG_EXTRA, stationCoordinates.longitude);
        Log.i(TAG, "Starting station map activity...");
        startActivity(mapIntent);
    }

}
