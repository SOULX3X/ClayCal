package com.example.ui.calendar

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
    modifier: Modifier = Modifier
) {
    val filteredEvents = uiState.filteredScheduleEvents
    // Group events by date
    val groupedByDate = filteredEvents.groupBy { it.date }

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
                ClayCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    surfaceColor = ClayColors.SurfaceSoftClay,
                    elevation = 3.dp
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
        } else {
            // Grouped dates
            groupedByDate.forEach { (date, eventsForDate) ->
                item(key = "header_${date.year}_${date.month}_${date.day}") {
                    val isToday = date.isToday()
                    val headerTitle = when {
                        isToday -> "Today • ${date.formattedShort()}"
                        date.isSameDay(SimpleDate.today().addDays(1)) -> "Tomorrow • ${date.formattedShort()}"
                        date.isSameDay(SimpleDate.today().addDays(-1)) -> "Yesterday • ${date.formattedShort()}"
                        else -> date.formatted()
                    }

                    Row(
                        modifier = Modifier
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
                            text = "${eventsForDate.size} items",
                            fontSize = 12.sp,
                            color = ClayColors.TextTertiary
                        )
                    }
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
