package com.example.stren.feat.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import com.example.stren.R
import kotlinx.coroutines.delay

@Composable
internal fun SplashScreen(onNavigateToNextScreen: () -> Unit = {}) {
    LaunchedEffect(key1 = true) {
        delay(2500)
        onNavigateToNextScreen()
    }
    SplashScreenImpl()
}

@Composable
private fun SplashScreenImpl() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier.size(dimensionResource(id = R.dimen.logo_size_extra_large)),
            painter = painterResource(R.drawable.ic_app_logo),
            contentDescription = "App logo",
            tint = Color.White
        )
    }
}