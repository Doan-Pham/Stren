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
    filterStandards: List<FilterStandard>,
    onResetFilters: () -> Unit,
    onApplyFilters: () -> Unit
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
                onClickHandler = onResetFilters,
                text = "Reset",
                textStyle = MaterialTheme.typography.bodyMedium
            )
            StrenFilledButton(
                modifier = Modifier.weight(1f),
                onClickHandler = onApplyFilters,
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
            filterStandard.filterLabels.forEach { filterLabel ->
//                Timber.d("FilterRegion", "filterLabel: $filterLabel")
                FilterChip(
                    selected = filterLabel.isSelected,
                    onClick = { filterStandard.onLabelSelected(filterLabel) },
                    label = { Text(filterLabel.name) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.White,
                        labelColor = MaterialTheme.colorScheme.primary,
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    ),
                    border = if (!filterLabel.isSelected) FilterChipDefaults.filterChipBorder(
                        borderColor = MaterialTheme.colorScheme.primary
                    ) else null
                )
            }
        }

    }
}

data class FilterStandard(
    val standardName: String,
    val onLabelSelected: (FilterLabel) -> Unit,
    val filterLabels: List<FilterLabel>
)

data class FilterLabel(val id: String, val name: String, val isSelected: Boolean)