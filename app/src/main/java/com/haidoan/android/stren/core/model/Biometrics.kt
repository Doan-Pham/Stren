package com.haidoan.android.stren.core.model

import com.google.firebase.firestore.DocumentId
import com.haidoan.android.stren.core.utils.DateUtils

data class Biometrics(
    @DocumentId
    val id: String = "Undefined Biometrics Id",
    val name: String = "Undefined Biometrics Name",
    val measurementUnit: String = "Undefined Biometrics Measurement Unit",
) {
    /**
     * Returns a BiometricsRecord from a Biometrics object (fields that are present in BiometricsRecord but not in Biometrics will be assigned to some default values).
     *
     * This method is useful for handling default biometrics (which every user will have even if they
     * haven't made any records). In these cases, the record details (recordDate, value of the biometrics being recorded) don't matter, the developer just needs a simple BiometricsRecord to work with
     */
    fun toBiometricsRecordWithDefaultValue() = BiometricsRecord(
        biometricsId = this.id,
        biometricsName = this.name,
        measurementUnit = this.measurementUnit,
        value = 0f,
        recordDate = DateUtils.getCurrentDate()
    )
}

enum class CommonBiometrics(
    val id: String, val biometricsName: String, val measurementUnit: String,
) {
    WEIGHT(id = "BIOMETRICS_ID_WEIGHT", biometricsName = "Weight", measurementUnit = "kg"),
    HEIGHT(id = "BIOMETRICS_ID_HEIGHT", biometricsName = "Height", measurementUnit = "cm"),
}