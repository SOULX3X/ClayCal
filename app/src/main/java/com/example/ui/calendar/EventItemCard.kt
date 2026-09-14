package com.example.ui.calendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CalendarEvent
import com.example.model.Category
import com.example.ui.clay.ClayBadge
import com.example.ui.clay.ClayCard
import com.example.ui.clay.ClayColors

@Composable
fun EventItemCard(
    event: CalendarEvent,
    onToggleCompleted: (Boolean) -> Unit,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val category = Category.fromName(event.category)
    val shape = RoundedCornerShape(22.dp)

    val scale by animateFloatAsState(
        targetValue = if (event.isCompleted) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "event_card_scale"
    )

    ClayCard(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(onClick = onClick)
            .testTag("event_item_${event.id}"),
        shape = shape,
        surfaceColor = if (event.isCompleted) ClayColors.SurfaceDimmed.copy(alpha = 0.85f) else ClayColors.SurfaceMarshmallow,
        elevation = if (event.isCompleted) 3.dp else 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Tactile Clay Checkbox
            ClayCheckbox(
                checked = event.isCompleted,
                onCheckedChange = onToggleCompleted,
                accentColor = category.clayColor,
                modifier = Modifier.padding(top = 2.dp)
            )

            // Event Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Top row: Category Badge & Priority
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

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (event.priority == "High") {
                            ClayBadge(
                                text = "HIGH",
                                backgroundColor = ClayColors.ClayTerracotta,
                                contentColor = Color.White
                            )
                        } else if (event.priority == "Medium") {
                            ClayBadge(
                                text = "MED",
                                backgroundColor = ClayColors.ClayAmber,
                                contentColor = Color.White
                            )
                        }

                        // Quick action buttons
                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit event",
                                tint = ClayColors.TextTertiary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete event",
                                tint = ClayColors.ClayTerracotta.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Title
                Text(
                    text = event.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (event.isCompleted) ClayColors.TextTertiary else ClayColors.TextPrimary,
                    textDecoration = if (event.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Description (if any)
                if (event.description.isNotBlank()) {
                    Text(
                        text = event.description,
                        fontSize = 13.sp,
                        color = ClayColors.TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )
                }

                // Metadata: Time & Location
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Time chip
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Time",
                            tint = category.clayColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${event.startTime.formatted()} (${event.durationFormatted})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = ClayColors.TextSecondary
                        )
                    }

                    // Location chip (if any)
                    if (event.location.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = ClayColors.TextTertiary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = event.location,
                                fontSize = 12.sp,
                                color = ClayColors.TextTertiary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClayCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (checked) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "checkbox_scale"
    )

    Box(
        modifier = modifier
            .size(28.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = if (checked) 2.dp else 4.dp,
                shape = CircleShape,
                ambientColor = if (checked) accentColor.copy(alpha = 0.35f) else ClayColors.ShadowAmbient,
                spotColor = if (checked) accentColor.copy(alpha = 0.4f) else ClayColors.ShadowSpot
            )
            .background(
                color = if (checked) accentColor else ClayColors.SurfaceMarshmallow,
                shape = CircleShape
            )
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = if (checked) {
                        listOf(Color.White.copy(alpha = 0.7f), Color.Black.copy(alpha = 0.15f))
                    } else {
                        listOf(Color.White.copy(alpha = 0.9f), ClayColors.ShadowBevel.copy(alpha = 0.35f))
                    }
                ),
                shape = CircleShape
            )
            .clip(CircleShape)
            .clickable { onCheckedChange(!checked) }
            .testTag("clay_checkbox"),
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Completed",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
