package com.haidoan.android.stren.core.repository.impl

import com.haidoan.android.stren.core.database.dao.CoordinateDao
import com.haidoan.android.stren.core.database.model.asExternalModel
import com.haidoan.android.stren.core.model.training.Coordinate
import com.haidoan.android.stren.core.repository.base.CoordinatesRepository
import com.haidoan.android.stren.core.repository.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class CoordinatesRepositoryImpl @Inject constructor(private val dao: CoordinateDao) : CoordinatesRepository {
    override fun getCoordinates(): Flow<List<Coordinate>> =
        dao.getCoordinates()
            .map { entities -> entities.map { it.asExternalModel() } }
            .catch {
                Timber.e("CoordinatesRepositoryImpl - getCoordinates() - Exception: $it ${it.printStackTrace()}")
            }

    override fun getTotalDistanceTravelled(): Flow<Float?> =
        dao.getTotalDistanceTravelled()
            .catch {
                Timber.e("CoordinatesRepositoryImpl - getTotalDistanceTravelled() - Exception: $it ${it.printStackTrace()}")
            }

    override suspend fun insertCoordinate(coordinate: Coordinate) {
        try {
            val latestCoordinate = dao.getLatestCoordinate()
            val coordinateEntityToInsert =
                if (latestCoordinate == null) {
                    coordinate.toEntity()
                } else {
                    val distanceTravelled = latestCoordinate.asExternalModel().distanceTo(coordinate)
                    coordinate.toEntity().copy(distanceTravelled = distanceTravelled)
                }

            dao.insertOrIgnoreCoordinate(coordinateEntityToInsert)
        } catch (ex: Exception) {
            ex.printStackTrace()
            Timber.e("insertCoordinates() - Exception: $ex")
        }
    }

    override suspend fun deleteAllCoordinates() {
        try {
            dao.deleteAllCoordinates()
        } catch (ex: Exception) {
            ex.printStackTrace()
            Timber.e("insertCoordinates() - Exception: $ex")
        }
    }

}
