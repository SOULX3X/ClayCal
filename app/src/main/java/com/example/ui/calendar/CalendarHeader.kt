package com.example.ui.calendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.model.SimpleDate
import com.example.ui.clay.ClayButton
import com.example.ui.clay.ClayCard
import com.example.ui.clay.ClayColors
import com.example.ui.clay.ClayIconButton
import com.example.ui.clay.ClayPill
import com.example.ui.clay.ClayTextField

@Composable
fun CalendarHeader(
    uiState: CalendarUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onJumpToToday: () -> Unit,
    onViewModeSelected: (CalendarViewMode) -> Unit,
    onToggleSearch: (Boolean) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val monthName = SimpleDate.getMonthName(uiState.displayedMonth)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // App Title Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App branding pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Tactile Clay Logo Icon
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(14.dp),
                            ambientColor = ClayColors.ClayTerracotta.copy(alpha = 0.35f),
                            spotColor = ClayColors.ClayTerracotta.copy(alpha = 0.4f)
                        )
                        .background(ClayColors.ClayTerracotta, shape = RoundedCornerShape(14.dp))
                        .border(
                            width = 1.5.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.7f),
                                    Color.Black.copy(alpha = 0.15f)
                                ),
                                start = Offset.Zero,
                                end = Offset.Infinite
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🗓️", fontSize = 20.sp)
                }

                Column {
                    Text(
                        text = "Claycal",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = ClayColors.TextPrimary
                    )
                    Text(
                        text = "Tactile Daily Flow",
                        fontSize = 12.sp,
                        color = ClayColors.TextTertiary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Action Buttons (Search & Today jump)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ClayIconButton(
                    onClick = { onToggleSearch(!uiState.isSearchActive) },
                    icon = if (uiState.isSearchActive) Icons.Default.Close else Icons.Default.Search,
                    contentDescription = "Search",
                    containerColor = if (uiState.isSearchActive) ClayColors.ClayTerracotta else ClayColors.SurfaceMarshmallow,
                    iconTint = if (uiState.isSearchActive) Color.White else ClayColors.TextPrimary,
                    size = 42.dp,
                    shape = RoundedCornerShape(14.dp)
                )

                ClayIconButton(
                    onClick = onOpenSettings,
                    icon = Icons.Default.Settings,
                    contentDescription = "Settings",
                    containerColor = ClayColors.SurfaceMarshmallow,
                    iconTint = ClayColors.TextPrimary,
                    size = 42.dp,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("settings_button")
                )

                val isCurrentMonthToday = uiState.selectedDate.isToday() &&
                        uiState.displayedMonth == SimpleDate.today().month &&
                        uiState.displayedYear == SimpleDate.today().year

                ClayButton(
                    onClick = onJumpToToday,
                    containerColor = if (isCurrentMonthToday) ClayColors.SurfaceMarshmallow else ClayColors.ClayPeach,
                    contentColor = if (isCurrentMonthToday) ClayColors.TextPrimary else Color.White,
                    shape = RoundedCornerShape(14.dp),
                    elevation = 4.dp,
                    modifier = Modifier.height(42.dp).testTag("today_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Today,
                        contentDescription = "Today",
                        tint = if (isCurrentMonthToday) ClayColors.ClayTerracotta else Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Today",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Search bar (collapsible)
        AnimatedVisibility(
            visible = uiState.isSearchActive,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            ClayTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = "Search events by title, tag, location...",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = ClayColors.TextTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchQueryChange("") },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = ClayColors.TextTertiary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Month Navigation Card (only for Month / Week view)
        if (uiState.viewMode == CalendarViewMode.MONTH || uiState.viewMode == CalendarViewMode.WEEK) {
            ClayCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                surfaceColor = ClayColors.SurfaceMarshmallow,
                elevation = 5.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ClayIconButton(
                        onClick = onPreviousMonth,
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Month",
                        size = 38.dp,
                        shape = RoundedCornerShape(12.dp),
                        elevation = 3.dp
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = monthName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClayColors.TextPrimary
                        )
                        Text(
                            text = "${uiState.displayedYear}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = ClayColors.TextTertiary
                        )
                    }

                    ClayIconButton(
                        onClick = onNextMonth,
                        icon = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Month",
                        size = 38.dp,
                        shape = RoundedCornerShape(12.dp),
                        elevation = 3.dp
                    )
                }
            }
        }

        // View Mode Switcher Pills: [Month] [Week] [Day] [Agenda]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalendarViewMode.values().forEach { mode ->
                ClayPill(
                    text = mode.label,
                    isSelected = uiState.viewMode == mode,
                    onClick = { onViewModeSelected(mode) },
                    iconEmoji = mode.iconEmoji,
                    selectedColor = ClayColors.ClayTerracotta,
                    unselectedColor = ClayColors.SurfaceMarshmallow,
                    modifier = Modifier.weight(1f).testTag("mode_${mode.name.lowercase()}")
                )
            }
        }
    }
}
