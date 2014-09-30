package com.brettsun.jcdecauxcycling;

import java.util.HashMap;

/**
 * Utility class for defining useful API endpoints and Volley details
 */
public class NetworkUtils {

    public static final String CONTRACTS_ENDPOINT = "https://ticketfi.com/api/v1/purchase/details";
    public static final String STATIONS_ENDPOINT = "https://api.jcdecaux.com/vls/v1/stations";

    private static final String APIKEY_PARAM_KEY = "apiKey";
    private static final String APIKEY_VALUE = "235158aaaa2e53e503c22bf4115b214011330aca"; // API key for accessing JCDeceaux
    private static final String CONTRACTS_PARAM_KEY = "contract";

    // Creates the parameters for requests to the station API
    static HashMap<String, String> stationsParams(String contractValue) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(CONTRACTS_PARAM_KEY, contractValue);
        params.put(APIKEY_PARAM_KEY, APIKEY_VALUE);
        return params;
    }

}
