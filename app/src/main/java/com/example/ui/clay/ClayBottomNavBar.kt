package com.example.ui.clay

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class MainNavTab(
    val label: String,
    val icon: ImageVector,
    val tag: String
) {
    CALENDAR("Home", Icons.Rounded.Home, "tab_home"),
    SEARCH("Search", Icons.Rounded.Search, "tab_search"),
    INSIGHTS("Insights", Icons.Rounded.DateRange, "tab_insights"),
    SETTINGS("Settings", Icons.Rounded.Settings, "tab_settings")
}

@Composable
fun ClayBottomNavBar(
    selectedTab: MainNavTab,
    onTabSelected: (MainNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val navBarShape = RoundedCornerShape(36.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Clay Container for bottom bar - moulded marshmallow with warm hue shadow
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clayMoulded(
                    color = ClayColors.SurfaceMarshmallow,
                    shape = navBarShape,
                    elevation = 12.dp
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MainNavTab.entries.forEach { tab ->
                val isSelected = tab == selectedTab
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()

                val scale by animateFloatAsState(
                    targetValue = if (isPressed) 0.88f else if (isSelected) 1.06f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    label = "tab_scale"
                )

                val iconColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else ClayColors.TextTertiary,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    label = "tab_color"
                )

                val tabPillShape = CircleShape

                Column(
                    modifier = Modifier
                        .testTag(tab.tag)
                        .clip(tabPillShape)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) { onTabSelected(tab) }
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .then(
                                if (isSelected) {
                                    Modifier.clayMoulded(
                                        color = ClayColors.PrimaryAccent,
                                        shape = tabPillShape,
                                        elevation = 6.dp
                                    )
                                } else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            tint = iconColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Text(
                        text = tab.label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) ClayColors.PrimaryAccent else ClayColors.TextTertiary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
