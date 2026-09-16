package com.example.ui.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppThemeMode
import com.example.data.CalendarDatabase
import com.example.data.CalendarDataTransfer
import com.example.data.CalendarRepository
import com.example.data.ThemePreferences
import com.example.model.CalendarEvent
import com.example.model.Category
import com.example.model.SimpleDate
import com.example.model.SimpleTime
import com.example.notification.EventNotificationScheduler
import com.example.ui.clay.ClayColors
import com.example.ui.clay.MainNavTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CalendarUiState(
    val selectedDate: SimpleDate = SimpleDate.today(),
    val displayedYear: Int = SimpleDate.today().year,
    val displayedMonth: Int = SimpleDate.today().month,
    val viewMode: CalendarViewMode = CalendarViewMode.MONTH,
    val mainTab: MainNavTab = MainNavTab.CALENDAR,
    val isOnboardingVisible: Boolean = false,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val selectedCategory: String? = null,
    val isCreateDialogOpen: Boolean = false,
    val isSettingsOpen: Boolean = false,
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val activeAccent: String = "Sage",
    val notificationsEnabled: Boolean = true,
    val defaultReminderMinutes: Int = 15,
    val snackbarMessage: String? = null,
    val editingEvent: CalendarEvent? = null,
    val detailEvent: CalendarEvent? = null,
    val allEvents: List<CalendarEvent> = emptyList()
) {
    val eventsForSelectedDate: List<CalendarEvent>
        get() = allEvents.filter { it.year == selectedDate.year && it.month == selectedDate.month && it.day == selectedDate.day }
            .sortedBy { it.startHour * 60 + it.startMinute }

    val eventsForDisplayedMonth: List<CalendarEvent>
        get() = allEvents.filter { it.year == displayedYear && it.month == displayedMonth }

    val eventsForToday: List<CalendarEvent>
        get() {
            val today = SimpleDate.today()
            return allEvents.filter { it.year == today.year && it.month == today.month && it.day == today.day }
        }

    val todayCompletedCount: Int
        get() = eventsForToday.count { it.isCompleted }

    val todayTotalCount: Int
        get() = eventsForToday.size

    val filteredScheduleEvents: List<CalendarEvent>
        get() {
            return allEvents.filter { event ->
                val matchesQuery = if (searchQuery.isBlank()) true else {
                    event.title.contains(searchQuery, ignoreCase = true) ||
                            event.description.contains(searchQuery, ignoreCase = true) ||
                            event.location.contains(searchQuery, ignoreCase = true) ||
                            event.category.contains(searchQuery, ignoreCase = true)
                }
                val matchesCategory = if (selectedCategory == null) true else {
                    event.category.equals(selectedCategory, ignoreCase = true)
                }
                matchesQuery && matchesCategory
            }.sortedWith(
                compareBy<CalendarEvent> { it.year }
                    .thenBy { it.month }
                    .thenBy { it.day }
                    .thenBy { it.startHour }
                    .thenBy { it.startMinute }
            )
        }

    fun getEventsCountForDate(date: SimpleDate): Int {
        return allEvents.count { it.year == date.year && it.month == date.month && it.day == date.day }
    }

    fun getEventsForDate(date: SimpleDate): List<CalendarEvent> {
        return allEvents.filter { it.year == date.year && it.month == date.month && it.day == date.day }
            .sortedBy { it.startHour * 60 + it.startMinute }
    }
}

class CalendarViewModel(private val app: Application) : AndroidViewModel(app) {

    private val repository: CalendarRepository
    private val themePreferences = ThemePreferences(app)

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        val database = CalendarDatabase.getDatabase(app, viewModelScope)
        repository = CalendarRepository(database.calendarDao())

        // Load persisted theme & notification preferences
        val savedMode = themePreferences.themeMode
        val savedAccent = themePreferences.accentPalette
        val savedNotifs = themePreferences.notificationsEnabled
        val savedReminderMins = themePreferences.defaultReminderMinutes
        ClayColors.activeAccentName = savedAccent
        val shouldShowTour = !themePreferences.hasCompletedTour
        _uiState.update {
            it.copy(
                themeMode = savedMode,
                activeAccent = savedAccent,
                notificationsEnabled = savedNotifs,
                defaultReminderMinutes = savedReminderMins,
                isOnboardingVisible = shouldShowTour
            )
        }

        // Initialize Notification Channel
        EventNotificationScheduler.createNotificationChannel(app)

        // Collect all events from database
        viewModelScope.launch {
            if (!themePreferences.defaultEventsCleaned) {
                repository.removeDefaultSampleEvents()
                themePreferences.defaultEventsCleaned = true
            }
            repository.allEvents.collect { events ->
                _uiState.update { it.copy(allEvents = events) }
                // Schedule notifications for active upcoming events if enabled
                if (_uiState.value.notificationsEnabled) {
                    val now = System.currentTimeMillis()
                    events.forEach { event ->
                        if (!event.isCompleted && event.reminderMinutesBefore >= 0) {
                            val trigger = EventNotificationScheduler.calculateTriggerMillis(event)
                            if (trigger > now) {
                                EventNotificationScheduler.scheduleNotification(app, event)
                            }
                        }
                    }
                }
            }
        }
    }

    fun selectDate(date: SimpleDate) {
        _uiState.update {
            it.copy(
                selectedDate = date,
                displayedYear = date.year,
                displayedMonth = date.month
            )
        }
    }

    fun previousMonth() {
        _uiState.update {
            val newMonth = if (it.displayedMonth == 1) 12 else it.displayedMonth - 1
            val newYear = if (it.displayedMonth == 1) it.displayedYear - 1 else it.displayedYear
            it.copy(
                displayedYear = newYear,
                displayedMonth = newMonth
            )
        }
    }

    fun nextMonth() {
        _uiState.update {
            val newMonth = if (it.displayedMonth == 12) 1 else it.displayedMonth + 1
            val newYear = if (it.displayedMonth == 12) it.displayedYear + 1 else it.displayedYear
            it.copy(
                displayedYear = newYear,
                displayedMonth = newMonth
            )
        }
    }

    fun jumpToToday() {
        val today = SimpleDate.today()
        _uiState.update {
            it.copy(
                selectedDate = today,
                displayedYear = today.year,
                displayedMonth = today.month
            )
        }
    }

    fun setViewMode(mode: CalendarViewMode) {
        _uiState.update { it.copy(viewMode = mode) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleSearch(active: Boolean) {
        _uiState.update {
            it.copy(
                isSearchActive = active,
                searchQuery = if (!active) "" else it.searchQuery
            )
        }
    }

    fun setSelectedCategory(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun setMainTab(tab: MainNavTab) {
        _uiState.update { it.copy(mainTab = tab) }
    }

    fun showOnboarding() {
        _uiState.update { it.copy(isOnboardingVisible = true) }
    }

    fun closeOnboarding() {
        themePreferences.hasCompletedTour = true
        _uiState.update { it.copy(isOnboardingVisible = false) }
    }

    fun openCreateDialog(prefillDate: SimpleDate? = null) {
        if (prefillDate != null) {
            _uiState.update {
                it.copy(
                    selectedDate = prefillDate,
                    isCreateDialogOpen = true,
                    editingEvent = null
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    isCreateDialogOpen = true,
                    editingEvent = null
                )
            }
        }
    }

    fun openEditDialog(event: CalendarEvent) {
        _uiState.update {
            it.copy(
                isCreateDialogOpen = true,
                editingEvent = event,
                detailEvent = null
            )
        }
    }

    fun openDetailDialog(event: CalendarEvent) {
        _uiState.update { it.copy(detailEvent = event) }
    }

    fun closeDialogs() {
        _uiState.update {
            it.copy(
                isCreateDialogOpen = false,
                editingEvent = null,
                detailEvent = null
            )
        }
    }

    fun saveEvent(
        title: String,
        description: String,
        category: String,
        date: SimpleDate,
        startTime: SimpleTime,
        endTime: SimpleTime,
        location: String,
        priority: String,
        reminderMinutesBefore: Int = 15,
        existingId: Long = 0
    ) {
        viewModelScope.launch {
            val cat = Category.fromName(category)
            val event = CalendarEvent(
                id = existingId,
                title = title.trim(),
                description = description.trim(),
                category = category,
                year = date.year,
                month = date.month,
                day = date.day,
                startHour = startTime.hour,
                startMinute = startTime.minute,
                endHour = endTime.hour,
                endMinute = endTime.minute,
                colorHex = cat.hexColor,
                location = location.trim(),
                priority = priority,
                reminderMinutesBefore = reminderMinutesBefore,
                isCompleted = _uiState.value.editingEvent?.isCompleted ?: false
            )

            val finalId = if (existingId > 0) {
                repository.update(event)
                existingId
            } else {
                repository.insert(event)
            }

            val savedEvent = event.copy(id = finalId)
            if (_uiState.value.notificationsEnabled && reminderMinutesBefore >= 0 && !savedEvent.isCompleted) {
                EventNotificationScheduler.scheduleNotification(app, savedEvent)
            } else {
                EventNotificationScheduler.cancelNotification(app, finalId)
            }

            closeDialogs()
        }
    }

    fun deleteEvent(id: Long) {
        viewModelScope.launch {
            EventNotificationScheduler.cancelNotification(app, id)
            repository.deleteById(id)
            if (_uiState.value.detailEvent?.id == id) {
                _uiState.update { it.copy(detailEvent = null) }
            }
        }
    }

    fun toggleCompleted(id: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleCompleted(id, isCompleted)
            if (isCompleted) {
                EventNotificationScheduler.cancelNotification(app, id)
            } else if (_uiState.value.notificationsEnabled) {
                val event = _uiState.value.allEvents.find { it.id == id }
                if (event != null && event.reminderMinutesBefore >= 0) {
                    EventNotificationScheduler.scheduleNotification(app, event.copy(isCompleted = false))
                }
            }
            // Update detail if open
            _uiState.value.detailEvent?.let { current ->
                if (current.id == id) {
                    _uiState.update { it.copy(detailEvent = current.copy(isCompleted = isCompleted)) }
                }
            }
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        themePreferences.notificationsEnabled = enabled
        _uiState.update { it.copy(notificationsEnabled = enabled) }
        viewModelScope.launch {
            val all = repository.getAllList()
            if (enabled) {
                val now = System.currentTimeMillis()
                all.forEach { ev ->
                    if (!ev.isCompleted && ev.reminderMinutesBefore >= 0) {
                        val trigger = EventNotificationScheduler.calculateTriggerMillis(ev)
                        if (trigger > now) {
                            EventNotificationScheduler.scheduleNotification(app, ev)
                        }
                    }
                }
                showSnackbar("Event notifications enabled")
            } else {
                all.forEach { ev ->
                    EventNotificationScheduler.cancelNotification(app, ev.id)
                }
                showSnackbar("Event notifications disabled")
            }
        }
    }

    fun setDefaultReminderMinutes(minutes: Int) {
        themePreferences.defaultReminderMinutes = minutes
        _uiState.update { it.copy(defaultReminderMinutes = minutes) }
    }

    fun sendTestNotification() {
        EventNotificationScheduler.sendTestNotification(app)
        showSnackbar("Dispatched test notification to status bar!")
    }

    fun openEventById(eventId: Long) {
        viewModelScope.launch {
            val all = repository.getAllList()
            val target = all.find { it.id == eventId }
            if (target != null) {
                _uiState.update {
                    it.copy(
                        detailEvent = target,
                        selectedDate = target.date,
                        displayedYear = target.year,
                        displayedMonth = target.month,
                        mainTab = MainNavTab.CALENDAR
                    )
                }
            }
        }
    }

    fun openSettings() {
        _uiState.update { it.copy(isSettingsOpen = true) }
    }

    fun closeSettings() {
        _uiState.update { it.copy(isSettingsOpen = false) }
    }

    fun setThemeMode(mode: AppThemeMode) {
        themePreferences.themeMode = mode
        _uiState.update { it.copy(themeMode = mode) }
    }

    fun setAccentPalette(name: String) {
        themePreferences.accentPalette = name
        ClayColors.activeAccentName = name
        _uiState.update { it.copy(activeAccent = name) }
    }

    fun exportEventsJson(): String {
        return CalendarDataTransfer.exportToJson(_uiState.value.allEvents)
    }

    fun importEvents(jsonString: String, replaceExisting: Boolean): Result<Int> {
        val parseResult = CalendarDataTransfer.importFromJson(jsonString)
        return parseResult.map { events ->
            viewModelScope.launch {
                if (replaceExisting) {
                    _uiState.value.allEvents.forEach {
                        EventNotificationScheduler.cancelNotification(app, it.id)
                    }
                    repository.deleteAll()
                }
                repository.insertAll(events)
                if (_uiState.value.notificationsEnabled) {
                    val now = System.currentTimeMillis()
                    events.forEach { ev ->
                        if (!ev.isCompleted && ev.reminderMinutesBefore >= 0) {
                            val trigger = EventNotificationScheduler.calculateTriggerMillis(ev)
                            if (trigger > now) {
                                EventNotificationScheduler.scheduleNotification(app, ev)
                            }
                        }
                    }
                }
                showSnackbar("Successfully imported ${events.size} events")
            }
            events.size
        }
    }

    fun deleteAllData() {
        viewModelScope.launch {
            _uiState.value.allEvents.forEach {
                EventNotificationScheduler.cancelNotification(app, it.id)
            }
            repository.deleteAll()
            _uiState.update {
                it.copy(
                    allEvents = emptyList(),
                    detailEvent = null,
                    editingEvent = null
                )
            }
            showSnackbar("All calendar data has been removed")
        }
    }

    fun showSnackbar(message: String) {
        _uiState.update { it.copy(snackbarMessage = message) }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
