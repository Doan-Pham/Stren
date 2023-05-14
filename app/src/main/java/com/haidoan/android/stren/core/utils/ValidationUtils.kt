package com.haidoan.android.stren.core.utils

object ValidationUtils {
    fun validateDouble(doubleAsString: String): String {
        val filteredChars = doubleAsString.filterIndexed { index, c ->
            c.isDigit()
                    || (c == '.' && index != 0 && doubleAsString.indexOf('.') == index)
                    || (c == '.' && index != 0 && doubleAsString.count { it == '.' } <= 1)
        }
        return if (filteredChars.count { it == '.' } == 1) {
            val beforeDecimal = filteredChars.substringBefore('.')
            val afterDecimal = filteredChars.substringAfter('.')
            beforeDecimal + "." + afterDecimal.take(2)
        } else {
            filteredChars
        }
    }
}