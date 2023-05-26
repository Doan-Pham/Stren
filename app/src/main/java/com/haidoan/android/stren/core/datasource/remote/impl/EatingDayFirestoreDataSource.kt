package com.haidoan.android.stren.core.datasource.remote.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.haidoan.android.stren.core.datasource.remote.base.EatingDayRemoteDataSource
import com.haidoan.android.stren.core.datasource.remote.model.FirestoreEatingDay
import com.haidoan.android.stren.core.datasource.remote.model.toExternalModel
import com.haidoan.android.stren.core.model.EatingDay
import com.haidoan.android.stren.core.utils.DateUtils.toTimeStampDayEnd
import com.haidoan.android.stren.core.utils.DateUtils.toTimeStampDayStart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject

private const val EATING_DAY_COLLECTION_PATH = "EatingDay"
private const val USER_COLLECTION_PATH = "User"

class EatingDayFirestoreDataSource @Inject constructor() : EatingDayRemoteDataSource {
    private val firestore = FirebaseFirestore.getInstance()

    override fun getEatingDayStream(userId: String, date: LocalDate): Flow<EatingDay> =
        firestore.collection("$USER_COLLECTION_PATH/$userId/$EATING_DAY_COLLECTION_PATH")
            .whereGreaterThanOrEqualTo("date", date.toTimeStampDayStart())
            .whereLessThanOrEqualTo("date", date.toTimeStampDayEnd())
            .snapshots()
            .mapNotNull {
                it.toObjects(FirestoreEatingDay::class.java).firstOrNull()?.toExternalModel()
                    ?: EatingDay(date = date)
            }

    override suspend fun getEatingDayById(userId: String, eatingDayId: String): EatingDay =
        firestore.collection("$USER_COLLECTION_PATH/$userId/$EATING_DAY_COLLECTION_PATH")
            .document(eatingDayId).get().await().toObject(FirestoreEatingDay::class.java)
            ?.toExternalModel()
            ?: EatingDay(date = LocalDate.of(1000, 10, 10))

    override suspend fun getEatingDayByDate(userId: String, selectedDate: LocalDate): EatingDay =
        firestore.collection("$USER_COLLECTION_PATH/$userId/$EATING_DAY_COLLECTION_PATH")
            .whereGreaterThanOrEqualTo("date", selectedDate.toTimeStampDayStart())
            .whereLessThanOrEqualTo("date", selectedDate.toTimeStampDayEnd())
            .get().await()
            .firstOrNull()?.toObject(FirestoreEatingDay::class.java)?.toExternalModel()
            ?: EatingDay.undefined

    override suspend fun addEatingDay(userId: String, eatingDay: EatingDay): String =
        firestore.collection(
            "$USER_COLLECTION_PATH/$userId/$EATING_DAY_COLLECTION_PATH"
        )
            .add(FirestoreEatingDay.from(eatingDay))
            .await().id

    override suspend fun getDatesUserTracked(userId: String): List<LocalDate> =
        firestore.collection(
            "$USER_COLLECTION_PATH/$userId/$EATING_DAY_COLLECTION_PATH"
        ).get().await().toObjects(FirestoreEatingDay::class.java).mapNotNull {
            it.toExternalModel().date
        }

    override suspend fun updateEatingDay(userId: String, eatingDay: EatingDay) {
        firestore.collection("$USER_COLLECTION_PATH/$userId/$EATING_DAY_COLLECTION_PATH")
            .document(eatingDay.id).set(FirestoreEatingDay.from(eatingDay))
            .await()
    }
}