package com.citrus.digitalsignage.di

import android.app.Activity
import android.app.Application
import com.citrus.digitalsignage.util.Prefs
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

val prefs: Prefs by lazy {
    MyApplication.prefs!!
}


@HiltAndroidApp
class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        prefs = Prefs(applicationContext)

    }


    companion object {
        var prefs: Prefs? = null
        var currentActivity:Activity? = null
    }

    fun setCurrentPage(activity:Activity){
        currentActivity = activity
    }

    fun getCurrentPage(): Activity? {
        return currentActivity
    }

}