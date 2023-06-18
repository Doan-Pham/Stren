package com.haidoan.android.stren.feat.settings.measurements

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.core.designsystem.component.*
import com.haidoan.android.stren.core.model.BiometricsRecord
import timber.log.Timber

internal const val MEASUREMENTS_SCREEN_ROUTE = "measurements_screen_route"
internal const val USER_ID_MEASUREMENTS_NAV_ARG = "USER_ID_MEASUREMENTS_NAV_ARG"

@Composable
internal fun MeasurementsRoute(
    modifier: Modifier = Modifier,
    viewModel: MeasurementsViewModel = hiltViewModel(),
    onBackToPreviousScreen: () -> Unit,
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var shouldShowAddMeasurementDialog by remember { mutableStateOf(false) }

    val editMeasurementsAppBarConfiguration = AppBarConfiguration.NavigationAppBar(
        title = "Measurements",
        navigationIcon = IconButtonInfo.BACK_ICON.copy(clickHandler = onBackToPreviousScreen),
        actionIcons = listOf(
            IconButtonInfo(
                drawableResourceId = R.drawable.ic_add,
                description = "Icon Add",
                clickHandler = {
                    shouldShowAddMeasurementDialog = true
                })
        )
    )

    var isAppBarConfigured by remember { mutableStateOf(false) }
    if (!isAppBarConfigured) {
        appBarConfigurationChangeHandler(editMeasurementsAppBarConfiguration)
        isAppBarConfigured = true
    }

    MeasurementsScreen(
        modifier = modifier,
        uiState = uiState,
        onAddBiometricsRecord = viewModel::addBiometricsRecord,
        shouldShowAddMeasurementDialog = shouldShowAddMeasurementDialog,
        showAddMeasurementDialog = { shouldShowAddMeasurementDialog = true },
        hideAddMeasurementDialog = { shouldShowAddMeasurementDialog = false }
    )
}

@SuppressLint("NewApi")
@Composable
private fun MeasurementsScreen(
    modifier: Modifier = Modifier,
    uiState: MeasurementsUiState,
    shouldShowAddMeasurementDialog: Boolean,
    showAddMeasurementDialog: () -> Unit,
    hideAddMeasurementDialog: () -> Unit,
    onAddBiometricsRecord: (BiometricsRecord) -> Unit
) {
    when (uiState) {
        is MeasurementsUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingAnimation()
            }
        }
        is MeasurementsUiState.LoadComplete -> {
            Timber.d("biometricsRecord: ${uiState.biometricsRecords}")
            val biometricsRecords = uiState.biometricsRecords
            var biometricsToAddRecord by remember {
                mutableStateOf(biometricsRecords.firstOrNull())
            }
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
            ) {
                items(biometricsRecords) { currentBiometricRecord ->
                    Row(
                        modifier = Modifier
                            .clickable {
                                biometricsToAddRecord = currentBiometricRecord
                                showAddMeasurementDialog()
                            }
                            .fillMaxWidth()
                            .padding(dimensionResource(id = R.dimen.padding_medium))
                    ) {
                        Text(
                            text = currentBiometricRecord.biometricsName,
                            modifier = Modifier.weight(1f)
                        )
                        val recordValueAsString =
                            if (currentBiometricRecord.value != 0f) {
                                "${currentBiometricRecord.value} ${currentBiometricRecord.measurementUnit}"
                            } else {
                                "___"
                            }

                        Text(
                            modifier = Modifier.weight(0.5f),
                            textAlign = TextAlign.End,
                            text = recordValueAsString
                        )
                    }
                }
            }
            if (shouldShowAddMeasurementDialog && biometricsToAddRecord != null) {
                AddMeasurementDialog(
                    onDismissDialog = { hideAddMeasurementDialog() },
                    biometricsRecords = uiState.biometricsRecords,
                    biometricsToAddRecord = biometricsToAddRecord!!,
                    onSaveClick = onAddBiometricsRecord
                )
            }
        }
    }
}
