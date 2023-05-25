package com.haidoan.android.stren.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.core.designsystem.theme.Green70
import com.haidoan.android.stren.core.designsystem.theme.Red60
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*


/**
 * @param markedDates Dates that should be marked with some special sign (Ex use case: dates that user tracked calories, dates that user had workouts)
 */
@Composable
fun CalendarDialog(
    onDismissDialog: () -> Unit,
    onSelectDate: (LocalDate) -> Unit,
    selectedDate: LocalDate,
    markedDates: List<LocalDate>
) {
    Dialog(
        onDismissRequest = onDismissDialog,
        properties = DialogProperties(
            usePlatformDefaultWidth = false // experimental
        )
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
        ) {
            val currentMonth = remember { YearMonth.from(selectedDate); }
            val startMonth = remember { currentMonth.minusMonths(100) }
            val endMonth = remember { currentMonth.plusMonths(100) }
            val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY)

            val state = rememberCalendarState(
                startMonth = startMonth,
                endMonth = endMonth,
                firstVisibleMonth = currentMonth,
                firstDayOfWeek = daysOfWeek.first()
            )
            StrenSmallTopAppBar(
                appBarConfiguration = AppBarConfiguration.NavigationAppBar(
                    title = "Select Date",
                    navigationIcon = IconButtonInfo.BACK_ICON.copy(clickHandler = onDismissDialog)
                )
            )
            VerticalCalendar(
                state = state,
                dayContent = {
                    Day(
                        day = it,
                        isSelected = it.date.isEqual(selectedDate),
                        haveWorkouts = markedDates.any { date -> date.isEqual(it.date) },
                        onClickHandler = { newlySelectedDate ->
                            if (!newlySelectedDate.isEqual(selectedDate)) {
                                onSelectDate(newlySelectedDate)
                                onDismissDialog()
                            }
                        }
                    )
                },
                monthHeader = {
                    MonthHeader(it)
                    DaysOfWeekTitle(daysOfWeek = daysOfWeek)
                }
            )
        }
    }
}

@Composable
private fun Day(
    day: CalendarDay,
    isSelected: Boolean,
    haveWorkouts: Boolean,
    onClickHandler: (LocalDate) -> Unit
) {
    val backgroundColor = if (isSelected) Red60 else Color.White
    val textColor = if (isSelected) Color.White else Color.Black
    if (day.position == DayPosition.MonthDate) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(15.dp))
                .clickable { onClickHandler(day.date) }
                .background(backgroundColor)
                .padding(dimensionResource(id = R.dimen.padding_small)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier,
                text = day.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = textColor,
                textAlign = TextAlign.Center
            )
            if (haveWorkouts) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Green70)
                )
            }
        }
    }

}

@Composable
private fun MonthHeader(month: CalendarMonth) {
    Text(
        modifier = Modifier
            .padding(top = dimensionResource(id = R.dimen.padding_medium))
            .fillMaxWidth(),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleLarge,
        text = month.yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
    )
}

@Composable
private fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleSmall,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            )
        }
    }
}