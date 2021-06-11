package com.citrus.digitalsignage.util

import android.content.Context
import android.content.SharedPreferences




class Prefs(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    var serverIP: String
        get() = prefs.getString(Constants.KEY_SERVER_IP, "")?: ""
        set(value) = prefs.edit().putString(Constants.KEY_SERVER_IP, value).apply()

    var storeID: String
        get() = prefs.getString(Constants.KEY_STORE_ID, "")?: ""
        set(value) = prefs.edit().putString(Constants.KEY_STORE_ID, value).apply()

    var deviceID: String
        get() = prefs.getString(Constants.KEY_DEVICE_ID, "")?: ""
        set(value) = prefs.edit().putString(Constants.KEY_DEVICE_ID, value).apply()

}
