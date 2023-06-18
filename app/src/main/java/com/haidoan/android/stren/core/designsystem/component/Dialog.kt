package com.haidoan.android.stren.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.haidoan.android.stren.R
import com.haidoan.android.stren.core.utils.DateUtils
import com.haidoan.android.stren.core.utils.DateUtils.defaultFormat
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import java.time.LocalDate


@Composable
fun SimpleConfirmationDialog(
    onDismissDialog: () -> Unit,
    title: String,
    body: String,
    onConfirmClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissDialog,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmClick()
                    onDismissDialog()
                }
            ) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissDialog
            ) {
                Text(text = "Dismiss")
            }
        },
        title = {
            Text(text = title)
        },
        text = {
            Text(text = body)
        },
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMeasurementDialog(
    onDismissDialog: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissDialog,
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
        ) {
            Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))) {

                Text(text = "Add Measurement", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.size(dimensionResource(R.dimen.padding_medium)))

                //TODO: Measurement biometrics name
                ExposedDropDownMenuTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = dimensionResource(id = R.dimen.padding_small)
                        ),
                    textFieldLabel = "Measured biometrics",
                    selectedText = "ha",
                    menuItemsTextAndClickHandler = mapOf("ha" to {}, "wa" to {})
                )
                // TODO: value
                var value by remember { mutableStateOf(0f) }
                OutlinedNumberTextField(
                    modifier = Modifier.fillMaxWidth(),
                    number = value,
                    onValueChange = {
                        // TODO: Change value
                        value = it.toFloat()
                    },
                    label = "Value",
                    suffixText = "cm",
                    isError = value <= 0f,
                    errorText = "Invalid value"
                )
                Spacer(Modifier.size(dimensionResource(R.dimen.padding_small)))

                // Date picker
                var shouldShowCalendarDialog by remember { mutableStateOf(false) }
                val calendarState = rememberUseCaseState(
                    visible = true,
                    onCloseRequest = { shouldShowCalendarDialog = false })

                var selectedDate: LocalDate by remember { mutableStateOf(DateUtils.getCurrentDate()) }

                if (shouldShowCalendarDialog) {
                    CalendarDialog(
                        state = calendarState,
                        selection = CalendarSelection.Date { date ->
                            selectedDate = date
                        },
                    )
                }


                StrenOutlinedTextField(
                    modifier = Modifier
                        .clickable { calendarState.show() }
                        .fillMaxWidth(),
                    enabled = false,
                    readOnly = true,
                    text = selectedDate.defaultFormat(),
                    onTextChange = {},
                    label = "Date",
                    isError = false,
                    errorText = ""
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StrenTextButton(
                        modifier = Modifier.weight(1f),
                        onClickHandler = onDismissDialog,
                        text = "Cancel",
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.size(dimensionResource(id = R.dimen.padding_small)))
                    StrenFilledButton(
                        modifier = Modifier.weight(1f),
                        onClickHandler = {
                            // TODO: On Save
                        },
                        text = "Save",
                        enabled = value > 0f,
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}