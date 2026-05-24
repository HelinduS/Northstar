package com.example.northstar.ui.analytics.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.northstar.ui.analytics.AnalyticsTab
import com.example.northstar.ui.analytics.TimeFilter
import com.example.northstar.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsControls(
    selectedTab: AnalyticsTab,
    selectedFilter: TimeFilter,
    onTabChanged: (AnalyticsTab) -> Unit,
    onFilterChanged: (TimeFilter) -> Unit
) {
    Column {
        // Toggle section
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            AnalyticsTab.values().forEachIndexed { index, tab ->
                val tabLabel = when (tab) {
                    AnalyticsTab.INCOME -> "Income"
                    AnalyticsTab.EXPENSE -> "Expense"
                    AnalyticsTab.COMPARISON -> "Inc vs Exp"
                }

                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = AnalyticsTab.values().size
                    ),
                    onClick = { onTabChanged(tab) },
                    selected = selectedTab == tab,
                    label = { Text(tabLabel) },
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = GreenDeep,
                        activeContentColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Period filter options
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(TimeFilter.values().filter { it != TimeFilter.CUSTOM }) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { onFilterChanged(filter) },
                    label = {
                        Text(filter.name.lowercase().replaceFirstChar { it.uppercase() })
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GreenDeep,
                        selectedLabelColor = Color.White
                    )
                )
            }

            // Custom Range Filter
            item {
                FilterChip(
                    selected = selectedFilter == TimeFilter.CUSTOM,
                    onClick = { onFilterChanged(TimeFilter.CUSTOM) },
                    label = { Text("Custom") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GreenDeep,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}