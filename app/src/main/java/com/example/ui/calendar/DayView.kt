package com.example.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CalendarEvent
import com.example.model.Category
import com.example.model.SimpleDate
import com.example.model.SimpleTime
import com.example.ui.clay.ClayButton
import com.example.ui.clay.ClayCard
import com.example.ui.clay.ClayColors

@Composable
fun DayView(
    uiState: CalendarUiState,
    onToggleCompleted: (Long, Boolean) -> Unit,
    onEventClicked: (CalendarEvent) -> Unit,
    onEditEvent: (CalendarEvent) -> Unit,
    onDeleteEvent: (Long) -> Unit,
    onAddNewEventAtHour: (SimpleDate, Int) -> Unit,
    isWideScreen: Boolean = false,
    modifier: Modifier = Modifier
) {
    val selected = uiState.selectedDate
    val events = uiState.eventsForSelectedDate
    val currentTime = SimpleTime.now()
    val isToday = selected.isToday()
    val hours = (7..22).toList()

    if (isWideScreen) {
        // Landscape & Tablet Two-Pane Layout
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Pane: Day Summary & Overview Card
            Box(
                modifier = Modifier
                    .weight(0.85f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.TopCenter
            ) {
                ClayCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    surfaceColor = ClayColors.SurfaceMarshmallow,
                    elevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = selected.dayOfWeekName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ClayColors.ClayTerracotta
                            )
                            Text(
                                text = selected.formatted(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = ClayColors.TextPrimary
                            )
                            val completedCount = events.count { it.isCompleted }
                            Text(
                                text = "${events.size} events • $completedCount completed",
                                fontSize = 13.sp,
                                color = ClayColors.TextSecondary
                            )
                        }

                        // Category distribution chips
                        if (events.isNotEmpty()) {
                            val categoriesInDay = events.map { it.category }.distinct()
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Categories Active",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ClayColors.TextTertiary
                                )
                                categoriesInDay.forEach { catName ->
                                    val cat = Category.fromName(catName)
                                    val count = events.count { it.category == catName }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(ClayColors.SurfaceSoftClay, RoundedCornerShape(10.dp))
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = cat.iconEmoji, fontSize = 13.sp)
                                            Text(text = cat.name, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = ClayColors.TextPrimary)
                                        }
                                        Text(text = "$count", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ClayColors.TextSecondary)
                                    }
                                }
                            }
                        }

                        ClayButton(
                            onClick = { onAddNewEventAtHour(selected, 9) },
                            containerColor = ClayColors.ClayTerracotta,
                            contentColor = Color.White,
                            shape = RoundedCornerShape(14.dp),
                            elevation = 4.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Event",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Add Event",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Right Pane: Hourly Timeline
            LazyColumn(
                modifier = Modifier
                    .weight(1.35f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(hours.size) { index ->
                    val hour = hours[index]
                    val eventsAtHour = events.filter { it.startHour == hour }
                    val isCurrentHour = isToday && currentTime.hour == hour

                    TimelineHourRow(
                        hour = hour,
                        eventsAtHour = eventsAtHour,
                        isCurrentHour = isCurrentHour,
                        selected = selected,
                        onToggleCompleted = onToggleCompleted,
                        onEventClicked = onEventClicked,
                        onEditEvent = onEditEvent,
                        onDeleteEvent = onDeleteEvent,
                        onAddNewEventAtHour = onAddNewEventAtHour
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(72.dp))
                }
            }
        }
    } else {
        // Standard Portrait Layout
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Date Banner
            item {
                ClayCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    surfaceColor = ClayColors.SurfaceMarshmallow,
                    elevation = 5.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = selected.formatted(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = ClayColors.TextPrimary
                            )
                            Text(
                                text = "${events.size} events • ${events.count { it.isCompleted }} completed",
                                fontSize = 13.sp,
                                color = ClayColors.TextSecondary
                            )
                        }

                        ClayButton(
                            onClick = { onAddNewEventAtHour(selected, 9) },
                            containerColor = ClayColors.ClayTerracotta,
                            contentColor = Color.White,
                            shape = RoundedCornerShape(14.dp),
                            elevation = 4.dp,
                            modifier = Modifier.height(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Add",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Hourly Timeline (7 AM to 10 PM)
            items(hours.size) { index ->
                val hour = hours[index]
                val eventsAtHour = events.filter { it.startHour == hour }
                val isCurrentHour = isToday && currentTime.hour == hour

                TimelineHourRow(
                    hour = hour,
                    eventsAtHour = eventsAtHour,
                    isCurrentHour = isCurrentHour,
                    selected = selected,
                    onToggleCompleted = onToggleCompleted,
                    onEventClicked = onEventClicked,
                    onEditEvent = onEditEvent,
                    onDeleteEvent = onDeleteEvent,
                    onAddNewEventAtHour = onAddNewEventAtHour
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun TimelineHourRow(
    hour: Int,
    eventsAtHour: List<CalendarEvent>,
    isCurrentHour: Boolean,
    selected: SimpleDate,
    onToggleCompleted: (Long, Boolean) -> Unit,
    onEventClicked: (CalendarEvent) -> Unit,
    onEditEvent: (CalendarEvent) -> Unit,
    onDeleteEvent: (Long) -> Unit,
    onAddNewEventAtHour: (SimpleDate, Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Time label column
        Column(
            modifier = Modifier.width(62.dp),
            horizontalAlignment = Alignment.End
        ) {
            val timeStr = SimpleTime(hour, 0).formatted()
            Text(
                text = timeStr,
                fontSize = 11.sp,
                fontWeight = if (isCurrentHour) FontWeight.Bold else FontWeight.Medium,
                color = if (isCurrentHour) ClayColors.ClayTerracotta else ClayColors.TextTertiary
            )

            if (isCurrentHour) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(8.dp)
                        .background(ClayColors.ClayTerracotta, shape = CircleShape)
                )
            }
        }

        // Vertical Divider Line
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(if (eventsAtHour.isNotEmpty()) 110.dp else 44.dp)
                .background(
                    if (isCurrentHour) ClayColors.ClayPeach else ClayColors.GridLine,
                    shape = RoundedCornerShape(1.dp)
                )
        )

        // Events column
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (eventsAtHour.isNotEmpty()) {
                eventsAtHour.forEach { event ->
                    EventItemCard(
                        event = event,
                        onToggleCompleted = { onToggleCompleted(event.id, it) },
                        onClick = { onEventClicked(event) },
                        onEdit = { onEditEvent(event) },
                        onDelete = { onDeleteEvent(event.id) }
                    )
                }
            } else {
                // Empty slot button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .border(
                            width = 1.dp,
                            color = ClayColors.ShadowBevel.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onAddNewEventAtHour(selected, hour) }
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add at this time",
                            tint = ClayColors.TextTertiary.copy(alpha = 0.6f),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Mold event at ${SimpleTime(hour, 0).formatted()}",
                            fontSize = 11.sp,
                            color = ClayColors.TextTertiary.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}
