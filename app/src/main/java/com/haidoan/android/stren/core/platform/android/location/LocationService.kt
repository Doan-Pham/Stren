package com.haidoan.android.stren.core.platform.android.location

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
import com.haidoan.android.stren.R
import com.haidoan.android.stren.core.model.training.Coordinate
import com.haidoan.android.stren.core.platform.android.ClockTicker
import com.haidoan.android.stren.core.platform.android.NOTIFICATION_CHANNEL_ID_LOCATION
import com.haidoan.android.stren.core.platform.android.hasLocationPermission
import com.haidoan.android.stren.core.repository.base.CoordinatesRepository
import com.haidoan.android.stren.core.utils.NumberUtils.roundToTwoDecimalPlace
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val LOCATION_NOTIFICATION_ID = 1

@AndroidEntryPoint
class LocationService : Service() {
    @Inject
    lateinit var coordinatesRepository: CoordinatesRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    private var updateNotificationJob: Job? = null
    private val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID_LOCATION)
        .setContentTitle("Stren")
        .setSmallIcon(R.drawable.ic_app_logo_no_padding)
        .setOngoing(true)

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        if (!applicationContext.hasLocationPermission()) {
            Timber.e("LocationService() - trying to start service without location permission")
            stopSelf()
            return
        }

        serviceScope.launch {
            ClockTicker.startTicking()
        }

        observeAndUpdateUserCoordinate()
        observeAndUpdateNotification()

        startForeground(
            LOCATION_NOTIFICATION_ID,
            notification.build()
        )
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun stop() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
        updateNotificationJob?.cancel()
        updateNotificationJob = null
        GlobalScope.launch { coordinatesRepository.deleteAllCoordinates() }
        notificationManager.cancel(LOCATION_NOTIFICATION_ID)
        ClockTicker.resetTick()
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onDestroy() {
        super.onDestroy()
        GlobalScope.launch { coordinatesRepository.deleteAllCoordinates() }
        ClockTicker.resetTick()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }

    private fun observeAndUpdateUserCoordinate() {
        serviceScope.launch {
            locationClient
                .getLocationUpdates(2000L)
                .collect { location ->
                    Timber.d("observeAndUpdateUserCoordinate()-location: $location  ")
                    coordinatesRepository.insertCoordinate(Coordinate.from(location))
                }
        }
    }

    private fun observeAndUpdateNotification() {
        updateNotificationJob =  serviceScope.launch {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            coordinatesRepository.getTotalDistanceTravelled().collect {
                Timber.d("observeAndUpdateNotification - distance: $it")
                val updatedNotification = notification.setContentText(
                    "You have travelled ${((it?.div(1000)) ?: 0f).roundToTwoDecimalPlace()} km "
                )
                notificationManager.notify(LOCATION_NOTIFICATION_ID, updatedNotification.build())
            }
        }
    }
}