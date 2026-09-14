package com.example

import com.example.model.Category
import com.example.model.SimpleDate
import com.example.model.SimpleTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun simpleDate_leapYearAndDaysCalculation() {
    val febLeap = SimpleDate(2024, 2, 1)
    assertEquals(29, febLeap.daysInMonth())

    val febNonLeap = SimpleDate(2023, 2, 1)
    assertEquals(28, febNonLeap.daysInMonth())

    val nextDay = febLeap.addDays(1)
    assertEquals(2, nextDay.day)
  }

  @Test
  fun simpleTime_formattingAndComparison() {
    val morning = SimpleTime(9, 30)
    val afternoon = SimpleTime(14, 0)
    assertTrue(morning < afternoon)
    assertEquals("09:30 AM", morning.formatted())
    assertEquals("02:00 PM", afternoon.formatted())
  }

  @Test
  fun category_retrieval() {
    val work = Category.fromName("Work")
    assertEquals("Work", work.name)
    assertEquals("💼", work.iconEmoji)
  }
}
