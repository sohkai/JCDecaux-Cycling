package com.brettsun.jcdecauxcycling;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * API's terminology for a city / location is a "contract"
 */
public class Contract {

    private static final String TAG = "Contract";
    private static final String NAME_KEY = "name";
    private static final String COMMERCIAL_KEY = "commercial_name";
    private static final String COUNTRY_KEY = "country_code";

    private String mName;
    private String mCommercialName;
    private String mCountry;

    String getName() { return mName; }
    String getCommercialName() { return mCommercialName; }
    String getCountry() { return mCountry; }

    // Parse a list of contracts from a given JSONArray in the expected format given from the API
    static List<Contract> parseArrayFromJson(JSONArray jsonArray) {
        ArrayList<Contract> contractList = new ArrayList<Contract>();
        for (int ii = 0; ii < jsonArray.length(); ++ii) {
            try {
                JSONObject contractJson = jsonArray.getJSONObject(ii);

                // Process the strings so they're prettier
                // Convert to UTF-8 in case any of the strings from the json have special characters
                String name = StringUtils.convertToUTF8(contractJson.getString(NAME_KEY));
                String commercialName = StringUtils.capitalizeFirstLetter(StringUtils.convertToUTF8(
                        contractJson.getString(COMMERCIAL_KEY)));
                String country = StringUtils.convertToUTF8(contractJson.getString(COUNTRY_KEY));

                Contract contract = new Contract(name, commercialName, country);
                contractList.add(contract);
            } catch (JSONException ex) {
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
