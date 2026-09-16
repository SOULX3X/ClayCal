package com.example.ui.calendar

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.CalendarEvent
import com.example.model.Category
import com.example.model.EventTemplate
import com.example.model.SimpleDate
import com.example.model.SimpleTime
import com.example.ui.clay.ClayButton
import com.example.ui.clay.ClayCard
import com.example.ui.clay.ClayColors
import com.example.ui.clay.ClayIconButton
import com.example.ui.clay.ClayPill
import com.example.ui.clay.ClayTextField

@Composable
fun EventDialog(
    editingEvent: CalendarEvent?,
    initialDate: SimpleDate,
    defaultReminderMinutes: Int = 15,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        description: String,
        category: String,
        date: SimpleDate,
        startTime: SimpleTime,
        endTime: SimpleTime,
        location: String,
        priority: String,
        reminderMinutes: Int,
        existingId: Long
    ) -> Unit
) {
    var title by remember { mutableStateOf(editingEvent?.title ?: "") }
    var description by remember { mutableStateOf(editingEvent?.description ?: "") }
    var category by remember { mutableStateOf(editingEvent?.category ?: "Personal") }
    var selectedDate by remember { mutableStateOf(editingEvent?.date ?: initialDate) }
    var startHour by remember { mutableIntStateOf(editingEvent?.startHour ?: 9) }
    var startMinute by remember { mutableIntStateOf(editingEvent?.startMinute ?: 0) }
    var endHour by remember { mutableIntStateOf(editingEvent?.endHour ?: 10) }
    var endMinute by remember { mutableIntStateOf(editingEvent?.endMinute ?: 0) }
    var location by remember { mutableStateOf(editingEvent?.location ?: "") }
    var priority by remember { mutableStateOf(editingEvent?.priority ?: "Medium") }
    var reminderMinutes by remember {
        mutableIntStateOf(editingEvent?.reminderMinutesBefore ?: defaultReminderMinutes)
    }

    var titleError by remember { mutableStateOf(false) }

    var dialogVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        dialogVisible = true
    }

    val dialogScale by animateFloatAsState(
        targetValue = if (dialogVisible) 1f else 0.92f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "dialog_scale"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 560.dp)
                .graphicsLayer {
                    scaleX = dialogScale
                    scaleY = dialogScale
                }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            ClayCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.94f),
                shape = RoundedCornerShape(32.dp),
                surfaceColor = ClayColors.SurfaceMarshmallow,
                elevation = 12.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "🎨", fontSize = 22.sp)
                            Text(
                                text = if (editingEvent != null) "Edit Calendar Event" else "Mold New Event",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = ClayColors.TextPrimary
                            )
                        }

                        ClayIconButton(
                            onClick = onDismiss,
                            icon = Icons.Rounded.Close,
                            contentDescription = "Close",
                            size = 36.dp,
                            elevation = 2.dp
                        )
                    }

                    // Quick Templates (only if creating new event)
                    if (editingEvent == null) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Quick Templates",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ClayColors.TextTertiary
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                EventTemplate.PRESETS.forEach { template ->
                                    val cat = Category.fromName(template.category)
                                    ClayPill(
                                        text = template.title,
                                        iconEmoji = cat.iconEmoji,
                                        isSelected = false,
                                        onClick = {
                                            title = template.title
                                            description = template.description
                                            category = template.category
                                            location = template.location
                                            priority = template.priority
                                            endHour = (startHour + template.durationHours).coerceIn(0, 23)
                                            endMinute = template.durationMinutes
                                        },
                                        unselectedColor = ClayColors.SurfaceSoftClay
                                    )
                                }
                            }
                        }
                    }

                    // Title Field
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Event Title *",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClayColors.TextTertiary
                        )
                        ClayTextField(
                            value = title,
                            onValueChange = {
                                title = it
                                titleError = false
                            },
                            placeholder = "e.g. Design review, Doctor, Birthday dinner...",
                            modifier = Modifier.fillMaxWidth().testTag("event_title_input")
                        )
                        if (titleError) {
                            Text(
                                text = "Please enter an event title",
                                color = ClayColors.ClayTerracotta,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Category Selector
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Category",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClayColors.TextTertiary
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Category.ALL.forEach { cat ->
                                val isSelected = category.equals(cat.name, ignoreCase = true)
                                ClayPill(
                                    text = cat.name,
                                    iconEmoji = cat.iconEmoji,
                                    isSelected = isSelected,
                                    onClick = { category = cat.name },
                                    selectedColor = cat.clayColor,
                                    unselectedColor = ClayColors.SurfaceSoftClay
                                )
                            }
                        }
                    }

                    // Date Picker Stepper
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Date",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClayColors.TextTertiary
                        )
                        ClayCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            surfaceColor = ClayColors.SurfaceSoftClay,
                            elevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ClayIconButton(
                                    onClick = { selectedDate = selectedDate.addDays(-1) },
                                    icon = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = "Previous Day",
                                    size = 36.dp,
                                    elevation = 2.dp
                                )

                                Text(
                                    text = selectedDate.formatted(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ClayColors.TextPrimary
                                )

                                ClayIconButton(
                                    onClick = { selectedDate = selectedDate.addDays(1) },
                                    icon = Icons.AutoMirrored.Rounded.ArrowForward,
                                    contentDescription = "Next Day",
                                    size = 36.dp,
                                    elevation = 2.dp
                                )
                            }
                        }
                    }

                    // Time Range Selector
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Time Range",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClayColors.TextTertiary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Start Time
                            ClayCard(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(24.dp),
                                surfaceColor = ClayColors.SurfaceSoftClay,
                                elevation = 2.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Starts",
                                        fontSize = 11.sp,
                                        color = ClayColors.TextTertiary
                                    )
                                    Text(
                                        text = SimpleTime(startHour, startMinute).formatted(),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ClayColors.TextPrimary
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        ClayPill(
                                            text = "-1h",
                                            isSelected = false,
                                            onClick = { startHour = if (startHour == 0) 23 else startHour - 1 },
                                            unselectedColor = ClayColors.SurfaceMarshmallow
                                        )
                                        ClayPill(
                                            text = "+1h",
                                            isSelected = false,
                                            onClick = { startHour = (startHour + 1) % 24 },
                                            unselectedColor = ClayColors.SurfaceMarshmallow
                                        )
                                    }
                                }
                            }

                            // End Time
                            ClayCard(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(24.dp),
                                surfaceColor = ClayColors.SurfaceSoftClay,
                                elevation = 2.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Ends",
                                        fontSize = 11.sp,
                                        color = ClayColors.TextTertiary
                                    )
                                    Text(
                                        text = SimpleTime(endHour, endMinute).formatted(),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ClayColors.TextPrimary
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        ClayPill(
                                            text = "-1h",
                                            isSelected = false,
                                            onClick = { endHour = if (endHour == 0) 23 else endHour - 1 },
                                            unselectedColor = ClayColors.SurfaceMarshmallow
                                        )
                                        ClayPill(
                                            text = "+1h",
                                            isSelected = false,
                                            onClick = { endHour = (endHour + 1) % 24 },
                                            unselectedColor = ClayColors.SurfaceMarshmallow
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Location Field
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Location / Virtual Link",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClayColors.TextTertiary
                        )
                        ClayTextField(
                            value = location,
                            onValueChange = { location = it },
                            placeholder = "e.g. Room 3A, Cafe, Google Meet...",
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.LocationOn,
                                    contentDescription = "Location",
                                    tint = ClayColors.TextTertiary,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            modifier = Modifier.fillMaxWidth().testTag("event_location_input")
                        )
                    }

                    // Priority Selector
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Priority Level",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClayColors.TextTertiary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Normal", "Medium", "High").forEach { p ->
                                val isSelected = priority.equals(p, ignoreCase = true)
                                val pillColor = when (p) {
                                    "High" -> ClayColors.ClayTerracotta
                                    "Medium" -> ClayColors.ClayAmber
                                    else -> ClayColors.ClayMint
                                }
                                ClayPill(
                                    text = p,
                                    isSelected = isSelected,
                                    onClick = { priority = p },
                                    selectedColor = pillColor,
                                    unselectedColor = ClayColors.SurfaceSoftClay,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // Reminder Notification Selector
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Reminder Notification",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ClayColors.TextTertiary
                            )
                            Text(
                                text = when (reminderMinutes) {
                                    -1 -> "Disabled"
                                    0 -> "At event time"
                                    5 -> "5m before"
                                    15 -> "15m before"
                                    30 -> "30m before"
                                    60 -> "1h before"
                                    1440 -> "1d before"
                                    else -> "${reminderMinutes}m before"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (reminderMinutes >= 0) ClayColors.PrimaryAccent else ClayColors.TextTertiary
                            )
                        }

                        val reminderOptions = listOf(
                            Pair("Off", -1),
                            Pair("At event", 0),
                            Pair("5m", 5),
                            Pair("15m", 15),
                            Pair("30m", 30),
                            Pair("1h", 60),
                            Pair("1d", 1440)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            reminderOptions.forEach { (label, minutes) ->
                                val isSelected = reminderMinutes == minutes
                                ClayPill(
                                    text = label,
                                    isSelected = isSelected,
                                    onClick = { reminderMinutes = minutes },
                                    selectedColor = ClayColors.PrimaryAccent,
                                    unselectedColor = ClayColors.SurfaceSoftClay
                                )
                            }
                        }
                    }

                    // Description Field
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Notes & Details",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClayColors.TextTertiary
                        )
                        ClayTextField(
                            value = description,
                            onValueChange = { description = it },
                            placeholder = "Add notes, agenda items, or reminders...",
                            singleLine = false,
                            maxLines = 3,
                            minHeight = 70.dp,
                            modifier = Modifier.fillMaxWidth().testTag("event_desc_input")
                        )
                    }

                    // Action Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ClayButton(
                            onClick = onDismiss,
                            containerColor = ClayColors.SurfaceSoftClay,
                            contentColor = ClayColors.TextSecondary,
                            shape = RoundedCornerShape(20.dp),
                            elevation = 2.dp,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Cancel", fontWeight = FontWeight.Bold)
                        }

                        ClayButton(
                            onClick = {
                                if (title.isBlank()) {
                                    titleError = true
                                } else {
                                    onSave(
                                        title,
                                        description,
                                        category,
                                        selectedDate,
                                        SimpleTime(startHour, startMinute),
                                        SimpleTime(endHour, endMinute),
                                        location,
                                        priority,
                                        reminderMinutes,
                                        editingEvent?.id ?: 0L
                                    )
                                }
                            },
                            containerColor = ClayColors.ClayTerracotta,
                            contentColor = Color.White,
                            shape = RoundedCornerShape(20.dp),
                            elevation = 6.dp,
                            modifier = Modifier.weight(1.5f).testTag("save_event_button")
                        ) {
                            Text(
                                text = if (editingEvent != null) "Update Event" else "Mold Event",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
