package com.example

import com.example.data.model.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun `task entity has title description dueDate priority and completion status`() {
    val dueTime = 1700000000000L
    val task = Task(
      title = "Buy groceries",
      description = "Milk, Eggs, Bread",
      dueDate = dueTime,
      priority = "HIGH",
      isCompleted = false
    )

    assertEquals("Buy groceries", task.title)
    assertEquals("Milk, Eggs, Bread", task.description)
    assertEquals(dueTime, task.dueDate)
    assertEquals("HIGH", task.priority)
    assertFalse(task.isCompleted)

    val completedTask = task.copy(isCompleted = true)
    assertTrue(completedTask.isCompleted)
  }
}
