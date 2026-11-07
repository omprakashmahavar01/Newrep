package com.example.data.repository

import com.example.data.local.TaskDao
import com.example.data.model.SubTask
import com.example.data.model.Task
import com.example.data.model.TaskCategory
import com.example.data.model.TaskEntity
import com.example.data.model.TaskPriority
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class TaskRepository(private val taskDao: TaskDao) {

  val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
  val habitTasks: Flow<List<TaskEntity>> = taskDao.getHabits()

  suspend fun insertTask(task: TaskEntity): Long {
    return taskDao.insertTask(task)
  }

  suspend fun updateTask(task: TaskEntity) {
    taskDao.updateTask(task)
  }

  suspend fun deleteTask(task: TaskEntity) {
    taskDao.deleteTask(task)
  }

  suspend fun deleteTaskById(id: Long) {
    taskDao.deleteTaskById(id)
  }

  suspend fun deleteCompletedTasks() {
    taskDao.deleteCompletedTasks()
  }

  suspend fun toggleTaskCompletion(task: TaskEntity) {
    val newCompletion = !task.isCompleted
    val newStreak = if (task.isHabit) {
      if (newCompletion) task.streakDays + 1 else maxOf(0, task.streakDays - 1)
    } else {
      task.streakDays
    }
    val updated = task.copy(
      isCompleted = newCompletion,
      completedAtMillis = if (newCompletion) System.currentTimeMillis() else null,
      streakDays = newStreak
    )
    taskDao.updateTask(updated)
  }

  suspend fun toggleSubtask(task: TaskEntity, subtaskId: String) {
    val currentSubtasks = SubTask.stringToList(task.subtasksRaw)
    val updatedSubtasks = currentSubtasks.map {
      if (it.id == subtaskId) it.copy(isCompleted = !it.isCompleted) else it
    }
    val allSubtasksDone = updatedSubtasks.isNotEmpty() && updatedSubtasks.all { it.isCompleted }
    val updatedTask = task.copy(
      subtasksRaw = SubTask.listToString(updatedSubtasks),
      isCompleted = if (allSubtasksDone) true else task.isCompleted
    )
    taskDao.updateTask(updatedTask)
  }

  suspend fun addSubtask(task: TaskEntity, title: String) {
    if (title.isBlank()) return
    val currentSubtasks = SubTask.stringToList(task.subtasksRaw).toMutableList()
    currentSubtasks.add(SubTask(title = title.trim()))
    val updatedTask = task.copy(subtasksRaw = SubTask.listToString(currentSubtasks))
    taskDao.updateTask(updatedTask)
  }

  suspend fun applyRoutineTemplate(templateName: String) {
    val todayCal = Calendar.getInstance().apply {
      set(Calendar.HOUR_OF_DAY, 12)
      set(Calendar.MINUTE, 0)
    }
    val todayMillis = todayCal.timeInMillis

    val tasksToInsert = when (templateName) {
      "MORNING" -> listOf(
        TaskEntity(
          title = "ताज़ा पानी पीना व ध्यान (Hydrate & 10m Meditation)",
          description = "दिन की शांत शुरुआत और मन की एकाग्रता",
          category = TaskCategory.ROUTINE.name,
          priority = TaskPriority.HIGH.name,
          dueDateMillis = todayMillis,
          timeSlot = "06:30 AM • सुबह",
          isHabit = true,
          streakDays = 1
        ),
        TaskEntity(
          title = "हल्का नाश्ता व प्रोटीन (Healthy Breakfast)",
          description = "पौष्टिक भोजन से ऊर्जावान बने रहें",
          category = TaskCategory.HEALTH.name,
          priority = TaskPriority.MEDIUM.name,
          dueDateMillis = todayMillis,
          timeSlot = "08:00 AM • सुबह"
        ),
        TaskEntity(
          title = "दैनिक लक्ष्यों की सूची तैयार करना (Set 3 Key Goals)",
          description = "आज के 3 सबसे महत्वपूर्ण परिणामों पर ध्यान दें",
          category = TaskCategory.WORK.name,
          priority = TaskPriority.HIGH.name,
          dueDateMillis = todayMillis,
          timeSlot = "09:00 AM • सुबह"
        )
      )
      "WORK" -> listOf(
        TaskEntity(
          title = "गहन एकाग्रता कार्य सत्र (Deep Work 90 min)",
          description = "बिना किसी फोन या नोटिफिकेशन व्यवधान के मुख्य प्रोजेक्ट पर काम करें",
          category = TaskCategory.WORK.name,
          priority = TaskPriority.HIGH.name,
          dueDateMillis = todayMillis,
          timeSlot = "10:30 AM • सुबह"
        ),
        TaskEntity(
          title = "टीम समन्वय व ईमेल (Team Updates & Comms)",
          description = "पेंडिंग संदेशों और सहयोगियों से संपर्क",
          category = TaskCategory.WORK.name,
          priority = TaskPriority.MEDIUM.name,
          dueDateMillis = todayMillis,
          timeSlot = "02:00 PM • दोपहर"
        ),
        TaskEntity(
          title = "दिन का समापन व कल की तैयारी (Evening Shutdown)",
          description = "आज के कार्य टिक करें और कल का शेड्यूल देखें",
          category = TaskCategory.WORK.name,
          priority = TaskPriority.MEDIUM.name,
          dueDateMillis = todayMillis,
          timeSlot = "06:00 PM • शाम"
        )
      )
      "HEALTH" -> listOf(
        TaskEntity(
          title = "10,000 कदम या 45 मिनट वर्कआउट (Daily Movement)",
          description = "शारीरिक सक्रियता और कार्डियो स्वास्थ्य",
          category = TaskCategory.HEALTH.name,
          priority = TaskPriority.HIGH.name,
          dueDateMillis = todayMillis,
          timeSlot = "05:30 PM • शाम",
          isHabit = true,
          streakDays = 2
        ),
        TaskEntity(
          title = "हरी सब्जियां व फल (Fresh Fruits & Salad)",
          description = "शाम के भोजन में ताज़े फल व सलाद शामिल करें",
          category = TaskCategory.HEALTH.name,
          priority = TaskPriority.MEDIUM.name,
          dueDateMillis = todayMillis,
          timeSlot = "07:30 PM • शाम"
        ),
        TaskEntity(
          title = "सोने से 1 घंटा पहले स्क्रीन बंद (No Screen before bed)",
          description = "गहरी और सुकून भरी नींद के लिए फोन अलग रखें",
          category = TaskCategory.ROUTINE.name,
          priority = TaskPriority.HIGH.name,
          dueDateMillis = todayMillis,
          timeSlot = "10:00 PM • रात",
          isHabit = true,
          streakDays = 3
        )
      )
      else -> emptyList()
    }

    if (tasksToInsert.isNotEmpty()) {
      taskDao.insertAll(tasksToInsert)
    }
  }
}
