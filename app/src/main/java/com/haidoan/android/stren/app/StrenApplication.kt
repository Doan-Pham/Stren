package com.haidoan.android.stren.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.haidoan.android.stren.BuildConfig
import com.haidoan.android.stren.core.platform.android.NOTIFICATION_CHANNEL_ID_LOCATION
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber


@HiltAndroidApp
class StrenApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(Timber.DebugTree())
        }

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID_LOCATION,
            "Location",
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

    }
}

/**
 * A Debug Tree that prints out class name and the line number of the log
 */
private class StrenDebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String {
        return String.format(
            "%s-(Line: %s):",
            super.createStackElementTag(element),
            element.lineNumber
        )
    }
}

private class ReleaseTree : Timber.Tree() {
    override fun log(
        priority: Int, tag: String?,
        message: String, t: Throwable?,
    ) {
        //TODO: Add some crash analytics
    }
}