package com.haidoan.android.stren.core.utils

import java.math.RoundingMode
import java.text.DecimalFormat

object NumberUtils {

    // Without this annotation, to call this method in Java you have to write NumberUtils.INSTANCE.method()
    /**
     * This method casts an arbitrary number type to another arbitrary type
     */
    @JvmStatic
    inline fun <reified NumberType : Number> Number.castTo(): NumberType =
        when (NumberType::class) {
            Byte::class -> this.toByte() as NumberType
            Short::class -> this.toShort() as NumberType
            Long::class -> this.toLong() as NumberType
            Int::class -> this.toInt() as NumberType
            Double::class -> this.toDouble() as NumberType
            Float::class -> this.toFloat() as NumberType
            else -> throw IllegalArgumentException("Unknown input type: ${this.javaClass}")
        }

    fun Float.roundToTwoDecimalPlace(): String {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.DOWN
        return df.format(this)
    }

    fun Double.roundToTwoDecimalPlace(): String {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.DOWN
        return df.format(this)
    }
    /**
     * Format [seconds] as duration in cardio exercise
     *
     * Example: "5 seconds" becomes "00:00:05" and so on
     */
    fun formatAsDuration(seconds: Long): String {
        return String.format("%02d:%02d:%02d", seconds / 3600, seconds % 3600 / 60, seconds % 60)
    }
}