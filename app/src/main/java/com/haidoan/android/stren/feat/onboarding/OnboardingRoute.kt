package com.haidoan.android.stren.feat.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.haidoan.android.stren.R
import com.haidoan.android.stren.core.designsystem.component.*
import com.haidoan.android.stren.core.model.ActivityLevel
import com.haidoan.android.stren.core.model.WeightGoal
import kotlinx.coroutines.launch

internal const val USER_ID_ONBOARDING_NAV_ARG = "USER_ID_ONBOARDING_NAV_ARG"

@Composable
internal fun OnboardingRoute(
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel(),
    onCompleteOnboarding: () -> Unit,
) {
    if (viewModel.isOnboardingComplete) {
        onCompleteOnboarding()
    }
    OnboardingScreen(
        modifier = modifier,
        uiState = viewModel.uiState,
        onUiStateChange = { viewModel.uiState = it },
        onCompleteOnboarding = viewModel::completeOnboarding
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OnboardingScreen(
    modifier: Modifier = Modifier,
    uiState: OnboardingUiState,
    onUiStateChange: (OnboardingUiState) -> Unit,
    onCompleteOnboarding: () -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        val pagerState = rememberPagerState()
        var isError by remember {
            mutableStateOf(false)
        }
        val coroutineScope = rememberCoroutineScope()

        val onboardingScreensComposables = listOf<@Composable () -> Unit>({
            BasicProfileOnboardingScreen(onErrorStatusChange = { isError = it },
                uiState = uiState,
                onUiStateChange = { onUiStateChange(it) })
        }, {
            ActivityLevelOnboardingScreen(activityLevels = uiState.activityLevels,
                onSelectActivityLevel = { onUiStateChange(uiState.copy(selectedActivityLevel = it)) })
        }, {
            GoalOnboardingScreen(weightGoals = uiState.weightGoals,
                onSelectWeightGoal = { onUiStateChange(uiState.copy(selectedWeightGoal = it)) })
        })

        StrenHorizontalPager(
            modifier = Modifier.weight(1f),
            pagerState = pagerState,
            userScrollEnabled = false,
            contents = onboardingScreensComposables,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AnimatedVisibility(
                modifier = Modifier.weight(1f), visible = pagerState.currentPage != 0
            ) {
                StrenTextButton(
                    enabled = pagerState.currentPage != 0, onClickHandler = {
                        if (pagerState.currentPage != 0) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    }, text = "Previous", textStyle = MaterialTheme.typography.bodyMedium
                )
            }

            StrenFilledButton(
                modifier = Modifier.weight(1f),
                onClickHandler = {
                    if (pagerState.currentPage != onboardingScreensComposables.size - 1) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        onCompleteOnboarding()
                    }
                },
                text = "Next",
                textStyle = MaterialTheme.typography.bodyMedium,
                enabled = !isError
            )
        }
    }
}

@Composable
private fun BasicProfileOnboardingScreen(
    modifier: Modifier = Modifier,
    onErrorStatusChange: (Boolean) -> Unit,
    uiState: OnboardingUiState,
    onUiStateChange: (OnboardingUiState) -> Unit,
) {
    val weight = uiState.weight
    val height = uiState.height
    val age = uiState.age
    val displayName = uiState.displayName

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = dimensionResource(id = R.dimen.padding_medium))
                .fillMaxWidth(),
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        StrenOutlinedTextField(
            text = displayName,
            onTextChange = {
                onUiStateChange(uiState.copy(displayName = it))
            },
            label = "Name",
            isError = displayName.isBlank(),
            errorText = "Field can't be empty"
        )

        ExposedDropDownMenuTextField(modifier = Modifier.fillMaxWidth(),
            textFieldLabel = "Sex",
            selectedText = uiState.sex.name,
            menuItemsTextAndClickHandler = uiState.sexes.associate {
                it.name to { onUiStateChange(uiState.copy(sex = it)) }
            }
        )
        OutlinedNumberTextField(
            modifier = Modifier.fillMaxWidth(),
            number = age,
            onValueChange = {
                onUiStateChange(uiState.copy(age = it))
            },
            label = "Age",
            isError = age == 0L,
            errorText = "Field can't be empty"
        )

        OutlinedNumberTextField(
            modifier = Modifier.fillMaxWidth(),
            number = weight,
            onValueChange = {
                onUiStateChange(uiState.copy(weight = it))
            },
            label = "Weight",
            suffixText = "kg",
            isError = weight == 0F,
            errorText = "Field can't be empty"
        )
        OutlinedNumberTextField(
            modifier = Modifier.fillMaxWidth(),
            number = height,
            onValueChange = {
                onUiStateChange(uiState.copy(height = it))
            },
            label = "Height",
            suffixText = "cm",
            isError = height == 0F,
            errorText = "Field can't be empty"
        )
        val isError = displayName.isBlank() || weight == 0f || height == 0F || age == 0L
        onErrorStatusChange(isError)
    }
}


@Composable
private fun ActivityLevelOnboardingScreen(
    modifier: Modifier = Modifier,
    activityLevels: List<ActivityLevel>,
    onSelectActivityLevel: (ActivityLevel) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = dimensionResource(id = R.dimen.padding_medium))
                .fillMaxWidth(),
            text = "Activity Level",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        var selectedIndex by remember {
            mutableStateOf(0)
        }
        RadioGroup(
            modifier = Modifier.fillMaxWidth(),
            radioOptions = activityLevels.map { activityLevel ->
                { modifier ->
                    TitleAndSubtitleColumn(
                        modifier = modifier,
                        title = activityLevel.activityLevelName,
                        subtitle = activityLevel.description
                    )
                }

            },
            selectedOptionIndex = selectedIndex,
            onOptionSelected = {
                selectedIndex = it
                onSelectActivityLevel(activityLevels[it])
            })
    }
}

@Composable
private fun GoalOnboardingScreen(
    modifier: Modifier = Modifier,
    weightGoals: List<WeightGoal>,
    onSelectWeightGoal: (WeightGoal) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = dimensionResource(id = R.dimen.padding_medium))
                .fillMaxWidth(),
            text = "Goal",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        var selectedIndex by remember {
            mutableStateOf(0)
        }
        RadioGroup(
            modifier = Modifier.fillMaxWidth(),
            radioOptions = weightGoals.map { goal ->
                { modifier ->
                    TitleAndSubtitleColumn(
                        modifier = modifier,
                        title = goal.weightGoalName,
                        subtitle = goal.description
                    )
                }

            },
            selectedOptionIndex = selectedIndex,
            onOptionSelected = {
                selectedIndex = it
                onSelectWeightGoal(weightGoals[it])
            })

        Text(
            modifier = Modifier
                .padding(vertical = dimensionResource(id = R.dimen.padding_medium))
                .fillMaxWidth(),
            text = "*Note: Trying to lose/gain weight faster than the above rates without consulting " +
                    "a professional may lead to unhealthy side effects",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun TitleAndSubtitleColumn(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String = ""
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )
        if (subtitle.isNotBlank()) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}