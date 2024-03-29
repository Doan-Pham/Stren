package com.haidoan.android.stren.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@Composable
fun SimpleConfirmationDialog(
    onDismissRequest: () -> Unit,
    title: String,
    body: String,
    onDismissClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmClick()
                    onDismissRequest()
                }
            ) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissClick()
                    onDismissRequest()
                }
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

@Composable
fun SimpleConfirmationDialog(
    state: ConfirmationDialogState
) {
    AlertDialog(
        onDismissRequest = state.onDismissDialog,
        confirmButton = {
            TextButton(
                onClick = {
                    state.onConfirmClick()
                    state.onDismissDialog()
                }
            ) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = state.onDismissDialog
            ) {
                Text(text = "Dismiss")
            }
        },
        title = {
            Text(text = state.title)
        },
        text = {
            Text(text = state.body)
        },
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White
    )
}

data class ConfirmationDialogState constructor(
    val title: String,
    val body: String,
    val onConfirmClick: () -> Unit,
    val onDismissDialog: () -> Unit
) {
    companion object {
        val undefined = ConfirmationDialogState(
            title = "title",
            body = "Body",
            onConfirmClick = {},
            onDismissDialog = {})
    }
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
        Column(
            modifier = Modifier
                .wrapContentHeight(unbounded = true)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(dimensionResource(id = R.dimen.padding_large))
        ) {

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
                    inputValue = it
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleSelectionDialog(
    state: SingleSelectionDialogState,
) {
    AlertDialog(
        onDismissRequest = state.onDismissDialog,
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(dimensionResource(id = R.dimen.padding_large))
        ) {
            Text(text = state.title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.size(dimensionResource(R.dimen.padding_medium)))

            var selectedIndex by remember {
                mutableStateOf(0)
            }
            RadioGroup(
                modifier = Modifier.fillMaxWidth(),
                radioOptions = state.options.map { option ->
                    { modifier ->
                        Text(modifier = modifier, text = option)
                    }
                },
                selectedOptionIndex = selectedIndex,
                onOptionSelected = {
                    selectedIndex = it
                })

            Spacer(Modifier.size(dimensionResource(R.dimen.padding_medium)))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StrenTextButton(
                    modifier = Modifier.weight(1f),
                    onClickHandler = state.onDismissDialog,
                    text = "Cancel",
                    textStyle = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.size(dimensionResource(id = R.dimen.padding_small)))
                StrenFilledButton(
                    modifier = Modifier.weight(1f),
                    onClickHandler = {
                        state.onConfirmClick(selectedIndex)
                        state.onDismissDialog()
                    },
                    text = "Confirm",
                    textStyle = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleSelectionDialog(
    title: String,
    options: List<@Composable (Modifier) -> Unit>,
    onIndexChange: (index: Int) -> Unit = {},
    onConfirmClick: (selectedOptionIndex: Int) -> Unit,
    onDismissDialog: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissDialog,
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(dimensionResource(id = R.dimen.padding_large))
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.size(dimensionResource(R.dimen.padding_medium)))

            var selectedIndex by remember {
                mutableStateOf(0)
            }
            RadioGroup(
                modifier = Modifier.fillMaxWidth(),
                radioOptions = options,
                selectedOptionIndex = selectedIndex,
                onOptionSelected = {
                    selectedIndex = it
                })

            Spacer(Modifier.size(dimensionResource(R.dimen.padding_medium)))

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
                        onConfirmClick(selectedIndex)
                        onDismissDialog()
                    },
                    text = "Confirm",
                    textStyle = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

data class SingleSelectionDialogState constructor(
    val title: String,
    val options: List<String>,
    val onConfirmClick: (selectedOptionIndex: Int) -> Unit,
    val onDismissDialog: () -> Unit,
) {
    companion object {
        val undefined = SingleSelectionDialogState(
            title = "Title",
            options = listOf(),
            onConfirmClick = {},
            onDismissDialog = {})
    }
}