package com.example.stren.feat.auth.utils

import android.text.TextUtils
import android.util.Patterns

internal object ValidationUtils {
    internal fun isEmailValid(email: String) =
        !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
}