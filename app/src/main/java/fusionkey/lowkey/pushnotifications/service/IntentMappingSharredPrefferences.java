package fusionkey.lowkey.pushnotifications.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

public class IntentMappingSharredPrefferences {

    public static String FLAG_TO_COMMENTS_STRING = "COMMENTS";
    public static String NO_FLAG_TO_COMMENTS_STRING = "JUSTSAIDNO";

    public static void saveTheIntentMap(String FLAG, @Nullable String timestamp, Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("FLAG", FLAG);
        if(timestamp == null)
        editor.putString("timestamp", "null");
        else
            editor.putString("timestamp", timestamp);
        editor.apply();
    }

    public static String getTheIntentFlag(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("FLAG","");
    }
    public static String getTheIntentTimestamp(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("timestamp","");
    }

}
