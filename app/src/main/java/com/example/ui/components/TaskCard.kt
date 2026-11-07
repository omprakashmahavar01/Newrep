package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SubTask
import com.example.data.model.TaskCategory
import com.example.data.model.TaskEntity
import com.example.data.model.TaskPriority
import com.example.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskCard(
  task: TaskEntity,
  onToggleComplete: () -> Unit,
  onToggleSubtask: (String) -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  onSpeakTask: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  var isExpanded by remember { mutableStateOf(false) }
  var showMenu by remember { mutableStateOf(false) }

  val subtasks = remember(task.subtasksRaw) {
    SubTask.stringToList(task.subtasksRaw)
  }
  val completedSubtasks = subtasks.count { it.isCompleted }

  val priorityColor = when (task.priority) {
    TaskPriority.HIGH.name -> PriorityHigh
    TaskPriority.LOW.name -> PriorityLow
    else -> PriorityMedium
  }

  val categoryColor = when (task.category) {
    TaskCategory.WORK.name -> CategoryWork
    TaskCategory.PERSONAL.name -> CategoryPersonal
    TaskCategory.HEALTH.name -> CategoryHealth
    TaskCategory.STUDY.name -> CategoryStudy
    TaskCategory.SHOPPING.name -> CategoryShopping
    else -> CategoryRoutine
  }

  val categoryLabel = TaskCategory.entries.find { it.name == task.category }?.let {
    "${it.displayNameHi} • ${it.displayNameEn}"
  } ?: task.category

  val cardBg by animateColorAsState(
    targetValue = if (task.isCompleted) {
      MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    } else {
      MaterialTheme.colorScheme.surface
    },
    label = "cardBgColor"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("task_card_${task.id}"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = cardBg),
    elevation = CardDefaults.cardElevation(defaultElevation = if (task.isCompleted) 0.dp else 1.5.dp),
    border = if (task.isCompleted) null else CardDefaults.outlinedCardBorder()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
      ) {
        // Custom Checkbox button with ripple
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(
              if (task.isCompleted) MaterialTheme.colorScheme.primary
              else MaterialTheme.colorScheme.surfaceVariant
            )
            .border(
              width = 2.dp,
              color = if (task.isCompleted) MaterialTheme.colorScheme.primary
              else MaterialTheme.colorScheme.outline,
              shape = CircleShape
            )
            .clickable(onClick = onToggleComplete)
            .testTag("task_checkbox_${task.id}"),
          contentAlignment = Alignment.Center
        ) {
          if (task.isCompleted) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Completed",
              tint = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Main text content
        Column(
          modifier = Modifier
            .weight(1f)
            .clickable { onToggleComplete() }
        ) {
          Text(
            text = task.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (task.isCompleted) {
              MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            } else {
              MaterialTheme.colorScheme.onSurface
            },
            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
          )

          if (task.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = task.description,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                alpha = if (task.isCompleted) 0.5f else 0.85f
              )
            )
          }
        }

        // Speaker button to speak task aloud
        IconButton(
          onClick = onSpeakTask,
          modifier = Modifier.testTag("task_speak_button_${task.id}")
        ) {
          Icon(
            imageVector = Icons.Default.VolumeUp,
            contentDescription = "बोलकर सुनाएं",
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
            modifier = Modifier.size(20.dp)
          )
        }

        // Action menu
        Box {
          IconButton(
            onClick = { showMenu = true },
            modifier = Modifier.testTag("task_menu_button_${task.id}")
          ) {
            Icon(
              imageVector = Icons.Default.MoreVert,
              contentDescription = "Options",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
          ) {
            DropdownMenuItem(
              text = { Text("बदलें (Edit)") },
              leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
              onClick = {
                showMenu = false
                onEdit()
              }
            )
            DropdownMenuItem(
              text = { Text("हटाएं (Delete)") },
              leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = PriorityHigh) },
              onClick = {
                showMenu = false
                onDelete()
              }
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Badges: Category, Priority, Time, Streak, Subtasks count
      FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // Category Badge
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = categoryColor.copy(alpha = 0.12f)
        ) {
          Text(
            text = categoryLabel,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = categoryColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }

        // Priority Badge
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = priorityColor.copy(alpha = 0.12f)
        ) {
          val priorityText = when (task.priority) {
            TaskPriority.HIGH.name -> "उच्च • High"
            TaskPriority.LOW.name -> "सामान्य • Low"
            else -> "मध्यम • Med"
          }
          Text(
            text = priorityText,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = priorityColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }

        // Time Slot Badge
        if (task.timeSlot.isNotBlank()) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = task.timeSlot,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        // Habit Streak Badge
        if (task.isHabit) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFFEF3C7)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint = Color(0xFFD97706),
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "आदत • ${task.streakDays} दिन",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF92400E)
              )
            }
          }
        }

        // Subtasks expandable counter badge
        if (subtasks.isNotEmpty()) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
            modifier = Modifier.clickable { isExpanded = !isExpanded }
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = "$completedSubtasks/${subtasks.size} चरण",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
              Spacer(modifier = Modifier.width(2.dp))
              Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(14.dp)
              )
            }
          }
        }
      }

      // Expandable Subtasks Checklist
      AnimatedVisibility(
        visible = isExpanded && subtasks.isNotEmpty(),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .background(
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
              shape = RoundedCornerShape(12.dp)
            )
            .padding(8.dp)
        ) {
          subtasks.forEach { subtask ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleSubtask(subtask.id) }
                .padding(vertical = 4.dp, horizontal = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Checkbox(
                checked = subtask.isCompleted,
                onCheckedChange = { onToggleSubtask(subtask.id) },
                colors = CheckboxDefaults.colors(
                  checkedColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = subtask.title,
                style = MaterialTheme.typography.bodySmall,
                textDecoration = if (subtask.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                color = if (subtask.isCompleted) {
                  MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                } else {
                  MaterialTheme.colorScheme.onSurface
                }
              )
            }
          }
        }
      }
    }
  }
}
