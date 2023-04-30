package com.haidoan.android.stren.feat.trainining.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.haidoan.android.stren.core.repository.ExercisesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class ExercisesViewModel @Inject constructor(exercisesRepository: ExercisesRepository) :
    ViewModel() {
    val exercises =
        exercisesRepository.getAllExercisesStream().cachedIn(viewModelScope)
}