package com.brettsun.jcdecauxcycling;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Station {

    private static final String TAG = "Station";
    private static final String NAME_KEY = "name";
    private static final String ADDRESS_KEY = "address";
    private static final String STATUS_KEY = "status";
    private static final String AVAILABLE_BIKES_KEY = "available_bikes";
    private static final String TOTAL_STANDS_KEY = "bike_stands";
    private static final String COORDINATES_KEY = "position";
    private static final String COORDINATES_LAT_KEY = "lat";
    private static final String COORDINATES_LNG_KEY = "lng";

    private String mName;
    private String mAddress;
    private String mStatus;
    private int mAvailableBikes;
    private int mTotalBikeStands;
    private LatLng mCoordinates;

    public String getName() { return mName; }
    public String getAddress() { return mAddress; }
    public String getStatus() { return mStatus; }
    public int getAvailableBikes() { return mAvailableBikes; }
    public int getTotalBikeStands() { return mTotalBikeStands; }
    public LatLng getCoordinates() { return mCoordinates; }

    // Parse a list of stations from a given JSONArray in the expected format given from the API
    static List<Station> parseArrayFromJson(JSONArray jsonArray) {
        ArrayList<Station> stationList = new ArrayList<Station>();
        for (int ii = 0; ii < jsonArray.length(); ++ii) {
            try {
                JSONObject stationJson = jsonArray.getJSONObject(ii);

                // In case any of the strings from the json have special letters
                String name = StringUtils.convertToUTF8(stationJson.getString(NAME_KEY));
                String address = StringUtils.convertToUTF8(stationJson.getString(ADDRESS_KEY));
                String status = StringUtils.convertToUTF8(stationJson.getString(STATUS_KEY));
                int availableBikes = stationJson.getInt(AVAILABLE_BIKES_KEY);
                int totalBikeStands = stationJson.getInt(TOTAL_STANDS_KEY);

                // Process the strings so they're prettier
                name = StringUtils.capitalizeFirstLetter(StringUtils.convertWordSeperatorToSpace(
                        StringUtils.removeNumericPrefixFromString(name)));
                address = StringUtils.capitalizeFirstLetter(StringUtils.convertWordSeperatorToSpace(
                        StringUtils.removeNumericPrefixFromString(address)));
                status = StringUtils.capitalizeFirstLetter(status);

                JSONObject coordinatesJson = stationJson.getJSONObject(COORDINATES_KEY);
                LatLng coordinates = new LatLng(coordinatesJson.getDouble(COORDINATES_LAT_KEY),
                                                    coordinatesJson.getDouble(COORDINATES_LNG_KEY));

                Station station = new Station(name, address, status, availableBikes, totalBikeStands, coordinates);
                stationList.add(station);
            } catch (JSONException ex) {
                Log.e(TAG, "Station parsing failed at array index: " + ii + " due to: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        return stationList;
    }

    // Stations must be created by parsing through the JSON returned from the API
    private Station(String name, String address, String status, int availableBikes,
                        int totalBikeStands, LatLng coordinates) {
        mName = name;
        mAddress = address;
        mStatus = status;
        mAvailableBikes = availableBikes;
        mTotalBikeStands = totalBikeStands;
        mCoordinates = coordinates;
    }

}
