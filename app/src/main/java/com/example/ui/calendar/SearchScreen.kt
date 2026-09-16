package com.example.ui.calendar

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CalendarEvent
import com.example.model.Category
import com.example.model.SimpleDate
import com.example.ui.clay.ClayColors
import com.example.ui.clay.clayMoulded

@Composable
fun SearchScreen(
    uiState: CalendarUiState,
    onQueryChange: (String) -> Unit,
    onSelectCategory: (String?) -> Unit = {},
    onEventClick: (CalendarEvent) -> Unit,
    onToggleCompleted: (Long, Boolean) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val recentSearches = remember {
        mutableStateListOf("Math", "Gym", "Read", "Study", "Work")
    }

    val query = uiState.searchQuery
    val searchResults = remember(query, uiState.allEvents) {
        if (query.isBlank()) {
            emptyList()
        } else {
            uiState.allEvents.filter { event ->
                event.title.contains(query, ignoreCase = true) ||
                        event.description.contains(query, ignoreCase = true) ||
                        event.location.contains(query, ignoreCase = true) ||
                        event.category.contains(query, ignoreCase = true)
            }.sortedWith(
                compareBy<CalendarEvent> { it.year }
                    .thenBy { it.month }
                    .thenBy { it.day }
                    .thenBy { it.startHour }
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ClayColors.Background)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Title: Search
        Text(
            text = "Search",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = ClayColors.TextPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Clay Inset Search Box
        val searchBoxShape = RoundedCornerShape(28.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clayMoulded(
                    color = ClayColors.SurfaceDimmed.copy(alpha = 0.55f),
                    shape = searchBoxShape,
                    elevation = 3.dp
                )
                .padding(horizontal = 18.dp, vertical = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search",
                    tint = ClayColors.TextSecondary,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Box(modifier = Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(
                            text = "Search events, notes, tags...",
                            fontSize = 15.sp,
                            color = ClayColors.TextTertiary
                        )
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = ClayColors.TextPrimary
                        ),
                        cursorBrush = SolidColor(ClayColors.PrimaryAccent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_text_input")
                    )
                }

                if (query.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(ClayColors.TextSecondary.copy(alpha = 0.18f))
                            .clickable { onQueryChange("") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = "Clear",
                            tint = ClayColors.TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Recent Searches section
        if (query.isBlank()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ClayColors.TextPrimary
                )
                if (recentSearches.isNotEmpty()) {
                    Text(
                        text = "Clear",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = ClayColors.TextTertiary,
                        modifier = Modifier
                            .clickable { recentSearches.clear() }
                            .padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                recentSearches.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onQueryChange(item) }
                            .padding(vertical = 10.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            tint = ClayColors.TextTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = item,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal,
                            color = ClayColors.TextPrimary
                        )
                    }
                }
            }
        } else {
            // Search Results
            Text(
                text = "Results (${searchResults.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = ClayColors.TextSecondary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (searchResults.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No events found for \"$query\"",
                        fontSize = 14.sp,
                        color = ClayColors.TextTertiary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(searchResults, key = { it.id }) { event ->
                        val cat = Category.fromName(event.category)
                        val itemShape = RoundedCornerShape(28.dp)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clayMoulded(
                                    color = ClayColors.SurfaceMarshmallow,
                                    shape = itemShape,
                                    elevation = 4.dp
                                )
                                .clip(itemShape)
                                .clickable { onEventClick(event) }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clayMoulded(cat.clayColor, CircleShape, elevation = 1.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = event.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ClayColors.TextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${event.date.formatted()} • ${event.timeRangeFormatted}",
                                    fontSize = 13.sp,
                                    color = ClayColors.TextSecondary
                                )
                            }
                            Text(
                                text = cat.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = cat.clayColor
                            )
                        }
                    }
                }
            }
        }
    }
}
