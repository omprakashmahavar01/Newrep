package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.SubTask
import com.example.data.model.Task
import com.example.data.model.TaskCategory
import com.example.data.model.TaskPriority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
  abstract fun taskDao(): TaskDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "app_database.db"
        )
          .addCallback(AppDatabaseCallback(scope))
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }

    private class AppDatabaseCallback(
      private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            populateInitialData(database.taskDao())
          }
        }
      }
    }

    suspend fun populateInitialData(taskDao: TaskDao) {
      if (taskDao.getTaskCount() > 0) return

      val todayCal = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 12)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
      }
      val todayMillis = todayCal.timeInMillis

      val initialTasks = listOf(
        Task(
          title = "सुबह की सैर व हल्का व्यायाम (Morning Walk & Yoga)",
          description = "ताज़ा हवा में 20-30 मिनट टहलना और प्राणायाम करना",
          dueDate = todayMillis,
          category = TaskCategory.HEALTH.name,
          priority = TaskPriority.HIGH.name,
          timeSlot = "06:30 AM • सुबह",
          isCompleted = false,
          isHabit = true,
          streakDays = 5,
          subtasksRaw = SubTask.listToString(listOf(
            SubTask(title = "10 मिनट वार्म अप व स्ट्रेचिंग", isCompleted = false),
            SubTask(title = "20 मिनट ब्रिस्क वॉक", isCompleted = false)
          ))
        ),
        Task(
          title = "दैनिक प्राथमिकताओं की समीक्षा (Review daily goals)",
          description = "आज के 3 सबसे महत्वपूर्ण कार्यों को पहचानें और समय निर्धारित करें",
          dueDate = todayMillis,
          category = TaskCategory.WORK.name,
          priority = TaskPriority.HIGH.name,
          timeSlot = "09:30 AM • सुबह",
          isCompleted = false,
          isHabit = false
        ),
        Task(
          title = "पर्याप्त पानी पीना (Drink 8 glasses of water)",
          description = "पूरे दिन शरीर को हाइड्रेटेड और ऊर्जावान रखें",
          dueDate = todayMillis,
          category = TaskCategory.ROUTINE.name,
          priority = TaskPriority.MEDIUM.name,
          timeSlot = "पूरे दिन • All Day",
          isCompleted = false,
          isHabit = true,
          streakDays = 7,
          subtasksRaw = SubTask.listToString(listOf(
            SubTask(title = "सुबह 2 गिलास गर्म पानी", isCompleted = true),
            SubTask(title = "दोपहर तक 3 गिलास", isCompleted = false),
            SubTask(title = "शाम तक 3 गिलास", isCompleted = false)
          ))
        ),
        Task(
          title = "ज्ञानवर्धक पुस्तक के 10 पेज पढ़ना (Read 10 pages)",
          description = "सोने से पहले मन को शांत करने और नया सीखने के लिए पढ़ें",
          dueDate = todayMillis,
          category = TaskCategory.STUDY.name,
          priority = TaskPriority.MEDIUM.name,
          timeSlot = "09:00 PM • रात",
          isCompleted = false,
          isHabit = true,
          streakDays = 3
        )
      )

      taskDao.insertAll(initialTasks)
    }
  }
}
