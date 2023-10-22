package com.haidoan.android.stren.core.datasource.remote.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.haidoan.android.stren.core.datasource.remote.base.RoutinesRemoteDataSource
import com.haidoan.android.stren.core.datasource.remote.model.FirestoreRoutine
import com.haidoan.android.stren.core.datasource.remote.model.toRoutine
import com.haidoan.android.stren.core.datasource.remote.model.toRoutines
import com.haidoan.android.stren.core.model.Routine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

private const val ROUTINE_COLLECTION_PATH = "Routine"
private const val USER_COLLECTION_PATH = "User"

class RoutinesFirestoreDataSource @Inject constructor() : RoutinesRemoteDataSource {
    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun getRoutinesStreamByUserId(userId: String): Flow<List<Routine>> =
        firestore.collection("$USER_COLLECTION_PATH/$userId/$ROUTINE_COLLECTION_PATH").snapshots()
            .mapNotNull { it.toRoutines() }

    override suspend fun getRoutinesByUserId(userId: String): List<Routine> =
        firestore.collection("$USER_COLLECTION_PATH/$userId/$ROUTINE_COLLECTION_PATH").get().await()
            .toRoutines()

    override suspend fun getRoutineById(userId: String, routineId: String): Routine =
        firestore.collection("$USER_COLLECTION_PATH/$userId/$ROUTINE_COLLECTION_PATH")
            .document(routineId).get().await().toRoutine()

    override suspend fun addRoutine(userId: String, routine: Routine): String {
        Timber.d("addRoutine() - routine: $routine")
        Timber.d("addRoutine() - FirestoreRoutine: ${FirestoreRoutine.from(routine)}")
        return firestore.collection("$USER_COLLECTION_PATH/$userId/$ROUTINE_COLLECTION_PATH")
            .add(FirestoreRoutine.from(routine))
            .await().id
    }


    override suspend fun updateRoutine(userId: String, routine: Routine) {
        firestore.collection("$USER_COLLECTION_PATH/$userId/$ROUTINE_COLLECTION_PATH")
            .document(routine.id)
            .set(FirestoreRoutine.from(routine))
            .await()
    }

    override suspend fun deleteRoutine(userId: String, routineId: String) {
        firestore.collection("$USER_COLLECTION_PATH/$userId/$ROUTINE_COLLECTION_PATH")
            .document(routineId).delete().await()
    }
}

