package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
  @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, dueDate ASC, id DESC")
  fun getAllTasks(): Flow<List<Task>>

  @Query("SELECT * FROM tasks WHERE isHabit = 1 ORDER BY isCompleted ASC, id DESC")
  fun getHabits(): Flow<List<Task>>

  @Query("SELECT * FROM tasks WHERE (dueDate >= :startOfDay AND dueDate <= :endOfDay) OR (dueDateMillis >= :startOfDay AND dueDateMillis <= :endOfDay) ORDER BY isCompleted ASC, priority ASC, id DESC")
  fun getTasksForDate(startOfDay: Long, endOfDay: Long): Flow<List<Task>>

  @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
  suspend fun getTaskById(id: Long): Task?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTask(task: Task): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(tasks: List<Task>)

  @Update
  suspend fun updateTask(task: Task)

  @Delete
  suspend fun deleteTask(task: Task)

  @Query("DELETE FROM tasks WHERE id = :id")
  suspend fun deleteTaskById(id: Long)

  @Query("DELETE FROM tasks WHERE isCompleted = 1 AND isHabit = 0")
  suspend fun deleteCompletedTasks()

  @Query("SELECT COUNT(*) FROM tasks")
  suspend fun getTaskCount(): Int
}
