package com.brettsun.jcdecauxcycling;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Station {

    private static final String TAG = "Station";
    private static final String NAME_KEY = "name";
    private static final String ADDRESS_KEY = "address";
    private static final String STATUS_KEY = "status";
    private static final String AVAILABLE_BIKES_KEY = "available_bikes";
    private static final String AVAILABLE_STANDS_KEY = "available_bike_stands";
    private static final String COORDINATES_KEY = "position";
    private static final String COORDINATES_LAT_KEY = "lat";
    private static final String COORDINATES_LNG_KEY = "lng";

    private String mName;
    private String mAddress;
    private String mStatus;
    private int mAvailableBikes;
    private int mAvailableBikeStands;
    private LatLng mCoordinates;

    public String getName() { return mName; }
    public String getAddress() { return mAddress; }
    public String getStatus() { return mStatus; }
    public int getAvailableBikes() { return mAvailableBikes; }
    public int getAvailableBikeStands() { return mAvailableBikeStands; }
    public LatLng getCoordinates() { return mCoordinates; }

    // Parse a list of stations from a given JSONArray in the expected format given from the API
    static ArrayList<Station> parseFromJson(JSONArray jsonArray) {
        ArrayList<Station> stationList = new ArrayList<Station>();
        for (int ii = 0; ii < jsonArray.length(); ++ii) {
            try {
                JSONObject stationJson = jsonArray.getJSONObject(ii);

                // In case any of the strings have special letters
                // From http://stackoverflow.com/questions/9069799/android-json-charset-utf-8-problems
                String name = new String(stationJson.getString(NAME_KEY).getBytes("ISO-8859-1"), "UTF-8");
                String address = new String(stationJson.getString(ADDRESS_KEY).getBytes("ISO-8859-1"), "UTF-8");
                String status = new String(stationJson.getString(STATUS_KEY).getBytes("ISO-8859-1"), "UTF-8");
                int availableBikes = stationJson.getInt(AVAILABLE_BIKES_KEY);
                int availableBikeStands = stationJson.getInt(AVAILABLE_STANDS_KEY);

                JSONObject coordinatesJson = stationJson.getJSONObject(COORDINATES_KEY);
                LatLng coordinates = new LatLng(coordinatesJson.getDouble(COORDINATES_LAT_KEY),
                                                    coordinatesJson.getDouble(COORDINATES_LNG_KEY));

                Station station = new Station(name, address, status, availableBikes, availableBikeStands, coordinates);
                stationList.add(station);
            } catch (Exception ex) {
                Log.e(TAG, "Station parsing failed at array index: " + ii + " due to: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        return stationList;
    }

    // Stations must be created by parsing through the JSON returned from the API
    private Station(String name, String address, String status, int availableBikes,
                        int availableBikeStands, LatLng coordinates) {
        mName = name;
        mAddress = address;
        mStatus = status;
        mAvailableBikes = availableBikes;
        mAvailableBikeStands = availableBikeStands;
        mCoordinates = coordinates;
    }

}
