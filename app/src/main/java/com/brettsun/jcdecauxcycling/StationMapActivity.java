package com.brettsun.jcdecauxcycling;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Map activity to show location of a station.
 * A dialog will prompt for directions if the marker's info window is clicked.
 */
public class StationMapActivity extends Activity implements MapsDirectionDialog.MapsDirectionDialogListener {

    private final static String TAG = "StationMapActivity";
    private final static String DIRECTIONS_URI = "http://maps.google.com/maps?daddr=";
    private final static String DIRECTIONS_DIALOG_TAG = "com.brett.sun.jcdecauxcycling.DirectionsDialog";
    private final static float DEFAULT_ZOOM = 14;

    static String STATION_NAME_EXTRA = "com.brettsun.jcdecauxcycling.StationName";
    static String STATION_LAT_EXTRA = "com.brettsun.jcdecauxcycling.StationLat";
    static String STATION_LNG_EXTRA = "com.brettsun.jcdecauxcycling.StationLng";

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLng mStationCoordinates = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_map);

        // Get station if available
        Intent activityIntent = getIntent();
        if (activityIntent.hasExtra(STATION_LAT_EXTRA) && activityIntent.hasExtra(STATION_LNG_EXTRA)) {
            double stationLat = activityIntent.getDoubleExtra(STATION_LAT_EXTRA, 0);
            double stationLng = activityIntent.getDoubleExtra(STATION_LNG_EXTRA, 0);
            mStationCoordinates = new LatLng(stationLat, stationLng);
        }

        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public void onGetDirectionsClick(DialogFragment dialog) {
        dialog.dismiss();
        getDirections();
    }

    private void getDirections() {
        Log.i(TAG, "Opening intent switcher for maps to get directions to station");
        String directionsUriString = DIRECTIONS_URI + Double.toString(mStationCoordinates.latitude) +
                                        "," + Double.toString(mStationCoordinates.longitude);
        Intent directionsIntent = new Intent(android.content.Intent.ACTION_VIEW,
                                        Uri.parse(directionsUriString));
        startActivity(directionsIntent);
    }

    /**
     * Adapted from sample Map Activity code
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setStationMarker()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link MapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        Log.i(TAG, "Setting up map...");
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the MapFragment.
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                Log.i(TAG, "Map obtained from MapFragment");
                mMap.setMyLocationEnabled(true);

                // Only zoom camera and set up markers if we have the station's coordinates
                if (mStationCoordinates != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mStationCoordinates, DEFAULT_ZOOM));
                    setStationMarker();
                } else {
                    Toast toast = Toast.makeText(this, R.string.maps_no_station, Toast.LENGTH_LONG);
                    toast.show();
                }

                Log.i(TAG, "Map set up");
            }
        }
    }

    /**
     * Add station marker on the map
     * This should only be called once and when we are sure that {@link #mMap} and
     * {@link #mStationCoordinates} are not null.
     */
    private void setStationMarker() {
        Intent activityIntent = getIntent();
        String stationName = activityIntent.getStringExtra(STATION_NAME_EXTRA);
        if (stationName == null) {
            stationName = "Station";
        }
        Marker stationMarker = mMap.addMarker(new MarkerOptions().position(mStationCoordinates)
                                                                 .title(stationName));

        // Prompt for directions if info window clicked
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                DialogFragment directionsDialog = new MapsDirectionDialog();
                directionsDialog.show(getFragmentManager(), DIRECTIONS_DIALOG_TAG);
            }
        });
        stationMarker.showInfoWindow();
        Log.i(TAG, "Station marker added");
    }

}
