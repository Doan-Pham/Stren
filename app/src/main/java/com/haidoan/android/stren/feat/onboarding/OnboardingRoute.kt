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
import kotlinx.coroutines.launch

@Composable
internal fun OnboardingRoute(
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    OnboardingScreen(
        modifier = modifier,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OnboardingScreen(
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        val pagerState = rememberPagerState()
        var isError by remember {
            mutableStateOf(false)
        }
        val coroutineScope = rememberCoroutineScope()

        val onboardingScreensComposables =
            listOf<@Composable () -> Unit>(
                { BasicProfileOnboardingScreen(onErrorStatusChange = { isError = it }) },
                { ActivityLevelOnboardingScreen() },
                { GoalOnboardingScreen() })


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
                modifier = Modifier.weight(1f), onClickHandler = {
                    if (pagerState.currentPage != onboardingScreensComposables.size - 1) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                }, text = "Next", textStyle = MaterialTheme.typography.bodyMedium,
                enabled = !isError
            )
        }
    }
}

@Composable
private fun BasicProfileOnboardingScreen(
    modifier: Modifier = Modifier,
    onErrorStatusChange: (Boolean) -> Unit,
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
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        ExposedDropDownMenuTextField(
            modifier = Modifier.fillMaxWidth(),
            textFieldLabel = "Sex",
            selectedText = "Male",
            menuItemsTextAndClickHandler = mapOf("Male" to {
                // TODO
            }, "Female" to {
                // TODO
            })
        )
        var age by remember { mutableStateOf(20L) }

        OutlinedNumberTextField(
            modifier = Modifier.fillMaxWidth(),
            number = age,
            onValueChange = { age = it.toLong() },
            label = "Age",
            isError = age == 0L,
            errorText = "Field can't be empty"
        )

        var weight by remember { mutableStateOf(65.5F) }
        var height by remember { mutableStateOf(170L) }

        OutlinedNumberTextField(
            modifier = Modifier.fillMaxWidth(),
            number = weight,
            onValueChange = { weight = it.toFloat() },
            label = "Weight",
            suffixText = "kg",
            isError = weight == 0F,
            errorText = "Field can't be empty"
        )
        OutlinedNumberTextField(
            modifier = Modifier.fillMaxWidth(),
            number = height,
            onValueChange = { height = it.toLong() },
            label = "Height",
            suffixText = "cm",
            isError = height == 0L,
            errorText = "Field can't be empty"
        )
        val isError = weight == 0f || height == 0L || age == 0L
        onErrorStatusChange(isError)
    }
}


private val activityLevelsAndDescription = listOf(
    "Sedentary" to "Little or no exercise",
    "Lightly Active" to "Light exercise/sports 1-3 days/week",
    "Moderately Active" to "Moderate exercise/sports 3-5 days/week",
    "Very Active" to "Hard exercise/sports 6-7 days a week",
    "Athlete" to "Very hard exercise/sports & physical job or training 2x per day"

)
private val weightGoals = listOf(
    "Lose Weight" to "(-0.5kg/week)",
    "Lose Weight" to "(-0.25kg/week)",
    "Maintain Weight" to "",
    "Gain Weight" to "(+0.25kg/week)",
    "Gain Weight" to "(+0.5kg/week)"
)

@Composable
private fun ActivityLevelOnboardingScreen(
    modifier: Modifier = Modifier,
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
            radioOptions = activityLevelsAndDescription.map { activityLevelAndDescription ->
                { modifier ->
                    TitleAndSubtitleColumn(
                        modifier = modifier,
                        title = activityLevelAndDescription.first,
                        subtitle = activityLevelAndDescription.second
                    )
                }

            },
            selectedOptionIndex = selectedIndex,
            onOptionSelected = { selectedIndex = it })
    }
}

@Composable
private fun GoalOnboardingScreen(
    modifier: Modifier = Modifier,
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
            radioOptions = weightGoals.map { goalAndDescription ->
                { modifier ->
                    TitleAndSubtitleColumn(
                        modifier = modifier,
                        title = goalAndDescription.first,
                        subtitle = goalAndDescription.second
                    )
                }

            },
            selectedOptionIndex = selectedIndex,
            onOptionSelected = { selectedIndex = it })

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