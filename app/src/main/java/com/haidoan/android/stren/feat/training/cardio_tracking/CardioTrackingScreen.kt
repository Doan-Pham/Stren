package com.haidoan.android.stren.feat.training.cardio_tracking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.haidoan.android.stren.app.navigation.AppBarConfiguration

@Composable
internal fun CardioTrackingRoute(
    modifier: Modifier = Modifier,
    viewModel: CardioTrackingViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
) {
    CardioTrackingScreen(modifier = modifier)
}

@Composable
private fun CardioTrackingScreen(modifier: Modifier = Modifier) {
    val singapore = LatLng(1.35, 103.87)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 10f)
    }
    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    )
}

@Preview
@Composable
private fun PreviewCardioTrackingScreen(){
    Surface(modifier = Modifier.fillMaxSize().background(Color.White)) {
        CardioTrackingScreen(modifier = Modifier.fillMaxSize())

    }
}