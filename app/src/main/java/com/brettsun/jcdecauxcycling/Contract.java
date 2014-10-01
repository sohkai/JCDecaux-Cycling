package com.brettsun.jcdecauxcycling;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Contract {

    private static final String TAG = "Contract";
    private static final String NAME_KEY = "name";
    private static final String COMMERCIAL_KEY = "commercial_name";
    private static final String COUNTRY_KEY = "country_code";

    private String mName;
    private String mCommercialName;
    private String mCountry;
    //TODO: could add scrolling list of cities

    String getName() { return mName; }
    String getCommercialName() { return mCommercialName; }
    String getCountry() { return mCountry; }

    // Parse a list of contracts from a given JSONArray in the expected format given from the API
    static ArrayList<Contract> parseFromJson(JSONArray jsonArray) {
        ArrayList<Contract> contractList = new ArrayList<Contract>();
        for (int ii = 0; ii < jsonArray.length(); ++ii) {
            try {
                JSONObject contractJson = jsonArray.getJSONObject(ii);

                // In case any of the strings have special letters
                // From http://stackoverflow.com/questions/9069799/android-json-charset-utf-8-problems
                String name = new String(contractJson.getString(NAME_KEY).getBytes("ISO-8859-1"), "UTF-8");
                String commercialName = new String(contractJson.getString(COMMERCIAL_KEY).getBytes("ISO-8859-1"), "UTF-8");
                String country = new String(contractJson.getString(COUNTRY_KEY).getBytes("ISO-8859-1"), "UTF-8");

                Contract contract = new Contract(name, commercialName, country);
                contractList.add(contract);
            } catch (Exception ex) {
                Log.e(TAG, "Contract parsing failed at array index: " + ii + " due to: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        return contractList;
    }

    // Contracts must be created by parsing through the JSON returned from the API
    private Contract(String name, String commercialName, String country) {
        mName = name;
        mCommercialName = commercialName;
        mCountry = country;
    }

}
