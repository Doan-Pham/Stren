package com.haidoan.android.stren.core.designsystem.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.haidoan.android.stren.R

@Composable
fun RadioGroup(
    modifier: Modifier = Modifier,
    radioOptions: List<@Composable (Modifier) -> Unit>,
    selectedOptionIndex: Int,
    onOptionSelected: (optionIndex: Int) -> Unit
) {
    if (radioOptions.isEmpty()) return

    // Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior
    Column(
        modifier = modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(
            dimensionResource(id = R.dimen.padding_medium)
        )
    ) {
        radioOptions.forEachIndexed { index, OptionComposable ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (index == selectedOptionIndex),
                        onClick = { onOptionSelected(index) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.Top
            ) {
                RadioButton(
                    selected = (index == selectedOptionIndex),
                    onClick = null // null recommended for accessibility with screenreaders
                )
                OptionComposable(Modifier.padding(start = 16.dp))
            }
        }
    }
}