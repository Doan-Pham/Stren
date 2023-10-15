package com.haidoan.android.stren.feat.training.programs.view_programs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo

@Composable
internal fun TrainingProgramsRoute(
    modifier: Modifier = Modifier,
    viewModel: TrainingProgramsViewModel = hiltViewModel(),
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
    onNavigateToAddProgramScreen: (userId: String) -> Unit,
) {
    val trainingHistoryAppBarConfiguration = AppBarConfiguration.NavigationAppBar(
        actionIcons = listOf(
            IconButtonInfo(drawableResourceId = R.drawable.ic_add,
                description = "Menu Item Add",
                clickHandler = {
                    onNavigateToAddProgramScreen(viewModel.userId)
                }),
            IconButtonInfo(
                drawableResourceId = R.drawable.ic_search,
                description = "Menu Item Search",
                clickHandler = {
                    //TODO: Implement search program
//                    val searchBarConfiguration = AppBarConfiguration.SearchAppBar(
//                        text = viewModel.searchBarText,
//                        placeholder = "Search routine",
//                        onTextChange = {
//                            viewModel.searchBarText.value = it
//                            viewModel.searchRoutineByName(it)
//                        },
//                        shouldShowSearchIcon = false,
//                        onSearchClicked = { viewModel.searchRoutineByName(it) })
//                    appBarConfigurationChangeHandler(searchBarConfiguration)
                })
        )
    )
    var isAppBarConfigured by remember { mutableStateOf(false) }
    if (!isAppBarConfigured) {
        appBarConfigurationChangeHandler(trainingHistoryAppBarConfiguration)
        isAppBarConfigured = true
    }

    TrainingProgramsScreen(modifier = modifier)
}

@Composable
private fun TrainingProgramsScreen(modifier: Modifier = Modifier) {
    Scaffold {
        Box(
            modifier = modifier
                .padding(it)
                .fillMaxSize()
                .background(Color.Blue)
        )
    }
}