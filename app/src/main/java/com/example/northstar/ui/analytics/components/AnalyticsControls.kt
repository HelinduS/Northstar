package com.example.northstar.ui.analytics.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        //  Toggle section
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            AnalyticsTab.values().forEachIndexed { index, tab ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = AnalyticsTab.values().size
                    ),
                    onClick = { onTabChanged(tab) },
                    selected = selectedTab == tab,
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = Navy900,
                        activeContentColor = White
                    )
                ) {
                    Text(
                        text = tab.name.lowercase().replaceFirstChar { it.uppercase() },
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        //  Time Period Filters
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Standard Filters
            items(TimeFilter.values().filter { it != TimeFilter.CUSTOM }) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { onFilterChanged(filter) },
                    label = {
                        Text(filter.name.lowercase().replaceFirstChar { it.uppercase() })
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Navy900,
                        selectedLabelColor = White
                    )
                )
            }

            // Custom Range Filter
            item {
                FilterChip(
                    selected = selectedFilter == TimeFilter.CUSTOM,
                    onClick = {
                        // Trigger the custom filter state
                        onFilterChanged(TimeFilter.CUSTOM)

                    },
                    label = { Text("Custom") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Navy900,
                        selectedLabelColor = White
                    )
                )
            }
        }
    }
}