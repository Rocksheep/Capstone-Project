package nl.codesheep.android.pagesforreddit.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import nl.codesheep.android.pagesforreddit.MainActivity;
import nl.codesheep.android.pagesforreddit.R;

/**
 * Created by Rien on 06/08/2016.
 */
public class Utility {

    public static String getNextPageString(Context context) {
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_next_page_string), "");
    }

    public static void setNextPageString(Context context, String nextPage) {
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.pref_next_page_string), nextPage);
        editor.apply();
    }

    public static void setAuthCode(Context context, String authCode) {
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.auth_code), authCode);
        editor.apply();
    }

    public static String getAuthCode(Context context) {
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.auth_code), "");
    }

    public static void setToken(Context context, String token) {
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.token), token);
        editor.apply();
    }

    public static String getToken(Context context) {
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.token), "");
    }
}
