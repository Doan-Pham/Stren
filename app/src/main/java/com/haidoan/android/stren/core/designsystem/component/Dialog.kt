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
import com.haidoan.android.stren.core.model.BiometricsRecord
import com.haidoan.android.stren.core.utils.DateUtils
import com.haidoan.android.stren.core.utils.DateUtils.defaultFormat
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import kotlinx.coroutines.launch
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
    onDismissDialog: () -> Unit,
    biometricsRecords: List<BiometricsRecord>,
    biometricsToAddRecord: BiometricsRecord,
    onSaveClick: (BiometricsRecord) -> Unit
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

                var selectedBiometrics by remember { mutableStateOf(biometricsToAddRecord) }
                var inputValue by remember { mutableStateOf(selectedBiometrics.value) }
                var selectedDate: LocalDate by remember { mutableStateOf(DateUtils.getCurrentDate()) }

                ExposedDropDownMenuTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = dimensionResource(id = R.dimen.padding_small)
                        ),
                    textFieldLabel = "Measured biometrics",
                    selectedText = selectedBiometrics.biometricsName,
                    menuItemsTextAndClickHandler = biometricsRecords.associate {
                        it.biometricsName to {
                            if (it != selectedBiometrics) inputValue = it.value
                            selectedBiometrics = it
                        }
                    }
                )

                OutlinedNumberTextField(
                    modifier = Modifier.fillMaxWidth(),
                    number = inputValue,
                    onValueChange = {
                        inputValue = it.toFloat()
                    },
                    label = "Value",
                    suffixText = selectedBiometrics.measurementUnit,
                    isError = inputValue <= 0f,
                    errorText = "Invalid value"
                )
                Spacer(Modifier.size(dimensionResource(R.dimen.padding_small)))

                val calendarState = rememberUseCaseState(
                    embedded = true,
                    visible = false,
                    onCloseRequest = { })

                CalendarDialog(
                    state = calendarState,
                    selection = CalendarSelection.Date { date ->
                        selectedDate = date
                    },
                )

                val coroutineScope = rememberCoroutineScope()
                StrenOutlinedTextField(
                    modifier = Modifier
                        .clickable {
                            coroutineScope.launch {
                                calendarState.show()
                            }
                        }
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
                            onSaveClick(
                                selectedBiometrics.copy(
                                    value = inputValue,
                                    recordDate = selectedDate
                                )
                            )
                            onDismissDialog()
                        },
                        text = "Save",
                        enabled = inputValue > 0f,
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}