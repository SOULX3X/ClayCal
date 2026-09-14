package com.example.model

import java.util.Calendar

data class SimpleDate(
    val year: Int,
    val month: Int, // 1..12
    val day: Int    // 1..31
) : Comparable<SimpleDate> {

    companion object {
        fun today(): SimpleDate {
            val cal = Calendar.getInstance()
            return SimpleDate(
                year = cal.get(Calendar.YEAR),
                month = cal.get(Calendar.MONTH) + 1,
                day = cal.get(Calendar.DAY_OF_MONTH)
            )
        }

        fun fromCalendar(cal: Calendar): SimpleDate {
            return SimpleDate(
                year = cal.get(Calendar.YEAR),
                month = cal.get(Calendar.MONTH) + 1,
                day = cal.get(Calendar.DAY_OF_MONTH)
            )
        }

        fun getMonthName(month: Int): String {
            return when (month) {
                1 -> "January"
                2 -> "February"
                3 -> "March"
                4 -> "April"
                5 -> "May"
                6 -> "June"
                7 -> "July"
                8 -> "August"
                9 -> "September"
                10 -> "October"
                11 -> "November"
                12 -> "December"
                else -> "Month $month"
            }
        }

        fun getShortMonthName(month: Int): String {
            return when (month) {
                1 -> "Jan"
                2 -> "Feb"
                3 -> "Mar"
                4 -> "Apr"
                5 -> "May"
                6 -> "Jun"
                7 -> "Jul"
                8 -> "Aug"
                9 -> "Sep"
                10 -> "Oct"
                11 -> "Nov"
                12 -> "Dec"
                else -> "$month"
            }
        }

        fun getDayOfWeekShortName(dayOfWeek: Int): String {
            // Calendar.SUNDAY is 1, SATURDAY is 7
            return when (dayOfWeek) {
                Calendar.SUNDAY -> "Sun"
                Calendar.MONDAY -> "Mon"
                Calendar.TUESDAY -> "Tue"
                Calendar.WEDNESDAY -> "Wed"
                Calendar.THURSDAY -> "Thu"
                Calendar.FRIDAY -> "Fri"
                Calendar.SATURDAY -> "Sat"
                else -> ""
            }
        }

        fun getDaysInMonth(year: Int, month: Int): Int {
            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month - 1)
                set(Calendar.DAY_OF_MONTH, 1)
            }
            return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        }

        fun getFirstDayOfWeekInMonth(year: Int, month: Int): Int {
            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month - 1)
                set(Calendar.DAY_OF_MONTH, 1)
            }
            return cal.get(Calendar.DAY_OF_WEEK) // 1 (Sun) .. 7 (Sat)
        }

        fun fromIsoString(iso: String): SimpleDate {
            val parts = iso.split("-")
            return if (parts.size == 3) {
                SimpleDate(
                    year = parts[0].toIntOrNull() ?: today().year,
                    month = parts[1].toIntOrNull() ?: today().month,
                    day = parts[2].toIntOrNull() ?: today().day
                )
            } else {
                today()
            }
        }
    }

    val dayOfWeek: Int
        get() {
            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month - 1)
                set(Calendar.DAY_OF_MONTH, day)
            }
            return cal.get(Calendar.DAY_OF_WEEK)
        }

    val dayOfWeekName: String
        get() = getDayOfWeekShortName(dayOfWeek)

    val monthName: String
        get() = getMonthName(month)

    val shortMonthName: String
        get() = getShortMonthName(month)

    fun formatted(): String = "$dayOfWeekName, $shortMonthName $day, $year"

    fun formattedShort(): String = "$shortMonthName $day"

    fun isSameDay(other: SimpleDate): Boolean {
        return year == other.year && month == other.month && day == other.day
    }

    fun isToday(): Boolean {
        return isSameDay(today())
    }

    fun addDays(days: Int): SimpleDate {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
            add(Calendar.DAY_OF_MONTH, days)
        }
        return fromCalendar(cal)
    }

    fun nextMonth(): SimpleDate {
        return if (month == 12) {
            SimpleDate(year + 1, 1, 1)
        } else {
            SimpleDate(year, month + 1, 1)
        }
    }

    fun prevMonth(): SimpleDate {
        return if (month == 1) {
            SimpleDate(year - 1, 12, 1)
        } else {
            SimpleDate(year, month - 1, 1)
        }
    }

    fun toIsoString(): String {
        return String.format(java.util.Locale.US, "%04d-%02d-%02d", year, month, day)
    }

    fun daysInMonth(): Int = getDaysInMonth(year, month)

    fun startOfWeek(): SimpleDate {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
        }
        // Sunday is 1
        val diff = cal.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY
        cal.add(Calendar.DAY_OF_MONTH, -diff)
        return fromCalendar(cal)
    }

    fun daysOfWeek(): List<SimpleDate> {
        val start = startOfWeek()
        return (0..6).map { start.addDays(it) }
    }

    override fun compareTo(other: SimpleDate): Int {
        if (year != other.year) return year.compareTo(other.year)
        if (month != other.month) return month.compareTo(other.month)
        return day.compareTo(other.day)
    }
}
