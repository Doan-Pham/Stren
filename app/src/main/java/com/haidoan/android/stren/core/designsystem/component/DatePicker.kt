package com.haidoan.android.stren.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.haidoan.android.stren.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerWithDialog(
    modifier: Modifier = Modifier,
    value: LocalDate?,
    yearRange: IntRange = DatePickerDefaults.YearRange,
    dateFormatter: (LocalDate) -> String,
    label: String,
    enabled: Boolean = true,
    dateValidator: (Long) -> Boolean = { true },
    onChange: (LocalDate?) -> Unit,
) {
    var openDialog by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = value?.atStartOfDay()?.toEpochSecond(ZoneOffset.UTC)
            ?.times(1000),
        yearRange = yearRange
    )

    Box(modifier = modifier) {
        StrenOutlinedTextField(
            text = value?.let(dateFormatter).orEmpty(),
            onTextChange = {},
            readOnly = true,
            enabled = enabled,
            label = label,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = ""
                )
            },
            trailingIcon = null
        )
        Box(
            Modifier
                .clickable(enabled = enabled) { openDialog = true }
                .matchParentSize()) {
        }
    }

    if (openDialog) {
        val confirmEnabled by remember { derivedStateOf { datePickerState.selectedDateMillis != null } }
        DatePickerDialog(
            onDismissRequest = {
                openDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog = false
                        onChange(datePickerState.selectedDateMillis?.let {
                            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        })
                    },
                    enabled = enabled && confirmEnabled
                ) {
                    Text(stringResource(id = android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog = false
                    }
                ) {
                    Text(stringResource(id = android.R.string.cancel))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                dateValidator = dateValidator,
            )
        }
    }
}