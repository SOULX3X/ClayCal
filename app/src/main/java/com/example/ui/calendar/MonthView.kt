package com.example.ui.calendar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
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
    val prevMonthDaysCount = firstDayOfWeek - 1 // If Sunday (1), 0 items. If Monday (2), 1 item.

    for (i in (daysInPrevMonth - prevMonthDaysCount + 1)..daysInPrevMonth) {
        calendarDays.add(MonthDayItem(SimpleDate(prevYear, prevMonth, i), isCurrentMonth = false))
    }

    // Current month days
    for (i in 1..daysInMonth) {
        calendarDays.add(MonthDayItem(SimpleDate(year, month, i), isCurrentMonth = true))
    }

    // Next month padding to fill complete weeks (multiples of 7, up to 35 or 42)
    val remainingDays = (7 - (calendarDays.size % 7)) % 7
    val nextMonth = if (month == 12) 1 else month + 1
    val nextYear = if (month == 12) year + 1 else year

    for (i in 1..remainingDays) {
        calendarDays.add(MonthDayItem(SimpleDate(nextYear, nextMonth, i), isCurrentMonth = false))
    }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Month Calendar Clay Container
        item {
            ClayCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                surfaceColor = ClayColors.SurfaceMarshmallow,
                elevation = 7.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Weekday headers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val weekdays = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
                        weekdays.forEachIndexed { index, day ->
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (index == 0 || index == 6) ClayColors.ClayCoral else ClayColors.TextTertiary
                                )
                            }
                        }
                    }

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
                                            .padding(3.dp),
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
                    val count = uiState.eventsForSelectedDate.size
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
        val selectedEvents = uiState.eventsForSelectedDate
        if (selectedEvents.isEmpty()) {
            item {
                ClayCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
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
                        Text(text = "🌱", fontSize = 36.sp)
                        Text(
                            text = "Free & Open Schedule",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClayColors.TextPrimary
                        )
                        Text(
                            text = "No events mold-formed for this day yet. Tap '+ Add' to shape a new schedule!",
                            fontSize = 13.sp,
                            color = ClayColors.TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
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
    val shape = RoundedCornerShape(14.dp)

    val surfaceColor = when {
        isSelected -> ClayColors.ClayTerracotta
        isToday -> ClayColors.ClayPeach.copy(alpha = 0.18f)
        isCurrentMonth -> ClayColors.SurfaceSoftClay
        else -> ClayColors.SurfaceDimmed.copy(alpha = 0.5f)
    }

    val textColor = when {
        isSelected -> Color.White
        isToday -> ClayColors.ClayCoral
        isCurrentMonth -> ClayColors.TextPrimary
        else -> ClayColors.TextTertiary.copy(alpha = 0.5f)
    }

    Box(
        modifier = modifier
            .aspectRatio(0.9f)
            .shadow(
                elevation = if (isSelected) 5.dp else if (isCurrentMonth) 2.dp else 0.dp,
                shape = shape,
                ambientColor = if (isSelected) ClayColors.ClayTerracotta.copy(alpha = 0.35f) else ClayColors.ShadowAmbient,
                spotColor = if (isSelected) ClayColors.ClayTerracotta.copy(alpha = 0.4f) else ClayColors.ShadowSpot
            )
            .background(surfaceColor, shape = shape)
            .border(
                width = if (isToday && !isSelected) 2.dp else 1.dp,
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
            .testTag("day_cell_$day"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$day",
                fontSize = 13.sp,
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Medium,
                color = textColor
            )

            // Event dots (max 3)
            if (events.isNotEmpty() && isCurrentMonth) {
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val displayEvents = events.take(3)
                    displayEvents.forEach { event ->
                        val cat = Category.fromName(event.category)
                        Box(
                            modifier = Modifier
                                .size(4.5.dp)
                                .background(
                                    color = if (isSelected) Color.White else cat.clayColor,
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }
        }
    }
}
