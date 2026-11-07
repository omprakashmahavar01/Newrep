package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class TaskPriority(val displayNameEn: String, val displayNameHi: String) {
  HIGH("High", "उच्च (ज़रूरी)"),
  MEDIUM("Medium", "मध्यम"),
  LOW("Low", "सामान्य")
}

enum class TaskCategory(val displayNameEn: String, val displayNameHi: String) {
  ROUTINE("Daily Routine", "दिनचर्या"),
  WORK("Work & Business", "काम / ऑफिस"),
  HEALTH("Health & Fitness", "सेहत / व्यायाम"),
  STUDY("Study & Learning", "पढ़ाई / ज्ञान"),
  PERSONAL("Personal & Home", "व्यक्तिगत / घर"),
  SHOPPING("Shopping", "खरीदारी")
}

data class SubTask(
  val id: String = UUID.randomUUID().toString(),
  val title: String,
  val isCompleted: Boolean = false
) {
  fun serialize(): String = "${id.replace("|", "")}|${title.replace("|", "")}|$isCompleted"

  companion object {
    fun deserialize(line: String): SubTask? {
      val parts = line.split("|")
      if (parts.size >= 3) {
        return SubTask(
          id = parts[0],
          title = parts[1],
          isCompleted = parts[2].toBooleanStrictOrNull() ?: false
        )
      }
      return null
    }

    fun listToString(subtasks: List<SubTask>): String =
      subtasks.joinToString("\n") { it.serialize() }

    fun stringToList(data: String?): List<SubTask> {
      if (data.isNullOrBlank()) return emptyList()
      return data.lines().mapNotNull { deserialize(it) }
    }
  }
}

/**
 * Room database entity for 'Task' with fields for:
 * - title: Title of the task
 * - description: Detailed description / notes
 * - dueDate: Target due date timestamp in milliseconds
 * - priority: Priority level ("HIGH", "MEDIUM", "LOW")
 * - isCompleted: Task completion status
 */
@Entity(tableName = "tasks")
data class Task(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val title: String,
  val description: String = "",
  val dueDate: Long = System.currentTimeMillis(),
  val priority: String = TaskPriority.MEDIUM.name,
  val isCompleted: Boolean = false,
  val dueDateMillis: Long = dueDate,
  val category: String = TaskCategory.ROUTINE.name,
  val timeSlot: String = "Morning / सुबह",
  val completedAtMillis: Long? = null,
  val isHabit: Boolean = false,
  val streakDays: Int = 0,
  val subtasksRaw: String = "",
  val createdAtMillis: Long = System.currentTimeMillis()
)

typealias TaskEntity = Task
