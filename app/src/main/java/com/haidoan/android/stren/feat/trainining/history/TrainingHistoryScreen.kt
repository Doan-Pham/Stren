package com.haidoan.android.stren.feat.trainining.history

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.core.designsystem.component.*
import com.haidoan.android.stren.core.designsystem.theme.Gray50
import com.haidoan.android.stren.core.designsystem.theme.Gray90
import com.haidoan.android.stren.core.designsystem.theme.Gray95
import com.haidoan.android.stren.core.utils.DateUtils
import timber.log.Timber
import java.time.LocalDate


@Composable
internal fun TrainingHistoryRoute(
    modifier: Modifier = Modifier,
    viewModel: TrainingHistoryViewModel = hiltViewModel(),
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
) {
    val trainingHistoryAppBarConfiguration = AppBarConfiguration.NavigationAppBar(
        actionIcons = listOf(
            IconButtonInfo(drawableResourceId = R.drawable.ic_add,
                description = "MenuItem-Add",
                clickHandler = {
                    //TODO: Implement "add" menu item
                }),
            IconButtonInfo(drawableResourceId = R.drawable.ic_calendar,
                description = "MenuItem-Calendar",
                clickHandler = {
                    //TODO: Implement "calendar" menu item
                })
        )
    )
    var isAppBarConfigured by remember { mutableStateOf(false) }
    if (!isAppBarConfigured) {
        appBarConfigurationChangeHandler(trainingHistoryAppBarConfiguration)
        isAppBarConfigured = true
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    TrainingHistoryScreen(
        modifier = modifier,
        uiState = uiState
    )
}

@SuppressLint("NewApi")
@Composable
internal fun TrainingHistoryScreen(
    modifier: Modifier = Modifier,
    uiState: TrainingHistoryUiState
) {
    when (uiState) {
        TrainingHistoryUiState.Loading -> {
            Timber.d("Loading")
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingAnimation()
            }
        }
        is TrainingHistoryUiState.LoadComplete -> {

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
            ) {
                val currentMonth = uiState.currentDate.month.name.toLowerCase(Locale.current)
                    .capitalize(Locale.current)
                val currentYear = uiState.currentDate.year
                val currentDate = uiState.currentDate

                MonthYearHeader(
                    modifier = Modifier.fillMaxWidth(),
                    headerTitle = "$currentMonth $currentYear"
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DateUtils.getAllWeekDays(uiState.currentDate).forEach { dateInWeek ->
                        DateItem(
                            date = dateInWeek,
                            isSelected = dateInWeek.isEqual(currentDate),
                            isDateNotInCurrentMonth = dateInWeek.monthValue != currentDate.monthValue
                        )
                    }
                }

            }
            Timber.d("currentDate: ${uiState.currentDate}")
            Timber.d("currentDaysOfWeek: ${DateUtils.getAllWeekDays(uiState.currentDate)}")
            Timber.d("workouts: ${uiState.workouts}")
        }
    }

}

@Composable
private fun MonthYearHeader(modifier: Modifier = Modifier, headerTitle: String) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_left),
                contentDescription = "Icon arrow left"
            )
        }
        Text(
            modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_medium)),
            text = headerTitle,
            style = MaterialTheme.typography.titleMedium
        )
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = "Icon arrow left"
            )
        }
    }
}

@Composable
private fun DateItem(date: LocalDate, isSelected: Boolean, isDateNotInCurrentMonth: Boolean) {
    var backgroundColorStart = Gray90
    var backgroundColorEnd = Gray90
    var textColor = Gray50

    if (isSelected) {
        backgroundColorStart = MaterialTheme.colorScheme.primary
        backgroundColorEnd = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        textColor = Color.White
    } else if (isDateNotInCurrentMonth) {
        backgroundColorStart = Gray95
        backgroundColorEnd = Gray95
        textColor = Gray50
    }
    Column(
        modifier = Modifier
            .width(dimensionResource(id = R.dimen.icon_size_extra_large))
            .clip(RoundedCornerShape(5.dp))
            .background(
                brush = Brush.linearGradient(
                    0f to backgroundColorStart,
                    1f to backgroundColorEnd
                )
            )
            .padding(
                vertical = dimensionResource(id = R.dimen.padding_small),
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = date.dayOfWeek.name.first().toString(),
            style = MaterialTheme.typography.bodySmall,
            color = textColor
        )
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.titleSmall,
            color = textColor
        )
    }
}
