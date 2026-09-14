package com.example.ui.calendar

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SimpleDate
import com.example.model.SimpleTime
import com.example.ui.clay.ClayCard
import com.example.ui.clay.ClayColors

@Composable
fun CalendarStatsBanner(
    uiState: CalendarUiState,
    modifier: Modifier = Modifier
) {
    val totalToday = uiState.todayTotalCount
    val completedToday = uiState.todayCompletedCount
    val progressFraction = if (totalToday > 0) completedToday.toFloat() / totalToday.toFloat() else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "stats_progress"
    )

    // Next upcoming event today
    val now = SimpleTime.now()
    val upcomingToday = uiState.eventsForToday
        .filter { !it.isCompleted && (it.startHour > now.hour || (it.startHour == now.hour && it.startMinute >= now.minute)) }
        .minByOrNull { it.startHour * 60 + it.startMinute }

    ClayCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        surfaceColor = ClayColors.SurfaceMarshmallow,
        elevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Today's Pulse",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClayColors.ClayTerracotta
                    )
                    Text(
                        text = "• ${SimpleDate.today().formattedShort()}",
                        fontSize = 12.sp,
                        color = ClayColors.TextTertiary
                    )
                }

                Text(
                    text = if (totalToday == 0) {
                        "No events scheduled for today"
                    } else if (completedToday == totalToday) {
                        "All $totalToday events completed! 🎉"
                    } else {
                        "$completedToday of $totalToday events done"
                    },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ClayColors.TextPrimary
                )

                if (upcomingToday != null) {
                    Text(
                        text = "Next: ${upcomingToday.title} at ${upcomingToday.startTime.formatted()}",
                        fontSize = 12.sp,
                        color = ClayColors.TextSecondary,
                        maxLines = 1
                    )
                } else if (totalToday > 0) {
                    Text(
                        text = "Great pacing for the day!",
                        fontSize = 12.sp,
                        color = ClayColors.ClaySage,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Circular progress with Clay ring
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .shadow(
                        elevation = 3.dp,
                        shape = CircleShape,
                        ambientColor = ClayColors.ShadowAmbient,
                        spotColor = ClayColors.ShadowSpot
                    )
                    .background(ClayColors.SurfaceSoftClay, shape = CircleShape)
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(Color.White.copy(alpha = 0.9f), ClayColors.ShadowBevel.copy(alpha = 0.3f))
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.size(46.dp),
                    color = ClayColors.ClaySage,
                    trackColor = ClayColors.SurfaceDimmed,
                    strokeWidth = 5.dp,
                    strokeCap = StrokeCap.Round
                )
                Text(
                    text = "${(progressFraction * 100).toInt()}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ClayColors.TextPrimary
                )
            }
        }
    }
}
