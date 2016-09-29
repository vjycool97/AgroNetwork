package agroconnectapp.agroconnect.in.agroconnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by shakti on 7/18/2015.
 */
public class SharedPrefHandler {
    static Context context = AgroConnect.getInstance().getContext();
    static SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);//context.getSharedPreferences("com.agroconnect.app",Context.MODE_PRIVATE);

    public enum PREFS_NAME { UUID , LAST_APP_INIT_TIME, REFERRER , LAST_NOTIFICAION, GCMID}

    public static void save(PREFS_NAME prefs_name, String value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(prefs_name.toString(), value);
        editor.apply();
    }

    public static String retreiveString(PREFS_NAME prefs_name) {
        return sharedPref.getString(prefs_name.toString(), null);
    }

    public static void save(PREFS_NAME prefs_name, boolean value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(prefs_name.toString(), value);
        editor.apply();
    }

    public static boolean retreiveBoolean(PREFS_NAME prefs_name) {
        return sharedPref.getBoolean(prefs_name.toString(), false);
    }

    public static void delete(PREFS_NAME prefs_name) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(prefs_name.toString());
        editor.apply();
    }

    public static void save(PREFS_NAME prefs_name, long value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(prefs_name.toString(), value);
        editor.apply();
    }

    public static long retreiveLong(PREFS_NAME prefs_name) {
        return sharedPref.getLong(prefs_name.toString(), -1);
    }


    public static String retreiveString(String prefs_name, String defVal) {
        return sharedPref.getString(prefs_name, defVal);
    }

    public static int retreiveInt(String prefs_name, int defVal) {
        return sharedPref.getInt(prefs_name, defVal);
    }

    public static void saveInt(String prefs_name, int value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(prefs_name, value);
        editor.apply();
    }

    public static void saveString(String prefs_name, String  value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(prefs_name, value);
        editor.apply();
    }

    public static void saveBoolean(String prefs_name, boolean  value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(prefs_name, value);
        editor.apply();
    }



}
