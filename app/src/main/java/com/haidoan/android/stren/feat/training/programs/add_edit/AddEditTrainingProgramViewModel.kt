package com.haidoan.android.stren.feat.training.programs.add_edit

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class AddEditTrainingProgramViewModel @Inject constructor() : ViewModel() {
    private var _programName = mutableStateOf("New program")
    val programName: State<String> = _programName

    fun onProgramNameChange(value: String) {
        _programName.value = value
    }
}