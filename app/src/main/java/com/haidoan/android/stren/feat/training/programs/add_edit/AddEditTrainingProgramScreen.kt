package com.haidoan.android.stren.feat.training.programs.add_edit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haidoan.android.stren.R
import com.haidoan.android.stren.app.navigation.AppBarConfiguration
import com.haidoan.android.stren.app.navigation.IconButtonInfo
import com.haidoan.android.stren.core.designsystem.component.DatePickerWithDialog
import com.haidoan.android.stren.core.designsystem.component.DropDownMenuScaffold
import com.haidoan.android.stren.core.designsystem.component.OutlinedNumberTextField
import com.haidoan.android.stren.core.designsystem.component.SimpleConfirmationDialog
import com.haidoan.android.stren.core.designsystem.component.StrenOutlinedTextField
import com.haidoan.android.stren.core.designsystem.theme.Gray60
import com.haidoan.android.stren.core.designsystem.theme.Gray90
import com.haidoan.android.stren.core.designsystem.theme.Green70
import com.haidoan.android.stren.core.designsystem.theme.Red60
import com.haidoan.android.stren.core.model.Routine
import com.haidoan.android.stren.core.utils.DateUtils.defaultFormat
import com.haidoan.android.stren.feat.training.TrainingViewModel
import java.time.LocalDate


private val daysInWeek = listOf("M", "T", "W", "T", "F", "S", "S")

@Composable
internal fun AddEditTrainingProgramsRoute(
    modifier: Modifier = Modifier,
    trainingViewModel: TrainingViewModel,
    viewModel: AddEditTrainingProgramViewModel = hiltViewModel(),
    onNavigateToAddRoutineScreen: (dayOffset: Int) -> Unit,
    onNavigateToEditRoutineScreen: (routineId: String) -> Unit,
    appBarConfigurationChangeHandler: (AppBarConfiguration) -> Unit,
) {
    val routinesForTrainingProgram by trainingViewModel.routinesForTrainingProgram.collectAsStateWithLifecycle()
    val routinesIdsByDayOffset by trainingViewModel.routinesIdsByDayOffset.collectAsStateWithLifecycle()
    val selectedDayGlobalOffset by viewModel.selectedDayOffset.collectAsStateWithLifecycle()
    val routinesOfSelectedDate by viewModel.routinesOfSelectedDate.collectAsStateWithLifecycle()
    val programTotalNumOfWeeks by viewModel.programTotalNumOfWeeks.collectAsStateWithLifecycle()
    val programStartDate by viewModel.programStartDate.collectAsStateWithLifecycle()
    val dayOffsetsWithWorkouts by viewModel.dayOffsetsWithWorkouts.collectAsStateWithLifecycle(initialValue = emptySet())

    LaunchedEffect(key1 = routinesForTrainingProgram, block = {
        viewModel.updateRoutines(routinesForTrainingProgram)
    })

    LaunchedEffect(key1 = routinesIdsByDayOffset, block = {
        viewModel.updateRoutinesIdsByDayOffset(routinesIdsByDayOffset)
    })

    LaunchedEffect(key1 = Unit, block = {
        viewModel.addDisposable {
            trainingViewModel.clearRoutinesOfTrainingProgram()
            trainingViewModel.clearRoutinesIdsByDayOffset()
        }
    })

    var showDeleteDialog by remember { mutableStateOf(false) }
    var routineIdToDelete by remember { mutableStateOf("") }
    if (showDeleteDialog) {
        SimpleConfirmationDialog(
            onDismissDialog = { showDeleteDialog = false },
            title = "Delete Confirmation",
            body = "Are you sure want to delete this workout?"
        ) {
            trainingViewModel.removeRoutineFromDay(selectedDayGlobalOffset, routineIdToDelete)
        }
    }

    AddEditTrainingProgramsScreen(
        modifier = modifier,
        programStartDate = programStartDate,
        onProgramStartDateChange = viewModel::onProgramStartDateChange,
        programTotalNumOfWeeks = programTotalNumOfWeeks,
        onProgramTotalNumOfWeeksChange = viewModel::onProgramTotalNumOfWeeksChange,
        programName = viewModel.programName.value,
        onProgramNameChange = viewModel::onProgramNameChange,
        selectedDayGlobalOffset = selectedDayGlobalOffset,
        selectDay = viewModel::selectDate,
        routinesOfSelectedDate = routinesOfSelectedDate,
        dayOffsetsWithWorkouts = dayOffsetsWithWorkouts,
        addRoutine = { onNavigateToAddRoutineScreen(it) },
        editRoutine = { onNavigateToEditRoutineScreen(it) },
        deleteRoutine = {
            routineIdToDelete = it
            showDeleteDialog = true
        }
    )

    val addEditProgramAppBarConfiguration = AppBarConfiguration.NavigationAppBar(
        title = "Program",
        navigationIcon = IconButtonInfo.BACK_ICON,
        actionIcons = listOf(
            IconButtonInfo(
                drawableResourceId = R.drawable.ic_save,
                description = "Menu Item Save",
                clickHandler = {
                    // TODO: AppBar
//                    if (uiState is AddEditProgramUiState.EmptyProgram) {
//                        coroutineScope.launch {
//                            snackbarHostState.showSnackbar(
//                                message = "Empty program",
//                                duration = SnackbarDuration.Short,
//                                withDismissAction = true
//                            )
//                        }
//                    } else if (uiState is AddEditProgramUiState.IsEditing) {
//                        val programName = viewModel.routineNameTextFieldValue
//                        Timber.d("Save clicked - programName: $routineName")
//
//                        if (programName.isBlank() || programName.isEmpty()) {
//                            coroutineScope.launch {
//                                snackbarHostState.showSnackbar(
//                                    message = "Please input program name",
//                                    duration = SnackbarDuration.Short,
//                                    withDismissAction = true
//                                )
//                            }
//                        } else {
//                            viewModel.addEditProgram()
//                            onBackToPreviousScreen()
//                        }
//                    }
                }
            ),
        )
    )

    var isAppBarConfigured by remember { mutableStateOf(false) }
    if (!isAppBarConfigured) {
        appBarConfigurationChangeHandler(addEditProgramAppBarConfiguration)
        isAppBarConfigured = true
    }
}

@OptIn(ExperimentalStdlibApi::class, ExperimentalFoundationApi::class)
@Composable
private fun AddEditTrainingProgramsScreen(
    modifier: Modifier = Modifier,

    // PROGRAM_START_DATE
    programStartDate: LocalDate,
    onProgramStartDateChange: (LocalDate) -> Unit,

    // PROGRAM_DURATION
    programTotalNumOfWeeks: Int,
    onProgramTotalNumOfWeeksChange: (Int) -> Unit,

    // PROGRAM_NAME
    programName: String,
    onProgramNameChange: (String) -> Unit,

    // SELECTED_TRAINING_DAY
    selectedDayGlobalOffset: Int,
    selectDay: (dayOffset: Int) -> Unit,
    routinesOfSelectedDate: List<Routine>,

    // TRAINING_DAYS_THAT_HAVE_WORKOUTS
    dayOffsetsWithWorkouts: Set<Int>,

    // MODIFY_ROUTINES_OF_TRAINING_DAY
    addRoutine: (dayOffset: Int) -> Unit,
    editRoutine: (routineId: String) -> Unit,
    deleteRoutine: (routineId: String) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(id = R.dimen.padding_medium)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val numOfDaysPerWeek = DEFAULT_NUM_OF_DAYS_PER_WEEK
            val numOfWeeksPerGroup = DEFAULT_NUM_OF_WEEKS_PER_GROUP
            val selectedDayWeekIndex = selectedDayGlobalOffset / numOfDaysPerWeek
            val selectedDayWeeklyOffset = selectedDayGlobalOffset % numOfDaysPerWeek

            val localizedDayOffsetsWithWorkouts = dayOffsetsWithWorkouts.map {
                val weekIndex = it / numOfDaysPerWeek
                val weeklyOffset = it % numOfDaysPerWeek
                Pair(weekIndex, weeklyOffset)
            }

            //region PROGRAM's NAME
            StrenOutlinedTextField(
                text = programName,
                onTextChange = onProgramNameChange,
                label = "Program name",
                isError = programName.isBlank() || programName.isEmpty(),
                errorText = "Program name can't be empty"
            )

            //endregion

            //region PROGRAM'S DURATION & PROGRAM's START DATE
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                OutlinedNumberTextField(
                    modifier = Modifier.weight(1f),
                    number = programTotalNumOfWeeks,
                    label = "Duration",
                    onValueChange = {
                        onProgramTotalNumOfWeeksChange(
                            it.coerceIn(
                                (MIN_NUM_OF_WEEKS - 1)..MAX_NUM_OF_WEEKS
                            )
                        )
                    },
                    suffixText = "Weeks",
                    isError = programTotalNumOfWeeks < MIN_NUM_OF_WEEKS,
                    errorText = "Duration can't be 0"
                )

                DatePickerWithDialog(
                    modifier = Modifier.weight(1.5f),
                    value = programStartDate,
                    dateFormatter = {
                        it.defaultFormat()
                    },
                    label = "Start Date",
                    onChange = {
                        it?.let { onProgramStartDateChange(it) }
                    },
                    yearRange = programStartDate.year - 1..programStartDate.year + 3
                )
            }

            //endregion

            Divider(Modifier.padding(vertical = dimensionResource(id = R.dimen.padding_medium)))

            //region SCHEDULE
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Schedule",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )

            val listState = rememberLazyListState()

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
            ) {
                for (startWeekIndex in 0..<programTotalNumOfWeeks step numOfWeeksPerGroup) {
                    item {
                        val numOfWeeks =
                            (programTotalNumOfWeeks - startWeekIndex)
                                .coerceAtMost(numOfWeeksPerGroup)

                        val endWeekIndex = startWeekIndex + numOfWeeks - 1

                        val selectedDayWeekGroupOffset =
                            if (selectedDayWeekIndex in startWeekIndex..endWeekIndex) {
                                (selectedDayWeekIndex - startWeekIndex) * numOfDaysPerWeek +
                                        selectedDayWeeklyOffset
                            } else {
                                null
                            }

                        TrainingWeekGroup(
                            startWeekIndex = startWeekIndex,
                            numOfWeek = numOfWeeks,
                            onSelectDay = {
                                val selectedDayOffsetGlobal = startWeekIndex * numOfDaysPerWeek + it
                                selectDay(selectedDayOffsetGlobal)
                            },
                            dayOffsetsWithWorkouts =
                            localizedDayOffsetsWithWorkouts
                                .filter { it.first in startWeekIndex..endWeekIndex }
                                .map {
                                    (it.first - startWeekIndex) * numOfDaysPerWeek + it.second
                                }.toSet(),
                            selectedDayWeekGroupOffset = selectedDayWeekGroupOffset
                        )
                    }
                }

            }

            //endregion

            Divider(
                Modifier.padding(
                    top = dimensionResource(id = R.dimen.padding_large),
                    bottom = dimensionResource(id = R.dimen.padding_medium)
                )
            )

            //region ROUTINES OF SELECTED DATE
            if (programTotalNumOfWeeks > MIN_NUM_OF_WEEKS) {
                // TITLE & ADD_ROUTINE BUTTON
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "Week ${selectedDayWeekIndex + 1} - Day ${selectedDayWeeklyOffset + 1}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    IconButton(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        onClick = {
                            addRoutine(selectedDayGlobalOffset)
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add),
                            contentDescription = "Back Icon",
                        )
                    }
                }

                routinesOfSelectedDate.forEach { routine ->
                    RoutineItem(
                        routine = routine,
                        onEditRoutineClickHandler = {
                            editRoutine(routine.id)
                        },
                        onDeleteRoutineClick = {
                            deleteRoutine(routine.id)
                        })
                    Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.padding_medium)))
                }
            }
            //endregion
        }
    }
}

//region CALENDAR
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TrainingWeekGroup(
    startWeekIndex: Int,
    numOfWeek: Int,
    selectedDayWeekGroupOffset: Int? = null,
    dayOffsetsWithWorkouts: Set<Int>,
    onSelectDay: (selectedDayOffsetLocalized: Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(8.dp))
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val headerText = if (numOfWeek > 1) {
            "Week ${startWeekIndex + 1} - ${startWeekIndex + numOfWeek}"
        } else {
            "Week ${startWeekIndex + 1}"
        }

        Text(
            text = headerText,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.size(16.dp))

        FlowRow(
            maxItemsInEachRow = 7
        ) {
            repeat(numOfWeek * 7) {
                Day(
                    offset = it,
                    isSelected = selectedDayWeekGroupOffset == it,
                    haveWorkouts = dayOffsetsWithWorkouts.contains(it),
                    onClickHandler = onSelectDay
                )
            }
        }
    }
}


@Composable
private fun Day(
    offset: Int,
    isSelected: Boolean,
    haveWorkouts: Boolean,
    onClickHandler: (Int) -> Unit,
) {
    val backgroundColor = if (isSelected) Red60 else Color.White
    val textColor = if (isSelected) Color.White else Color.Black
    Box(
        modifier = Modifier
            .height(46.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClickHandler(offset) }
            .background(backgroundColor)
            .padding(dimensionResource(id = R.dimen.padding_small)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier,
            text = daysInWeek[offset % daysInWeek.size],
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
            textAlign = TextAlign.Center
        )
        if (haveWorkouts) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(Green70)
            )
        }
    }
}

//endregion

//region ROUTINE LIST

@Composable
private fun RoutineItem(
    routine: Routine,
    // TODO: Routine click
    // onItemClickHandler: (routineId: String) -> Unit,
    onEditRoutineClickHandler: () -> Unit,
    onDeleteRoutineClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = (1.5).dp, color = Gray90, shape = RoundedCornerShape(15.dp))
            .clip(RoundedCornerShape(15.dp))
//            .clickable { onItemClickHandler(routine.id) }
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.Top,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensionResource(id = R.dimen.padding_small)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = routine.name,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.weight(1f))
            DropDownMenuScaffold(
                menuItemsTextAndClickHandler = mapOf(
                    "Edit" to { onEditRoutineClickHandler() },
                    "Delete" to { onDeleteRoutineClick() })
            )
            { onExpandMenu ->
                Icon(
                    modifier = Modifier
                        .clickable {
                            onExpandMenu()
                        }
                        .size(dimensionResource(id = R.dimen.icon_size_medium)),
                    painter = painterResource(id = R.drawable.ic_more_horizontal),
                    contentDescription = "Icon more"
                )
            }

        }

        routine.trainedExercises.subList(0, minOf(routine.trainedExercises.size, 3))
            .forEach {
                Text(
                    text = it.exercise.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray60
                )
            }
    }
}


//endregion