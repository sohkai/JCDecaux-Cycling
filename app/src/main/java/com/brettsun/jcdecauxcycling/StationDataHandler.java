package com.brettsun.jcdecauxcycling;

import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class that handles the transferring of station data from the LocationActivity
 * to the StationActivity.
 *
 * NOTE: This class may be destructed arbitrarily if the user moves this app to the background.
 * In such a case, the data would be destroyed and any activities using the data obtained from
 * this class would have to handle getting an empty list back.
 */
public class StationDataHandler {

    private static final String TAG = "StationDataHandler";
    private static StationDataHandler mInstance;

    private List<Station> mStations = new ArrayList<Station>(0);

    private StationDataHandler() { }

    public static synchronized StationDataHandler getInstance() {
        if (mInstance == null) {
            mInstance = new StationDataHandler();
        }
        return mInstance;
    }

    public List<Station> getData() {
        return mStations;
    }

    public void setDataFromJsonResponse(JSONArray jsonResponse) {
        Log.i(TAG, "Setting station data by parsing json response");
        mStations = Station.parseArrayFromJson(jsonResponse);
    }

}
