package com.haidoan.android.stren.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.haidoan.android.stren.R
import com.haidoan.android.stren.core.designsystem.theme.Gray90
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@Composable
fun CalendarDialog(onDismissDialog: () -> Unit) {
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
            val currentMonth = remember { YearMonth.now() }
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
                dayContent = { Day(it) },
                monthHeader = {
                    MonthHeader(it)
                    DaysOfWeekTitle(daysOfWeek = daysOfWeek)
                }
            )
        }
    }
}

@Composable
private fun Day(day: CalendarDay) {
    Box(
        modifier = Modifier
            .aspectRatio(1f), // This is important for square sizing!
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = if (day.position == DayPosition.MonthDate) Color.Black else Gray90
        )
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