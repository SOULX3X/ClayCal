package com.example.ui.calendar

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CalendarEvent
import com.example.model.Category
import com.example.model.SimpleDate
import com.example.ui.clay.ClayCard
import com.example.ui.clay.ClayColors
import com.example.ui.clay.ClayPill

@Composable
fun ScheduleView(
    uiState: CalendarUiState,
    onCategorySelected: (String?) -> Unit,
    onToggleCompleted: (Long, Boolean) -> Unit,
    onEventClicked: (CalendarEvent) -> Unit,
    onEditEvent: (CalendarEvent) -> Unit,
    onDeleteEvent: (Long) -> Unit,
    isWideScreen: Boolean = false,
    modifier: Modifier = Modifier
) {
    val filteredEvents = uiState.filteredScheduleEvents
    // Group events by date
    val groupedByDate = filteredEvents.groupBy { it.date }

    if (isWideScreen) {
        // Landscape & Tablet Two-Pane Layout
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Pane: Category Filter Sidebar Card
            Box(
                modifier = Modifier
                    .weight(0.85f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.TopCenter
            ) {
                ClayCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    surfaceColor = ClayColors.SurfaceMarshmallow,
                    elevation = 7.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Filter by Category",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClayColors.TextTertiary,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )

                        ClayPill(
                            text = "All Events",
                            isSelected = uiState.selectedCategory == null,
                            onClick = { onCategorySelected(null) },
                            selectedColor = ClayColors.ClayTerracotta,
                            badgeCount = uiState.allEvents.size,
                            modifier = Modifier.fillMaxWidth().testTag("filter_all")
                        )

                        Category.ALL.forEach { cat ->
                            val count = uiState.allEvents.count { it.category.equals(cat.name, ignoreCase = true) }
                            ClayPill(
                                text = cat.name,
                                iconEmoji = cat.iconEmoji,
                                isSelected = uiState.selectedCategory.equals(cat.name, ignoreCase = true),
                                onClick = { onCategorySelected(cat.name) },
                                selectedColor = cat.clayColor,
                                badgeCount = count,
                                modifier = Modifier.fillMaxWidth().testTag("filter_${cat.name.lowercase()}")
                            )
                        }
                    }
                }
            }

            // Right Pane: Schedule Events List
            Column(
                modifier = Modifier
                    .weight(1.35f)
                    .fillMaxHeight()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (uiState.searchQuery.isNotBlank()) "Search Results" else "Upcoming Schedule",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClayColors.TextPrimary
                    )
                    Text(
                        text = "${filteredEvents.size} events found",
                        fontSize = 12.sp,
                        color = ClayColors.TextSecondary
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (filteredEvents.isEmpty()) {
                        item {
                            EmptyFilterCard()
                        }
                    } else {
                        groupedByDate.forEach { (date, eventsForDate) ->
                            item(key = "header_${date.year}_${date.month}_${date.day}") {
                                DateHeaderRow(date = date, count = eventsForDate.size)
                            }

                            items(eventsForDate, key = { it.id }) { event ->
                                EventItemCard(
                                    event = event,
                                    onToggleCompleted = { onToggleCompleted(event.id, it) },
                                    onClick = { onEventClicked(event) },
                                    onEdit = { onEditEvent(event) },
                                    onDelete = { onDeleteEvent(event.id) }
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    } else {
        // Standard Portrait Layout
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Category Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ClayPill(
                        text = "All Events",
                        isSelected = uiState.selectedCategory == null,
                        onClick = { onCategorySelected(null) },
                        selectedColor = ClayColors.ClayTerracotta,
                        badgeCount = uiState.allEvents.size,
                        modifier = Modifier.testTag("filter_all")
                    )

                    Category.ALL.forEach { cat ->
                        val count = uiState.allEvents.count { it.category.equals(cat.name, ignoreCase = true) }
                        ClayPill(
                            text = cat.name,
                            iconEmoji = cat.iconEmoji,
                            isSelected = uiState.selectedCategory.equals(cat.name, ignoreCase = true),
                            onClick = { onCategorySelected(cat.name) },
                            selectedColor = cat.clayColor,
                            badgeCount = count,
                            modifier = Modifier.testTag("filter_${cat.name.lowercase()}")
                        )
                    }
                }
            }

            // Summary Info
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (uiState.searchQuery.isNotBlank()) "Search Results" else "Upcoming Schedule",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClayColors.TextPrimary
                    )
                    Text(
                        text = "${filteredEvents.size} events found",
                        fontSize = 13.sp,
                        color = ClayColors.TextSecondary
                    )
                }
            }

            // Empty state
            if (filteredEvents.isEmpty()) {
                item {
                    EmptyFilterCard()
                }
            } else {
                // Grouped dates
                groupedByDate.forEach { (date, eventsForDate) ->
                    item(key = "header_${date.year}_${date.month}_${date.day}") {
                        DateHeaderRow(date = date, count = eventsForDate.size)
                    }

                    items(eventsForDate, key = { it.id }) { event ->
                        EventItemCard(
                            event = event,
                            onToggleCompleted = { onToggleCompleted(event.id, it) },
                            onClick = { onEventClicked(event) },
                            onEdit = { onEditEvent(event) },
                            onDelete = { onDeleteEvent(event.id) }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun DateHeaderRow(date: SimpleDate, count: Int, modifier: Modifier = Modifier) {
    val isToday = date.isToday()
    val headerTitle = when {
        isToday -> "Today • ${date.formattedShort()}"
        date.isSameDay(SimpleDate.today().addDays(1)) -> "Tomorrow • ${date.formattedShort()}"
        date.isSameDay(SimpleDate.today().addDays(-1)) -> "Yesterday • ${date.formattedShort()}"
        else -> date.formatted()
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = headerTitle,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isToday) ClayColors.ClayCoral else ClayColors.TextSecondary
        )
        Text(
            text = "$count items",
            fontSize = 12.sp,
            color = ClayColors.TextTertiary
        )
    }
}

@Composable
private fun EmptyFilterCard(modifier: Modifier = Modifier) {
    ClayCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        surfaceColor = ClayColors.SurfaceSoftClay,
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = "🔍", fontSize = 34.sp)
            Text(
                text = "No events match this filter",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ClayColors.TextPrimary
            )
            Text(
                text = "Try picking another category or clear your search query.",
                fontSize = 13.sp,
                color = ClayColors.TextSecondary
            )
        }
    }
}
