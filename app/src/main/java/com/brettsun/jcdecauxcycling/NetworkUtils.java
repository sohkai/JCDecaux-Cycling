package com.brettsun.jcdecauxcycling;

/**
 * Utility class for defining useful API endpoints and Volley details
 */
public class NetworkUtils {

    private static final String CONTRACTS_ENDPOINT = "https://api.jcdecaux.com/vls/v1/contracts";
    private static final String STATIONS_ENDPOINT = "https://api.jcdecaux.com/vls/v1/stations";

    private static final String APIKEY_PARAM = "apiKey=235158aaaa2e53e503c22bf4115b214011330aca"; // API key for accessing JCDecaux
    private static final String CONTRACTS_PARAM_KEY = "contract";

    // Creates the parameters for requests to the contracts API
    static String contractsUrlWithParams() {
        return CONTRACTS_ENDPOINT + "?" + APIKEY_PARAM;
    }

    // Creates the parameters for requests to the station API
    static String stationsUrlWithParams(String contractValue) {
        return STATIONS_ENDPOINT + "?" + CONTRACTS_PARAM_KEY + "=" + contractValue + "&" + APIKEY_PARAM;
    }

    // Overloaded method for creating the parameters for a request to a single station in the API
    static String stationsUrlWithParams(String contractValue, int stationNumber) {
        return STATIONS_ENDPOINT + "/" + Integer.toString(stationNumber) + "?" +
                    CONTRACTS_PARAM_KEY + "=" + contractValue + "&" + APIKEY_PARAM;
    }

}
