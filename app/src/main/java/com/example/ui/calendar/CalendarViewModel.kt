package com.example.ui.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.CalendarDatabase
import com.example.data.CalendarRepository
import com.example.model.CalendarEvent
import com.example.model.Category
import com.example.model.SimpleDate
import com.example.model.SimpleTime
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
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val selectedCategory: String? = null,
    val isCreateDialogOpen: Boolean = false,
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

class CalendarViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CalendarRepository

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        val database = CalendarDatabase.getDatabase(application, viewModelScope)
        repository = CalendarRepository(database.calendarDao())

        // Collect all events from database
        viewModelScope.launch {
            repository.ensureSampleData()
            repository.allEvents.collect { events ->
                _uiState.update { it.copy(allEvents = events) }
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
                isCompleted = _uiState.value.editingEvent?.isCompleted ?: false
            )

            if (existingId > 0) {
                repository.update(event)
            } else {
                repository.insert(event)
            }

            closeDialogs()
        }
    }

    fun deleteEvent(id: Long) {
        viewModelScope.launch {
            repository.deleteById(id)
            if (_uiState.value.detailEvent?.id == id) {
                _uiState.update { it.copy(detailEvent = null) }
            }
        }
    }

    fun toggleCompleted(id: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleCompleted(id, isCompleted)
            // Update detail if open
            _uiState.value.detailEvent?.let { current ->
                if (current.id == id) {
                    _uiState.update { it.copy(detailEvent = current.copy(isCompleted = isCompleted)) }
                }
            }
        }
    }
}
