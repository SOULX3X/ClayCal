package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppThemeMode
import com.example.model.SimpleDate
import com.example.ui.calendar.CalendarHeader
import com.example.ui.calendar.CalendarStatsBanner
import com.example.ui.calendar.CalendarViewMode
import com.example.ui.calendar.CalendarViewModel
import com.example.ui.calendar.DayView
import com.example.ui.calendar.EventDetailDialog
import com.example.ui.calendar.EventDialog
import com.example.ui.calendar.InsightsScreen
import com.example.ui.calendar.MonthView
import com.example.ui.calendar.OnboardingFlow
import com.example.ui.calendar.ScheduleView
import com.example.ui.calendar.SearchScreen
import com.example.ui.calendar.SettingsDialog
import com.example.ui.calendar.SettingsScreen
import com.example.ui.calendar.WeekView
import com.example.ui.clay.ClayBottomNavBar
import com.example.ui.clay.ClayColors
import com.example.ui.clay.MainNavTab
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: CalendarViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val systemDark = isSystemInDarkTheme()
            val isDark = when (uiState.themeMode) {
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
                AppThemeMode.SYSTEM -> systemDark
            }
            ClayColors.isDark = isDark
            ClayColors.activeAccentName = uiState.activeAccent

            MyApplicationTheme(darkTheme = isDark) {
                CalendarApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CalendarApp(
    viewModel: CalendarViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    if (uiState.isOnboardingVisible) {
        OnboardingFlow(
            onFinish = { viewModel.closeOnboarding() },
            onRestoreBackup = {
                viewModel.showSnackbar("Backup restoration ready")
            }
        )
        return
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ClayColors.Background),
        containerColor = ClayColors.Background,
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            ClayBottomNavBar(
                selectedTab = uiState.mainTab,
                onTabSelected = { viewModel.setMainTab(it) }
            )
        },
        floatingActionButton = {
            if (uiState.mainTab == MainNavTab.CALENDAR) {
                ClayFloatingActionButton(
                    onClick = { viewModel.openCreateDialog() },
                    containerColor = ClayColors.PrimaryAccent,
                    modifier = Modifier.testTag("fab_add_event")
                )
            }
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isWideScreen = maxWidth >= 600.dp

            AnimatedContent(
                targetState = uiState.mainTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "main_tab_transition",
                modifier = Modifier.fillMaxSize()
            ) { tab ->
                when (tab) {
                    MainNavTab.CALENDAR -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            CalendarHeader(
                                uiState = uiState,
                                onPreviousMonth = { viewModel.previousMonth() },
                                onNextMonth = { viewModel.nextMonth() },
                                onJumpToToday = { viewModel.jumpToToday() },
                                onViewModeSelected = { viewModel.setViewMode(it) },
                                onAddNewEvent = { viewModel.openCreateDialog() },
                                isWideScreen = isWideScreen
                            )

                            AnimatedContent(
                                targetState = uiState.viewMode,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "view_mode_transition",
                                modifier = Modifier.weight(1f)
                            ) { mode ->
                                when (mode) {
                                    CalendarViewMode.MONTH -> {
                                        MonthView(
                                            uiState = uiState,
                                            onDateSelected = { viewModel.selectDate(it) },
                                            onToggleCompleted = { id, done -> viewModel.toggleCompleted(id, done) },
                                            onEventClicked = { viewModel.openDetailDialog(it) },
                                            onEditEvent = { viewModel.openEditDialog(it) },
                                            onDeleteEvent = { viewModel.deleteEvent(it) },
                                            onAddNewEvent = { viewModel.openCreateDialog(it) },
                                            isWideScreen = isWideScreen
                                        )
                                    }

                                    CalendarViewMode.WEEK -> {
                                        WeekView(
                                            uiState = uiState,
                                            onDateSelected = { viewModel.selectDate(it) },
                                            onToggleCompleted = { id, done -> viewModel.toggleCompleted(id, done) },
                                            onEventClicked = { viewModel.openDetailDialog(it) },
                                            onEditEvent = { viewModel.openEditDialog(it) },
                                            onDeleteEvent = { viewModel.deleteEvent(it) },
                                            onAddNewEvent = { viewModel.openCreateDialog(it) },
                                            isWideScreen = isWideScreen
                                        )
                                    }

                                    CalendarViewMode.DAY -> {
                                        DayView(
                                            uiState = uiState,
                                            onToggleCompleted = { id, done -> viewModel.toggleCompleted(id, done) },
                                            onEventClicked = { viewModel.openDetailDialog(it) },
                                            onEditEvent = { viewModel.openEditDialog(it) },
                                            onDeleteEvent = { viewModel.deleteEvent(it) },
                                            onAddNewEventAtHour = { date, hour ->
                                                viewModel.openCreateDialog(date)
                                            },
                                            isWideScreen = isWideScreen
                                        )
                                    }

                                    CalendarViewMode.SCHEDULE -> {
                                        ScheduleView(
                                            uiState = uiState,
                                            onCategorySelected = { viewModel.setSelectedCategory(it) },
                                            onToggleCompleted = { id, done -> viewModel.toggleCompleted(id, done) },
                                            onEventClicked = { viewModel.openDetailDialog(it) },
                                            onEditEvent = { viewModel.openEditDialog(it) },
                                            onDeleteEvent = { viewModel.deleteEvent(it) },
                                            isWideScreen = isWideScreen
                                        )
                                    }
                                }
                            }
                        }
                    }

                    MainNavTab.SEARCH -> {
                        SearchScreen(
                            uiState = uiState,
                            onQueryChange = { viewModel.setSearchQuery(it) },
                            onSelectCategory = { viewModel.setSelectedCategory(it) },
                            onEventClick = { viewModel.openDetailDialog(it) },
                            onToggleCompleted = { id, done -> viewModel.toggleCompleted(id, done) }
                        )
                    }

                    MainNavTab.INSIGHTS -> {
                        InsightsScreen(
                            uiState = uiState,
                            onAddEventClick = { viewModel.openCreateDialog() }
                        )
                    }

                    MainNavTab.SETTINGS -> {
                        SettingsScreen(
                            currentThemeMode = uiState.themeMode,
                            activeAccent = uiState.activeAccent,
                            eventsCount = uiState.allEvents.size,
                            onThemeModeChange = { viewModel.setThemeMode(it) },
                            onAccentColorChange = { viewModel.setAccentPalette(it) },
                            onExportData = { viewModel.exportEventsJson() },
                            onImportData = { viewModel.showSnackbar("File picker ready for backup restore") },
                            onClearAllData = { viewModel.deleteAllData() },
                            onOpenOnboarding = { viewModel.showOnboarding() }
                        )
                    }
                }
            }
        }

        // Create / Edit Event Dialog
        if (uiState.isCreateDialogOpen) {
            EventDialog(
                editingEvent = uiState.editingEvent,
                initialDate = uiState.selectedDate,
                onDismiss = { viewModel.closeDialogs() },
                onSave = { title, desc, cat, date, start, end, loc, priority, existingId ->
                    viewModel.saveEvent(
                        title = title,
                        description = desc,
                        category = cat,
                        date = date,
                        startTime = start,
                        endTime = end,
                        location = loc,
                        priority = priority,
                        existingId = existingId
                    )
                }
            )
        }

        // Event Detail Dialog
        uiState.detailEvent?.let { event ->
            EventDetailDialog(
                event = event,
                onDismiss = { viewModel.closeDialogs() },
                onToggleCompleted = { done -> viewModel.toggleCompleted(event.id, done) },
                onEdit = { viewModel.openEditDialog(event) },
                onDelete = { viewModel.deleteEvent(event.id) }
            )
        }

        // Settings Dialog (if opened directly)
        if (uiState.isSettingsOpen) {
            SettingsDialog(
                currentThemeMode = uiState.themeMode,
                activeAccent = uiState.activeAccent,
                eventsCount = uiState.allEvents.size,
                onThemeModeChange = { viewModel.setThemeMode(it) },
                onAccentChange = { viewModel.setAccentPalette(it) },
                onExportJson = { viewModel.exportEventsJson() },
                onImportJson = { json, replace -> viewModel.importEvents(json, replace) },
                onDeleteAllData = { viewModel.deleteAllData() },
                onDismiss = { viewModel.closeSettings() }
            )
        }
    }
}

@Composable
fun ClayFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = ClayColors.PrimaryAccent
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val shape = CircleShape

    Box(
        modifier = modifier
            .size(58.dp)
            .graphicsLayer {
                val scale = if (isPressed) 0.92f else 1f
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = if (isPressed) 2.dp else 6.dp,
                shape = shape,
                ambientColor = containerColor.copy(alpha = 0.35f),
                spotColor = containerColor.copy(alpha = 0.4f)
            )
            .background(containerColor, shape = shape)
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.7f),
                        Color.Transparent
                    ),
                    start = Offset.Zero,
                    end = Offset.Infinite
                ),
                shape = shape
            )
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add new event",
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
    }
}
