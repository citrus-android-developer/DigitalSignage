package com.citrus.digitalsignage.util

import android.app.Activity
import android.app.Application
import com.citrus.digitalsignage.di.MyApplication


object Constants {
    const val SHARED_PREFERENCES_NAME = "sharedPref"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"
    const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Tracking"
    const val NOTIFICATION_ID = 1

    const val GET_ALL_DATA = "/POS/digisinageWS/Service1.asmx/GetLayoutData"
    const val IMG_DOMAIN ="http://cms.citrus.tw/demo/SIGINAGE/Assets/Upload/"

    const val KEY_SERVER_IP = "KEY_SERVER_IP"
    const val KEY_STORE_ID = "KEY_STORE_ID"
    const val KEY_DEVICE_ID = "KEY_DEVICE_ID"


    fun Application.getCurrentPage(): Activity? {
        return MyApplication().getCurrentPage()
    }

}