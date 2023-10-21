package com.haidoan.android.stren.app

import android.app.Application
import com.haidoan.android.stren.BuildConfig
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
        message: String, t: Throwable?
    ) {
        //TODO: Add some crash analytics
    }
}