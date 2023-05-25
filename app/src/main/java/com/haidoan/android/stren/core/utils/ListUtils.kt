package com.haidoan.android.stren.core.utils

object ListUtils {
    fun <T> List<T>.replaceWith(newValue: T, replacementCondition: (T) -> Boolean): List<T> {
        return map {
            if (replacementCondition(it)) newValue else it
        }
    }
}