package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.TaskCategory
import com.example.ui.FilterTab
import com.example.ui.TaskViewModel
import com.example.ui.components.AddEditTaskSheet
import com.example.ui.components.DailyProgressCard
import com.example.ui.components.RoutineTemplateSheet
import com.example.ui.components.TaskCard
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

  private val viewModel: TaskViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        RozanaApp(viewModel = viewModel)
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RozanaApp(viewModel: TaskViewModel) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  var showClearCompletedDialog by remember { mutableStateOf(false) }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    contentWindowInsets = WindowInsets.safeDrawing,
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.TaskAlt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Rozana • रोज़ाना",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "दैनिक टास्क व आदतें",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        },
        actions = {
          IconButton(
            onClick = { viewModel.toggleSearchOpen() },
            modifier = Modifier.testTag("search_toggle_button")
          ) {
            Icon(
              imageVector = if (uiState.isSearchOpen) Icons.Default.Clear else Icons.Default.Search,
              contentDescription = "Search tasks"
            )
          }

          if (uiState.selectedTab == FilterTab.COMPLETED && uiState.tasks.isNotEmpty()) {
            IconButton(
              onClick = { showClearCompletedDialog = true },
              modifier = Modifier.testTag("clear_completed_button")
            ) {
              Icon(
                imageVector = Icons.Default.DeleteSweep,
                contentDescription = "Clear completed",
                tint = MaterialTheme.colorScheme.error
              )
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = { viewModel.openAddSheet() },
        modifier = Modifier
          .testTag("add_task_fab")
          .windowInsetsPadding(WindowInsets.navigationBars),
        icon = { Icon(Icons.Default.Add, contentDescription = null) },
        text = {
          Text(
            text = "नया टास्क • Add Task",
            fontWeight = FontWeight.Bold
          )
        },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // Search Bar expandable
      AnimatedVisibility(visible = uiState.isSearchOpen) {
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
          OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            placeholder = { Text("टास्क खोजें... Search tasks") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
              if (uiState.searchQuery.isNotBlank()) {
                IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                  Icon(Icons.Default.Clear, contentDescription = "Clear search")
                }
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("search_input"),
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
          )
        }
      }

      // Filter Tabs
      ScrollableTabRow(
        selectedTabIndex = uiState.selectedTab.ordinal,
        edgePadding = 16.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        indicator = { tabPositions ->
          if (uiState.selectedTab.ordinal < tabPositions.size) {
            TabRowDefaults.SecondaryIndicator(
              modifier = Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab.ordinal]),
              color = MaterialTheme.colorScheme.primary
            )
          }
        }
      ) {
        FilterTab.entries.forEach { tab ->
          val selected = uiState.selectedTab == tab
          Tab(
            selected = selected,
            onClick = { viewModel.selectTab(tab) },
            text = {
              Text(
                text = "${tab.titleHi} (${tab.titleEn})",
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 13.sp
              )
            },
            selectedContentColor = MaterialTheme.colorScheme.primary,
            unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // Category filter chips
      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        item {
          FilterChip(
            selected = uiState.selectedCategory == null,
            onClick = { viewModel.selectCategory(null) },
            label = { Text("सभी • All") },
            shape = RoundedCornerShape(12.dp)
          )
        }
        items(TaskCategory.entries) { category ->
          FilterChip(
            selected = uiState.selectedCategory == category,
            onClick = { viewModel.selectCategory(category) },
            label = { Text(category.displayNameHi) },
            shape = RoundedCornerShape(12.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
          )
        }
      }

      // Task List
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
      ) {
        // Daily Progress Card shown on "TODAY" tab
        if (uiState.selectedTab == FilterTab.TODAY && uiState.searchQuery.isBlank()) {
          item(key = "progress_card") {
            DailyProgressCard(
              totalCount = uiState.totalTodayCount,
              completedCount = uiState.completedTodayCount,
              progressPercent = uiState.progressPercent,
              bestStreak = uiState.bestHabitStreak,
              onOpenTemplates = { viewModel.openTemplateSheet() }
            )
            Spacer(modifier = Modifier.height(6.dp))
          }
        }

        if (uiState.tasks.isEmpty()) {
          item(key = "empty_state") {
            EmptyTasksView(
              selectedTab = uiState.selectedTab,
              hasSearch = uiState.searchQuery.isNotBlank(),
              onAddTask = { viewModel.openAddSheet() },
              onOpenTemplates = { viewModel.openTemplateSheet() }
            )
          }
        } else {
          items(
            items = uiState.tasks,
            key = { it.id }
          ) { task ->
            TaskCard(
              task = task,
              onToggleComplete = { viewModel.toggleTaskCompletion(task) },
              onToggleSubtask = { subtaskId -> viewModel.toggleSubtask(task, subtaskId) },
              onEdit = { viewModel.openAddSheet(task) },
              onDelete = { viewModel.deleteTask(task) }
            )
          }
        }
      }
    }
  }

  // Add / Edit Task Modal Sheet
  if (uiState.isAddSheetOpen) {
    AddEditTaskSheet(
      initialTask = uiState.editingTask,
      onDismiss = { viewModel.closeAddSheet() },
      onSave = { title, desc, cat, prio, date, slot, habit, subtasks ->
        viewModel.saveTask(
          id = uiState.editingTask?.id ?: 0L,
          title = title,
          description = desc,
          category = cat,
          priority = prio,
          dueDateMillis = date,
          timeSlot = slot,
          isHabit = habit,
          subtasks = subtasks
        )
      }
    )
  }

  // Routine Template Sheet
  if (uiState.isTemplateSheetOpen) {
    RoutineTemplateSheet(
      onDismiss = { viewModel.closeTemplateSheet() },
      onApplyTemplate = { templateName ->
        viewModel.applyRoutineTemplate(templateName)
      }
    )
  }

  // Clear completed tasks confirmation dialog
  if (showClearCompletedDialog) {
    AlertDialog(
      onDismissRequest = { showClearCompletedDialog = false },
      title = { Text("पूर्ण कार्य हटाएं? (Clear Completed)") },
      text = { Text("क्या आप सभी पूर्ण किए गए सामान्य टास्कों को सूची से हटाना चाहते हैं?") },
      confirmButton = {
        TextButton(
          onClick = {
            viewModel.deleteCompletedTasks()
            showClearCompletedDialog = false
          }
        ) {
          Text("हटाएं • Clear", color = MaterialTheme.colorScheme.error)
        }
      },
      dismissButton = {
        TextButton(onClick = { showClearCompletedDialog = false }) {
          Text("रद्द करें • Cancel")
        }
      }
    )
  }
}

@Composable
fun EmptyTasksView(
  selectedTab: FilterTab,
  hasSearch: Boolean,
  onAddTask: () -> Unit,
  onOpenTemplates: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 40.dp, horizontal = 24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(
      modifier = Modifier
        .size(80.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primaryContainer),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = if (selectedTab == FilterTab.COMPLETED) Icons.Default.CheckCircle else Icons.Default.TaskAlt,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier.size(44.dp)
      )
    }

    Spacer(modifier = Modifier.height(18.dp))

    val title = when {
      hasSearch -> "कोई टास्क नहीं मिला"
      selectedTab == FilterTab.COMPLETED -> "अभी कोई कार्य पूर्ण नहीं हुआ है"
      selectedTab == FilterTab.HABITS -> "कोई दैनिक आदत नहीं बनाई गई"
      selectedTab == FilterTab.UPCOMING -> "कोई आगामी कार्य निर्धारित नहीं है"
      else -> "आज की कार्य सूची खाली है!"
    }

    val subtitle = when {
      hasSearch -> "कृपया अलग शब्द या श्रेणी से खोजें"
      selectedTab == FilterTab.COMPLETED -> "टास्क पूरा होने पर वह यहाँ दिखाई देगा"
      selectedTab == FilterTab.HABITS -> "कसरत, ध्यान या किताब पढ़ने जैसी आदतें जोड़ें"
      else -> "अपने दिन को व्यवस्थित करने के लिए नया टास्क या रुटीन जोड़ें।"
    }

    Text(
      text = title,
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onSurface,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = subtitle,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center,
      lineHeight = 20.sp
    )

    if (!hasSearch) {
      Spacer(modifier = Modifier.height(20.dp))
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        TextButton(
          onClick = onOpenTemplates,
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("रुटीन टेम्पलेट देखें")
        }

        TextButton(
          onClick = onAddTask,
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("टास्क जोड़ें (+)")
        }
      }
    }
  }
}
