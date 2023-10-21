package com.haidoan.android.stren.feat.training

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor() : ViewModel() {
    private var a = 0;
    fun test() {
        Timber.d("${a++}")
    }
}