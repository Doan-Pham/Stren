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
}