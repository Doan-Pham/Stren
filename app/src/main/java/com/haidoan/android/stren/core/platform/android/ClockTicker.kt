package com.haidoan.android.stren.core.platform.android

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber

object ClockTicker {
    private var coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var _secondsElapsedCount = 0L
    private val _secondsElapsed = MutableSharedFlow<Long>()
    private var tickingJob: Job? = null
    val secondsElapsed: SharedFlow<Long> = _secondsElapsed

    suspend fun startTicking() {
        tickingJob?.cancelAndJoin()
        tickingJob = coroutineScope.launch {
            while (isActive) {
                _secondsElapsed.emit(_secondsElapsedCount)
                _secondsElapsedCount++
                delay(1000L)
                Timber.d("_secondsElapsedCount: ${_secondsElapsedCount}")
            }
        }
    }

    fun resetTick() {
        coroutineScope.cancel()
        coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        _secondsElapsedCount = 0L
    }
}