package com.qkvpn.vpn.handlers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesManager {


    //this will contains the app preferences
    private static SharedPreferences mSharedPref;


    synchronized public static void init(Context context) {
        if (mSharedPref == null)
            mSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }


    public static void setString(String key, String value) {
        mSharedPref.edit().putString(key, value).apply();
    }


    public static String getString(String key,String extra) {

        return mSharedPref.getString(key, extra);
    }



}
