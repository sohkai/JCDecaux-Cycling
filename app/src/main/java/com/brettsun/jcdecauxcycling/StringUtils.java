package com.brettsun.jcdecauxcycling;


import android.util.Log;

import java.io.UnsupportedEncodingException;

/**
 * Utility string functions for pretty printing what the API gives us
 */
public class StringUtils {

    private static final String TAG = "StringUtils";
    // The API uses either '-' or '_' after a number to denote it as a prefix
    private static final char[] PREFIX_MARKERS = new char[] { '-', '_' };

    // Capitalize only the first letter in each word
    public static String capitalizeFirstLetter(String input) {
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

    // Convert text to UTF-8 to print special characters
    public static String convertToUTF8(String input) {
        String converted;
        try {
            // From http://stackoverflow.com/questions/9069799/android-json-charset-utf-8-problems
            converted = new String(input.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Log.e(TAG, "Unable to convert given string \"" + input +
                            "\" to UTF-8 from ISO-8859-1 due to: " + ex.getMessage());
            converted = input;
        }
        return converted;
    }

    // Replace underscores with spaces if no spaces are found in the input
    public static String convertWordSeperatorToSpace(String input) {
        if (input.indexOf(' ') == -1 && input.indexOf('_') != -1) {
            return input.replace("_", " ");
        }
        return input;
    }

    // Erase a number prefix in the input if there exists one
    public static String removeNumericPrefixFromString(String input) {
        return removeNumericPrefixFromString(input, 0);
    }

    public static String removeNumericPrefixFromString(String input, int markerIndex) {
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
