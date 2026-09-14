package com.example.ui.calendar

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Today
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
import com.example.model.SimpleDate
import com.example.ui.clay.ClayButton
import com.example.ui.clay.ClayColors
import com.example.ui.clay.ClayIconButton
import com.example.ui.clay.ClayPill

@Composable
fun CalendarHeader(
    uiState: CalendarUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onJumpToToday: () -> Unit,
    onViewModeSelected: (CalendarViewMode) -> Unit,
    onAddNewEvent: () -> Unit = {},
    isWideScreen: Boolean = false,
    modifier: Modifier = Modifier
) {
    val monthName = SimpleDate.getMonthName(uiState.displayedMonth)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = if (isWideScreen) 6.dp else 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top Row: "ClayCal" title and circular Green '+' button (Screen 7)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ClayCal",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = ClayColors.TextPrimary
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Today badge jump button
                Box(
                    modifier = Modifier
                        .shadow(
                            elevation = 2.dp,
                            shape = RoundedCornerShape(14.dp),
                            ambientColor = ClayColors.ShadowAmbient,
                            spotColor = ClayColors.ShadowSpot
                        )
                        .background(ClayColors.SurfaceMarshmallow, RoundedCornerShape(14.dp))
                        .border(
                            width = 1.dp,
                            color = ClayColors.ShadowBevel.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onJumpToToday() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("today_jump_badge"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Today",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ClayColors.ClaySage
                    )
                }

                // Circular Green '+' Clay Button (Screen 7 top right)
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = CircleShape,
                            ambientColor = ClayColors.ClaySage.copy(alpha = 0.35f),
                            spotColor = ClayColors.ClaySage.copy(alpha = 0.4f)
                        )
                        .background(ClayColors.ClaySage, CircleShape)
                        .border(
                            width = 1.2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(Color.White.copy(alpha = 0.7f), Color.Transparent)
                            ),
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .clickable { onAddNewEvent() }
                        .testTag("header_add_event_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Event",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Subheader: Month Nav (< September 2026 >)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .clickable { onPreviousMonth() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous",
                    tint = ClayColors.TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Text(
                text = "$monthName ${uiState.displayedYear}",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = ClayColors.TextPrimary
            )

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .clickable { onNextMonth() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next",
                    tint = ClayColors.TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // View Mode Switcher Pills: [Month] [Week] [Day] [Agenda]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalendarViewMode.entries.forEach { mode ->
                val isSelected = uiState.viewMode == mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .shadow(
                            elevation = if (isSelected) 3.dp else 1.dp,
                            shape = RoundedCornerShape(14.dp),
                            ambientColor = ClayColors.ShadowAmbient,
                            spotColor = ClayColors.ShadowSpot
                        )
                        .background(
                            color = if (isSelected) ClayColors.ClaySage else ClayColors.SurfaceMarshmallow,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) Color.White.copy(alpha = 0.5f) else ClayColors.ShadowBevel.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onViewModeSelected(mode) }
                        .padding(vertical = 7.dp)
                        .testTag("mode_${mode.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mode.label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else ClayColors.TextSecondary
                    )
                }
            }
        }
    }
}
