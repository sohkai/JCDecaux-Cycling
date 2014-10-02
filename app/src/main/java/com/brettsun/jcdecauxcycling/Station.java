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

                // Process the strings so they're prettier
                name = capitalizeFirstLetter(convertWordSeperatorToSpace(removeNumericPrefixFromString(name)));
                address = capitalizeFirstLetter(convertWordSeperatorToSpace(removeNumericPrefixFromString(address)));
                status = capitalizeFirstLetter(status);

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

    /**** String utility functions for pretty printing what the API gives us ****/
    // The API uses either '-' or '_' after a number to denote it as a prefix
    private static final char[] PREFIX_MARKERS = new char[] { '-', '_' };

    // Capitalize only the first letter in each word
    private static String capitalizeFirstLetter(String input) {
        input = input.toLowerCase();
        String[] words = input.split("\\s+");
        StringBuilder sb = new StringBuilder(input.length());
        if (words[0].length() > 0) {
            sb.append(Character.toUpperCase(words[0].charAt(0))).append(words[0].substring(1, words[0].length()));
            for (int ii = 1; ii < words.length; ii++) {
                sb.append(" ");
                sb.append(Character.toUpperCase(words[ii].charAt(0))).append(words[ii].substring(1, words[ii].length()));
            }
        }
        return sb.toString();
    }

    // Replace underscores with spaces if no spaces are found in the input
    private static String convertWordSeperatorToSpace(String input) {
        if (input.indexOf(' ') == -1 && input.indexOf('_') != -1) {
            return input.replace("_", " ");
        }
        return input;
    }

    // Erase a number prefix in the input if there exists one
    private static String removeNumericPrefixFromString(String input) {
        return removeNumericPrefixFromString(input, 0);
    }

    private static String removeNumericPrefixFromString(String input, int markerIndex) {
        char prefixMarker = PREFIX_MARKERS[markerIndex];
        int prefixIndex = input.indexOf(prefixMarker);

        PrefixTest:
            if (prefixIndex > 0) {
                // If we find there is a prefix marker, test the characters before it to see if it
                // is a numeric prefix
                for (int ii = prefixIndex - 1; ii >= 0; --ii) {
                    char prefixTestChar = input.charAt(ii);
                    if (prefixTestChar != ' ' && !Character.isDigit(prefixTestChar)) {
                        // The prefix char wasn't a space or digit character so the input didn't have a number prefix
                        // Break out to the outer brace
                        break PrefixTest;
                    }
                }
                // All characters before the prefix marker were space or digit characters, so the input
                // contains a number prefix
                // Ignore any trailing whitespace after the prefix
                while (prefixIndex < input.length() && input.charAt(prefixIndex + 1) == ' ') {
                    ++prefixIndex;
                }
                return input.substring(prefixIndex + 1);
            }

        // Didn't find a prefix marker character or the prefix wasn't numeric
        // Try the next prefix marker if there is one or return the original string
        if (markerIndex < PREFIX_MARKERS.length - 1) {
            return removeNumericPrefixFromString(input, markerIndex + 1);
        } else {
            return input;
        }
    }

}
