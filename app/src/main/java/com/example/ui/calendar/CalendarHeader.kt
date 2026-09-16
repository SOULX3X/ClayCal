package com.example.ui.calendar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SimpleDate
import com.example.ui.clay.Clay3DBlob
import com.example.ui.clay.ClayColors
import com.example.ui.clay.clayMoulded

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
            .padding(horizontal = 18.dp, vertical = if (isWideScreen) 6.dp else 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Row: Title + 3D clay decorative blob and "Today" button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Clay3DBlob(
                    color = ClayColors.PrimaryAccent,
                    size = 28.dp
                )
                Text(
                    text = "ClayCal",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = ClayColors.TextPrimary
                )
            }

            // Today jump pill button - puffy marshmallow clay
            val todayShape = CircleShape
            val todayInteraction = remember { MutableInteractionSource() }
            val isTodayPressed by todayInteraction.collectIsPressedAsState()
            val todayScale by animateFloatAsState(
                targetValue = if (isTodayPressed) 0.90f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "today_scale"
            )

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = todayScale
                        scaleY = todayScale
                    }
                    .clayMoulded(
                        color = ClayColors.SurfaceMarshmallow,
                        shape = todayShape,
                        elevation = if (isTodayPressed) 2.dp else 6.dp,
                        isPressed = isTodayPressed
                    )
                    .clickable(
                        interactionSource = todayInteraction,
                        indication = null,
                        onClick = onJumpToToday
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
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
            val arrowShape = CircleShape
            val prevInteraction = remember { MutableInteractionSource() }
            val isPrevPressed by prevInteraction.collectIsPressedAsState()
            val prevScale by animateFloatAsState(
                targetValue = if (isPrevPressed) 0.88f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "prev_scale"
            )

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .graphicsLayer {
                        scaleX = prevScale
                        scaleY = prevScale
                    }
                    .clayMoulded(
                        color = ClayColors.SurfaceMarshmallow,
                        shape = arrowShape,
                        elevation = if (isPrevPressed) 1.dp else 5.dp,
                        isPressed = isPrevPressed
                    )
                    .clickable(
                        interactionSource = prevInteraction,
                        indication = null,
                        onClick = onPreviousMonth
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Previous",
                    tint = ClayColors.TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = "$monthName ${uiState.displayedYear}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ClayColors.TextPrimary
            )

            val nextInteraction = remember { MutableInteractionSource() }
            val isNextPressed by nextInteraction.collectIsPressedAsState()
            val nextScale by animateFloatAsState(
                targetValue = if (isNextPressed) 0.88f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "next_scale"
            )

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .graphicsLayer {
                        scaleX = nextScale
                        scaleY = nextScale
                    }
                    .clayMoulded(
                        color = ClayColors.SurfaceMarshmallow,
                        shape = arrowShape,
                        elevation = if (isNextPressed) 1.dp else 5.dp,
                        isPressed = isNextPressed
                    )
                    .clickable(
                        interactionSource = nextInteraction,
                        indication = null,
                        onClick = onNextMonth
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = "Next",
                    tint = ClayColors.TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // View Mode Switcher Pills: [Month] [Week] [Day] [Schedule]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val pillShape = CircleShape
            CalendarViewMode.entries.forEach { mode ->
                val isSelected = uiState.viewMode == mode
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()

                val scale by animateFloatAsState(
                    targetValue = if (isPressed) 0.92f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "mode_scale"
                )

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
                    targetValue = if (isPressed) 1.dp else if (isSelected) 6.dp else 2.dp,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    label = "mode_elev"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .clayMoulded(
                            color = animatedBg,
                            shape = pillShape,
                            elevation = animatedElev,
                            isPressed = isPressed
                        )
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            onViewModeSelected(mode)
                        }
                        .padding(vertical = 10.dp)
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
