package com.example.ui.calendar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Category
import com.example.ui.clay.ClayColors

enum class InsightsPeriod(val label: String) {
    THIS_WEEK("This Week"),
    THIS_MONTH("This Month"),
    ALL_TIME("All Time")
}

@Composable
fun InsightsScreen(
    uiState: CalendarUiState,
    onAddEventClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedPeriod by remember { mutableStateOf(InsightsPeriod.THIS_WEEK) }
    var isPeriodDropdownOpen by remember { mutableStateOf(false) }

    // Calculate real stats from all events
    val events = uiState.allEvents

    // Calculate duration in minutes for each event
    val categoryMinutes = remember(events, selectedPeriod) {
        val map = mutableMapOf<String, Int>()
        // Initialize standard categories
        listOf("Study", "Health", "Personal", "Other").forEach { map[it] = 0 }

        events.forEach { event ->
            val startMin = event.startHour * 60 + event.startMinute
            val endMin = event.endHour * 60 + event.endMinute
            val duration = maxOf(30, endMin - startMin)

            val catKey = when (event.category.lowercase()) {
                "study" -> "Study"
                "health" -> "Health"
                "personal" -> "Personal"
                else -> "Other"
            }
            map[catKey] = (map[catKey] ?: 0) + duration
        }

        // If no events entered yet, provide standard sample distribution matching Screen 15
        if (map.values.sum() == 0) {
            map["Study"] = 330    // 5h 30m
            map["Health"] = 180   // 3h 00m
            map["Personal"] = 120 // 2h 00m
            map["Other"] = 120    // 2h 00m
        }
        map
    }

    val totalMinutes = categoryMinutes.values.sum()
    val totalHours = totalMinutes / 60
    val remMinutes = totalMinutes % 60

    val categoryColors = mapOf(
        "Study" to ClayColors.ClayTerracotta,
        "Health" to ClayColors.ClayMint,
        "Personal" to ClayColors.ClayLavender,
        "Other" to ClayColors.ClayAmber
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ClayColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header Row: Insights & Period Dropdown
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Insights",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = ClayColors.TextPrimary
            )

            Box {
                Row(
                    modifier = Modifier
                        .shadow(
                            elevation = 2.dp,
                            shape = RoundedCornerShape(16.dp),
                            ambientColor = ClayColors.ShadowAmbient,
                            spotColor = ClayColors.ShadowSpot
                        )
                        .background(
                            color = ClayColors.SurfaceMarshmallow,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = ClayColors.ShadowBevel.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { isPeriodDropdownOpen = true }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedPeriod.label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = ClayColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Select period",
                        tint = ClayColors.TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                DropdownMenu(
                    expanded = isPeriodDropdownOpen,
                    onDismissRequest = { isPeriodDropdownOpen = false }
                ) {
                    InsightsPeriod.entries.forEach { period ->
                        DropdownMenuItem(
                            text = { Text(period.label) },
                            onClick = {
                                selectedPeriod = period
                                isPeriodDropdownOpen = false
                            }
                        )
                    }
                }
            }
        }

        // Donut Chart Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = ClayColors.ShadowAmbient,
                    spotColor = ClayColors.ShadowSpot
                )
                .background(
                    color = ClayColors.SurfaceMarshmallow,
                    shape = RoundedCornerShape(24.dp)
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.9f),
                            ClayColors.ShadowBevel.copy(alpha = 0.35f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.size(180.dp),
                contentAlignment = Alignment.Center
            ) {
                // Donut Chart Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    var startAngle = -90f
                    val strokeWidth = 28.dp.toPx()

                    categoryMinutes.forEach { (cat, mins) ->
                        val sweepAngle = if (totalMinutes > 0) {
                            (mins.toFloat() / totalMinutes) * 360f
                        } else 0f

                        val color = categoryColors[cat] ?: ClayColors.ClaySage

                        if (sweepAngle > 0) {
                            drawArc(
                                color = color,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle - 3f, // gap between segments
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                        startAngle += sweepAngle
                    }
                }

                // Center Label
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${totalHours}h ${remMinutes}m",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClayColors.TextPrimary
                    )
                    Text(
                        text = "Total time",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = ClayColors.TextSecondary
                    )
                }
            }
        }

        // Category Breakdown Rows
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            categoryMinutes.forEach { (categoryName, mins) ->
                val hours = mins / 60
                val minutes = mins % 60
                val color = categoryColors[categoryName] ?: ClayColors.ClaySage

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(color, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = categoryName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = ClayColors.TextPrimary
                        )
                    }

                    Text(
                        text = "${hours}h ${minutes.toString().padStart(2, '0')}m",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ClayColors.TextPrimary
                    )
                }
            }
        }

        // Most Active Day Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = ClayColors.ShadowAmbient,
                    spotColor = ClayColors.ShadowSpot
                )
                .background(
                    color = ClayColors.SurfaceMarshmallow,
                    shape = RoundedCornerShape(20.dp)
                )
                .border(
                    width = 1.2.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.9f),
                            ClayColors.ShadowBevel.copy(alpha = 0.35f)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Most active day",
                        fontSize = 12.sp,
                        color = ClayColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Tuesday",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClayColors.TextPrimary
                    )
                }

                Text(
                    text = "4h 30m",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ClayColors.ClaySage
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
