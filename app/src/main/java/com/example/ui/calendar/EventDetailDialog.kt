package com.example.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.CalendarEvent
import com.example.model.Category
import com.example.ui.clay.ClayBadge
import com.example.ui.clay.ClayButton
import com.example.ui.clay.ClayCard
import com.example.ui.clay.ClayColors
import com.example.ui.clay.ClayIconButton

@Composable
fun EventDetailDialog(
    event: CalendarEvent,
    onDismiss: () -> Unit,
    onToggleCompleted: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val category = Category.fromName(event.category)

    Dialog(onDismissRequest = onDismiss) {
        ClayCard(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 20.dp),
            shape = RoundedCornerShape(28.dp),
            surfaceColor = ClayColors.SurfaceMarshmallow,
            elevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top row with Category and Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ClayBadge(
                        text = category.name,
                        backgroundColor = category.clayColor,
                        iconEmoji = category.iconEmoji
                    )

                    ClayIconButton(
                        onClick = onDismiss,
                        icon = Icons.Default.Close,
                        contentDescription = "Close",
                        size = 36.dp,
                        elevation = 2.dp
                    )
                }

                // Title
                Text(
                    text = event.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = ClayColors.TextPrimary,
                    textDecoration = if (event.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )

                // Date & Time Card
                ClayCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    surfaceColor = ClayColors.SurfaceSoftClay,
                    elevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Event,
                                contentDescription = "Date",
                                tint = category.clayColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = event.date.formatted(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ClayColors.TextPrimary
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Time",
                                tint = category.clayColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "${event.timeRangeFormatted} (${event.durationFormatted})",
                                fontSize = 14.sp,
                                color = ClayColors.TextSecondary
                            )
                        }

                        if (event.location.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Location",
                                    tint = ClayColors.TextTertiary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = event.location,
                                    fontSize = 14.sp,
                                    color = ClayColors.TextSecondary
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PriorityHigh,
                                contentDescription = "Priority",
                                tint = ClayColors.ClayAmber,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Priority: ${event.priority}",
                                fontSize = 13.sp,
                                color = ClayColors.TextTertiary
                            )
                        }
                    }
                }

                // Description (if present)
                if (event.description.isNotBlank()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Notes",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClayColors.TextTertiary
                        )
                        Text(
                            text = event.description,
                            fontSize = 14.sp,
                            color = ClayColors.TextPrimary,
                            lineHeight = 20.sp
                        )
                    }
                }

                // Toggle Completed Button
                ClayButton(
                    onClick = { onToggleCompleted(!event.isCompleted) },
                    containerColor = if (event.isCompleted) ClayColors.ClaySage else ClayColors.ClayTerracotta,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    elevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Complete",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (event.isCompleted) "Completed! Tap to Reopen" else "Mark as Completed",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // Edit & Delete Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ClayButton(
                        onClick = onEdit,
                        containerColor = ClayColors.SurfaceSoftClay,
                        contentColor = ClayColors.TextPrimary,
                        shape = RoundedCornerShape(16.dp),
                        elevation = 2.dp,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = ClayColors.TextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Edit", fontWeight = FontWeight.Bold)
                    }

                    ClayButton(
                        onClick = onDelete,
                        containerColor = ClayColors.ClayTerracotta.copy(alpha = 0.15f),
                        contentColor = ClayColors.ClayTerracotta,
                        shape = RoundedCornerShape(16.dp),
                        elevation = 2.dp,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = ClayColors.ClayTerracotta,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Delete", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
