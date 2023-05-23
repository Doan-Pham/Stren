package com.haidoan.android.stren.core.utils

import java.util.*

object StringFormatUtils {
    fun String.capitalizeFirstChar() =
        this.trim().lowercase(Locale.getDefault())
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

    fun String.capitalizeEveryWord() =
        this.trim().lowercase(Locale.getDefault()).split(' ')
            .joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }
}