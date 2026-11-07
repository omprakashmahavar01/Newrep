package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.TaskDatabase
import com.example.data.model.SubTask
import com.example.data.model.TaskCategory
import com.example.data.model.TaskEntity
import com.example.data.model.TaskPriority
import com.example.data.model.UserProfile
import com.example.data.repository.TaskRepository
import com.example.data.repository.UserPreferencesRepository
import com.example.util.SpeechManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class FilterTab(val titleHi: String, val titleEn: String) {
  TODAY("आज", "Today"),
  UPCOMING("आगामी", "Upcoming"),
  HABITS("दैनिक आदतें", "Habits"),
  COMPLETED("पूर्ण", "Done"),
  ALL("सभी", "All")
}

data class TaskUiState(
  val tasks: List<TaskEntity> = emptyList(),
  val selectedTab: FilterTab = FilterTab.TODAY,
  val selectedCategory: TaskCategory? = null,
  val searchQuery: String = "",
  val totalTodayCount: Int = 0,
  val completedTodayCount: Int = 0,
  val progressPercent: Int = 0,
  val bestHabitStreak: Int = 0,
  val isAddSheetOpen: Boolean = false,
  val isTemplateSheetOpen: Boolean = false,
  val isProfileSheetOpen: Boolean = false,
  val isMobileAuthOpen: Boolean = false,
  val userProfile: UserProfile = UserProfile(),
  val editingTask: TaskEntity? = null,
  val isSearchOpen: Boolean = false
)

private data class InternalFilterState(
  val tab: FilterTab = FilterTab.TODAY,
  val category: TaskCategory? = null,
  val searchQuery: String = "",
  val isAddSheetOpen: Boolean = false,
  val isTemplateSheetOpen: Boolean = false,
  val isProfileSheetOpen: Boolean = false,
  val isMobileAuthOpen: Boolean = false,
  val editingTask: TaskEntity? = null,
  val isSearchOpen: Boolean = false
)

class TaskViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: TaskRepository
  private val userPreferencesRepository = UserPreferencesRepository(application)
  private val speechManager = SpeechManager(application)
  private val _filterState = MutableStateFlow(InternalFilterState())

  init {
    val database = TaskDatabase.getDatabase(application, viewModelScope)
    repository = TaskRepository(database.taskDao())
  }

  val uiState: StateFlow<TaskUiState> = combine(
    repository.allTasks,
    _filterState,
    userPreferencesRepository.userProfile
  ) { allTasks: List<TaskEntity>, filter: InternalFilterState, profile: UserProfile ->

    val todayCalendar = Calendar.getInstance().apply {
      set(Calendar.HOUR_OF_DAY, 0)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }
    val startOfToday = todayCalendar.timeInMillis
    val endOfToday = startOfToday + (24 * 60 * 60 * 1000) - 1

    // Today's tasks (due today OR habits)
    val todayTasks = allTasks.filter {
      it.isHabit || (it.dueDateMillis in startOfToday..endOfToday)
    }
    val totalToday = todayTasks.size
    val completedToday = todayTasks.count { it.isCompleted }
    val progress = if (totalToday > 0) ((completedToday.toFloat() / totalToday) * 100).toInt() else 0

    val maxStreak = allTasks.filter { it.isHabit }.map { it.streakDays }.maxOrNull() ?: 0

    // Filter by Tab
    val tabFiltered = when (filter.tab) {
      FilterTab.TODAY -> todayTasks
      FilterTab.UPCOMING -> allTasks.filter { !it.isCompleted && it.dueDateMillis > endOfToday }
      FilterTab.HABITS -> allTasks.filter { it.isHabit }
      FilterTab.COMPLETED -> allTasks.filter { it.isCompleted }
      FilterTab.ALL -> allTasks
    }

    // Filter by Category
    val categoryFiltered = if (filter.category != null) {
      tabFiltered.filter { it.category == filter.category.name }
    } else {
      tabFiltered
    }

    // Filter by Search Query
    val finalFiltered = if (filter.searchQuery.isNotBlank()) {
      categoryFiltered.filter {
        it.title.contains(filter.searchQuery, ignoreCase = true) ||
          it.description.contains(filter.searchQuery, ignoreCase = true) ||
          it.timeSlot.contains(filter.searchQuery, ignoreCase = true)
      }
    } else {
      categoryFiltered
    }

    TaskUiState(
      tasks = finalFiltered,
      selectedTab = filter.tab,
      selectedCategory = filter.category,
      searchQuery = filter.searchQuery,
      totalTodayCount = totalToday,
      completedTodayCount = completedToday,
      progressPercent = progress,
      bestHabitStreak = maxStreak,
      isAddSheetOpen = filter.isAddSheetOpen,
      isTemplateSheetOpen = filter.isTemplateSheetOpen,
      isProfileSheetOpen = filter.isProfileSheetOpen,
      isMobileAuthOpen = filter.isMobileAuthOpen,
      userProfile = profile,
      editingTask = filter.editingTask,
      isSearchOpen = filter.isSearchOpen
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = TaskUiState()
  )

  fun selectTab(tab: FilterTab) {
    _filterState.value = _filterState.value.copy(tab = tab)
  }

  fun selectCategory(category: TaskCategory?) {
    val current = _filterState.value.category
    _filterState.value = _filterState.value.copy(
      category = if (current == category) null else category
    )
  }

  fun updateSearchQuery(query: String) {
    _filterState.value = _filterState.value.copy(searchQuery = query)
  }

  fun toggleSearchOpen() {
    val current = _filterState.value.isSearchOpen
    _filterState.value = _filterState.value.copy(
      isSearchOpen = !current,
      searchQuery = if (current) "" else _filterState.value.searchQuery
    )
  }

  fun openAddSheet(taskToEdit: TaskEntity? = null) {
    _filterState.value = _filterState.value.copy(
      isAddSheetOpen = true,
      editingTask = taskToEdit
    )
  }

  fun closeAddSheet() {
    _filterState.value = _filterState.value.copy(
      isAddSheetOpen = false,
      editingTask = null
    )
  }

  fun openTemplateSheet() {
    _filterState.value = _filterState.value.copy(isTemplateSheetOpen = true)
  }

  fun closeTemplateSheet() {
    _filterState.value = _filterState.value.copy(isTemplateSheetOpen = false)
  }

  fun openProfileSheet() {
    _filterState.value = _filterState.value.copy(isProfileSheetOpen = true)
  }

  fun closeProfileSheet() {
    _filterState.value = _filterState.value.copy(isProfileSheetOpen = false)
  }

  fun openMobileAuth() {
    _filterState.value = _filterState.value.copy(isMobileAuthOpen = true)
  }

  fun closeMobileAuth() {
    _filterState.value = _filterState.value.copy(isMobileAuthOpen = false)
  }

  fun verifyPhoneNumber(phoneNumber: String, userName: String) {
    userPreferencesRepository.verifyPhoneNumber(phoneNumber, userName)
    closeMobileAuth()
    speechManager.speak("मोबाइल नंबर सही है! आपका स्वागत है ${userName.ifBlank { "साथी" }}!")
  }

  fun updateUserProfile(newProfile: UserProfile) {
    userPreferencesRepository.updateProfile(newProfile)
    closeProfileSheet()
    speechManager.speak("प्रोफाइल सफलतापूर्वक अपडेट हो गई!")
  }

  fun speak(text: String) {
    speechManager.speak(text)
  }

  fun speakTodaySummary() {
    val state = uiState.value
    val profile = state.userProfile
    val remaining = maxOf(0, state.totalTodayCount - state.completedTodayCount)
    val text = "नमस्ते ${profile.userName}! आज आपके कुल ${state.totalTodayCount} कार्यों में से ${state.completedTodayCount} कार्य पूरे हो चुके हैं, और $remaining कार्य बाकी हैं। लक्ष्य की ओर बढ़ते रहें!"
    speechManager.speak(text)
  }

  fun speakTask(task: TaskEntity) {
    val status = if (task.isCompleted) "यह कार्य सही और पूर्ण हो चुका है।" else "यह कार्य अभी पेंडिंग है।"
    val text = "टास्क: ${task.title}। समय: ${task.timeSlot}। स्थिति: $status"
    speechManager.speak(text)
  }

  fun toggleTaskCompletion(task: TaskEntity) {
    viewModelScope.launch {
      val isNowCompleted = !task.isCompleted
      repository.toggleTaskCompletion(task)
      if (isNowCompleted) {
        speechManager.speak("शाबाश! कार्य सही और पूरा हो गया: ${task.title}")
      } else {
        speechManager.speak("${task.title} पेंडिंग सूची में है।")
      }
    }
  }

  fun toggleSubtask(task: TaskEntity, subtaskId: String) {
    viewModelScope.launch {
      repository.toggleSubtask(task, subtaskId)
    }
  }

  fun saveTask(
    id: Long = 0,
    title: String,
    description: String,
    category: TaskCategory,
    priority: TaskPriority,
    dueDateMillis: Long,
    timeSlot: String,
    isHabit: Boolean,
    subtasks: List<SubTask>
  ) {
    if (title.isBlank()) return
    viewModelScope.launch {
      val task = TaskEntity(
        id = id,
        title = title.trim(),
        description = description.trim(),
        category = category.name,
        priority = priority.name,
        dueDate = dueDateMillis,
        dueDateMillis = dueDateMillis,
        timeSlot = timeSlot.ifBlank { "09:00 AM • सुबह" },
        isHabit = isHabit,
        subtasksRaw = SubTask.listToString(subtasks),
        isCompleted = false
      )
      if (id == 0L) {
        repository.insertTask(task)
        speechManager.speak("नया टास्क सही से जुड़ गया: ${task.title}")
      } else {
        val existing = _filterState.value.editingTask
        val updated = task.copy(
          isCompleted = existing?.isCompleted ?: false,
          streakDays = existing?.streakDays ?: 0,
          completedAtMillis = existing?.completedAtMillis
        )
        repository.updateTask(updated)
        speechManager.speak("टास्क अपडेट हो गया!")
      }
      closeAddSheet()
    }
  }

  fun deleteTask(task: TaskEntity) {
    viewModelScope.launch {
      repository.deleteTask(task)
      speechManager.speak("टास्क हटा दिया गया।")
    }
  }

  fun deleteCompletedTasks() {
    viewModelScope.launch {
      repository.deleteCompletedTasks()
      speechManager.speak("सभी पूर्ण कार्य सूची से हटा दिए गए।")
    }
  }

  fun applyRoutineTemplate(templateName: String) {
    viewModelScope.launch {
      repository.applyRoutineTemplate(templateName)
      closeTemplateSheet()
      speechManager.speak("रूटीन टेम्पलेट सफलता से लागू हो गया!")
    }
  }

  fun getExportReport(): String {
    val state = uiState.value
    val profile = state.userProfile
    val sb = StringBuilder()
    sb.appendLine("📋 Rozana Tasks - दैनिक कार्य रिपोर्ट व बैकअप")
    sb.appendLine("👤 उपयोगकर्ता: ${profile.userName} (${profile.avatarEmoji})")
    if (profile.phoneNumber.isNotBlank()) {
      sb.appendLine("📱 मोबाइल: +91 ${profile.phoneNumber}")
    }
    sb.appendLine("🎯 दैनिक लक्ष्य: ${profile.dailyTargetTasks} कार्य")
    if (profile.customMotto.isNotBlank()) {
      sb.appendLine("💡 विचार: ${profile.customMotto}")
    }
    sb.appendLine("📊 आज की प्रगति: ${state.completedTodayCount}/${state.totalTodayCount} पूर्ण (${state.progressPercent}%)")
    if (state.bestHabitStreak > 0) {
      sb.appendLine("🔥 सक्रिय स्ट्रीक: ${state.bestHabitStreak} दिन")
    }
    sb.appendLine("----------------------------------------")
    sb.appendLine("कार्य सूची (Tasks List):")
    if (state.tasks.isEmpty()) {
      sb.appendLine("(कोई टास्क नहीं है)")
    } else {
      state.tasks.forEachIndexed { index, task ->
        val status = if (task.isCompleted) "[✓ पूर्ण]" else "[  बाकी ]"
        sb.appendLine("${index + 1}. $status ${task.title}")
        if (task.timeSlot.isNotBlank()) sb.appendLine("   समय: ${task.timeSlot}")
        if (task.description.isNotBlank()) sb.appendLine("   नोट्स: ${task.description}")
      }
    }
    sb.appendLine("----------------------------------------")
    sb.appendLine("Rozana Tasks App द्वारा सफलतापूर्वक निर्यातित")
    return sb.toString()
  }

  override fun onCleared() {
    super.onCleared()
    speechManager.shutdown()
  }
}
