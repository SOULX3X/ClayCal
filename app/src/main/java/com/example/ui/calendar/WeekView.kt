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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CalendarEvent
import com.example.model.SimpleDate
import com.example.ui.clay.ClayButton
import com.example.ui.clay.ClayCard
import com.example.ui.clay.ClayColors

@Composable
fun WeekView(
    uiState: CalendarUiState,
    onDateSelected: (SimpleDate) -> Unit,
    onToggleCompleted: (Long, Boolean) -> Unit,
    onEventClicked: (CalendarEvent) -> Unit,
    onEditEvent: (CalendarEvent) -> Unit,
    onDeleteEvent: (Long) -> Unit,
    onAddNewEvent: (SimpleDate) -> Unit,
    isWideScreen: Boolean = false,
    modifier: Modifier = Modifier
) {
    val selected = uiState.selectedDate
    // Calculate 7 days of the week starting Sunday
    val dayOfWeek = selected.dayOfWeek // 1 (Sun) .. 7 (Sat)
    val daysFromSunday = dayOfWeek - 1
    val weekStart = selected.addDays(-daysFromSunday)

    val weekDays = (0..6).map { weekStart.addDays(it) }
    val dayEvents = uiState.eventsForSelectedDate

    if (isWideScreen) {
        // Landscape & Tablet Two-Pane Split View
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Pane: Week Days List Card & Summary
            Box(
                modifier = Modifier
                    .weight(0.9f)
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
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Week of ${weekStart.formattedShort()}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClayColors.TextTertiary,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )

                        weekDays.forEach { date ->
                            val isSelected = date.isSameDay(selected)
                            val isToday = date.isToday()
                            val events = uiState.getEventsForDate(date)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) ClayColors.ClayTerracotta
                                        else if (isToday) ClayColors.ClayPeach.copy(alpha = 0.2f)
                                        else ClayColors.SurfaceSoftClay
                                    )
                                    .clickable { onDateSelected(date) }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = date.dayOfWeekName.take(3).uppercase(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White.copy(alpha = 0.85f) else ClayColors.TextTertiary
                                    )
                                    Text(
                                        text = "${date.day}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isSelected) Color.White else if (isToday) ClayColors.ClayCoral else ClayColors.TextPrimary
                                    )
                                }

                                if (events.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .background(
                                                color = if (isSelected) Color.White.copy(alpha = 0.3f) else ClayColors.ClayTerracotta.copy(alpha = 0.15f),
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${events.size}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else ClayColors.ClayTerracotta
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Right Pane: Hourly Timeline for Selected Day
            Column(
                modifier = Modifier
                    .weight(1.3f)
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
                    Column {
                        Text(
                            text = selected.formatted(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClayColors.TextPrimary
                        )
                        Text(
                            text = "${dayEvents.size} events on this day",
                            fontSize = 12.sp,
                            color = ClayColors.TextSecondary
                        )
                    }

                    ClayButton(
                        onClick = { onAddNewEvent(selected) },
                        containerColor = ClayColors.ClayTerracotta,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(14.dp),
                        elevation = 4.dp,
                        modifier = Modifier.height(36.dp).testTag("week_add_button")
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

                // Events List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (dayEvents.isEmpty()) {
                        item {
                            ClayCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(22.dp),
                                surfaceColor = ClayColors.SurfaceSoftClay,
                                elevation = 2.dp
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(text = "☀️", fontSize = 28.sp)
                                    Text(
                                        text = "No events on this day",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ClayColors.TextPrimary
                                    )
                                    Text(
                                        text = "Tap '+ Add' above to schedule tasks or meetings",
                                        fontSize = 12.sp,
                                        color = ClayColors.TextSecondary
                                    )
                                }
                            }
                        }
                    } else {
                        items(dayEvents, key = { it.id }) { event ->
                            EventItemCard(
                                event = event,
                                onToggleCompleted = { onToggleCompleted(event.id, it) },
                                onClick = { onEventClicked(event) },
                                onEdit = { onEditEvent(event) },
                                onDelete = { onDeleteEvent(event.id) }
                            )
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
            // Week Days Strip
            item {
                ClayCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    surfaceColor = ClayColors.SurfaceMarshmallow,
                    elevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        weekDays.forEach { date ->
                            val isSelected = date.isSameDay(selected)
                            val isToday = date.isToday()
                            val events = uiState.getEventsForDate(date)

                            ClayWeekDayPill(
                                date = date,
                                isSelected = isSelected,
                                isToday = isToday,
                                eventCount = events.size,
                                completedCount = events.count { it.isCompleted },
                                onClick = { onDateSelected(date) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Timeline Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = selected.formatted(),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClayColors.TextPrimary
                        )
                        Text(
                            text = "Daily schedule breakdown",
                            fontSize = 13.sp,
                            color = ClayColors.TextSecondary
                        )
                    }

                    ClayButton(
                        onClick = { onAddNewEvent(selected) },
                        containerColor = ClayColors.ClayTerracotta,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(14.dp),
                        elevation = 4.dp,
                        modifier = Modifier.height(38.dp).testTag("week_add_button")
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

            // Events on this selected day
            if (dayEvents.isEmpty()) {
                item {
                    ClayCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        surfaceColor = ClayColors.SurfaceSoftClay,
                        elevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "☀️", fontSize = 32.sp)
                            Text(
                                text = "No events on this day",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = ClayColors.TextPrimary
                            )
                            Text(
                                text = "Tap '+ Add' above to schedule tasks or meetings",
                                fontSize = 13.sp,
                                color = ClayColors.TextSecondary
                            )
                        }
                    }
                }
            } else {
                items(dayEvents, key = { it.id }) { event ->
                    EventItemCard(
                        event = event,
                        onToggleCompleted = { onToggleCompleted(event.id, it) },
                        onClick = { onEventClicked(event) },
                        onEdit = { onEditEvent(event) },
                        onDelete = { onDeleteEvent(event.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun ClayWeekDayPill(
    date: SimpleDate,
    isSelected: Boolean,
    isToday: Boolean,
    eventCount: Int,
    completedCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)

    val surfaceColor = when {
        isSelected -> ClayColors.ClayTerracotta
        isToday -> ClayColors.ClayPeach.copy(alpha = 0.2f)
        else -> ClayColors.SurfaceSoftClay
    }

    val textColor = when {
        isSelected -> Color.White
        isToday -> ClayColors.ClayCoral
        else -> ClayColors.TextPrimary
    }

    Box(
        modifier = modifier
            .padding(horizontal = 2.dp)
            .shadow(
                elevation = if (isSelected) 5.dp else 1.dp,
                shape = shape,
                ambientColor = if (isSelected) ClayColors.ClayTerracotta.copy(alpha = 0.35f) else ClayColors.ShadowAmbient,
                spotColor = if (isSelected) ClayColors.ClayTerracotta.copy(alpha = 0.4f) else ClayColors.ShadowSpot
            )
            .background(surfaceColor, shape = shape)
            .border(
                width = if (isToday && !isSelected) 1.5.dp else 1.dp,
                brush = when {
                    isSelected -> Brush.linearGradient(
                        colors = listOf(Color.White.copy(alpha = 0.7f), Color.Black.copy(alpha = 0.15f))
                    )
                    isToday -> Brush.linearGradient(
                        colors = listOf(ClayColors.ClayCoral, ClayColors.ClayTerracotta)
                    )
                    else -> Brush.linearGradient(
                        colors = listOf(Color.White.copy(alpha = 0.85f), ClayColors.ShadowBevel.copy(alpha = 0.3f))
                    )
                },
                shape = shape
            )
            .clip(shape)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = date.dayOfWeekName.take(3).uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White.copy(alpha = 0.85f) else ClayColors.TextTertiary
            )

            Text(
                text = "${date.day}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = textColor
            )

            // Event indicator
            if (eventCount > 0) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(
                            color = if (isSelected) Color.White else ClayColors.ClayCoral,
                            shape = CircleShape
                        )
                )
            } else {
                Spacer(modifier = Modifier.size(6.dp))
            }
        }
    }
}
