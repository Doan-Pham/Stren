package com.haidoan.android.stren.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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

@Composable
fun CalendarDialog(
    onDismissDialog: () -> Unit,
    selectedDate: LocalDate,
    datesThatHaveWorkouts: List<LocalDate>
) {
    Dialog(
        onDismissRequest = onDismissDialog,
        properties = DialogProperties(
            usePlatformDefaultWidth = false // experimental
        )
    ) {
        Surface(
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
            VerticalCalendar(
                state = state,
                dayContent = {
                    Day(
                        day = it,
                        isSelected = it.date.isEqual(selectedDate),
                        haveWorkouts = datesThatHaveWorkouts.any { date -> date.isEqual(it.date) }
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
private fun Day(day: CalendarDay, isSelected: Boolean, haveWorkouts: Boolean) {
    val backgroundColor = if (isSelected) Red60 else Color.White
    val textColor = if (isSelected) Color.White else Color.Black
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(15.dp))
            .background(backgroundColor)
            .padding(dimensionResource(id = R.dimen.padding_small)),
        contentAlignment = Alignment.Center
    ) {
        if (day.position == DayPosition.MonthDate) {
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