package com.example.mindfulnesstracker.ui.viewmodels

import android.annotation.SuppressLint
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.random.Random

class ProgressViewModel : ViewModel() {
    private val todayCalendar = Calendar.getInstance()
    private val calendar = Calendar.getInstance()

    val monthName = mutableStateOf("")
    val days = mutableStateListOf<Pair<Int, Boolean?>>()

    init {
        updateFields()
    }

    fun previousMonth() {
        calendar.add(Calendar.MONTH, -1)
        updateFields()
    }

    fun currentMonth() {
        calendar.time = todayCalendar.time
        updateFields()
    }

    fun nextMonth() {
        calendar.add(Calendar.MONTH, 1)
        updateFields()
    }

    private fun updateFields() {
        monthName.value = formatMonth()
        days.clear()
        days.addAll(getDays())
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatMonth(): String {
        return if (todayCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
            SimpleDateFormat("LLLL").format(calendar.time)
        } else {
            SimpleDateFormat("LLLL yyyy").format(calendar.time)
        }
    }

    private fun getDays(): List<Pair<Int, Boolean?>> {
        val weekCalendar = Calendar.getInstance()
        weekCalendar.time = calendar.time
        weekCalendar.set(Calendar.DAY_OF_MONTH, 1)
        weekCalendar.add(Calendar.MONTH, 1)
        weekCalendar.add(Calendar.DAY_OF_MONTH, -1)
        weekCalendar.add(Calendar.WEEK_OF_YEAR, 1)
        val lastWeek = weekCalendar.get(Calendar.WEEK_OF_YEAR)
        weekCalendar.set(Calendar.DAY_OF_MONTH, 1)
        weekCalendar.add(Calendar.MONTH, -1)
        weekCalendar.set(Calendar.DAY_OF_WEEK, weekCalendar.firstDayOfWeek)

        val days = mutableListOf<Pair<Int, Boolean?>>()

        while (weekCalendar.get(Calendar.WEEK_OF_YEAR) != lastWeek) {
            val day = weekCalendar.get(Calendar.DAY_OF_MONTH)
            val status =
                if (weekCalendar.time > todayCalendar.time) {
                    null
                } else {
                    Random.nextBoolean()
                }
            days.add(Pair(day, status))
            weekCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return days
    }
}
