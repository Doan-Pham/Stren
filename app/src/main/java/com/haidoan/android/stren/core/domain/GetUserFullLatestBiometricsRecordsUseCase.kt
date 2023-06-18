package com.haidoan.android.stren.core.domain

import com.haidoan.android.stren.core.model.BiometricsRecord
import com.haidoan.android.stren.core.repository.base.DefaultValuesRepository
import com.haidoan.android.stren.core.repository.base.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

/**
 * Returns the LATEST records for biometrics that user has recorded at least once, and DEFAULT VALUE records for default biometrics if user hasn't recorded these default biometrics (Default biometrics are available to all users even if they have never recorded these biometrics)
 */
class GetUserFullLatestBiometricsRecordsUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val defaultValuesRepository: DefaultValuesRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(
        userId: String,
    ): Flow<List<BiometricsRecord>> =
        userRepository.getAllBiometricsRecordsStream(userId).mapLatest { biometricsRecords ->
            val result = mutableListOf<BiometricsRecord>()
            result.addAll(biometricsRecords)
            defaultValuesRepository.getDefaultBiometrics().forEach { defaultBiometrics ->
                if (!biometricsRecords.any { it.biometricsId == defaultBiometrics.id }) {
                    result.add(defaultBiometrics.toBiometricsRecordWithDefaultValue())
                }
            }
            result
        }
}