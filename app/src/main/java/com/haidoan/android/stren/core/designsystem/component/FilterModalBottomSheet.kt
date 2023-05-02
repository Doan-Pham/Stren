package com.haidoan.android.stren.core.designsystem.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.haidoan.android.stren.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterModalBottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    bottomSheetState: SheetState,
    filterStandards: List<FilterStandard>
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
        containerColor = Color.White,
        tonalElevation = 0.dp,
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .align(CenterHorizontally),
            text = "Filter",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
                .verticalScroll(rememberScrollState())
                .weight(1f, false)

        ) {
            filterStandards.forEach { standard ->
                FilterRegion(filterStandard = standard)
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(id = R.dimen.padding_medium),
                    end = dimensionResource(id = R.dimen.padding_medium),
                    top = dimensionResource(id = R.dimen.padding_medium),
                    bottom = 50.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StrenTextButton(
                modifier = Modifier.weight(1f),
                onClickHandler = {},
                text = "Reset",
                textStyle = MaterialTheme.typography.bodyMedium
            )
            StrenFilledButton(
                modifier = Modifier.weight(1f),
                onClickHandler = {},
                text = "Apply",
                textStyle = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun FilterRegion(modifier: Modifier = Modifier, filterStandard: FilterStandard) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(id = R.dimen.padding_medium))
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .align(Start),
            text = filterStandard.standardName,
            style = MaterialTheme.typography.bodyLarge
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            filterStandard.selectedStateOfFilterLabels.forEach { labelAndSelectedState ->
                var selected by remember { mutableStateOf(labelAndSelectedState.value) }
                FilterChip(
                    selected = selected,
                    onClick = { selected = !selected },
                    label = { Text(labelAndSelectedState.key) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.White,
                        labelColor = MaterialTheme.colorScheme.primary,
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    ),
                    border = if (!selected) FilterChipDefaults.filterChipBorder(
                        borderColor = MaterialTheme.colorScheme.primary
                    ) else null
                )
            }
        }

    }
}

data class FilterStandard(
    val standardName: String,
    val selectedStateOfFilterLabels: Map<String, Boolean>
)
