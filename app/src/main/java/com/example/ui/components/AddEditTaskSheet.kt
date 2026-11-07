package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SubTask
import com.example.data.model.TaskCategory
import com.example.data.model.TaskEntity
import com.example.data.model.TaskPriority
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditTaskSheet(
  initialTask: TaskEntity?,
  onDismiss: () -> Unit,
  onSave: (
    title: String,
    description: String,
    category: TaskCategory,
    priority: TaskPriority,
    dueDateMillis: Long,
    timeSlot: String,
    isHabit: Boolean,
    subtasks: List<SubTask>
  ) -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var title by remember { mutableStateOf(initialTask?.title ?: "") }
  var description by remember { mutableStateOf(initialTask?.description ?: "") }
  var selectedCategory by remember {
    mutableStateOf(
      initialTask?.category?.let { catName ->
        TaskCategory.entries.find { it.name == catName }
      } ?: TaskCategory.ROUTINE
    )
  }
  var selectedPriority by remember {
    mutableStateOf(
      initialTask?.priority?.let { pName ->
        TaskPriority.entries.find { it.name == pName }
      } ?: TaskPriority.MEDIUM
    )
  }
  var selectedTimeSlot by remember {
    mutableStateOf(initialTask?.timeSlot ?: "09:00 AM • सुबह")
  }
  var isHabit by remember { mutableStateOf(initialTask?.isHabit ?: false) }

  val subtasks = remember {
    mutableStateListOf<SubTask>().apply {
      if (initialTask != null) {
        addAll(SubTask.stringToList(initialTask.subtasksRaw))
      }
    }
  }
  var newSubtaskText by remember { mutableStateOf("") }

  val suggestions = listOf(
    "सुबह की सैर (Morning Walk)",
    "दैनिक रिव्यू (Daily Review)",
    "8 गिलास पानी पीना",
    "किताब के 10 पेज पढ़ना",
    "किराने का सामान लाना",
    "महत्वपूर्ण कॉल्स निपटाना"
  )

  val timeSlots = listOf(
    "06:30 AM • सुबह",
    "09:00 AM • सुबह",
    "11:30 AM • दोपहर",
    "02:00 PM • दोपहर",
    "05:30 PM • शाम",
    "08:30 PM • रात",
    "पूरे दिन • Any time"
  )

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp)
        .padding(bottom = 32.dp)
        .imePadding()
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (initialTask == null) "नया टास्क जोड़ें • Add Task" else "टास्क बदलें • Edit Task",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close")
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Title Input
      OutlinedTextField(
        value = title,
        onValueChange = { title = it },
        label = { Text("टास्क का शीर्षक • Title *") },
        placeholder = { Text("उदा. 30 मिनट कसरत, रिपोर्ट पूरी करें") },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("task_title_input"),
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
      )

      // Quick Suggestions
      if (title.isBlank()) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = "सुझाव (Quick ideas):",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          suggestions.forEach { suggestion ->
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier.clickable { title = suggestion }
            ) {
              Text(
                text = suggestion,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Description Input
      OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        label = { Text("विवरण या नोट्स • Notes (वैकल्पिक)") },
        placeholder = { Text("ज़रूरी बिंदु, लिंक या विवरण") },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("task_description_input"),
        maxLines = 3,
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Category Selection
      Text(
        text = "श्रेणी चुनें • Category:",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(8.dp))
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        TaskCategory.entries.forEach { category ->
          FilterChip(
            selected = selectedCategory == category,
            onClick = { selectedCategory = category },
            label = { Text("${category.displayNameHi} (${category.displayNameEn})") },
            shape = RoundedCornerShape(10.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Priority Selection
      Text(
        text = "प्राथमिकता • Priority:",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        TaskPriority.entries.forEach { priority ->
          val isSelected = selectedPriority == priority
          FilterChip(
            selected = isSelected,
            onClick = { selectedPriority = priority },
            label = { Text(priority.displayNameHi) },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = when (priority) {
                TaskPriority.HIGH -> Color(0xFFFEE2E2)
                TaskPriority.MEDIUM -> Color(0xFFFEF3C7)
                TaskPriority.LOW -> Color(0xFFD1FAE5)
              },
              selectedLabelColor = when (priority) {
                TaskPriority.HIGH -> Color(0xFF991B1B)
                TaskPriority.MEDIUM -> Color(0xFF92400E)
                TaskPriority.LOW -> Color(0xFF065F46)
              }
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Time Slot Selection
      Text(
        text = "समय • Time Slot:",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(8.dp))
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        timeSlots.forEach { slot ->
          FilterChip(
            selected = selectedTimeSlot == slot,
            onClick = { selectedTimeSlot = slot },
            label = { Text(slot) },
            shape = RoundedCornerShape(10.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Habit Toggle
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { isHabit = !isHabit }
          .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.LocalFireDepartment,
              contentDescription = null,
              tint = Color(0xFFD97706),
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "दैनिक आदत बनाएं (Daily Habit)",
              fontWeight = FontWeight.SemiBold,
              style = MaterialTheme.typography.bodyLarge
            )
          }
          Text(
            text = "यह टास्क रोज़ाना दोहराया जाएगा और आपकी स्ट्रीक गिनेगा",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Switch(
          checked = isHabit,
          onCheckedChange = { isHabit = it },
          colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.primary
          )
        )
      }

      Spacer(modifier = Modifier.height(16.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
      Spacer(modifier = Modifier.height(16.dp))

      // Subtasks Checklist Section
      Text(
        text = "चरण / सबटास्क • Subtasks (वैकल्पिक):",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(8.dp))

      subtasks.forEachIndexed { index, subtask ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "${index + 1}.",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(24.dp)
          )
          Text(
            text = subtask.title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
          )
          IconButton(
            onClick = { subtasks.removeAt(index) },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = "Remove subtask",
              tint = MaterialTheme.colorScheme.error,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = newSubtaskText,
          onValueChange = { newSubtaskText = it },
          placeholder = { Text("नया चरण लिखें...") },
          modifier = Modifier.weight(1f),
          singleLine = true,
          shape = RoundedCornerShape(10.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
          onClick = {
            if (newSubtaskText.isNotBlank()) {
              subtasks.add(SubTask(title = newSubtaskText.trim()))
              newSubtaskText = ""
            }
          },
          modifier = Modifier.size(48.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add subtask",
            tint = MaterialTheme.colorScheme.primary
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Save and Cancel Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedButton(
          onClick = onDismiss,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(14.dp)
        ) {
          Text("रद्द करें • Cancel")
        }

        Button(
          onClick = {
            if (title.isNotBlank()) {
              val todayCal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 12)
                set(Calendar.MINUTE, 0)
              }
              onSave(
                title,
                description,
                selectedCategory,
                selectedPriority,
                todayCal.timeInMillis,
                selectedTimeSlot,
                isHabit,
                subtasks.toList()
              )
            }
          },
          enabled = title.isNotBlank(),
          modifier = Modifier
            .weight(1.5f)
            .testTag("save_task_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
          )
        ) {
          Text(
            text = if (initialTask == null) "टास्क सेव करें • Save" else "अपडेट करें • Update",
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}
