package com.haidoan.android.stren.feat.training.programs.add_edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.haidoan.android.stren.app.navigation.AppBarConfiguration


@Composable
internal fun AddEditTrainingProgramsRoute(
    modifier: Modifier = Modifier,
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
) {

    AddEditTrainingProgramsScreen(modifier = modifier)
}

@Composable
private fun AddEditTrainingProgramsScreen(modifier: Modifier = Modifier) {
    Scaffold {
        Box(
            modifier = modifier
                .padding(it)
                .fillMaxSize()
                .background(Color.Red)
        )
    }
}