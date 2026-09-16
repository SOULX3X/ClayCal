package com.example.ui.calendar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SimpleDate
import com.example.ui.clay.ClayButton
import com.example.ui.clay.ClayColors
import com.example.ui.clay.ClayIconButton
import com.example.ui.clay.ClayPill
import com.example.ui.clay.clayBounceClickable

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
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Row: "ClayCal" title and "Today" button
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

            // Today badge jump button with bouncy spring
            val todayShape = RoundedCornerShape(18.dp)
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = 3.dp,
                        shape = todayShape,
                        ambientColor = ClayColors.ShadowAmbient,
                        spotColor = ClayColors.ShadowSpot
                    )
                    .background(ClayColors.SurfaceMarshmallow, todayShape)
                    .border(
                        width = 1.dp,
                        color = ClayColors.ShadowBevel.copy(alpha = 0.5f),
                        shape = todayShape
                    )
                    .clayBounceClickable(shape = todayShape, pressedScale = 0.90f) {
                        onJumpToToday()
                    }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .testTag("today_jump_badge"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Today",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ClayColors.PrimaryAccent
                )
            }
        }

        // Subheader: Month Nav (< September 2026 >)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val arrowShape = RoundedCornerShape(14.dp)
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .shadow(
                        elevation = 2.dp,
                        shape = arrowShape,
                        ambientColor = ClayColors.ShadowAmbient,
                        spotColor = ClayColors.ShadowSpot
                    )
                    .background(ClayColors.SurfaceMarshmallow, arrowShape)
                    .border(
                        width = 1.dp,
                        color = ClayColors.ShadowBevel.copy(alpha = 0.4f),
                        shape = arrowShape
                    )
                    .clayBounceClickable(shape = arrowShape, pressedScale = 0.88f) {
                        onPreviousMonth()
                    },
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
                    .size(36.dp)
                    .shadow(
                        elevation = 2.dp,
                        shape = arrowShape,
                        ambientColor = ClayColors.ShadowAmbient,
                        spotColor = ClayColors.ShadowSpot
                    )
                    .background(ClayColors.SurfaceMarshmallow, arrowShape)
                    .border(
                        width = 1.dp,
                        color = ClayColors.ShadowBevel.copy(alpha = 0.4f),
                        shape = arrowShape
                    )
                    .clayBounceClickable(shape = arrowShape, pressedScale = 0.88f) {
                        onNextMonth()
                    },
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

        // View Mode Switcher Pills: [Month] [Week] [Day] [Schedule]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val pillShape = RoundedCornerShape(18.dp)
            CalendarViewMode.entries.forEach { mode ->
                val isSelected = uiState.viewMode == mode

                val animatedBg by animateColorAsState(
                    targetValue = if (isSelected) ClayColors.PrimaryAccent else ClayColors.SurfaceMarshmallow,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    label = "mode_bg"
                )

                val animatedTextColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else ClayColors.TextSecondary,
                    label = "mode_text"
                )

                val animatedElev by animateDpAsState(
                    targetValue = if (isSelected) 4.dp else 1.dp,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    label = "mode_elev"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .shadow(
                            elevation = animatedElev,
                            shape = pillShape,
                            ambientColor = if (isSelected) ClayColors.PrimaryAccent.copy(alpha = 0.35f) else ClayColors.ShadowAmbient,
                            spotColor = if (isSelected) ClayColors.PrimaryAccent.copy(alpha = 0.4f) else ClayColors.ShadowSpot
                        )
                        .background(color = animatedBg, shape = pillShape)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) Color.White.copy(alpha = 0.6f) else ClayColors.ShadowBevel.copy(alpha = 0.35f),
                            shape = pillShape
                        )
                        .clayBounceClickable(shape = pillShape, pressedScale = 0.92f) {
                            onViewModeSelected(mode)
                        }
                        .padding(vertical = 8.dp)
                        .testTag("mode_${mode.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mode.label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = animatedTextColor
                    )
                }
            }
        }
    }
}
