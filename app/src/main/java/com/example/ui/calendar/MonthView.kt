package com.example.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import com.example.model.Category
import com.example.model.SimpleDate
import com.example.ui.clay.ClayButton
import com.example.ui.clay.ClayCard
import com.example.ui.clay.ClayColors

private data class MonthDayItem(
    val date: SimpleDate,
    val isCurrentMonth: Boolean
)

@Composable
fun MonthView(
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
    val year = uiState.displayedYear
    val month = uiState.displayedMonth
    val daysInMonth = SimpleDate.getDaysInMonth(year, month)
    val firstDayOfWeek = SimpleDate.getFirstDayOfWeekInMonth(year, month) // 1 (Sun) .. 7 (Sat)

    // Calculate grid items (Sunday is first column)
    val calendarDays = mutableListOf<MonthDayItem>()

    // Previous month padding
    val prevMonth = if (month == 1) 12 else month - 1
    val prevYear = if (month == 1) year - 1 else year
    val daysInPrevMonth = SimpleDate.getDaysInMonth(prevYear, prevMonth)
    val prevMonthDaysCount = firstDayOfWeek - 1

    for (i in (daysInPrevMonth - prevMonthDaysCount + 1)..daysInPrevMonth) {
        calendarDays.add(MonthDayItem(SimpleDate(prevYear, prevMonth, i), isCurrentMonth = false))
    }

    // Current month days
    for (i in 1..daysInMonth) {
        calendarDays.add(MonthDayItem(SimpleDate(year, month, i), isCurrentMonth = true))
    }

    // Next month padding to fill complete weeks
    val remainingDays = (7 - (calendarDays.size % 7)) % 7
    val nextMonth = if (month == 12) 1 else month + 1
    val nextYear = if (month == 12) year + 1 else year

    for (i in 1..remainingDays) {
        calendarDays.add(MonthDayItem(SimpleDate(nextYear, nextMonth, i), isCurrentMonth = false))
    }

    val selectedEvents = uiState.eventsForSelectedDate

    if (isWideScreen) {
        // Landscape & Tablet Two-Pane Split View
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Pane: Month Calendar Grid Card
            Box(
                modifier = Modifier
                    .weight(1.15f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.TopCenter
            ) {
                MonthCalendarGridCard(
                    calendarDays = calendarDays,
                    uiState = uiState,
                    onDateSelected = onDateSelected
                )
            }

            // Right Pane: Selected Date Events & Agenda
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                // Selected Date Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = uiState.selectedDate.formatted(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClayColors.TextPrimary
                        )
                        val count = selectedEvents.size
                        Text(
                            text = if (count == 0) "No events planned" else "$count events scheduled",
                            fontSize = 12.sp,
                            color = ClayColors.TextSecondary
                        )
                    }

                    ClayButton(
                        onClick = { onAddNewEvent(uiState.selectedDate) },
                        containerColor = ClayColors.ClayTerracotta,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(14.dp),
                        elevation = 4.dp,
                        modifier = Modifier.height(36.dp).testTag("quick_add_button")
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
                    if (selectedEvents.isEmpty()) {
                        item {
                            EmptyEventsCard()
                        }
                    } else {
                        items(selectedEvents, key = { it.id }) { event ->
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
        // Standard Portrait Single-Column View
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Month Calendar Clay Container
            item {
                MonthCalendarGridCard(
                    calendarDays = calendarDays,
                    uiState = uiState,
                    onDateSelected = onDateSelected
                )
            }

            // Selected Date Header Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = uiState.selectedDate.formatted(),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClayColors.TextPrimary
                        )
                        val count = selectedEvents.size
                        Text(
                            text = if (count == 0) "No events planned" else "$count events scheduled",
                            fontSize = 13.sp,
                            color = ClayColors.TextSecondary
                        )
                    }

                    ClayButton(
                        onClick = { onAddNewEvent(uiState.selectedDate) },
                        containerColor = ClayColors.ClayTerracotta,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(14.dp),
                        elevation = 4.dp,
                        modifier = Modifier.height(38.dp).testTag("quick_add_button")
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

            // Selected Day Events List
            if (selectedEvents.isEmpty()) {
                item {
                    EmptyEventsCard()
                }
            } else {
                items(selectedEvents, key = { it.id }) { event ->
                    EventItemCard(
                        event = event,
                        onToggleCompleted = { onToggleCompleted(event.id, it) },
                        onClick = { onEventClicked(event) },
                        onEdit = { onEditEvent(event) },
                        onDelete = { onDeleteEvent(event.id) }
                    )
                }
            }

            // Bottom spacing for FAB
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun MonthCalendarGridCard(
    calendarDays: List<MonthDayItem>,
    uiState: CalendarUiState,
    onDateSelected: (SimpleDate) -> Unit,
    modifier: Modifier = Modifier
) {
    ClayCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        surfaceColor = ClayColors.SurfaceMarshmallow,
        elevation = 5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Weekday headers: S M T W T F S (Screen 7)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val weekdays = listOf("S", "M", "T", "W", "T", "F", "S")
                weekdays.forEach { day ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ClayColors.TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Month grid rows
            val totalRows = (calendarDays.size + 6) / 7
            for (rowIndex in 0 until totalRows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (colIndex in 0 until 7) {
                        val dayIndex = rowIndex * 7 + colIndex
                        if (dayIndex < calendarDays.size) {
                            val item = calendarDays[dayIndex]
                            val events = uiState.getEventsForDate(item.date)
                            val isSelected = item.date.isSameDay(uiState.selectedDate)
                            val isToday = item.date.isToday()

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                ClayDayCell(
                                    day = item.date.day,
                                    isCurrentMonth = item.isCurrentMonth,
                                    isSelected = isSelected,
                                    isToday = isToday,
                                    events = events,
                                    onClick = { onDateSelected(item.date) }
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyEventsCard(modifier: Modifier = Modifier) {
    ClayCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(22.dp),
        surfaceColor = ClayColors.SurfaceMarshmallow,
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "🌿", fontSize = 32.sp)
            Text(
                text = "No events scheduled",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ClayColors.TextPrimary
            )
            Text(
                text = "Your day is clear and relaxed. Tap '+' to create a new event.",
                fontSize = 13.sp,
                color = ClayColors.TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun ClayDayCell(
    day: Int,
    isCurrentMonth: Boolean,
    isSelected: Boolean,
    isToday: Boolean,
    events: List<CalendarEvent>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cellShape = CircleShape

    val textColor = when {
        isSelected -> Color.White
        isToday -> ClayColors.ClaySage
        isCurrentMonth -> ClayColors.TextPrimary
        else -> ClayColors.TextTertiary.copy(alpha = 0.45f)
    }

    Column(
        modifier = modifier
            .size(40.dp)
            .clip(cellShape)
            .clickable(onClick = onClick)
            .testTag("day_cell_$day"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .then(
                    if (isSelected) {
                        Modifier
                            .shadow(3.dp, CircleShape, ambientColor = ClayColors.ClaySage.copy(alpha = 0.35f), spotColor = ClayColors.ClaySage.copy(alpha = 0.4f))
                            .background(ClayColors.ClaySage, CircleShape)
                    } else if (isToday) {
                        Modifier
                            .border(1.5.dp, ClayColors.ClaySage, CircleShape)
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$day",
                fontSize = 14.sp,
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                color = textColor
            )
        }

        // Event dots (max 3)
        if (events.isNotEmpty() && isCurrentMonth) {
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val displayEvents = events.take(3)
                displayEvents.forEach { event ->
                    val cat = Category.fromName(event.category)
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(
                                color = if (isSelected) ClayColors.ClaySage else cat.clayColor,
                                shape = CircleShape
                            )
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}
