package nl.codesheep.android.pagesforreddit.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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

}
