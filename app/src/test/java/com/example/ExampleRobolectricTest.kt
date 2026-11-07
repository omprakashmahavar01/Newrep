package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.SubTask
import com.example.data.model.TaskCategory
import com.example.data.model.TaskPriority
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Rozana Tasks", appName)
  }

  @Test
  fun `subtask serialization and deserialization works correctly`() {
    val original = listOf(
      SubTask(id = "sub1", title = "Drink water", isCompleted = true),
      SubTask(id = "sub2", title = "Workout", isCompleted = false)
    )
    val serialized = SubTask.listToString(original)
    val parsed = SubTask.stringToList(serialized)

    assertEquals(2, parsed.size)
    assertEquals("Drink water", parsed[0].title)
    assertTrue(parsed[0].isCompleted)
    assertEquals("Workout", parsed[1].title)
    assertEquals(false, parsed[1].isCompleted)
  }

  @Test
  fun `task categories and priorities are defined with hindi labels`() {
    assertNotNull(TaskCategory.ROUTINE.displayNameHi)
    assertNotNull(TaskPriority.HIGH.displayNameHi)
    assertEquals("दिनचर्या", TaskCategory.ROUTINE.displayNameHi)
  }
}
