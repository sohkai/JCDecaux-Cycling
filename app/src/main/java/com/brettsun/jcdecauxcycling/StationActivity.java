package com.brettsun.jcdecauxcycling;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Station selection activity.
 *
 * Originally this was suppose to request the details of each station independently from each other
 * (ie. load station 1, then 2, then etc...) so we could load only a few at a time and keep loading
 * the rest with "endless" scrolling as the user went down the list.
 *
 * However, my assumption that the API started all stations numbers for any contract from 1 and then kept
 * incrementing by 1 is apparently incorrect. It appears, for whatever reason, that (at least) Paris' contract doesn't
 * follow this and so I've had to make do with loading the entire station JSON and keeping it in memory.
 */
public class StationActivity extends ListActivity {

    static final String STATIONS_NAME_EXTRA = "com.brettsun.jcdecauxcycling.StationsName";
    static final String CURRENT_LAT_EXTRA = "com.brettsun.jcdecauxcycling.CurrentLat";
    static final String CURRENT_LNG_EXTRA = "com.brettsun.jcdecauxcycling.CurrentLng";

    private static final String TAG = "StationActivity";
    private static final String ACTION_BAR_TITLE_BASE = "Stations: ";

    private boolean mListItemClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);

        Intent activityIntent = getIntent();
        ActionBar actionBar = getActionBar();
        if (actionBar != null && activityIntent.hasExtra(STATIONS_NAME_EXTRA)) {
            actionBar.setTitle(ACTION_BAR_TITLE_BASE + activityIntent.getStringExtra(STATIONS_NAME_EXTRA));
        }

        // Set up list view with the data available from the handler
        List<Station> stations = StationDataHandler.getInstance().getData();
        setUpListView(stations);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        if (mListItemClicked) {
            // Setting the focusable traits to false does not seem to eliminate click events
            // in the list view so this is a workaround to discard extra click events
            // while the initial event is processing
            return;
        }

        mListItemClicked = true;
        Log.i(TAG, "Station at position " + position + " with id " + id + " clicked");

        // Open map activity for station
        Station selectedStation = (Station) listView.getItemAtPosition(position);
        LatLng stationCoordinates = selectedStation.getCoordinates();
        Intent mapIntent = new Intent(this, StationMapActivity.class);
        mapIntent.putExtra(StationMapActivity.STATION_NAME_EXTRA, selectedStation.getName());
        mapIntent.putExtra(StationMapActivity.STATION_LAT_EXTRA, stationCoordinates.latitude);
        mapIntent.putExtra(StationMapActivity.STATION_LNG_EXTRA, stationCoordinates.longitude);
        Log.i(TAG, "Starting station map activity...");
        startActivity(mapIntent);

        mListItemClicked = false;
    }

    private void setUpListView(List<Station> stations) {
        Intent activityIntent = getIntent();
        // If we can, sort the stations based on their proximity to the user
        if (activityIntent.hasExtra(CURRENT_LAT_EXTRA) && activityIntent.hasExtra(CURRENT_LNG_EXTRA)) {
            final double currentLat = activityIntent.getDoubleExtra(CURRENT_LAT_EXTRA, 0);
            final double currentLng = activityIntent.getDoubleExtra(CURRENT_LNG_EXTRA, 0);
            Collections.sort(stations, new Comparator<Station>() {
                @Override
                public int compare(Station lhs, Station rhs) {
                    LatLng lhsCoord = lhs.getCoordinates();
                    LatLng rhsCoord = rhs.getCoordinates();
                    double lhsDistance = calculateDistance(currentLat, currentLng,
                                            lhsCoord.latitude, lhsCoord.longitude);
                    double rhsDistance = calculateDistance(currentLat, currentLng,
                                            rhsCoord.latitude, rhsCoord.longitude);

                    return Double.compare(lhsDistance, rhsDistance);
                }
            });
        } else {
            // Otherwise sort it alphabetically
            Collections.sort(stations, new Comparator<Station>() {
                @Override
                public int compare(Station lhs, Station rhs) {
                    return lhs.getName().compareToIgnoreCase(rhs.getName());
                }
            });
        }

        StationAdapter stationAdapter = new StationAdapter(this, stations);
        setListAdapter(stationAdapter);
    }

    private double calculateDistance(double xLat, double xLng, double yLat, double yLng) {
        double latDelta = xLat - yLat;
        double lngDelta = xLng - yLng;
        return Math.sqrt((latDelta * latDelta) + (lngDelta * lngDelta));
    }

}
