package com.haidoan.android.stren.core.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import java.time.*
import java.time.temporal.TemporalAdjusters
import java.util.stream.Collectors
import java.util.stream.IntStream


@RequiresApi(Build.VERSION_CODES.O)
object DateUtils {
    // The zoneId is currently Asia/Ho_Chi_Minh which has an offset of 7 hours
    private val DEFAULT_TIMEZONE_OFFSET_IN_HOURS = 7
    private val ZONE_ID = ZoneId.of("Asia/Ho_Chi_Minh")

    fun getCurrentDate(): LocalDate = LocalDate.now(ZONE_ID)
    private fun getMondayFrom(date: LocalDate): LocalDate =
        date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    fun getAllWeekDays(date: LocalDate): List<LocalDate> {
        return IntStream.range(0, 7)
            .mapToObj { getMondayFrom(date).plusDays(it.toLong()) }
            .collect(Collectors.toList())
    }

    fun LocalDate.toTimeStampDayStart() = Timestamp(
        this.atStartOfDay(ZONE_ID).toEpochSecond(), 0
    )

    fun LocalDate.toTimeStampDayEnd() = Timestamp(
        this.atTime(LocalTime.MAX)
            .toEpochSecond(ZoneOffset.ofHours(DEFAULT_TIMEZONE_OFFSET_IN_HOURS)), 0
    )

    fun Timestamp.toLocalDate(): LocalDate = this.toDate().toInstant().atZone(ZONE_ID).toLocalDate()
}
