package com.haidoan.android.stren.feat.training.cardio_tracking

import android.Manifest
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.core.designsystem.component.LoadingScreen
import com.haidoan.android.stren.core.designsystem.component.SimpleConfirmationDialog
import com.haidoan.android.stren.core.platform.android.location.LocationService
import com.haidoan.android.stren.feat.training.cardio_tracking.model.CardioTrackingUiState
import com.haidoan.android.stren.feat.training.cardio_tracking.model.toLocation

private const val POLYLINE_STROKE_WIDTH_PX = 12

@Composable
internal fun CardioTrackingRoute(
    modifier: Modifier = Modifier,
    viewModel: CardioTrackingViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CardioTrackingScreen(
        modifier = modifier,
        uiState = uiState,
        onPermissionNotGranted = onNavigateBack
    )

    LaunchedEffect(Unit) {
        appBarConfigurationChangeHandler(
            AppBarConfiguration.NavigationAppBar(
                title = "Tracking Cardio",
                navigationIcon = IconButtonInfo.BACK_ICON.copy(clickHandler = onNavigateBack),
            )
        )
    }

}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun CardioTrackingScreen(
    modifier: Modifier = Modifier,
    uiState: CardioTrackingUiState,
    onPermissionNotGranted: () -> Unit
) {
    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    val context = LocalContext.current

    when (uiState) {
        is CardioTrackingUiState.CoordinateLoaded -> {
            var previousZoom by remember {
                mutableFloatStateOf(20f)
            }

            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition
                    .fromLatLngZoom(uiState.currentCoordinate, 20f)
            }
            val locationSource = remember {
                object : LocationSource {
                    private var listener: LocationSource.OnLocationChangedListener? = null

                    override fun activate(listener: LocationSource.OnLocationChangedListener) {
                        this.listener = listener
                    }

                    override fun deactivate() {
                        listener = null
                    }

                    fun onLocationChanged(location: Location) {
                        listener?.onLocationChanged(location)
                    }
                }
            }

            GoogleMap(
                modifier = modifier.fillMaxSize(),
                uiSettings = MapUiSettings(myLocationButtonEnabled = true),
                properties = MapProperties(isMyLocationEnabled = true),
                cameraPositionState = cameraPositionState,
                locationSource = locationSource
            ) {
                Polyline(
                    points = uiState.coordinates,
                    startCap = RoundCap(),
                    endCap = RoundCap(),
                    jointType = JointType.ROUND,
                    width = POLYLINE_STROKE_WIDTH_PX.toFloat(),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            LaunchedEffect(key1 = uiState.currentCoordinate, block = {
                locationSource.onLocationChanged(uiState.currentCoordinate.toLocation())

                val cameraPosition = CameraPosition
                    .fromLatLngZoom(uiState.currentCoordinate, previousZoom)

                cameraPositionState.animate(
                    CameraUpdateFactory.newCameraPosition(cameraPosition),
                    1_000
                )
            })

            LaunchedEffect(cameraPositionState) {
                snapshotFlow { cameraPositionState.position.zoom }.collect {
                    previousZoom = it
                }
            }
        }

        CardioTrackingUiState.Loading -> {
            LoadingScreen()
        }
    }

    if (locationPermissionsState.allPermissionsGranted) {
        LaunchedEffect(true) {
            Intent(context.applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_START
                context.applicationContext.startService(this)
            }
        }
    } else {
        val textToShow = if (locationPermissionsState.shouldShowRationale) {
            """
                    Stren needs your location to track the cardio activity you are doing. So it'll be nice if you could grant us the permission to do so.
            """.trimIndent()
        } else {
            "This feature requires location permission to work. Please grant Stren permission to access your location in device's settings"
        }

        SimpleConfirmationDialog(
            onDismissClick = onPermissionNotGranted,
            title = "Permission Required",
            body = textToShow,
            onDismissRequest = {}
        ) {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null)
            )
            context.startActivity(intent)
        }
    }

    LaunchedEffect(Unit) {
        locationPermissionsState.launchMultiplePermissionRequest()
    }
}


@Composable
private fun EmptyScreen(
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_whole_screen)),
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "Icon edit"
            )
            Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.padding_small)))
            Text(
                text = "No exercise found",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Click to create custom exercise",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
private fun PreviewCardioTrackingScreen() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        CardioTrackingScreen(
            modifier = Modifier.fillMaxSize(),
            onPermissionNotGranted = {},
            uiState = CardioTrackingUiState.CoordinateLoaded(
                coordinates = listOf(
                    LatLng(1.0, 1.0),
                    LatLng(2.0, 1.0),
                    LatLng(3.0, 1.0),
                    LatLng(4.0, 1.0)
                ),
                currentCoordinate = LatLng(1.0, 1.0)
            )
        )
    }
}
